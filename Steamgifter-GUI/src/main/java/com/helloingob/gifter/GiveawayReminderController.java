package com.helloingob.gifter;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tabpanel;

import com.helloingob.gifter.components.FontImage;
import com.helloingob.gifter.dao.WonGiveawayDAO;
import com.helloingob.gifter.to.WonGiveawayTO;
import com.helloingob.gifter.utilities.GUIHelper;
import com.helloingob.gifter.utilities.ImageCreator;
import com.helloingob.gifter.utilities.MenuCreator;
import com.helloingob.gifter.utilities.TimeHelper;

public class GiveawayReminderController extends GenericForwardComposer<Component> {
    private static final long serialVersionUID = 1L;

    private Grid grdGiveaways;
    private List<WonGiveawayTO> unnoticedGiveaways = new WonGiveawayDAO().getUnnoticed();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        init();
    }

    private void init() {
        initPaging();
        fillView();
        refreshTabtitle();
    }

    private void refreshTabtitle() {
        ((Tabpanel) this.self.getParent().getParent()).getLinkedTab().setLabel("Giveaway Reminder (" + unnoticedGiveaways.size() + ")");
    }

    private void initPaging() {
        grdGiveaways.setMold("paging");
        grdGiveaways.setPageSize(20);
        grdGiveaways.getPaginal().setAutohide(false);
    }

    private void fillView() {
        fillGiveawayReminderGrid();
    }

    private void fillGiveawayReminderGrid() {
        grdGiveaways.getRows().getChildren().clear();
        Row row;
        for (WonGiveawayTO wonGiveaway : unnoticedGiveaways) {
            row = new Row();
            row.setHeight("75px");
            row.appendChild(ImageCreator.createGiveawayImage(wonGiveaway.getImageLink()));
            row.appendChild(new Label(GUIHelper.convertDateDifferenceToString(TimeHelper.getCurrentTimestamp().getTime(), wonGiveaway.getEndDate().getTime())));
            row.appendChild(new Label(wonGiveaway.getUserName()));

            Label lblTitle = new Label(wonGiveaway.getTitle());
            lblTitle.setStyle("text-decoration: underline; cursor: pointer;");
            lblTitle.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    Executions.getCurrent().sendRedirect("http://www.steamgifts.com/giveaways/won", "_blank");
                }
            });
            row.appendChild(lblTitle);

            FontImage fmgSteamgifts = new FontImage();
            fmgSteamgifts.setSize(20);
            fmgSteamgifts.setCustomStyle("text-shadow: -1px 0 black, 0 1px black, 1px 0 black, 0 -1px black;");
            if (wonGiveaway.getHasReceived().isPresent()) {
                fmgSteamgifts.setIconSclass("z-icon-check");
                fmgSteamgifts.setColor("00FF00");
                fmgSteamgifts.setTooltiptext("ok");
            } else {
                fmgSteamgifts.setIconSclass("z-icon-close");
                fmgSteamgifts.setColor("FF0000");
                fmgSteamgifts.setTooltiptext("Update the receive status on steamgifts.com");
            }
            row.appendChild(fmgSteamgifts);

            FontImage fmgSteam = new FontImage();
            fmgSteam.setSize(20);
            fmgSteam.setCustomStyle("text-shadow: -1px 0 black, 0 1px black, 1px 0 black, 0 -1px black;");
            if (wonGiveaway.getSteamActivationDate().isPresent()) {
                fmgSteam.setIconSclass("z-icon-check");
                fmgSteam.setColor("00FF00");
                fmgSteam.setTooltiptext("ok");
            } else {
                fmgSteam.setIconSclass("z-icon-close");
                fmgSteam.setColor("FF0000");
                fmgSteam.setTooltiptext("Activate the game in Steam");
            }
            row.appendChild(fmgSteam);

            Menupopup menupopup = MenuCreator.createMenupopup(wonGiveaway.getSteamLink(), wonGiveaway.getGiveawayLink(), wonGiveaway.getTitle(), wonGiveaway.getPk());
            menupopup.setPage(getPage());
            row.setContext(menupopup);

            grdGiveaways.getRows().appendChild(row);
        }
    }
}
