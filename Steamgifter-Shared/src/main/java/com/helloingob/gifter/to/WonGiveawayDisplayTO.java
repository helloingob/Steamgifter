package com.helloingob.gifter.to;

import java.sql.Timestamp;

public class WonGiveawayDisplayTO {
    private String name;
    private Integer wins;
    private Double sumPrice;
    private Integer lastWonGamePk;
    private String lastWonGame;
    private Timestamp lastWonGameDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Double getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(Double sumPrice) {
        this.sumPrice = sumPrice;
    }

    public Integer getLastWonGamePk() {
        return lastWonGamePk;
    }

    public void setLastWonGamePk(Integer lastWonGamePk) {
        this.lastWonGamePk = lastWonGamePk;
    }

    public String getLastWonGame() {
        return lastWonGame;
    }

    public void setLastWonGame(String lastWonGame) {
        this.lastWonGame = lastWonGame;
    }

    public Timestamp getLastWonGameDate() {
        return lastWonGameDate;
    }

    public void setLastWonGameDate(Timestamp lastWonGameDate) {
        this.lastWonGameDate = lastWonGameDate;
    }
}
