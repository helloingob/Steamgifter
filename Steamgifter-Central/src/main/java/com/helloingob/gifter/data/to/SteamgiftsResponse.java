package com.helloingob.gifter.data.to;

public class SteamgiftsResponse {

    private String type;
    private String entry_count;
    private Integer points;
    private String msg;
    private Integer statusCode;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEntry_count() {
        return entry_count;
    }

    public void setEntry_count(String entry_count) {
        this.entry_count = entry_count;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return String.format("ResponseTO [type=%s, entry_count=%s, points=%s, msg=%s, statusCode=%s]", type, entry_count, points, msg, statusCode);
    }

}
