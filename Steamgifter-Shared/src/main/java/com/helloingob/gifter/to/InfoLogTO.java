package com.helloingob.gifter.to;

import java.sql.Timestamp;
import java.util.Optional;

public class InfoLogTO {
    private Integer pk;
    private String title;
    private Integer points;
    private Double winChance;
    private String steamLink;
    private String giveawayLink;
    private String imageLink;
    private Timestamp date;
    private Integer userAssetPk;

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Double getWinChance() {
        return winChance;
    }

    public void setWinChance(Double winChance) {
        this.winChance = winChance;
    }

    public Optional<String> getSteamLink() {
        return Optional.ofNullable(steamLink);
    }

    public void setSteamLink(String steamLink) {
        this.steamLink = steamLink;
    }

    public String getGiveawayLink() {
        return giveawayLink;
    }

    public void setGiveawayLink(String giveawayLink) {
        this.giveawayLink = giveawayLink;
    }

    public Optional<String> getImageLink() {
        return Optional.ofNullable(imageLink);
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Integer getUserAssetPk() {
        return userAssetPk;
    }

    public void setUserAssetPk(Integer userAssetPk) {
        this.userAssetPk = userAssetPk;
    }
}
