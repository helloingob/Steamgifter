package com.helloingob.gifter.data.to;

import org.jsoup.Jsoup;

public class ViewKeyResponse {

    private Integer success;
    private String html;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getKey() {
        if (html != null && !html.isEmpty()) {
            return Jsoup.parse(html).select("i").first().attr("data-clipboard-text");
        }
        return null;
    }

    @Override
    public String toString() {
        return "ViewKeyResponse [success=" + success + ", html=" + html + ", getSuccess()=" + getSuccess() + ", getHtml()=" + getHtml() + ", getKey()=" + getKey() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
    }

}