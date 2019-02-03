package com.helloingob.gifter.algorithm;

import java.util.List;

import com.helloingob.gifter.data.to.EnteredGiveaway;
import com.helloingob.gifter.data.to.Giveaway;
import com.helloingob.gifter.handler.EnteredGiveawayHandler;
import com.helloingob.gifter.handler.UserHandler;
import com.helloingob.gifter.utilities.dlc.DLCAnalyzer;

public class SnitchAlg implements Algorithm {
    private UserHandler userHandler;
    private List<Giveaway> giveaways;

    private final static int MAX_POINTS_LIMIT = 250;
    private final static double MIN_WINCHANCE = 0.5;

    public SnitchAlg(UserHandler userHandler, List<Giveaway> giveaways) {
        super();
        this.userHandler = userHandler;
        this.giveaways = giveaways;
        removeLowPercentageEnteredGiveaways();
    }

    private void removeLowPercentageEnteredGiveaways() {
        for (EnteredGiveaway enteredGiveaway : new EnteredGiveawayHandler(userHandler.getUser()).getEnteredGiveaways()) {
            if (userHandler.getPoints() >= MAX_POINTS_LIMIT) {
                break;
            }
            if (enteredGiveaway.getWinChance() <= MIN_WINCHANCE) {
                userHandler.removeEnteredGiveaway(enteredGiveaway);
            }
        }
        userHandler.updateUserAssetPoints();
    }

    public void spendPoints() {
        for (Giveaway giveaway : giveaways) {
            //enough points? more than min points limit?
            if (giveaway.getPoints() < userHandler.getPoints() && giveaway.getWinChance() >= MIN_WINCHANCE) {
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
