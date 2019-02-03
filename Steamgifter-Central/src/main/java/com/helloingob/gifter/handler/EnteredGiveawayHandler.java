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
import com.helloingob.gifter.data.connection.HTTPConnection;
import com.helloingob.gifter.data.to.AdvUserTO;
import com.helloingob.gifter.data.to.EnteredGiveaway;
import com.helloingob.gifter.parser.helper.GiveawayParserHelper;
import com.helloingob.gifter.utilities.CentralSettings;
import com.helloingob.gifter.utilities.SharedSettings;

public class EnteredGiveawayHandler {
    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);
    private List<EnteredGiveaway> enteredGiveaways = new LinkedList<EnteredGiveaway>();
    private AdvUserTO user;

    public EnteredGiveawayHandler(AdvUserTO user) {
        this.user = user;
    }

    public List<EnteredGiveaway> getEnteredGiveaways() {
        int lastPage = getLastEnteredGiveawayPage();
        for (int i = 2; i <= lastPage; i++) {
            HttpResponse response = new HTTPConnection(user.getUserAgent()).doGet(CentralSettings.Url.ENTEREDGIVEAWAY_URL + i, user.getPhpsessionid());
            if (response != null) {
                try {
                    HttpEntity responseEntity = response.getEntity();
                    if (!parseEnteredGiveawaysFromPageSource(EntityUtils.toString(responseEntity))) {
                        break;
                    }
                } catch (Exception e) {
                    new ErrorLogHandler(user).error(e, logger);
                }
            }
        }
        return enteredGiveaways;
    }

    private Boolean parseEnteredGiveawaysFromPageSource(String pageSource) {
        Elements enteredGiveawayPosts = Jsoup.parse(pageSource, CentralSettings.Url.STEAMGIFTS_BASE_URL).select(".table__row-outer-wrap");
        for (Element enteredGiveawayPost : enteredGiveawayPosts) {
            if (enteredGiveawayPost.select(".is-clickable").isEmpty()) {
                return false;
            } else {
                try {
                    EnteredGiveaway enteredGiveaway = new EnteredGiveaway();

                    //Title
                    String title = enteredGiveawayPost.select(".table__column__heading").text();
                    enteredGiveaway.setTitle(title);

                    //Copies from title
                    String copies = GiveawayParserHelper.getCopyFromTitle(title);
                    if (copies != null) {
                        enteredGiveaway.setCopies(GiveawayParserHelper.getCopiesFromString(copies));
                    } else {
                        enteredGiveaway.setCopies(1);
                    }

                    String entries = enteredGiveawayPost.select(".table__column--width-small").first().text();
                    enteredGiveaway.setEntries(GiveawayParserHelper.getEntryFromString(entries));

                    Element imageLinkElement = enteredGiveawayPost.select(".global__image-inner-wrap").first();
                    String imageLink = "";
                    if (imageLinkElement != null) {
                        imageLink = GiveawayParserHelper.getImageFromStyle(imageLinkElement.attr("style"));
                    }
                    enteredGiveaway.setImageLink(imageLink);

                    //Giveaway Link
                    String giveawayLink = enteredGiveawayPost.select(".global__image-outer-wrap--game-small").attr("abs:href");
                    enteredGiveaway.setGiveawayLink(giveawayLink);

                    //Code
                    enteredGiveaway.setCode(GiveawayParserHelper.getEnterCode(enteredGiveaway.getGiveawayLink()));

                    //Winchance
                    enteredGiveaway.setWinChance(GiveawayParserHelper.getWinChance(enteredGiveaway.getCopies(), enteredGiveaway.getEntries()));

                    enteredGiveaways.add(enteredGiveaway);
                } catch (Exception e) {
                    if (enteredGiveawayPost.text().isEmpty()) {
                        new ErrorLogHandler(user).error(e, pageSource, logger);
                    } else {
                        new ErrorLogHandler(user).error(e, enteredGiveawayPost.text(), logger);
                    }
                }
            }
        }
        return true;
    }

    private int getLastEnteredGiveawayPage() {
        HttpResponse response = new HTTPConnection(user.getUserAgent()).doGet(CentralSettings.Url.ENTEREDGIVEAWAY_URL + "1", user.getPhpsessionid());
        if (response != null) {
            try {
                HttpEntity responseEntity = response.getEntity();
                String responseString = EntityUtils.toString(responseEntity);
                parseEnteredGiveawaysFromPageSource(responseString);
                Element lastPageElement = Jsoup.parse(responseString).select(".pagination__navigation a").last();
                //has ever entered a giveaway? otherwise page = 1
                if (lastPageElement != null) {
                    String lastPage = lastPageElement.attr("href");
                    lastPage = lastPage.substring(lastPage.indexOf("=") + 1);
                    return Integer.parseInt(lastPage);
                }
            } catch (Exception e) {
                new ErrorLogHandler(user).error(e, logger);
            }
        }
        return 1;
    }

}
