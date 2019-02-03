package com.helloingob.gifter.to;

import java.sql.Timestamp;
import java.util.Optional;

public class ErrorLogTO {
    private Integer pk;
    private String message;
    private String value;
    private Timestamp date;
    private Integer userPk;

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Optional<Integer> getUserPk() {
        return Optional.ofNullable(userPk);
    }

    public void setUserPk(Integer userPk) {
        this.userPk = userPk;
    }

    @Override
    public String toString() {
        return String.format("ErrorLogTO [pk=%s, message=%s, value=%s, date=%s, userPk=%s]", pk, message, value, date, userPk);
    }

}
