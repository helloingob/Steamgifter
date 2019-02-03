package com.helloingob.gifter.to;

import java.sql.Timestamp;

public class UserAssetTO {
    private Integer pk;
    private Integer points;
    private Double level;
    private Boolean synced;
    private Timestamp date;
    private Integer userPk;
    private Integer spentPoints;
    private Integer enteredGiveaways;

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer pointsBefore) {
        this.points = pointsBefore;
    }

    public Double getLevel() {
        return level;
    }

    public void setLevel(Double level) {
        this.level = level;
    }

    public Boolean getSynced() {
        return synced;
    }

    public void setSynced(Boolean synced) {
        this.synced = synced;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Integer getUserPk() {
        return userPk;
    }

    public void setUserPk(Integer userPk) {
        this.userPk = userPk;
    }

    public Integer getSpentPoints() {
        return spentPoints;
    }

    public void setSpentPoints(int spentPoints) {
        this.spentPoints = spentPoints;
    }

    public Integer getEnteredGiveaways() {
        return enteredGiveaways;
    }

    public void setEnteredGiveaways(int enteredGiveaways) {
        this.enteredGiveaways = enteredGiveaways;
    }

    public Integer getRemainingPoints() {
        return points - spentPoints;
    }
}