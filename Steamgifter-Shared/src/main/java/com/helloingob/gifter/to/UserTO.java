package com.helloingob.gifter.to;

import java.sql.Timestamp;
import java.util.Optional;

import com.helloingob.gifter.dao.UserAssetDAO;

public class UserTO {
    private Integer pk;
    private String loginName;
    private String profileName;
    private Long steamId;
    private String password;
    private String notificationEmail;
    private String imageLink;
    private String phpsessionid;
    private Boolean skipDlc;
    private Boolean skipSub;
    private Boolean skipWishlist;
    private Timestamp createdDate;
    private Timestamp lastLogin;
    private Boolean isActive;
    private Boolean isAdmin;
    private Integer algorithmPk;
    private Integer wonGiveaways;
    private Double currentLevel;

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public Long getSteamId() {
        return steamId;
    }

    public void setSteamId(Long steamId) {
        this.steamId = steamId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNotificationEmail() {
        return notificationEmail;
    }

    public void setNotificationEmail(String notificationEmail) {
        this.notificationEmail = notificationEmail;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getPhpsessionid() {
        return phpsessionid;
    }

    public void setPhpsessionid(String phpsessionid) {
        this.phpsessionid = phpsessionid;
    }

    public Boolean getSkipDlc() {
        return skipDlc;
    }

    public void setSkipDlc(Boolean skipDlc) {
        this.skipDlc = skipDlc;
    }

    public Boolean getSkipSub() {
        return skipSub;
    }

    public void setSkipSub(Boolean skipSub) {
        this.skipSub = skipSub;
    }

    public Boolean getSkipWishlist() {
        return skipWishlist;
    }

    public void setSkipWishlist(Boolean skipWishlist) {
        this.skipWishlist = skipWishlist;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public Optional<Timestamp> getLastLogin() {
        return Optional.ofNullable(lastLogin);
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Integer getAlgorithmPk() {
        return algorithmPk;
    }

    public void setAlgorithmPk(Integer algorithmPk) {
        this.algorithmPk = algorithmPk;
    }

    public Integer getWonGiveaways() {
        return wonGiveaways;
    }

    public void setWonGiveaways(Integer wonGiveaways) {
        this.wonGiveaways = wonGiveaways;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public void setCurrentLevel(double currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Double getCurrentLevel() {
        return currentLevel;
    }

    public Optional<Timestamp> getLastSyncedDate() {
        return new UserAssetDAO().getLastSyncedDateForUser(pk);
    }

    public Integer getEntryCount() {
        return new UserAssetDAO().getEntryCountForUser(pk);
    }
}
