package com.helloingob.gifter.handler;

import java.util.LinkedList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.helloingob.gifter.ErrorLogHandler;
import com.helloingob.gifter.dao.WonGiveawayDAO;
import com.helloingob.gifter.data.connection.HTTPConnection;
import com.helloingob.gifter.data.to.AdvUserTO;
import com.helloingob.gifter.to.WonGiveawayTO;
import com.helloingob.gifter.utilities.CentralSettings;
import com.helloingob.gifter.utilities.SharedSettings;
import com.helloingob.gifter.utilities.TimeHelper;
import com.helloingob.gifter.utilities.mail.MailClient;

public class SteamActivationHandler {
    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);
    private AdvUserTO user;
    private List<Integer> ownedAppIds = new LinkedList<Integer>();
    private WonGiveawayDAO wonGiveawayDAO;

    public SteamActivationHandler(AdvUserTO user, WonGiveawayDAO wonGiveawayDAO) {
        this.user = user;
        this.wonGiveawayDAO = wonGiveawayDAO;
        ownedAppIds = getSteamAppIDs();
    }

    public void refreshSteamActivationStatus(List<WonGiveawayTO> wonGiveaways) {
        String notActivatedWonGiveawaysBan = "";
        String notActivatedWonGiveawaysWarning = "";

        for (WonGiveawayTO wonGiveaway : wonGiveaways) {
            if (!wonGiveaway.getSteamActivationDate().isPresent()) {
                Integer wonGiveawayAppId = getAppIDFromGiveaway(wonGiveaway);
                //no appID (Package, No SteamStoreLink, Soundtrack ...) OR a valid appID + already owned
                if (wonGiveawayAppId == null || ownedAppIds.contains(wonGiveawayAppId)) {
                    if (wonGiveawayAppId == null) {
                        logger.info("[" + user.getLoginName() + "] Steam appID not found! -> " + wonGiveaway.getTitle() + " (" + wonGiveaway.getSteamLink() + ")");
                    }
                    wonGiveaway.setSteamActivationDate(TimeHelper.getCurrentTimestamp());
                    wonGiveawayDAO.update(wonGiveaway);
                }

                //if giveaway is received, but not activated
                if (wonGiveaway.getHasReceived().isPresent() && wonGiveaway.getHasReceived().get() && !wonGiveaway.getSteamActivationDate().isPresent()) {
                    long timedifference = TimeHelper.getCurrentTimestamp().getTime() - wonGiveaway.getReceivedDate().get().getTime();

                    //3600000 = 1000*60*60
                    long hours = timedifference / 3600000;

                    //WARNING 
                    if (hours == CentralSettings.Penalties.STEAM_ACKNOWLEDGE_REMINDER_WARNING) {
                        notActivatedWonGiveawaysWarning += wonGiveaway.getTitle() + " needs to be activated!\n";
                    }

                    //BAN 
                    if (hours >= CentralSettings.Penalties.STEAM_ACKNOWLEDGE_REMINDER_BAN && hours < CentralSettings.Penalties.MAX_REMINDER_DAYS) {
                        notActivatedWonGiveawaysBan += wonGiveaway.getTitle() + " needs to be activated!\n";
                    }

                }
            }
        }

        //Warning after 3 days
        if (!notActivatedWonGiveawaysWarning.isEmpty()) {
            new MailClient(user).sendNotActivatedWarningEmail(notActivatedWonGiveawaysWarning);
        }

        //Ban after 5 days   
        if (!notActivatedWonGiveawaysBan.isEmpty()) {
            user.setIsActive(false);
            new MailClient(user).sendNotActivatedBanEmail(notActivatedWonGiveawaysBan);
        }
    }

    private Integer getAppIDFromGiveaway(WonGiveawayTO wonGiveaway) {
        if (wonGiveaway.getSteamLink().isPresent() && wonGiveaway.getSteamLink().get().contains("app")) {
            try {
                String appId = wonGiveaway.getSteamLink().get();
                appId = appId.substring(0, appId.length() - 1);
                return Integer.parseInt(appId.substring(appId.lastIndexOf("/") + 1, appId.length()));
            } catch (Exception e) {
                new ErrorLogHandler(user).error(e, wonGiveaway.getSteamLink().get(), logger);
            }
        }
        return null;
    }

    private List<Integer> getSteamAppIDs() {
        List<Integer> ownedAppIds = new LinkedList<Integer>();
        String url = CentralSettings.SteamActivation.PROFILE_URL + user.getSteamId() + CentralSettings.SteamActivation.PROFILE_APPENDIX;
        HttpResponse response = new HTTPConnection(user.getUserAgent()).doGet(url, user.getPhpsessionid());
        if (response != null) {
            try {
                HttpEntity responseEntity = response.getEntity();
                Elements gameElements = Jsoup.parse(EntityUtils.toString(responseEntity)).select("game");
                for (Element gameElement : gameElements) {
                    ownedAppIds.add(Integer.parseInt(gameElement.select("appID").text()));
                }
            } catch (Exception e) {
                new ErrorLogHandler(user).error(e, url, logger);
            }
        }
        return ownedAppIds;
    }
}
