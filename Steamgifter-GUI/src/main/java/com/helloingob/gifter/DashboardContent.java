package com.helloingob.gifter;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;

import com.helloingob.gifter.components.Widget;
import com.helloingob.gifter.components.comparators.PriceLabelComparator;
import com.helloingob.gifter.dao.WonGiveawayDAO;
import com.helloingob.gifter.to.WonGiveawayDisplayTO;
import com.helloingob.gifter.utilities.GUISettings;
import com.helloingob.gifter.utilities.MenuCreator;

public class DashboardContent {
    private List<Widget> weeklyWidgets = new LinkedList<Widget>();
    private List<Widget> globalWidgets = new LinkedList<Widget>();

    public DashboardContent() {
        createLuckyDevilWidget();
        createPayoffWidget();
        createGreedyWidget();
        createMoneymakerWidget();
        createPoorBastardWidget();
        creatAverageWidget();
    }

    public Component getTable() {
        Div div = new Div();
        div.setHflex("true");

        Hlayout hlayout = new Hlayout();
        hlayout.appendChild(createWonGiveawayTable());
        div.appendChild(hlayout);

        return div;
    }

    public Component getWidgets() {
        Div div = new Div();
        div.setHflex("true");
        div.setVflex("true");
        div.setStyle("text-align: center;");

        Panel weeklyPanel = new Panel();
        weeklyPanel.setTitle("Weekly Achievements");
        weeklyPanel.setStyle("text-align: left; padding-top: 5px;");
        div.appendChild(weeklyPanel);

        for (Widget widget : weeklyWidgets) {
            div.appendChild(widget);
        }

        Panel globalPanel = new Panel();
        globalPanel.setTitle("Global Achievements");
        globalPanel.setStyle("text-align: left; padding-top: 10px;");
        div.appendChild(globalPanel);

        for (Widget widget : globalWidgets) {
            div.appendChild(widget);
        }

        return div;
    }

    private Component createWonGiveawayTable() {
        Grid grid = new Grid();
        grid.setMold("paging");
        grid.setPageSize(10);
        grid.getPaginal().setAutohide(false);
        grid.setHflex("true");
        grid.setVflex("true");

        grid.appendChild(createWonGiveawayColumns());

        List<WonGiveawayDisplayTO> giveaways = new WonGiveawayDAO().getDisplayTOList();
        Rows rows = new Rows();
        grid.appendChild(rows);

        Integer previousUserWins = null;
        for (WonGiveawayDisplayTO giveaway : giveaways) {
            Row row = new Row();
            row.appendChild(new Label(giveaway.getName()));
            Label lblWins = new Label(giveaway.getWins().toString());
            if (previousUserWins != null) {
                lblWins.setTooltiptext((giveaway.getWins() - previousUserWins) + "");
            }
            row.appendChild(lblWins);
            previousUserWins = giveaway.getWins();
            
            if (previousUserWins > 0) {
                Label lblLastWonGame = new Label(giveaway.getLastWonGame());
                lblLastWonGame.setTooltiptext(GUISettings.SIMPLE_DATEFORMAT.format(giveaway.getLastWonGameDate()));
                row.appendChild(lblLastWonGame);

                row.appendChild(new Label(GUISettings.PRICE_FORMAT.format(giveaway.getSumPrice())));

                if (giveaway.getWins() > 0) {
                    row.appendChild(new Label(GUISettings.PRICE_FORMAT.format(giveaway.getSumPrice() / giveaway.getWins())));
                } else {
                    row.appendChild(new Label("-"));
                }

                Menupopup menu = MenuCreator.createMenupopup(Optional.empty(), null, null, giveaway.getLastWonGamePk());
                row.addEventListener(Events.ON_RIGHT_CLICK, new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        menu.setPage(rows.getPage());
                        menu.open(row);
                    }
                });	
            } else {
            	//LastWonGame
            	row.appendChild(new Label("-"));
            	//SumPrice
            	row.appendChild(new Label("-"));
            	//SumPrice/Wins
            	row.appendChild(new Label("-"));
            }
            rows.appendChild(row);
        }
        return grid;
    }

    private Columns createWonGiveawayColumns() {
        int clmCounter = 0;
        Columns columns = new Columns();

        Column clmName = new Column("Name");
        clmName.setWidth("150px");
        columns.appendChild(clmName);
        clmCounter++;

        Column clmWins = new Column("Wins");
        clmWins.setWidth("150px");
        clmWins.setSortAscending(new PriceLabelComparator(true, clmCounter));
        clmWins.setSortDescending(new PriceLabelComparator(false, clmCounter));
        columns.appendChild(clmWins);
        clmCounter++;

        Column clmLastWin = new Column("Last Win");
        columns.appendChild(clmLastWin);
        clmCounter++;

        Column clmWinSum = new Column("Win Sum");
        clmWinSum.setWidth("150px");
        clmWinSum.setSortAscending(new PriceLabelComparator(true, clmCounter));
        clmWinSum.setSortDescending(new PriceLabelComparator(false, clmCounter));
        columns.appendChild(clmWinSum);
        clmCounter++;

        Column clmAvg = new Column("Win Avg");
        clmAvg.setWidth("150px");
        clmAvg.setSortAscending(new PriceLabelComparator(true, clmCounter));
        clmAvg.setSortDescending(new PriceLabelComparator(false, clmCounter));
        columns.appendChild(clmAvg);
        clmCounter++;

        try {
            clmName.setSort("auto");
            clmLastWin.setSort("auto");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return columns;
    }

    private void createPoorBastardWidget() {
        Optional<Map<String, Object>> optionalMap = new WonGiveawayDAO().getPoorBastard();

        if (optionalMap.isPresent()) {
            Map<String, Object> map = optionalMap.get();
            Widget widget = new Widget();
            widget.setContent("Poor Bastard:", map.get("name") + " won nothing since " + new SimpleDateFormat("dd.MM.yy").format(map.get("date")));
            globalWidgets.add(widget);
        }
    }

    private void createMoneymakerWidget() {
        Optional<Map<String, Object>> optionalMap = new WonGiveawayDAO().getSumWonGiveaways();

        if (optionalMap.isPresent()) {
            Map<String, Object> map = optionalMap.get();
            Widget widget = new Widget();
            widget.setContent("Moneymaker:", "Value of all won giveaways is " + GUISettings.PRICE_FORMAT.format(map.get("sum")));
            globalWidgets.add(widget);
        }
    }

    private void createLuckyDevilWidget() {
        Optional<Map<String, Object>> optionalMap = new WonGiveawayDAO().getLuckyDevil();

        if (optionalMap.isPresent()) {
            Map<String, Object> map = optionalMap.get();
            Widget widget = new Widget();
            widget.setContent("Lucky Devil:", map.get("name") + " won '" + map.get("giveaway_title") + "' with a " + GUISettings.WINCHANCE_FORMAT.format(map.get("win_chance")) + " winchance.");
            widget.addMenupopup(map.get("steam_link").toString(), map.get("giveaway_link").toString(), map.get("giveaway_title").toString());
            weeklyWidgets.add(widget);
        }
    }

    private void createPayoffWidget() {
        Optional<Map<String, Object>> optionalMap = new WonGiveawayDAO().getMostExpensive();

        if (optionalMap.isPresent()) {
            Map<String, Object> map = optionalMap.get();
            Widget widget = new Widget();
            widget.setContent("Payoff:", map.get("name") + " with '" + map.get("giveaway_title") + " (" + GUISettings.PRICE_FORMAT.format(map.get("max_price")) + ")'.");
            widget.addMenupopup(map.get("steam_link").toString(), map.get("giveaway_link").toString(), map.get("giveaway_title").toString());
            weeklyWidgets.add(widget);
        }
    }

    private void createGreedyWidget() {
        Optional<Map<String, Object>> optionalMap = new WonGiveawayDAO().getGreedy();

        if (optionalMap.isPresent()) {
            Map<String, Object> map = optionalMap.get();
            Widget widget = new Widget();
            String quantity = "giveaway";
            if ((Integer) map.get("wins") > 1) {
                quantity = "giveaways";
            }
            widget.setContent("Greedy:", map.get("name") + " with " + map.get("wins") + " won " + quantity);
            weeklyWidgets.add(widget);
        }
    }

    private void creatAverageWidget() {
        Optional<Map<String, Object>> optionalMap = new WonGiveawayDAO().getAverage();

        if (optionalMap.isPresent()) {
            Map<String, Object> map = optionalMap.get();
            Widget widget = new Widget();
            widget.setContent("Giveaway In The Middle:", "The average value of a won giveaway is " + GUISettings.PRICE_FORMAT.format(map.get("average")));
            globalWidgets.add(widget);
        }
    }
}
