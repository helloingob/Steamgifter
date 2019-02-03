package com.helloingob.gifter.handler;

import java.util.LinkedList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.helloingob.gifter.ErrorLogHandler;
import com.helloingob.gifter.data.connection.HTTPConnection;
import com.helloingob.gifter.data.to.AdvUserTO;
import com.helloingob.gifter.data.to.Giveaway;
import com.helloingob.gifter.parser.GiveawayListParser;
import com.helloingob.gifter.to.WonGiveawayTO;
import com.helloingob.gifter.utilities.CentralSettings;
import com.helloingob.gifter.utilities.SharedSettings;

public class WishlistGiveawayHandler {
    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);
    private AdvUserTO user;

    public WishlistGiveawayHandler(AdvUserTO user) {
        this.user = user;
    }

    public List<Giveaway> getGiveaways(List<WonGiveawayTO> wonGiveaways) {
        List<Giveaway> giveaways = new LinkedList<Giveaway>();
        HttpResponse response = new HTTPConnection(user.getUserAgent()).doGet(CentralSettings.Url.WISHLIST_GIVEAWAY_URL, user.getPhpsessionid());
        if (response != null) {
            try {
                HttpEntity responseEntity = response.getEntity();
                String responseString = EntityUtils.toString(responseEntity);
                giveaways.addAll(GiveawayListParser.getGiveawaysFromPageSource(responseString, user, user.getUserAsset()));
            } catch (Exception e) {
                new ErrorLogHandler(user).error(e, logger);
            }
        }
        //remove already won games, for safety reasons
        //http://www.steamgifts.com/roles/guest -> Multiple Wins for the Same Game: 5 days
        if (wonGiveaways != null) {
            List<String> giveawaysTitles = new LinkedList<String>();
            wonGiveaways.forEach(wg -> giveawaysTitles.add(wg.getTitle()));
            giveaways.removeIf(g -> giveawaysTitles.contains(g.getTitle()));
        }
        return giveaways;
    }

}
