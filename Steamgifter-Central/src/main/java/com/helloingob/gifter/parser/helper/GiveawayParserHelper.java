package com.helloingob.gifter.parser.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.helloingob.gifter.ErrorLogHandler;
import com.helloingob.gifter.data.to.Giveaway;
import com.helloingob.gifter.utilities.CentralSettings;
import com.helloingob.gifter.utilities.SharedSettings;

public class GiveawayParserHelper {
    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);

    public static String getEnterCode(String giveawayLink) {
        giveawayLink = giveawayLink.replace(CentralSettings.Url.STEAMGIFTS_BASE_URL + "/giveaway/", "");
        return giveawayLink.substring(0, giveawayLink.indexOf("/"));
    }

    public static String getImageFromStyle(String input) {
        return input.replace("background-image:url(", "").replace(");", "");
    }

    public static int getLevelFromString(String input) {
        if (input.isEmpty()) {
            return 0;
        }
        try {
            input = input.replace("Level ", "").replace("+", "");
            return Integer.parseInt(input);
        } catch (Exception e) {
            new ErrorLogHandler().error(e, input, logger);
        }
        return 0;
    }

    public static Double getWinChance(Integer copy, Integer entry) {
        try {
            if (entry > 0) {
                Double winChance = ((double) copy / entry) * 100;
                if (winChance > 100) {
                    return 100.0;
                }
                return winChance;
            } else {
                return 100.0;
            }
        } catch (Exception e) {
            new ErrorLogHandler().error(e, "copy=" + copy + "; entry=" + entry, logger);
        }
        return 0.0;
    }

    public static Integer getEntryFromString(String input) {
        try {
            input = input.replace(" ", "").replace(",", "").replace("entries", "").replace("entry", "");
            return Integer.parseInt(input);
        } catch (Exception e) {
            new ErrorLogHandler().error(e, input, logger);
        }
        return null;
    }

    public static String getCopyFromTitle(String input) {
        if (input.contains("(") && input.contains("Copies)")) {
            return input.substring(input.lastIndexOf("(") + 1, input.lastIndexOf(")"));
        }
        return null;
    }

    public static Double getPriceFromString(String input) {
        if (input.isEmpty()) {
            return null;
        }

        try {
            String[] price = input.replaceAll(",", "").split(" ");
            return Double.parseDouble(price[price.length - 2]);
        } catch (Exception e) {
            new ErrorLogHandler().error(e, input, logger);
        }
        return null;
    }

    public static void fillID(Giveaway giveaway) {
        String steamLink = giveaway.getSteamLink();
        if (steamLink == null) {
            return;
        }
        if (steamLink.contains("app")) {
            try {
                Integer appID = Integer.parseInt(steamLink.replace(CentralSettings.Url.STEAMSTORE_BASE_URL + "/app/", "").replace("/", ""));
                giveaway.setAppId(appID);
            } catch (Exception e) {
                new ErrorLogHandler().error(e, "(app) " + giveaway.getSteamLink(), logger);
            }
        }
        if (steamLink.contains("sub")) {
            try {
                Integer subID = Integer.parseInt(steamLink.replace(CentralSettings.Url.STEAMSTORE_BASE_URL + "/sub/", "").replace("/", ""));
                giveaway.setSubId(subID);
            } catch (Exception e) {
                new ErrorLogHandler().error(e, "(sub) " + giveaway.getSteamLink(), logger);
            }
        }
    }

    public static Integer getPointsFromString(String input) {
        try {
            return Integer.parseInt(input.replace("(", "").replace("P)", ""));
        } catch (Exception e) {
            new ErrorLogHandler().error(e, input, logger);
        }
        return 0;
    }

    public static Integer getCopiesFromString(String input) {
        if (input != null) {
            try {
                return Integer.parseInt(input.replace("(", "").replace(" Copies", "").replace(")", "").replace(",", ""));
            } catch (Exception e) {
                new ErrorLogHandler().error(e, input, logger);
            }
        }
        return 1;
    }

}
