package com.helloingob.gifter.handler;

import java.util.LinkedList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.helloingob.gifter.ErrorLogHandler;
import com.helloingob.gifter.dao.WonGiveawayDAO;
import com.helloingob.gifter.data.connection.HTTPConnection;
import com.helloingob.gifter.data.to.AdvUserTO;
import com.helloingob.gifter.data.to.SimpleWonGiveaway;
import com.helloingob.gifter.data.to.ViewKeyResponse;
import com.helloingob.gifter.parser.GiveawayDetailParser;
import com.helloingob.gifter.to.WonGiveawayTO;
import com.helloingob.gifter.utilities.CentralSettings;
import com.helloingob.gifter.utilities.SharedSettings;
import com.helloingob.gifter.utilities.TimeHelper;
import com.helloingob.gifter.utilities.mail.MailClient;

public class WonGiveawayHandler {
    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);
    private List<SimpleWonGiveaway> simpleWonGiveaways = new LinkedList<SimpleWonGiveaway>();
    private AdvUserTO user;
    private MailClient mailClient;
    private WonGiveawayDAO wonGiveawayDAO;

    public WonGiveawayHandler(AdvUserTO user) {
        this.user = user;
    }

    public WonGiveawayHandler(AdvUserTO user, WonGiveawayDAO wonGiveawayDAO) {
        this.user = user;
        this.mailClient = new MailClient(user);
        this.wonGiveawayDAO = wonGiveawayDAO;
    }

    public List<SimpleWonGiveaway> getSimpleWonGiveaways() {
        Integer lastPage = getLastWonGiveawayPage();
        if (lastPage == null) {
            return null;
        }

        for (int i = 2; i <= lastPage; i++) {
            HttpResponse response = new HTTPConnection(user.getUserAgent()).doGet(CentralSettings.Url.WONGIVEAWAY_URL + i, user.getPhpsessionid());
            if (response != null) {
                try {
                    HttpEntity responseEntity = response.getEntity();
                    String responseString = EntityUtils.toString(responseEntity);
                    //if EVEN ONE page is empty skip complete check! (removeOldDatabaseEntries)
                    if (!responseString.isEmpty()) {
                        parseWonGiveawaysFromPageSource(responseString);
                    } else {
                        return null;
                    }
                } catch (Exception e) {
                    new ErrorLogHandler(user).error(e, logger);
                    return null;
                }
            } else {
                //if timeout
                return null;
            }
        }
        return simpleWonGiveaways;
    }

    public WonGiveawayTO getGiveawayDetailsFromLink(SimpleWonGiveaway simpleWonGiveaway) {
        HttpResponse response = new HTTPConnection(user.getUserAgent()).doGet(simpleWonGiveaway.getGiveawayLink(), user.getPhpsessionid());
        if (response != null) {
            try {
                HttpEntity responseEntity = response.getEntity();
                WonGiveawayTO wonGiveaway = GiveawayDetailParser.getGiveawayFromPageSource(EntityUtils.toString(responseEntity), simpleWonGiveaway.getGiveawayLink(), user);
                if (wonGiveaway != null) {
                    wonGiveaway.setSteamKey(simpleWonGiveaway.getSteamKey().orElse(null));
                    return wonGiveaway;
                }
            } catch (Exception e) {
                new ErrorLogHandler(user).error(e, simpleWonGiveaway.getGiveawayLink(), logger);
            }
        }
        return null;
    }

    private void parseWonGiveawaysFromPageSource(String pageSource) {
        Element wonGiveawayExceptionOutputKeeper = null;
        try {
            Elements wonGiveawayPosts = Jsoup.parse(pageSource, CentralSettings.Url.STEAMGIFTS_BASE_URL).select(".table__row-outer-wrap");
            for (Element wonGiveawayPost : wonGiveawayPosts) {

                //for advanced error information
                wonGiveawayExceptionOutputKeeper = wonGiveawayPost;

                SimpleWonGiveaway simpleWonGiveaway = new SimpleWonGiveaway();
                //Giveaway Link
                simpleWonGiveaway.setGiveawayLink(wonGiveawayPost.select(".table__column__heading").attr("abs:href"));

                //New?
                simpleWonGiveaway.setHasReceived(null);
                //if feedback is locked
                if (wonGiveawayPost.select(".trigger-popup").attr("data-popup").equals("popup--feedback-locked")) {
                    for (Element element : wonGiveawayPost.select(".trigger-popup")) {
                        if (!element.getElementsByClass("icon-green").isEmpty() && element.text().equals("Received")) {
                            simpleWonGiveaway.setHasReceived(true);
                            break;
                        } else {
                            simpleWonGiveaway.setHasReceived(false);
                        }
                    }
                    //is NOT locked!
                } else {
                    if (!wonGiveawayPost.select(".table__gift-feedback-received").not(".is-hidden").isEmpty()) {
                        simpleWonGiveaway.setHasReceived(true);
                    } else if (!wonGiveawayPost.select(".table__gift-feedback-not-received").not(".is-hidden").isEmpty()) {
                        simpleWonGiveaway.setHasReceived(false);
                    }
                }

                //Key
                String steamKey = null;
                if (wonGiveawayPost.select(".table__column--width-medium").first().text().equals("-")) {
                    //No key found or given.
                } else {
                    if (wonGiveawayPost.select(".view_key_btn").first() == null) {
                        //Key & Link
                        steamKey = wonGiveawayPost.select(".table__column--width-medium").select("i").first().attr("data-clipboard-text");
                    } else {
                        //Click key Button
                        String viewKeyDataForm = wonGiveawayPost.select(".view_key_btn").attr("data-form");
                        ViewKeyResponse viewKeyResponse = retrieveKey(viewKeyDataForm.substring(22, viewKeyDataForm.indexOf("&xsrf_token=")));
                        if (viewKeyResponse.getSuccess() == 1) {
                            steamKey = viewKeyResponse.getKey();
                        } else {
                            new ErrorLogHandler(user).error(viewKeyResponse.toString(), logger);
                        }
                    }
                }
                simpleWonGiveaway.setSteamKey(steamKey);
                simpleWonGiveaways.add(simpleWonGiveaway);
            }
        } catch (Exception e) {
            if (wonGiveawayExceptionOutputKeeper == null) {
                new ErrorLogHandler(user).error(e, pageSource, logger);
            } else {
                new ErrorLogHandler(user).error(e, wonGiveawayExceptionOutputKeeper.toString(), logger);
            }
        }
    }

    //gets last won page AND parses same page
    private Integer getLastWonGiveawayPage() {
        HttpResponse response = new HTTPConnection(user.getUserAgent()).doGet(CentralSettings.Url.WONGIVEAWAY_URL + "1", user.getPhpsessionid());
        if (response != null) {
            try {
                HttpEntity responseEntity = response.getEntity();
                String responseString = EntityUtils.toString(responseEntity);

                //if page is empty/null skip complete check! (removeOldDatabaseEntries)
                if (!responseString.isEmpty()) {
                    parseWonGiveawaysFromPageSource(responseString);
                } else {
                    return null;
                }

                Element lastPageElement = Jsoup.parse(responseString).select(".pagination__navigation a").last();
                //has ever won a giveaway? otherwise page = 1
                if (lastPageElement != null) {
                    String lastPage = lastPageElement.attr("href");
                    lastPage = lastPage.substring(lastPage.indexOf("=") + 1);
                    return Integer.parseInt(lastPage);
                } else {
                    //no navigation found, is only one page
                    return 1;
                }
            } catch (Exception e) {
                new ErrorLogHandler(user).error(e, logger);
            }
        }
        return null;
    }

    public void updateFieldsFromWonGiveaway(List<WonGiveawayTO> alreadyWonGiveaways, List<SimpleWonGiveaway> simpleWonGiveaways) {
        for (WonGiveawayTO alreadyWonGiveaway : alreadyWonGiveaways) {
            for (SimpleWonGiveaway simpleWonGiveaway : simpleWonGiveaways) {
                if (alreadyWonGiveaway.getGiveawayLink().equals(simpleWonGiveaway.getGiveawayLink())) {

                    if (receivedChanged(alreadyWonGiveaway, simpleWonGiveaway)) {
                        alreadyWonGiveaway.setHasReceived(simpleWonGiveaway.getHasReceived().orElse(null));
                        if (alreadyWonGiveaway.getHasReceived().isPresent()) {
                            alreadyWonGiveaway.setReceivedDate(TimeHelper.getCurrentTimestamp());
                        } else {
                            alreadyWonGiveaway.setReceivedDate(null);
                        }
                        wonGiveawayDAO.update(alreadyWonGiveaway);
                    }

                    if (keyChanged(alreadyWonGiveaway, simpleWonGiveaway)) {
                        alreadyWonGiveaway.setSteamKey(simpleWonGiveaway.getSteamKey().get());
                        wonGiveawayDAO.update(alreadyWonGiveaway);
                        mailClient.sendMailToWinner("You won " + alreadyWonGiveaway.getTitle(), alreadyWonGiveaway);
                    }

                }
            }
        }
    }

    public void handleNewWonGiveaways(List<WonGiveawayTO> alreadyWonGiveaways, List<SimpleWonGiveaway> simpleWonGiveaways) {
        for (SimpleWonGiveaway simpleWonGiveaway : simpleWonGiveaways) {
            WonGiveawayTO wonGiveaway = getGiveawayDetailsFromLink(simpleWonGiveaway);
            if (wonGiveaway != null) {
                wonGiveaway.setUserPk(user.getPk());
                wonGiveaway.setHasReceived(simpleWonGiveaway.getHasReceived().orElse(null));

                if (!simpleWonGiveaway.getHasReceived().isPresent()) {
                    if (simpleWonGiveaway.getSteamKey().isPresent()) {
                        mailClient.sendMailToWinner("You won " + wonGiveaway.getTitle(), wonGiveaway);
                    }
                } else {
                    wonGiveaway.setReceivedDate(TimeHelper.getCurrentTimestamp());
                }
                alreadyWonGiveaways.add(wonGiveaway);
                wonGiveawayDAO.save(wonGiveaway);
            }
        }
    }

    public void removeAlreadySavedWonGiveaways(List<WonGiveawayTO> alreadyWonGiveaways, List<SimpleWonGiveaway> simpleWonGiveaways) {
        for (WonGiveawayTO wonGiveaway : alreadyWonGiveaways) {
            simpleWonGiveaways.removeIf(wg -> wg.getGiveawayLink().equals(wonGiveaway.getGiveawayLink()));
        }
    }

    public Boolean keyChanged(WonGiveawayTO alreadyWonGiveaway, SimpleWonGiveaway simpleWonGiveaway) {
        if (simpleWonGiveaway.getSteamKey().isPresent()) {
            if (!simpleWonGiveaway.getSteamKey().get().equals(alreadyWonGiveaway.getSteamKey().orElse(null))) {
                return true;
            }
        }
        return false;
    }

    public Boolean receivedChanged(WonGiveawayTO alreadyWonGiveaway, SimpleWonGiveaway simpleWonGiveaway) {
        if (alreadyWonGiveaway.getHasReceived().isPresent()) {
            return !alreadyWonGiveaway.getHasReceived().get().equals(simpleWonGiveaway.getHasReceived().orElse(null));
        }
        if (simpleWonGiveaway.getHasReceived().isPresent()) {
            return !simpleWonGiveaway.getHasReceived().get().equals(alreadyWonGiveaway.getHasReceived().orElse(null));
        }
        return false;
    }

    public void checkForNotAcknowledgedWonGiveaways(List<WonGiveawayTO> alreadyWonGiveaways) {
        String notKnowledgedWonGiveawaysBan = "";
        String notKnowledgedWonGiveawaysWarning = "";

        for (WonGiveawayTO wonGiveaway : alreadyWonGiveaways) {
            if (!wonGiveaway.getHasReceived().isPresent()) {
                long timedifference = TimeHelper.getCurrentTimestamp().getTime() - wonGiveaway.getEndDate().getTime();

                //3600000 = 1000*60*60
                long hours = timedifference / 3600000;

                //WARNING
                if (hours == CentralSettings.Penalties.STEAMGIFTS_ACKNOWLEDGE_REMINDER_WARNING) {
                    notKnowledgedWonGiveawaysWarning += wonGiveaway.getTitle() + " (" + wonGiveaway.getGiveawayLink() + ") needs to be acknowledged!\n";
                }

                //BAN                
                if (hours >= CentralSettings.Penalties.STEAMGIFTS_ACKNOWLEDGE_REMINDER_BAN && hours < CentralSettings.Penalties.MAX_REMINDER_DAYS) {
                    notKnowledgedWonGiveawaysBan += wonGiveaway.getTitle() + " (" + wonGiveaway.getGiveawayLink() + ") needs to be acknowledged!\n";
                }

            }
        }
        //Warning after 7 days
        if (!notKnowledgedWonGiveawaysWarning.isEmpty()) {
            mailClient.sendNotAcknowledgedReminderEmail(notKnowledgedWonGiveawaysWarning);
        }
        //Ban after 10 days        
        if (!notKnowledgedWonGiveawaysBan.isEmpty()) {
            user.setIsActive(false);
            mailClient.sendNotAcknowledgedBanEmail(notKnowledgedWonGiveawaysBan);
        }
    }

    public List<WonGiveawayTO> removeOldDatabaseEntries(List<WonGiveawayTO> alreadyWonGiveaways, List<SimpleWonGiveaway> simpleWonGiveaways) {
        List<WonGiveawayTO> swapList = new LinkedList<WonGiveawayTO>(alreadyWonGiveaways);
        for (SimpleWonGiveaway simpleWonGiveaway : simpleWonGiveaways) {
            swapList.removeIf(db -> db.getGiveawayLink().equals(simpleWonGiveaway.getGiveawayLink()));
        }

        for (WonGiveawayTO wonGiveawayTO : swapList) {
            logger.info("[" + user.getLoginName() + "] " + wonGiveawayTO.getTitle() + " (" + wonGiveawayTO.getGiveawayLink() + ") no longer in won giveaways [received=" + wonGiveawayTO.getHasReceived() + ";activation=" + wonGiveawayTO.getSteamActivationDate() + "]", wonGiveawayTO.getSteamKey().isPresent() ? wonGiveawayTO.getSteamKey().get() : null, logger);
            wonGiveawayDAO.delete(wonGiveawayTO);
        }

        alreadyWonGiveaways.removeAll(swapList);
        return alreadyWonGiveaways;
    }

    private ViewKeyResponse retrieveKey(String winner_id) {
        List<NameValuePair> nameValuePairs = new LinkedList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("xsrf_token", user.getXrefToken()));
        nameValuePairs.add(new BasicNameValuePair("do", "view_key"));
        nameValuePairs.add(new BasicNameValuePair("winner_id", winner_id));

        String responseString = "";
        HttpResponse response = new HTTPConnection(user.getUserAgent()).doPost(CentralSettings.Url.COMMAND_URL, nameValuePairs, user.getPhpsessionid());
        if (response != null) {
            try {
                HttpEntity responseEntity = response.getEntity();
                responseString = EntityUtils.toString(responseEntity);
                return new Gson().fromJson(responseString, ViewKeyResponse.class);
            } catch (Exception e) {
                new ErrorLogHandler(user).error(e, responseString, logger);
            }
        }
        return null;
    }
}
