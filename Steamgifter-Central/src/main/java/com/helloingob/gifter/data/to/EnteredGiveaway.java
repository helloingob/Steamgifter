package com.helloingob.gifter.data.to;


public class EnteredGiveaway {

    private String title;
    private Integer copies;
    private Integer entries;
    private Double winChance;
    private String giveawayLink;
    private String imageLink;
    private String code;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getGiveawayLink() {
        return giveawayLink;
    }

    public void setGiveawayLink(String giveawayLink) {
        this.giveawayLink = giveawayLink;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return String.format("EntryGiveaway [title=%s, copies=%s, entries=%s, winChance=%s, giveawayLink=%s, imageLink=%s, code=%s]", title, copies, entries, winChance, giveawayLink, imageLink, code);
    }

}
