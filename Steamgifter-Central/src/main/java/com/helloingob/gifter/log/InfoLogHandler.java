package com.helloingob.gifter.log;

import com.helloingob.gifter.dao.InfoLogDAO;
import com.helloingob.gifter.data.to.AdvUserTO;
import com.helloingob.gifter.data.to.Giveaway;
import com.helloingob.gifter.to.InfoLogTO;
import com.helloingob.gifter.utilities.TimeHelper;

public class InfoLogHandler {

    private AdvUserTO user;
    private InfoLogDAO infoLogDAO;

    public InfoLogHandler(AdvUserTO user) {
        this.user = user;
        infoLogDAO = new InfoLogDAO();
    }

    public void createInfoLogEntry(Giveaway giveaway) {
        InfoLogTO infoLog = new InfoLogTO();
        infoLog.setDate(TimeHelper.getCurrentTimestamp());
        infoLog.setTitle(giveaway.getTitle());
        infoLog.setGiveawayLink(giveaway.getGiveawayLink());
        infoLog.setImageLink(giveaway.getImageLink());
        infoLog.setPoints(giveaway.getPoints());
        infoLog.setSteamLink(giveaway.getSteamLink());
        infoLog.setWinChance(giveaway.getWinChance());
        infoLog.setUserAssetPk(user.getUserAsset().getPk());
        infoLogDAO.save(infoLog);
    }
}
