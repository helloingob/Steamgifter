package com.helloingob.gifter.algorithm;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.helloingob.gifter.data.to.Giveaway;
import com.helloingob.gifter.handler.UserHandler;
import com.helloingob.gifter.utilities.TimeHelper;
import com.helloingob.gifter.utilities.dlc.DLCAnalyzer;

public class GrabberAlg implements Algorithm {
    private UserHandler userHandler;
    private List<Giveaway> giveaways;

    public GrabberAlg(UserHandler userHandler, List<Giveaway> giveaways) {
        super();
        this.userHandler = userHandler;
        this.giveaways = giveaways;
        removeGreaterThenOneHourLeftGiveaways();
        sortByWinchance();
    }

    private void sortByWinchance() {
        Collections.sort(giveaways, new Comparator<Giveaway>() {
            public int compare(Giveaway giveaway1, Giveaway giveaway2) {
                return giveaway2.getWinChance().compareTo(giveaway1.getWinChance());
            }
        });
    }

    private void removeGreaterThenOneHourLeftGiveaways() {
        int oneHour = 1000 * 60 * 60;
        giveaways.removeIf(giveaway -> giveaway.getEndDate().getTime() >= (TimeHelper.getCurrentTimestamp().getTime() + oneHour));
    }

    public void spendPoints() {
        for (Giveaway giveaway : giveaways) {
            if (giveaway.getPoints() < userHandler.getPoints()) {
                //should skip DLC?
                if (userHandler.skipDlc()) {
                    //if NOT dlc
                    if (!new DLCAnalyzer().isDLC(giveaway)) {
                        userHandler.enterGiveaway(giveaway);
                    }
                } else {
                    userHandler.enterGiveaway(giveaway);
                }
            }
        }
    }

}
