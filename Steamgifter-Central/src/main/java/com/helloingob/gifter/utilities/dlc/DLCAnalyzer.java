package com.helloingob.gifter.utilities.dlc;

import java.util.LinkedList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.helloingob.gifter.ErrorLogHandler;
import com.helloingob.gifter.dao.CachedGameDAO;
import com.helloingob.gifter.data.connection.HTTPConnection;
import com.helloingob.gifter.data.to.Giveaway;
import com.helloingob.gifter.utilities.CentralSettings;
import com.helloingob.gifter.utilities.SharedSettings;
import com.helloingob.gifter.utilities.useragent.UserAgentSpoofer;

public class DLCAnalyzer {
    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);
    private String userAgent;

    public DLCAnalyzer() {
        userAgent = new UserAgentSpoofer().generateUserAgent();
    }

    private List<Integer> getAppsFromSub(Giveaway giveaway) {
        List<Integer> apps = new LinkedList<Integer>();
        String jsonString = "";

        HttpResponse response = new HTTPConnection(userAgent).doGet(CentralSettings.DLC.STEAMAPI_SUBID + giveaway.getSubId());
        if (response != null) {
            try {
                HttpEntity responseEntity = response.getEntity();
                jsonString = EntityUtils.toString(responseEntity);

                if (!jsonString.isEmpty()) {
                    //prepare json string (cut rnd number)
                    jsonString = jsonString.substring(jsonString.indexOf(":") + 1, jsonString.length() - 1);

                    JSONObject outerJSON = new JSONObject(jsonString);
                    Boolean successfull = (Boolean) outerJSON.get("success");

                    //check for successfull entry
                    if (successfull) {
                        JSONObject innerJSON = (JSONObject) outerJSON.get("data");
                        if (!innerJSON.isNull("apps")) {
                            JSONArray steamAppArray = new JSONArray(innerJSON.get("apps").toString());
                            for (int i = 0; i < steamAppArray.length(); i++) {
                                JSONObject steamApp = steamAppArray.getJSONObject(i);
                                apps.add(Integer.parseInt(steamApp.get("id").toString()));
                            }
                        }
                    }
                }

            } catch (Exception e) {
                new ErrorLogHandler().error(e, CentralSettings.DLC.STEAMAPI_SUBID + giveaway.getSubId() + " has currently " + apps + ", '" + jsonString + "'", logger);
            }
        }
        return apps;
    }

    public boolean isDLC(Giveaway giveaway) {
        if (giveaway.getAppId() != null) {
            //if it is NO game!
            return !isGame(giveaway.getAppId());
        } else {
            if (giveaway.getSubId() != null) {
                for (Integer appId : getAppsFromSub(giveaway)) {
                    if (isGame(appId)) {
                        //as soon as a game is found its no DLC
                        return false;
                    }
                }
            }
        }
        return CentralSettings.DLC.ASSUME_EVERY_GAME_IS_DLC;
    }

    private boolean isGame(Integer appId) {
        final String STEAM_GAME_IDENTIFIER = "\"type\":\"game\"";
        String jsonString = "";

        CachedGameDAO cachedGameDAO = new CachedGameDAO();
        Boolean isDLC = cachedGameDAO.get(appId);

        //no cached appId found, try to get one
        if (isDLC == null) {
            HttpResponse response = new HTTPConnection(userAgent).doGet(CentralSettings.DLC.STEAMAPI_APPID + appId);
            if (response != null) {
                try {
                    HttpEntity responseEntity = response.getEntity();
                    jsonString = EntityUtils.toString(responseEntity);

                    //sometimes it returns a emtpy response
                    if (!jsonString.isEmpty()) {
                        if (jsonString.contains(STEAM_GAME_IDENTIFIER)) {
                            isDLC = false;
                        } else {
                            isDLC = true;
                        }
                        cachedGameDAO.save(appId, isDLC);
                        return !isDLC;
                    }

                } catch (Exception e) {
                    new ErrorLogHandler().error(e, appId + "", logger);
                }
            }
            //cached game found in database
        } else {
            return !isDLC;
        }
        return !CentralSettings.DLC.ASSUME_EVERY_GAME_IS_DLC;
    }
}
