package com.helloingob.gifter.algorithm;

import java.util.List;

import com.helloingob.gifter.data.to.Giveaway;
import com.helloingob.gifter.handler.UserHandler;
import com.helloingob.gifter.utilities.dlc.DLCAnalyzer;

public class DefaultAlg implements Algorithm {

    private UserHandler userHandler;
    private List<Giveaway> giveaways;

    public DefaultAlg(UserHandler userHandler, List<Giveaway> giveaways) {
        super();
        this.userHandler = userHandler;
        this.giveaways = giveaways;
    }

    public void spendPoints() {
        for (Giveaway giveaway : giveaways) {
            if (giveaway.getPoints() > userHandler.getPoints()) {
                continue;
            }
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
