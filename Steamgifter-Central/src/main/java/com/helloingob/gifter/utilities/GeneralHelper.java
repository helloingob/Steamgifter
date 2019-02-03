package com.helloingob.gifter.utilities;

import java.util.Random;

import com.helloingob.gifter.data.connection.HTTPConnection;
import com.helloingob.gifter.utilities.useragent.UserAgentSpoofer;

public class GeneralHelper {

    public static int generateRandomSleepTime(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    public static String getSteamStoreLink(String encodedUrl) {
        return new HTTPConnection(new UserAgentSpoofer().generateUserAgent()).getRedirectLocation(encodedUrl);
    }

}
