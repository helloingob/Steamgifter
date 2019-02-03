package com.helloingob.gifter;

import java.util.LinkedList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Row;

import com.helloingob.gifter.components.DateLabelComparator;
import com.helloingob.gifter.components.comparators.PriceLabelComparator;
import com.helloingob.gifter.components.comparators.WinChanceLabelComparator;
import com.helloingob.gifter.dao.WonGiveawayDAO;
import com.helloingob.gifter.to.WonGiveawayTO;
import com.helloingob.gifter.utilities.GUISettings;
import com.helloingob.gifter.utilities.ImageCreator;
import com.helloingob.gifter.utilities.MenuCreator;

public class WonGiveawaysController extends GenericForwardComposer<Component> {
    private static final long serialVersionUID = 1L;

    private Column clmDate;
    private Column clmValue;
    private Column clmWinChance;

    private Bandbox bbxSearchBar;

    private Grid grdWinnerLog;
    private List<Row> rowList = new LinkedList<Row>();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        init();
    }

    private void init() {
        initSortComparators();
        initPaging();
        initListener();
        initBandbox();
    }

    private void initSortComparators() {
        int priceColumnIndex = grdWinnerLog.getColumns().getChildren().indexOf(clmValue);
        clmValue.setSortAscending(new PriceLabelComparator(true, priceColumnIndex));
        clmValue.setSortDescending(new PriceLabelComparator(false, priceColumnIndex));

        int dateColumnIndex = grdWinnerLog.getColumns().getChildren().indexOf(clmDate);
        clmDate.setSortAscending(new DateLabelComparator(true, dateColumnIndex));
        clmDate.setSortDescending(new DateLabelComparator(false, dateColumnIndex));

        int winChanceColumnIndex = grdWinnerLog.getColumns().getChildren().indexOf(clmWinChance);
        clmWinChance.setSortAscending(new WinChanceLabelComparator(true, winChanceColumnIndex));
        clmWinChance.setSortDescending(new WinChanceLabelComparator(false, winChanceColumnIndex));
    }

    private void initListener() {
        this.self.addEventListener(GUISettings.SHOW_WON_GIVEAWAYS_EVENT, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (grdWinnerLog.getRows().getChildren().isEmpty()) {
                    fillGrid();
                }
            }
        });
    }

    private void initPaging() {
        grdWinnerLog.setMold("paging");
        grdWinnerLog.setPageSize(20);
        grdWinnerLog.getPaginal().setAutohide(false);
    }

    private void fillGrid() {
        Row row;
        for (WonGiveawayTO wonGiveaway : new WonGiveawayDAO().get()) {
            row = new Row();
            row.setHeight("75px");
            row.appendChild(ImageCreator.createGiveawayImage(wonGiveaway.getImageLink()));
            row.appendChild(new Label(GUISettings.SIMPLE_DATEFORMAT.format(wonGiveaway.getEndDate())));
            row.appendChild(new Label(wonGiveaway.getUserName()));
            row.appendChild(new Label(wonGiveaway.getTitle()));

            Label lblPrice = new Label();
            if (wonGiveaway.getSteamStorePrice().isPresent()) {
                lblPrice.setValue(GUISettings.PRICE_FORMAT.format(wonGiveaway.getSteamStorePrice().get()));
            } else {
                lblPrice.setValue("-");
            }
            row.appendChild(lblPrice);

            row.appendChild(new Label(GUISettings.WINCHANCE_FORMAT.format(wonGiveaway.getWinChance())));

            Menupopup menupopup = MenuCreator.createMenupopup(wonGiveaway.getSteamLink(), wonGiveaway.getGiveawayLink(), wonGiveaway.getTitle(), wonGiveaway.getPk());
            menupopup.setPage(getPage());
            row.setContext(menupopup);

            rowList.add(row);
        }
        filterGrid();
    }

    private void initBandbox() {
        bbxSearchBar.setInstant(true);
        bbxSearchBar.setPlaceholder("Search ... ");

        bbxSearchBar.addEventListener(Events.ON_OPEN, createSearchEvent());
        bbxSearchBar.addEventListener(Events.ON_OK, createSearchEvent());
    }

    private EventListener<Event> createSearchEvent() {
        return new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                filterGrid();
            }
        };
    }

    private void filterGrid() {
        grdWinnerLog.getRows().getChildren().clear();
        for (Row row : rowList) {
            for (Component component : row.getChildren()) {
                if (component instanceof Label) {
                    if (((Label) component).getValue().toLowerCase().contains(bbxSearchBar.getText().toLowerCase())) {
                        grdWinnerLog.getRows().getChildren().add(row);
                        break;
                    }
                }
            }
        }
    }
}
