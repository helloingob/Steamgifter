package com.helloingob.gifter.to;

import java.sql.Timestamp;
import java.util.Optional;

import com.helloingob.gifter.dao.UserDAO;

public class WonGiveawayTO {
    private Integer pk;
    private String title;
    private Integer points;
    private Integer copies;
    private Integer entries;
    private Double winChance;
    private String author;
    private Double steamStorePrice;
    private String giveawayLink;
    private String steamLink;
    private String imageLink;
    private Integer levelRequirement;
    private String steamKey;
    private Boolean has_received;
    private Timestamp receivedDate;
    private Timestamp steamActivationDate;
    private Timestamp endDate;
    private Integer userPk;
    private String userName;

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

    public Optional<Double> getSteamStorePrice() {
        return Optional.ofNullable(steamStorePrice);
    }

    public void setSteamStorePrice(Double steamStorePrice) {
        this.steamStorePrice = steamStorePrice;
    }

    public String getGiveawayLink() {
        return giveawayLink;
    }

    public void setGiveawayLink(String giveawayLink) {
        this.giveawayLink = giveawayLink;
    }

    public Optional<String> getSteamLink() {
        return Optional.ofNullable(steamLink);
    }

    public void setSteamLink(String steamLink) {
        this.steamLink = steamLink;
    }

    public Optional<String> getImageLink() {
        return Optional.ofNullable(imageLink);
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

    public Optional<String> getSteamKey() {
        return Optional.ofNullable(steamKey);
    }

    public void setSteamKey(String steamKey) {
        this.steamKey = steamKey;
    }

    public Optional<Boolean> getHasReceived() {
        return Optional.ofNullable(has_received);
    }

    public void setHasReceived(Boolean has_received) {
        this.has_received = has_received;
    }

    public Optional<Timestamp> getReceivedDate() {
        return Optional.ofNullable(receivedDate);
    }

    public void setReceivedDate(Timestamp receivedDate) {
        this.receivedDate = receivedDate;
    }

    public Optional<Timestamp> getSteamActivationDate() {
        return Optional.ofNullable(steamActivationDate);
    }

    public void setSteamActivationDate(Timestamp steamActivationDate) {
        this.steamActivationDate = steamActivationDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public Integer getUserPk() {
        return userPk;
    }

    public void setUserPk(Integer userPk) {
        this.userPk = userPk;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserTO getUser() {
        return new UserDAO().get(userPk);
    }

    @Override
    public String toString() {
        return "WonGiveawayTO [pk=" + pk + ", title=" + title + ", points=" + points + ", copies=" + copies + ", entries=" + entries + ", winChance=" + winChance + ", author=" + author + ", steamStorePrice=" + steamStorePrice + ", giveawayLink=" + giveawayLink + ", steamLink=" + steamLink + ", imageLink=" + imageLink + ", levelRequirement=" + levelRequirement + ", steamKey=" + steamKey + ", received=" + has_received + ", receivedDate=" + receivedDate + ", steamActivationDate=" + steamActivationDate + ", endDate=" + endDate + ", userPk=" + userPk + ", userName=" + userName + "]";
    }

    @Override
    public boolean equals(Object object) {
        return this.giveawayLink.equals(((WonGiveawayTO) object).giveawayLink);
    }
}
