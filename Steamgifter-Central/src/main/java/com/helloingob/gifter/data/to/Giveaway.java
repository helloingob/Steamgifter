package com.helloingob.gifter.data.to;

import java.sql.Timestamp;

public class Giveaway {

    private String title;
    private Integer points;
    private Integer copies;
    private Integer entries;
    private Double winChance;
    private String author;
    private String giveawayLink;
    private String steamLink;
    private String imageLink;
    private Integer levelRequirement;
    private String code;
    private Integer subId;
    private Integer appId;
    private Timestamp endDate;

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

    public Integer getCopies() {
        return copies;
    }

    public void setCopies(Integer copies) {
        this.copies = copies;
    }

    public Integer getEntries() {
        return entries;
    }

    public void setEntries(Integer entries) {
        this.entries = entries;
    }

    public Double getWinChance() {
        return winChance;
    }

    public void setWinChance(Double winChance) {
        this.winChance = winChance;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGiveawayLink() {
        return giveawayLink;
    }

    public void setGiveawayLink(String giveawayLink) {
        this.giveawayLink = giveawayLink;
    }

    public String getSteamLink() {
        return steamLink;
    }

    public void setSteamLink(String steamLink) {
        this.steamLink = steamLink;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public Integer getLevelRequirement() {
        return levelRequirement;
    }

    public void setLevelRequirement(Integer levelRequirement) {
        this.levelRequirement = levelRequirement;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getSubId() {
        return subId;
    }

    public void setSubId(Integer subId) {
        this.subId = subId;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return String.format("GiveawayTO [title=%s, points=%s, copies=%s, entries=%s, winChance=%s, author=%s, giveawayLink=%s, steamLink=%s, imageLink=%s, levelRequirement=%s, code=%s, subId=%s, appId=%s, endDate=%s]", title, points, copies, entries, winChance, author, giveawayLink, steamLink, imageLink, levelRequirement, code, subId, appId, endDate);
    }

}
