package com.helloingob.gifter.parser;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.helloingob.gifter.ErrorLogHandler;
import com.helloingob.gifter.data.connection.HTTPConnection;
import com.helloingob.gifter.data.to.AdvUserTO;
import com.helloingob.gifter.utilities.CentralSettings;
import com.helloingob.gifter.utilities.SharedSettings;

public class UserInformationParser {
    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);
    private final static String LOGOUT_INDICATOR = "do=logout&xsrf_token=";

    public static boolean shouldSyncBeExecuted(String pageSource) {
        String syncDate = Jsoup.parse(pageSource).select(".nav__absolute-dropdown").select(".nav__row__summary__description span").first().attr("data-timestamp");
        //epoch to millis
        syncDate = syncDate + "000";
        Long lastSyncedTimestamp = Long.parseLong(syncDate);
        Long currentTimestamp = Instant.now().toEpochMilli();
        //Randomize sync
        long randomHours = ThreadLocalRandom.current().nextInt(CentralSettings.Security.MIN_RANDOM_SYNC_RANGE, CentralSettings.Security.MAX_RANDOM_SYNC_RANGE + 1) * (1000 * 60 * 60); //1 hour;
        if (currentTimestamp - lastSyncedTimestamp > randomHours) {
            return true;
        }
        return false;
    }

    public static boolean isAuthenticated(String pageSource) {
        Element logoutElement = Jsoup.parse(pageSource).select(".nav__row").last();
        if (logoutElement != null && logoutElement.attributes().size() > 0) {
            String dataFormAttribute = logoutElement.attributes().get("data-form");
            if (dataFormAttribute != null && dataFormAttribute.length() > LOGOUT_INDICATOR.length()) {
                if (dataFormAttribute.substring(0, LOGOUT_INDICATOR.length()).equals(LOGOUT_INDICATOR)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String parseXrefToken(String pageSource) {
        Element logoutElement = Jsoup.parse(pageSource).select(".nav__row").last();
        String dataFormAttribute = logoutElement.attributes().get("data-form");
        return dataFormAttribute.substring(LOGOUT_INDICATOR.length(), dataFormAttribute.length());
    }

    public static Integer parsePoints(String pageSource) {
        String points = Jsoup.parse(pageSource).select(".nav__points").text();
        return Integer.parseInt(points);
    }

    public static Double parseLevel(String pageSource) {
        String level = Jsoup.parse(pageSource).select(".nav__button span").attr("title");
        return Double.parseDouble(level);
    }

    public static String parseProfileName(String pageSource) {
        return Jsoup.parse(pageSource).select(".nav__avatar-outer-wrap").attr("href").replace("/user/", "");
    }

    public static String parseProfileImage(String pageSource) {
        String style = Jsoup.parse(pageSource).select(".nav__avatar-inner-wrap").attr("style");
        return style.replace("background-image:url(", "").replace(");", "").replace("medium", "full");
    }

    public static Long getSteamId(AdvUserTO user) {
        if (user.getSteamId() == null) {
            HttpResponse response = new HTTPConnection(user.getUserAgent()).doGet(CentralSettings.Url.USER_URL + user.getProfileName(), user.getPhpsessionid());
            if (response != null) {
                try {
                    HttpEntity responseEntity = response.getEntity();
                    Element steamProfileElemnt = Jsoup.parse(EntityUtils.toString(responseEntity)).select(".sidebar__shortcut-inner-wrap a").first();
                    String steamIDString = steamProfileElemnt.attr("href");
                    return Long.parseLong(steamIDString.substring(steamIDString.lastIndexOf("/") + 1, steamIDString.length()));
                } catch (Exception e) {
                    new ErrorLogHandler(user).error(e, logger);
                }
            }
        }
        return user.getSteamId();
    }
}
