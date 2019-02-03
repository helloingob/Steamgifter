package com.helloingob.gifter;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Row;
import org.zkoss.zul.Span;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabs;

import com.helloingob.gifter.components.FontImage;
import com.helloingob.gifter.components.comparators.PointLabelComparator;
import com.helloingob.gifter.dao.InfoLogDAO;
import com.helloingob.gifter.dao.UserAssetDAO;
import com.helloingob.gifter.dao.UserDAO;
import com.helloingob.gifter.to.InfoLogTO;
import com.helloingob.gifter.to.UserAssetTO;
import com.helloingob.gifter.to.UserTO;
import com.helloingob.gifter.utilities.GUIHelper;
import com.helloingob.gifter.utilities.GUISettings;
import com.helloingob.gifter.utilities.ImageCreator;
import com.helloingob.gifter.utilities.MenuCreator;

public class InfoLogController extends GenericForwardComposer<Component> {
    private static final long serialVersionUID = 1L;

    private Column clmPoints;

    private Grid grdInfoLog;
    private Hlayout hlInfoLogUsers;
    private Hbox userBreadcrumb;
    private Div bcImageContainer;
    private Label lblBreadcrumbTitle;
    private Label lblEntries;

    private Grid grdInfoSummary;
    private Panel pnlSummary;
    private Hbox hbxInfoLogNavi;

    private Tabs tabs;

    private Button btnLeft;
    private Label lblPaging;
    private Button btnRight;

    private List<UserAssetTO> userAssets;
    private int activeUserAsset;

    private Double totalWinChance;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        Include include = (Include) comp.getParent();
        tabs = ((Tabpanel) include.getParent()).getTabbox().getTabs();

        init();
    }

    private void init() {
        initSortComparators();
        initTabListener();
        initInfoLog();
    }

    private void initSortComparators() {
        int pointColumnIndex = grdInfoLog.getColumns().getChildren().indexOf(clmPoints);
        clmPoints.setSortAscending(new PointLabelComparator(true, pointColumnIndex));
        clmPoints.setSortDescending(new PointLabelComparator(false, pointColumnIndex));
    }

    private void initTabListener() {
        for (Component component : tabs.getChildren()) {
            ((Tab) component).addEventListener(Events.ON_CLICK, new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    toggleInfoVisibility(false);
                }
            });
        }

        lblBreadcrumbTitle.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                toggleInfoVisibility(false);
            }
        });
    }

    private void initInfoLog() {
        for (final UserTO user : new UserDAO().getInactiveOrdered()) {
            Div imageContainer = new Div();
            imageContainer.setClass("image-gallery");
            Image userImage = GUIHelper.createImage(user.getImageLink(), user.getLoginName());
            userImage.setWidth("150px");
            userImage.setHeight("150px");

            Span userName = new Span();
            userName.setClass("bordered");
            userName.appendChild(new Label(user.getLoginName()));

            imageContainer.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    userAssets = new UserAssetDAO().getForUser(user.getPk());
                    activeUserAsset = userAssets.size() - 1;
                    Image bcImage = (Image) userImage.clone();
                    bcImage.setWidth("30px");
                    bcImage.setHeight("30px");
                    bcImageContainer.getChildren().clear();
                    bcImageContainer.appendChild(bcImage);
                    lblEntries.setValue(user.getEntryCount() + " overall entries.");
                    fillContent();
                    toggleInfoVisibility(true);
                }
            });
            imageContainer.appendChild(userImage);
            imageContainer.appendChild(userName);

            if (!user.getIsActive()) {
                FontImage imgInactive = new FontImage();
                imgInactive.setIconSclass("z-icon-ban");
                imgInactive.setSize(25);
                imgInactive.setColor("#ff0000");
                imgInactive.setCustomStyle("position: absolute; top: 20px; left: 20px; width: 1px; height: 1px;");
                imgInactive.setTooltiptext("inactive");
                imageContainer.appendChild(imgInactive);
            }

            if (user.getCurrentLevel() > 0) {
                Integer level = user.getCurrentLevel().intValue();
                Span spnLevel = new Span();
                spnLevel.setClass("bordered");
                spnLevel.appendChild(new Label(level + "+"));
                spnLevel.setStyle("position: absolute; top: 15px; right: 22px;");
                spnLevel.setTooltiptext("level: " + user.getCurrentLevel());
                imageContainer.appendChild(spnLevel);
            }
            hlInfoLogUsers.appendChild(imageContainer);
        }
    }

    private void toggleInfoVisibility(boolean toggle) {
        hlInfoLogUsers.setVisible(!toggle);
        grdInfoLog.setVisible(toggle);
        userBreadcrumb.setVisible(toggle);
        pnlSummary.setVisible(toggle);
        grdInfoSummary.setVisible(toggle);
        hbxInfoLogNavi.setVisible(toggle);
        lblEntries.setVisible(toggle);
    }

    protected void fillSummary() {
        UserAssetTO userAsset = userAssets.get(activeUserAsset);

        Row rowLevel = new Row();
        rowLevel.appendChild(new Label("Current Level: "));
        rowLevel.appendChild(new Label(userAsset.getLevel() + ""));
        grdInfoSummary.getRows().appendChild(rowLevel);

        Row rowDate = new Row();
        rowDate.appendChild(new Label("Date: "));
        rowDate.appendChild(new Label(GUISettings.SIMPLE_DATEFORMAT.format(userAsset.getDate())));
        grdInfoSummary.getRows().appendChild(rowDate);

        Row rowPoints = new Row();
        rowPoints.appendChild(new Label("Starting Points: "));
        rowPoints.appendChild(new Label(userAsset.getPoints() + ""));
        grdInfoSummary.getRows().appendChild(rowPoints);

        Row rowEnteredGiveaways = new Row();
        rowEnteredGiveaways.appendChild(new Label("Entered Giveaways: "));
        rowEnteredGiveaways.appendChild(new Label(userAsset.getEnteredGiveaways() + ""));
        grdInfoSummary.getRows().appendChild(rowEnteredGiveaways);

        Row rowPointsSpent = new Row();
        rowPointsSpent.appendChild(new Label("Spent Points: "));
        rowPointsSpent.appendChild(new Label(userAsset.getSpentPoints() + ""));
        grdInfoSummary.getRows().appendChild(rowPointsSpent);

        Row rowPointsRemaining = new Row();
        rowPointsRemaining.appendChild(new Label("Remaining Points: "));
        int remainingPoints = userAsset.getRemainingPoints();
        if (remainingPoints < 0) {
            remainingPoints = 0;
        }
        rowPointsRemaining.appendChild(new Label(remainingPoints + ""));
        grdInfoSummary.getRows().appendChild(rowPointsRemaining);

        Row rowAnyWinChance = new Row();
        rowAnyWinChance.appendChild(new Label("Chance to win anything: "));
        rowAnyWinChance.appendChild(new Label(GUISettings.WINCHANCE_FORMAT.format(getTotalWinChance())));
        grdInfoSummary.getRows().appendChild(rowAnyWinChance);
    }

    private void fillInfoGrid() {
        Row row;
        for (InfoLogTO infoLog : new InfoLogDAO().getForUserAsset(userAssets.get(activeUserAsset).getPk())) {
            row = new Row();
            row.setHeight("75px");
            row.appendChild(ImageCreator.createGiveawayImage(infoLog.getImageLink()));
            row.appendChild(new Label(GUISettings.SIMPLE_DATEFORMAT.format(infoLog.getDate())));
            row.appendChild(new Label(infoLog.getTitle()));
            row.appendChild(new Label(infoLog.getPoints() + " P"));
            row.appendChild(new Label(GUISettings.WINCHANCE_FORMAT.format(infoLog.getWinChance())));

            Menupopup menupopup = MenuCreator.createMenupopup(infoLog.getSteamLink(), infoLog.getGiveawayLink(), infoLog.getTitle(), null);
            menupopup.setPage(getPage());
            row.setContext(menupopup);

            updateTotalWinChance(infoLog.getWinChance());
            grdInfoLog.getRows().appendChild(row);
        }
    }

    public void onClick$btnLeft() {
        activeUserAsset--;
        fillContent();
    }

    public void onClick$btnRight() {
        activeUserAsset++;
        fillContent();
    }

    private void clearContent() {
        grdInfoLog.getRows().getChildren().clear();
        grdInfoSummary.getRows().getChildren().clear();
    }

    private void fillContent() {
        clearContent();

        if (activeUserAsset < 0) {
            return;
        }
        totalWinChance = 1.0;
        fillInfoGrid();
        fillSummary();
        updateNavigation();
    }

    private void updateNavigation() {
        if (activeUserAsset <= 0) {
            btnLeft.setDisabled(true);
        } else {
            btnLeft.setDisabled(false);
        }

        if (activeUserAsset >= userAssets.size() - 1) {
            btnRight.setDisabled(true);
        } else {
            btnRight.setDisabled(false);
        }

        lblPaging.setValue((activeUserAsset + 1) + "/" + userAssets.size());
    }

    private void updateTotalWinChance(Double winChance) {
        totalWinChance *= 1.0 - winChance / 100;
    }

    private double getTotalWinChance() {
        return (1.0 - totalWinChance) * 100;
    }
}
