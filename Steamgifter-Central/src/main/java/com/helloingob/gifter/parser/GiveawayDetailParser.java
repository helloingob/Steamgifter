package com.helloingob.gifter.parser;

import java.sql.Timestamp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.helloingob.gifter.ErrorLogHandler;
import com.helloingob.gifter.parser.helper.GiveawayParserHelper;
import com.helloingob.gifter.to.UserTO;
import com.helloingob.gifter.to.WonGiveawayTO;
import com.helloingob.gifter.utilities.CentralSettings;
import com.helloingob.gifter.utilities.SharedSettings;
import com.helloingob.gifter.utilities.TimeHelper;

public class GiveawayDetailParser {
    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);

    public static WonGiveawayTO getGiveawayFromPageSource(String pageSource, String giveawayLink, UserTO user) {
        WonGiveawayTO wonGiveaway = new WonGiveawayTO();
        Element container = null;
        try {
            container = Jsoup.parse(pageSource, CentralSettings.Url.STEAMGIFTS_BASE_URL);

            //Error handling
            if (container.select(".page__heading__breadcrumbs").text().equals("Error")) {
                wonGiveaway.setPoints(0);
                wonGiveaway.setCopies(0);
                wonGiveaway.setEntries(0);
                wonGiveaway.setWinChance(0.0);
                wonGiveaway.setAuthor("?");
                wonGiveaway.setLevelRequirement(0);
                wonGiveaway.setEndDate(TimeHelper.getCurrentTimestamp());
                wonGiveaway.setTitle(container.select(".table__column__secondary-link").text());
                wonGiveaway.setGiveawayLink(giveawayLink);
                //logs to error
                new ErrorLogHandler(user).error(container.select(".table__column--width-fill").last().text(), giveawayLink, logger);
                return wonGiveaway;
            }
            
            //Title
            String title = container.select("title").text();
            wonGiveaway.setTitle(title);
            
            container = container.select(".featured__container").first();

            //Link
            Element imageElement = container.select(".global__image-outer-wrap").first();
            wonGiveaway.setSteamLink(imageElement.attr("href"));

            //Image
            Element imageLinkElement = imageElement.select("img").first();
            String imageLinkString = null;
            if (imageLinkElement != null) {
                imageLinkString = imageLinkElement.attr("src");
            }
            wonGiveaway.setImageLink(imageLinkString);

            //Points+Copies
            Elements additionalFeaturedInformations = container.select(".featured__heading__small");
            Integer giveawayPoints = -1;
            Integer giveawayCopies = 1;
            for (Element additionalTitleInformation : additionalFeaturedInformations) {
                if (additionalTitleInformation.text().contains("P)")) {
                    //Points
                    giveawayPoints = GiveawayParserHelper.getPointsFromString(additionalTitleInformation.text());
                }
                if (additionalTitleInformation.text().contains("Copies)")) {
                    //Copies
                    giveawayCopies = GiveawayParserHelper.getCopiesFromString(additionalTitleInformation.text());
                }
            }
            wonGiveaway.setPoints(giveawayPoints);
            wonGiveaway.setCopies(giveawayCopies);

            //Enddate
            String date = container.select(".featured__column span").first().attr("data-timestamp");
            //http://stackoverflow.com/questions/3371326/java-date-from-unix-timestamp
            wonGiveaway.setEndDate(new Timestamp(Long.parseLong(date) * 1000));

            //Author
            String author = container.select(".featured__column a").last().text();
            wonGiveaway.setAuthor(author);

            //Level
            String levelRequirement = container.select(".featured__column--contributor-level--positive").text();
            wonGiveaway.setLevelRequirement(GiveawayParserHelper.getLevelFromString(levelRequirement));

        } catch (Exception e) {
            if (container == null) {
                new ErrorLogHandler(user).error(e, "'" + pageSource + "'", logger);
            } else {
                new ErrorLogHandler(user).error(e, container.toString(), logger);
            }
            return null;
        }

        try {
            container = Jsoup.parse(pageSource, CentralSettings.Url.STEAMGIFTS_BASE_URL).select(".sidebar").first();

            //GiveawayLink
            wonGiveaway.setGiveawayLink(giveawayLink);

            //Entries
            String entries = container.select(".live__entry-count").text();
            wonGiveaway.setEntries(GiveawayParserHelper.getEntryFromString(entries));

            //Price
            if (wonGiveaway.getPoints() != null) {
                wonGiveaway.setSteamStorePrice(wonGiveaway.getPoints().doubleValue());    
            }

            //WinChance
            wonGiveaway.setWinChance(GiveawayParserHelper.getWinChance(wonGiveaway.getCopies(), wonGiveaway.getEntries()));

            return wonGiveaway;
        } catch (Exception e) {
            if (container == null) {
                new ErrorLogHandler(user).error(e, "'" + pageSource + "'", logger);
            } else {
                new ErrorLogHandler(user).error(e, container.toString(), logger);
            }
        }
        return null;
    }
}
