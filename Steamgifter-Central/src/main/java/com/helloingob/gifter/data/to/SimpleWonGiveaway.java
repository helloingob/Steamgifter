package com.helloingob.gifter.data.to;

import java.util.Optional;

public class SimpleWonGiveaway {

    private String giveawayLink;
    private Boolean hasReceived;
    private String steamKey;

    public String getGiveawayLink() {
        return giveawayLink;
    }

    public void setGiveawayLink(String giveawayLink) {
        this.giveawayLink = giveawayLink;
    }

    public Optional<Boolean> getHasReceived() {
        return Optional.ofNullable(hasReceived);
    }

    public void setHasReceived(Boolean hasReceived) {
        this.hasReceived = hasReceived;
    }

    public Optional<String> getSteamKey() {
        return Optional.ofNullable(steamKey);
    }

    public void setSteamKey(String steamKey) {
        this.steamKey = steamKey;
    }

    @Override
    public String toString() {
        return String.format("SimpleWonGiveaway [giveawayLink=%s, hasAcknowledged=%s, steamKey=%s]", giveawayLink, hasReceived, steamKey);
    }

}
