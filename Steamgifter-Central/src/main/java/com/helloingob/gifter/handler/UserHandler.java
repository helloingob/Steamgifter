package com.helloingob.gifter.handler;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.helloingob.gifter.ErrorLogHandler;
import com.helloingob.gifter.algorithm.Algorithm;
import com.helloingob.gifter.algorithm.ChancerAlg;
import com.helloingob.gifter.algorithm.DefaultAlg;
import com.helloingob.gifter.algorithm.FakerAlg;
import com.helloingob.gifter.algorithm.GrabberAlg;
import com.helloingob.gifter.algorithm.SnatcherAlg;
import com.helloingob.gifter.algorithm.SnitchAlg;
import com.helloingob.gifter.dao.AlgorithmDAO;
import com.helloingob.gifter.dao.UserAssetDAO;
import com.helloingob.gifter.dao.UserDAO;
import com.helloingob.gifter.dao.WonGiveawayDAO;
import com.helloingob.gifter.data.connection.HTTPConnection;
import com.helloingob.gifter.data.to.AdvUserTO;
import com.helloingob.gifter.data.to.EnteredGiveaway;
import com.helloingob.gifter.data.to.Giveaway;
import com.helloingob.gifter.data.to.SimpleWonGiveaway;
import com.helloingob.gifter.data.to.SteamgiftsResponse;
import com.helloingob.gifter.enums.Status;
import com.helloingob.gifter.log.InfoLogHandler;
import com.helloingob.gifter.parser.UserInformationParser;
import com.helloingob.gifter.to.AlgorithmTO;
import com.helloingob.gifter.to.UserAssetTO;
import com.helloingob.gifter.to.WonGiveawayTO;
import com.helloingob.gifter.utilities.CentralSettings;
import com.helloingob.gifter.utilities.GeneralHelper;
import com.helloingob.gifter.utilities.SharedSettings;
import com.helloingob.gifter.utilities.TimeHelper;
import com.helloingob.gifter.utilities.mail.MailClient;
import com.helloingob.gifter.utilities.useragent.UserAgentSpoofer;

public class UserHandler implements Callable<Status> {
    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);
    private Integer points;
    private Double level;
    private List<Giveaway> giveaways;
    private Set<String> alreadyEnteredGiveaways;

    private InfoLogHandler infoLogHandler;

    private AdvUserTO user;

    public UserHandler(AdvUserTO user) {
        this.user = user;
    }

    private boolean init() {
        //create new asset
        user.setUserAsset(new UserAssetTO());
        //randomize UserAgent
        user.setUserAgent(new UserAgentSpoofer().generateUserAgent());

        String pageSource = doLogin(user.getPhpsessionid());
        //check for timeout
        if (pageSource != null) {
            if (!pageSource.isEmpty()) {
                //check for authenticated
                if (UserInformationParser.isAuthenticated(pageSource)) {
                    actionsAfterLogin(pageSource);
                } else {
                    new ErrorLogHandler(user).error("[LOGIN] Authentication failed -> " + user.getPhpsessionid() + " invalid", pageSource, logger);
                    return false;
                }
            } else {
                new ErrorLogHandler(user).error("[LOGIN] Page empty", "'" + pageSource + "'", logger);
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    private void actionsAfterLogin(String pageSource) {
        //init entered giveaway list
        alreadyEnteredGiveaways = new HashSet<>();

        //parse initial points and token
        user.setXrefToken(UserInformationParser.parseXrefToken(pageSource));
        points = UserInformationParser.parsePoints(pageSource);
        level = UserInformationParser.parseLevel(pageSource);

        //update profile
        user.setProfileName(UserInformationParser.parseProfileName(pageSource));
        user.setImageLink(UserInformationParser.parseProfileImage(pageSource));
        user.setSteamId(UserInformationParser.getSteamId(user));

        //check for won games
        List<WonGiveawayTO> wonGiveaways = handleWonGiveaways();

        //fill user asset
        user.getUserAsset().setDate(TimeHelper.getCurrentTimestamp());
        user.getUserAsset().setPoints(points);
        user.getUserAsset().setLevel(level);
        user.getUserAsset().setUserPk(user.getPk());

        //sync games once a day & set giveaway filter
        if (UserInformationParser.shouldSyncBeExecuted(pageSource)) {
            boolean isSynced = syncAccountwithSteam();
            if (isSynced) {
                logger.info("[" + user.getLoginName() + "] Giveaways synced.");
            } else {
                logger.info("[" + user.getLoginName() + "] Giveaways NOT synced :(");
            }
            user.getUserAsset().setSynced(isSynced);

            if (updateGiveawaysFilterSettings()) {
                logger.info("[" + user.getLoginName() + "] Giveaways filter updated.");
            } else {
                logger.info("[" + user.getLoginName() + "] Giveaways filter NOT set :(");
            }
        } else {
            user.getUserAsset().setSynced(false);
        }
        new UserAssetDAO().save(user.getUserAsset());

        //create Info Logger (user asset should be saved  before!)
        infoLogHandler = new InfoLogHandler(user);

        if (user.getSkipWishlist()) {
            giveaways = new GiveawayHandler(user).getGiveaways(wonGiveaways);
        } else {
            //checks if there a giveaway on wishlist
            if (!grantWishesFromWishlist(wonGiveaways)) {
                //if there are no giveaways return to normal
                giveaways = new GiveawayHandler(user).getGiveaways(wonGiveaways);
            }
        }

        //save profilename and image
        new UserDAO().update(user);

    }

    private boolean grantWishesFromWishlist(List<WonGiveawayTO> wonGiveaways) {
        //gets wishes from wishlist
        List<Giveaway> wishlistGiveaways = new WishlistGiveawayHandler(user).getGiveaways(wonGiveaways);
        if (wishlistGiveaways.size() > 0) {
            //enter giveaways on wishlist
            for (Giveaway giveaway : wishlistGiveaways) {
                if (giveaway.getPoints() < this.getPoints()) {
                    logger.info("[" + user.getLoginName() + "] Entered wishlist giveaway -> " + giveaway.getTitle() + " (" + giveaway.getGiveawayLink() + ")");
                    this.enterGiveaway(giveaway);
                } else {
                    //save points, skip wishes & giveaways
                    giveaways = new LinkedList<>();
                    return true;
                }
            }
        }
        //no wishes or all wishes fullfilled
        return false;
    }

    private List<WonGiveawayTO> handleWonGiveaways() {
        WonGiveawayDAO wonGiveawayDAO = new WonGiveawayDAO();
        WonGiveawayHandler wonGiveawayHandler = new WonGiveawayHandler(user, wonGiveawayDAO);

        //get saved won giveaways
        List<WonGiveawayTO> wonGiveaways = wonGiveawayDAO.get(user);
        //get new won giveaways
        List<SimpleWonGiveaway> simpleWonGiveaways = wonGiveawayHandler.getSimpleWonGiveaways();

        //only if no timeout has occured
        if (simpleWonGiveaways != null && simpleWonGiveaways.size() > 0) {
            //sync wongiveaways with database (if giveaway is reassigned)
            wonGiveaways = wonGiveawayHandler.removeOldDatabaseEntries(wonGiveaways, simpleWonGiveaways);

            //check for and update keys
            wonGiveawayHandler.updateFieldsFromWonGiveaway(wonGiveaways, simpleWonGiveaways);

            //remove already saved giveaways
            wonGiveawayHandler.removeAlreadySavedWonGiveaways(wonGiveaways, simpleWonGiveaways);

            //query new giveaway details, save and send mail
            wonGiveawayHandler.handleNewWonGiveaways(wonGiveaways, simpleWonGiveaways);

            //handle not knowledged giveaways
            wonGiveawayHandler.checkForNotAcknowledgedWonGiveaways(wonGiveaways);
        }
        //only gets steam appid xml list for not activated gifts
        if (hasNotActivatedSteamGame(wonGiveaways)) {
            SteamActivationHandler steamActivationHandler = new SteamActivationHandler(user, wonGiveawayDAO);
            steamActivationHandler.refreshSteamActivationStatus(wonGiveaways);
        }
        return wonGiveaways;
    }

    private boolean hasNotActivatedSteamGame(List<WonGiveawayTO> wonGiveaways) {
        for (WonGiveawayTO wonGiveaway : wonGiveaways) {
            if (!wonGiveaway.getSteamActivationDate().isPresent()) {
                return true;
            }
        }
        return false;
    }

    private boolean syncAccountwithSteam() {
        List<NameValuePair> nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("xsrf_token", user.getXrefToken()));
        nameValuePairs.add(new BasicNameValuePair("do", "sync"));

        SteamgiftsResponse response = executeCommand(nameValuePairs);
        if (response != null && response.getType().equals("success")) {
            return true;
        } else {
            return false;
        }
    }

    private String doLogin(String phpSessionId) {
        HttpResponse response = new HTTPConnection(user.getUserAgent()).doLogin(CentralSettings.Url.INDEX_URL, phpSessionId);
        if (response != null) {
            try {
                switch (response.getStatusLine().getStatusCode()) {
                case 301:
                    new SuspensionHandler(user).handleSuspension();
                    break;
                case 200:
                    HttpEntity responseEntity = response.getEntity();
                    return EntityUtils.toString(responseEntity);
                default:
                    new ErrorLogHandler(user).error("[LOGIN] Wrong status code at login!", String.valueOf(response.getStatusLine().getStatusCode()), logger);
                }
            } catch (Exception e) {
                new ErrorLogHandler(user).error(e, logger);
            }
        }
        return null;
    }

    public boolean enterGiveaway(Giveaway giveaway) {
        //Do not enter duplicate games on different giveaways in the same poll
        if (alreadyEnteredGiveaways.contains(giveaway.getTitle())) {
            return false;
        } else {
            //add giveaway to already entered giveaways
            alreadyEnteredGiveaways.add(giveaway.getTitle());

            List<NameValuePair> nameValuePairs = new LinkedList<>();
            nameValuePairs.add(new BasicNameValuePair("xsrf_token", user.getXrefToken()));
            nameValuePairs.add(new BasicNameValuePair("do", "entry_insert"));
            nameValuePairs.add(new BasicNameValuePair("code", giveaway.getCode()));

            SteamgiftsResponse steamgiftsResponse = executeCommand(nameValuePairs);
            if (steamgiftsResponse != null && steamgiftsResponse.getType().equals("success")) {
                points = steamgiftsResponse.getPoints();
                infoLogHandler.createInfoLogEntry(giveaway);
                return true;
            } else {
                handleErroneousGiveawayResponse(giveaway, steamgiftsResponse);
                return false;
            }
        }
    }

    private void handleErroneousGiveawayResponse(Giveaway giveaway, SteamgiftsResponse steamgiftsResponse) {
        if (steamgiftsResponse != null) {

            if (steamgiftsResponse.getMsg().equals("Sync Required")) {
                points = 0;
                user.setIsActive(false);
                new UserDAO().update(user);
                new MailClient(user).sendSyncRequiredEmail();
            }

            new ErrorLogHandler(user).error("Erroneous response", giveaway.getGiveawayLink() + " -> " + steamgiftsResponse.toString(), logger);
        }
    }

    public boolean removeEnteredGiveaway(EnteredGiveaway enteredGiveaway) {
        List<NameValuePair> nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("xsrf_token", user.getXrefToken()));
        nameValuePairs.add(new BasicNameValuePair("do", "entry_delete"));
        nameValuePairs.add(new BasicNameValuePair("code", enteredGiveaway.getCode()));

        SteamgiftsResponse response = executeCommand(nameValuePairs);
        if (response != null && response.getType().equals("success")) {
            points = response.getPoints();
            return true;
        } else {
            return false;
        }
    }

    public boolean updateGiveawaysFilterSettings() {
        List<NameValuePair> nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("xsrf_token", user.getXrefToken()));
        //Hide games you manually filtered?
        nameValuePairs.add(new BasicNameValuePair("filter_giveaways_additional_games", "1"));
        //Hide games you already own?
        nameValuePairs.add(new BasicNameValuePair("filter_giveaways_exist_in_account", "1"));
        //Hide giveaways above your level?
        nameValuePairs.add(new BasicNameValuePair("filter_giveaways_level", "1"));
        //Hide DLC if you're missing the base game?
        nameValuePairs.add(new BasicNameValuePair("filter_giveaways_missing_base_game", "1"));
        //Filter by OS - Windows
        nameValuePairs.add(new BasicNameValuePair("filter_os", "1"));

        //TODO not working since 19.04.2018
        executeCommand(nameValuePairs);
        return true;
        //        if (response != null && response.getType().equals("success")) {
        //            points = response.getPoints();
        //            return true;
        //        } else {
        //            return false;
        //        }
    }

    public List<EnteredGiveaway> getEnteredGiveaways() {
        return new EnteredGiveawayHandler(user).getEnteredGiveaways();
    }

    private SteamgiftsResponse executeCommand(List<NameValuePair> nameValuePairs) {
        SteamgiftsResponse responseTO = new SteamgiftsResponse();
        Gson gson = new Gson();
        String responseString = "";
        HttpResponse response = new HTTPConnection(user.getUserAgent()).doPost(CentralSettings.Url.COMMAND_URL, nameValuePairs, user.getPhpsessionid());
        if (response != null) {
            try {
                HttpEntity responseEntity = response.getEntity();
                responseString = EntityUtils.toString(responseEntity);
                responseTO.setStatusCode(response.getStatusLine().getStatusCode());
                return gson.fromJson(responseString, SteamgiftsResponse.class);
            } catch (Exception e) {
                new ErrorLogHandler(user).error(e, responseString, logger);
            }
        }
        return null;
    }

    private Algorithm determineAlgorithm() {
        AlgorithmTO algorithm = new AlgorithmDAO().get(user.getAlgorithmPk());

        switch (algorithm.getName()) {
        case "Snitch": {
            return new SnitchAlg(this, giveaways);
        }
        case "Grabber": {
            return new GrabberAlg(this, giveaways);
        }
        case "Snatcher": {
            return new SnatcherAlg(this, giveaways);
        }
        case "Chancer": {
            return new ChancerAlg(this, giveaways);
        }
        case "Faker": {
            return new FakerAlg(this, giveaways);
        }
        case "Default":
        default: {
            return new DefaultAlg(this, giveaways);
        }
        }
    }

    public Integer getPoints() {
        return points;
    }

    public Boolean skipDlc() {
        return user.getSkipDlc();
    }

    public Boolean updateUserAssetPoints() {
        user.getUserAsset().setPoints(points);
        return new UserAssetDAO().update(user.getUserAsset());
    }

    @Override
    public Status call() throws Exception {
        //Imitate human behavior
        try {
            Thread.sleep(GeneralHelper.generateRandomSleepTime(CentralSettings.Security.MIN_RANDOM_SLEEP_RANGE, CentralSettings.Security.MAX_RANDOM_SLEEP_RANGE));
        } catch (Exception e) {
            new ErrorLogHandler(user).error(e, logger);
            return Status.ERROR;
        }
        if (init()) {
            determineAlgorithm().spendPoints();
        } else {
            return Status.ERROR;
        }

        return Status.SUCCESS;
    }

    public AdvUserTO getUser() {
        return user;
    }

}
