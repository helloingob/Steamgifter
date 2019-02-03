package com.helloingob.gifter.parser;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.helloingob.gifter.ErrorLogHandler;
import com.helloingob.gifter.data.to.Giveaway;
import com.helloingob.gifter.parser.helper.GiveawayParserHelper;
import com.helloingob.gifter.to.UserAssetTO;
import com.helloingob.gifter.to.UserTO;
import com.helloingob.gifter.utilities.CentralSettings;
import com.helloingob.gifter.utilities.SharedSettings;

public class GiveawayListParser {
    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);

    public static List<Giveaway> getGiveawaysFromPageSource(String pageSource, UserTO user, UserAssetTO userAsset) {
        List<Giveaway> giveaways = new LinkedList<Giveaway>();
        Element giveawayExceptionOutputKeeper = null;
        try {
            //delete featured giveaways!
            Document document = Jsoup.parse(pageSource, CentralSettings.Url.STEAMGIFTS_BASE_URL);
            Elements featuredGiveawayPosts = document.select(".pinned-giveaways__outer-wrap");
            for (Element featuredGiveawayPost : featuredGiveawayPosts) {
                featuredGiveawayPost.remove();
            }
            //normal giveaways
            Elements giveawayPosts = document.select(".giveaway__row-outer-wrap");
            for (Element giveawayPost : giveawayPosts) {

                //for advanced error information
                giveawayExceptionOutputKeeper = giveawayPost;

                //Remove already participated
                if (giveawayPost.select(".is-faded").isEmpty()) {
                    Giveaway giveaway = new Giveaway();

                    Element titleInformation = giveawayPost.select(".giveaway__heading__name").first();

                    //Titel
                    giveaway.setTitle(titleInformation.text());

                    //Level
                    int requirementLevel = GiveawayParserHelper.getLevelFromString(giveawayPost.select(".giveaway__column--contributor-level").text());
                    giveaway.setLevelRequirement(requirementLevel);

                    //Giveaway Link
                    giveaway.setGiveawayLink(titleInformation.attr("abs:href").toString());
                    giveaway.setCode(GiveawayParserHelper.getEnterCode(giveaway.getGiveawayLink()));

                    Elements additionalTitleInformations = giveawayPost.select(".giveaway__heading__thin");
                    Integer giveawayPoints = -1;
                    Integer giveawayCopies = 1;
                    for (Element additionalTitleInformation : additionalTitleInformations) {
                        if (additionalTitleInformation.text().contains("P)")) {
                            //Points
                            giveawayPoints = GiveawayParserHelper.getPointsFromString(additionalTitleInformation.text());
                        }
                        if (additionalTitleInformation.text().contains("Copies)")) {
                            //Copies
                            giveawayCopies = GiveawayParserHelper.getCopiesFromString(additionalTitleInformation.text());
                        }
                    }
                    giveaway.setPoints(giveawayPoints);
                    giveaway.setCopies(giveawayCopies);

                    //Steamstore
                    String steamLink = giveawayPost.select(".giveaway__icon").attr("href");
                    if (!steamLink.isEmpty()) {
                        giveaway.setSteamLink(steamLink);
                    }
                    //APP/SUB ID
                    GiveawayParserHelper.fillID(giveaway);

                    //Date
                    String giveawayDate = giveawayPost.select(".giveaway__columns").first().select("span").first().attr("data-timestamp");
                    //http://stackoverflow.com/questions/3371326/java-date-from-unix-timestamp
                    giveaway.setEndDate(new Timestamp(Long.parseLong(giveawayDate) * 1000));

                    //Image
                    String imageLinkString = null;
                    if (giveawayPost.select(".giveaway_image_thumbnail_missing").isEmpty()) {
                        imageLinkString = GiveawayParserHelper.getImageFromStyle(giveawayPost.select(".giveaway_image_thumbnail").attr("style")); 
                    } 
                    giveaway.setImageLink(imageLinkString);

                    //Author
                    giveaway.setAuthor(giveawayPost.select(".giveaway__username").first().text());

                    Element giveawayLinks = giveawayPost.select(".giveaway__links").first();

                    //Entries
                    String giveawayEntries = giveawayLinks.select("a[href$=\"entries\"] span").text();
                    giveaway.setEntries(GiveawayParserHelper.getEntryFromString(giveawayEntries));

                    //WinChance
                    giveaway.setWinChance(GiveawayParserHelper.getWinChance(giveaway.getCopies(), giveaway.getEntries()));

                    //Filter (level, sub?) 
                    if (requirementLevel <= userAsset.getLevel()) {
                        if (user.getSkipSub()) {
                            //should have valid link AND is sub
                            if (giveaway.getGiveawayLink() != null && giveaway.getSubId() == null) {
                                giveaways.add(giveaway);
                            }
                        } else {
                            giveaways.add(giveaway);
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (giveawayExceptionOutputKeeper == null) {
                new ErrorLogHandler().error(e, pageSource, logger);
            } else {
                new ErrorLogHandler().error(e, giveawayExceptionOutputKeeper.toString(), logger);
            }
        }
        return giveaways;
    }
}
