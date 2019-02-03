package com.helloingob.gifter.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.helloingob.gifter.ErrorLogHandler;
import com.helloingob.gifter.utilities.CentralSettings;
import com.helloingob.gifter.utilities.SharedSettings;

public class SuspensionParser {
    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);

    public static String getSuspensionComment(String pageSource) {
        Element container = null;
        try {
            container = Jsoup.parse(pageSource, CentralSettings.Url.STEAMGIFTS_BASE_URL);
            Element element = container.select(".comment").last();
            String suspensionMessage = element.select("li").first().toString();
            return suspensionMessage.replace("<li>", "").replace("</li>", "").replace("<em>", "").replace("</em>", "").replace("<strong>", "").replace("</strong>", "");
        } catch (Exception e) {
            new ErrorLogHandler().error(e, pageSource, logger);
        }
        return null;
    }

}
