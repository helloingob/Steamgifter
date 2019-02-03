package com.helloingob.gifter.utilities.useragent;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import com.helloingob.gifter.ErrorLogHandler;
import com.helloingob.gifter.utilities.CentralSettings;
import com.helloingob.gifter.utilities.SharedSettings;

public class UserAgentSpoofer {
    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);

    public String generateUserAgent() {
        final String FILE_ENCODING = "UTF-8";
        File file = new File(CentralSettings.Files.USERAGENTS);

        if (file.exists()) {
            Elements userAgents;
            try {
                userAgents = Jsoup.parse(file, FILE_ENCODING, "").select("useragent");
                Random random = new Random();
                return userAgents.get(random.nextInt(userAgents.size())).attr("useragent");
            } catch (IOException e) {
                new ErrorLogHandler().error(e, logger);
            }
        }
        return generateRandomUserAgent();
    }

    private String generateRandomUserAgent() {
        final String BASE_AGENT = "Mozilla/%s.0 (Windows; U; Windows NT 5.1; en-US; rv:%s.%s) Gecko/%s%s Firefox/1.%s.%s";
        return String.format(BASE_AGENT, randInt(3, 5), randInt(1, 8), randInt(1, 2), randInt(2000, 2100), randInt(92215, 99999), randInt(3, 9), randInt(1, 2));
    }

    private static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }
}
