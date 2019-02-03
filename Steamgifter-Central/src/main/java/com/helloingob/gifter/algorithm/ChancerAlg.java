package com.helloingob.gifter.algorithm;

import java.util.List;

import com.helloingob.gifter.data.to.Giveaway;
import com.helloingob.gifter.handler.UserHandler;
import com.helloingob.gifter.utilities.TimeHelper;
import com.helloingob.gifter.utilities.dlc.DLCAnalyzer;

public class ChancerAlg implements Algorithm {
    private UserHandler userHandler;
    private List<Giveaway> giveaways;

    private final static double EXP_BASE = 2;

    public ChancerAlg(UserHandler userHandler, List<Giveaway> giveaways) {
        super();
        this.userHandler = userHandler;
        this.giveaways = giveaways;

        removeGreaterThenOneHourLeftGiveaways();
        removeLowPercentageGiveaways();
    }

    private void removeLowPercentageGiveaways() {
        final double currentPoints = userHandler.getPoints();
        final double minWinchance = Math.pow(EXP_BASE, (500 - (currentPoints * (1 + currentPoints * 5 / 1000))) / 300);
        giveaways.removeIf(giveaway -> giveaway.getWinChance() < minWinchance);
    }

    private void removeGreaterThenOneHourLeftGiveaways() {
        int oneHour = 1000 * 60 * 60;
        giveaways.removeIf(giveaway -> giveaway.getEndDate().getTime() >= (TimeHelper.getCurrentTimestamp().getTime() + oneHour));
    }

    public void spendPoints() {
        for (Giveaway giveaway : giveaways) {
            //enough points?
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
