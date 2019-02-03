package com.helloingob.gifter.to;

import java.sql.Timestamp;

public class CachedGameTO {
    private Integer pk;
    private Integer appId;
    private Boolean isDlc;
    private Timestamp date;

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Boolean getIsDlc() {
        return isDlc;
    }

    public void setIsDlc(Boolean isDlc) {
        this.isDlc = isDlc;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
