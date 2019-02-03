package com.helloingob.gifter;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import org.apache.commons.codec.digest.DigestUtils;
import org.ngi.zhighcharts.ZHighCharts;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Box;
import org.zkoss.zul.Column;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Row;
import org.zkoss.zul.SimpleXYModel;
import org.zkoss.zul.Textbox;

import com.helloingob.gifter.components.DateLabelComparator;
import com.helloingob.gifter.components.TitleVLayout;
import com.helloingob.gifter.components.comparators.PriceLabelComparator;
import com.helloingob.gifter.components.comparators.WinChanceLabelComparator;
import com.helloingob.gifter.dao.AlgorithmDAO;
import com.helloingob.gifter.dao.UserAssetDAO;
import com.helloingob.gifter.dao.UserDAO;
import com.helloingob.gifter.dao.WonGiveawayDAO;
import com.helloingob.gifter.to.AlgorithmTO;
import com.helloingob.gifter.to.UserTO;
import com.helloingob.gifter.to.WonGiveawayTO;
import com.helloingob.gifter.utilities.GUIHelper;
import com.helloingob.gifter.utilities.GUISettings;
import com.helloingob.gifter.utilities.ImageCreator;
import com.helloingob.gifter.utilities.MenuCreator;
import com.helloingob.gifter.utilities.manager.NotificationManager;
import com.helloingob.gifter.utilities.manager.SessionManager;

public class UserPanelController extends GenericForwardComposer<Component> {
    private static final long serialVersionUID = 1L;

    private Column clmDate;
    private Column clmValue;
    private Column clmWinChance;

    private Grid grdWonGiveaways;
    private Div imageContainer;
    private Label lblLastSynced;

    private Textbox tbxLoginName;
    private Textbox tbxProfileName;
    private Textbox tbxPassword;
    private Textbox tbxSessionId;
    private Textbox tbxEmail;
    private Combobox cbxAlgorithm;
    private Combobox cbxSkipWishlist;
    private Combobox cbxSkipSub;

    private Box boxStatistics;
    private Box boxLevelGroupedGiveaways;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        init();
    }

    private void init() {
        initSortComparators();
        initStartListener();
        if (SessionManager.getUser().isPresent()) {
            fillView();
        }
        initPaging();
    }

    private void initSortComparators() {
        int priceColumnIndex = grdWonGiveaways.getColumns().getChildren().indexOf(clmValue);
        clmValue.setSortAscending(new PriceLabelComparator(true, priceColumnIndex));
        clmValue.setSortDescending(new PriceLabelComparator(false, priceColumnIndex));

        int dateColumnIndex = grdWonGiveaways.getColumns().getChildren().indexOf(clmDate);
        clmDate.setSortAscending(new DateLabelComparator(true, dateColumnIndex));
        clmDate.setSortDescending(new DateLabelComparator(false, dateColumnIndex));

        int winChanceColumnIndex = grdWonGiveaways.getColumns().getChildren().indexOf(clmWinChance);
        clmWinChance.setSortAscending(new WinChanceLabelComparator(true, winChanceColumnIndex));
        clmWinChance.setSortDescending(new WinChanceLabelComparator(false, winChanceColumnIndex));
    }

    private void initPaging() {
        grdWonGiveaways.setMold("paging");
        grdWonGiveaways.setPageSize(20);
        grdWonGiveaways.getPaginal().setAutohide(false);
    }

    private void fillView() {
        UserTO user = SessionManager.getUser().get();
        imageContainer.getChildren().clear();
        Image image = GUIHelper.createImage(user.getImageLink(), user.getLoginName());
        image.setWidth("175px");
        image.setHeight("175px");
        imageContainer.appendChild(image);

        if (user.getLastSyncedDate().isPresent()) {
            lblLastSynced.setValue("Last Synced: " + GUISettings.SIMPLE_DATEFORMAT.format(user.getLastSyncedDate().get()));
        } else {
            lblLastSynced.setValue("Last Synced: never");
        }
        fillCombobox();
        fillUserData();
        fillWonGiveawayGrid();

        fillStatisticContent();
        fillLevelGroupedGiveaways();
    }

    private void fillWonGiveawayGrid() {
        grdWonGiveaways.getRows().getChildren().clear();

        Row row;
        for (WonGiveawayTO wonGiveaway : new WonGiveawayDAO().get(SessionManager.getUser().get())) {
            row = new Row();
            row.setHeight("75px");
            row.appendChild(ImageCreator.createGiveawayImage(wonGiveaway.getImageLink()));
            row.appendChild(new Label(GUISettings.SIMPLE_DATEFORMAT.format(wonGiveaway.getEndDate())));
            row.appendChild(createTitleContent(wonGiveaway));
            row.appendChild(new Label(wonGiveaway.getLevelRequirement() + ""));

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

            if (!wonGiveaway.getSteamActivationDate().isPresent() && wonGiveaway.getHasReceived().isPresent() && wonGiveaway.getHasReceived().get()) {
                Menuitem menuitem = new Menuitem("Manual Activate");
                menuitem.setIconSclass("z-icon-check");
                menupopup.appendChild(menuitem);

                menuitem.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        wonGiveaway.setSteamActivationDate(new Timestamp(System.currentTimeMillis()));

                        if (new WonGiveawayDAO().update(wonGiveaway)) {
                            SessionManager.setNotification("Giveaway marked as activated.");
                            Executions.getCurrent().sendRedirect("/");
                        } else {
                            NotificationManager.showWarning("Error", null, NotificationManager.CENTER);
                        }
                    }
                });
            }

            row.setContext(menupopup);

            row.setValue(wonGiveaway);

            grdWonGiveaways.getRows().appendChild(row);
        }
    }

    private Component createTitleContent(WonGiveawayTO wonGiveaway) {
        TitleVLayout layout = new TitleVLayout();
        layout.addTitle(wonGiveaway.getTitle());
        if (wonGiveaway.getSteamKey().isPresent()) {
            if (wonGiveaway.getSteamKey().get().contains("http")) {
                layout.addLink(wonGiveaway.getSteamKey().get());
            } else {
                layout.addSteamKey(wonGiveaway.getSteamKey().get());
            }
        }
        return layout;
    }

    private void fillCombobox() {
        cbxAlgorithm.getItems().clear();
        for (AlgorithmTO algorithm : new AlgorithmDAO().get()) {
            Comboitem comboitem = new Comboitem(algorithm.getName());
            comboitem.setDescription(algorithm.getDescription());
            comboitem.setValue(algorithm.getPk());
            cbxAlgorithm.appendChild(comboitem);
        }
    }

    private void initStartListener() {
        this.self.addEventListener(GUISettings.SHOW_USERPANEL_EVENT, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                fillView();
            }
        });
    }

    private void fillUserData() {
        UserTO user = SessionManager.getUser().get();
        tbxLoginName.setText(user.getLoginName());
        tbxProfileName.setText(user.getProfileName());
        tbxSessionId.setText(user.getPhpsessionid());
        tbxEmail.setText(user.getNotificationEmail());

        Optional<Comboitem> optItem = cbxAlgorithm.getItems().stream().filter(item -> item.getValue() == user.getAlgorithmPk()).findFirst();
        if (optItem.isPresent()) {
            cbxAlgorithm.setSelectedItem(optItem.get());
        }

        GUIHelper.fillBooleanCombobox(cbxSkipWishlist, user.getSkipWishlist());
        GUIHelper.fillBooleanCombobox(cbxSkipSub, user.getSkipSub());
    }

    public void onClick$btnUpdate() {
        boolean nameChanged = false;
        if (tbxLoginName.getText().isEmpty()) {
            NotificationManager.showWarning("Enter a login name", tbxLoginName, NotificationManager.RIGHT);
            return;
        }
        UserTO user = SessionManager.getUser().get();

        if (!user.getLoginName().equals(tbxLoginName.getText())) {
            nameChanged = true;
        }

        user.setLoginName(tbxLoginName.getText());
        user.setNotificationEmail(tbxEmail.getText());
        user.setPhpsessionid(tbxSessionId.getText());
        user.setSkipWishlist(cbxSkipWishlist.getSelectedItem().getValue());
        user.setSkipSub(cbxSkipSub.getSelectedItem().getValue());
        user.setAlgorithmPk(cbxAlgorithm.getSelectedItem().getValue());

        if (!tbxPassword.getText().isEmpty()) {
            user.setPassword(DigestUtils.sha1Hex(tbxPassword.getText()));
        }

        if (new UserDAO().update(user)) {
            SessionManager.setUser(user);
            if (nameChanged) {
                SessionManager.setNotification("User successful edited.");
                Executions.getCurrent().sendRedirect("/");
            } else {
                NotificationManager.showInfo("User successful edited.", null, NotificationManager.CENTER);
            }
        } else {
            NotificationManager.showWarning("Error saving user", null, NotificationManager.CENTER);
        }
    }

    private void fillStatisticContent() {
        SimpleXYModel xyModel = new SimpleXYModel();

        ZHighCharts zHighCharts = new ZHighCharts();
        zHighCharts.setTitle("Contribute Level Progress");
        zHighCharts.setWidth("1000px");
        zHighCharts.setType("line");

        zHighCharts.setTooltipFormatter("function formatTooltip(obj){return Highcharts.dateFormat('%e. %B %Y %H:%M', new Date(obj.x)) +'<br/>Contribute level: <b>' +obj.y +'</b>'}");
        zHighCharts.setExporting("{enabled: false}");
        zHighCharts.setyAxisOptions("{min: 0}");
        zHighCharts.setxAxisOptions("{type:'datetime', labels: { rotation: -90, y:25 }}");
        zHighCharts.setLegend("{enabled:false}");
        zHighCharts.setModel(xyModel);

        UserAssetDAO userAssetDAO = new UserAssetDAO();
        TreeMap<Long, Double> map = userAssetDAO.getUserLevelChanges(SessionManager.getUser().get().getPk());

        Double lastValue = null;
        for (Entry<Long, Double> entry : map.entrySet()) {
            xyModel.addValue("Contribute Level", entry.getKey(), entry.getValue());
            lastValue = entry.getValue();
        }
        if (lastValue != null) {
            xyModel.addValue("Contribute Level", (new Date()).getTime(), lastValue);
        }

        boxStatistics.getChildren().clear();
        boxStatistics.appendChild(zHighCharts);
    }

    private void fillLevelGroupedGiveaways() {
        SimpleXYModel xyModel = new SimpleXYModel();

        ZHighCharts zHighCharts = new ZHighCharts();
        zHighCharts.setTitle("Won Giveaways For Level Requirement");
        zHighCharts.setWidth("1000px");
        zHighCharts.setType("column");

        zHighCharts.setTooltipFormatter("function formatTooltip(obj){return 'won giveaways: <b>'+obj.y+'</b>'}");
        zHighCharts.setExporting("{enabled: false}");
        zHighCharts.setLegend("{enabled:false}");
        zHighCharts.setModel(xyModel);

        Optional<Map<Integer, Integer>> map = new WonGiveawayDAO().getLevelGroupedGiveaways(SessionManager.getUser().get().getPk());
        if (map.isPresent()) {
            for (Entry<Integer, Integer> entry : map.get().entrySet()) {
                xyModel.addValue("level-count", entry.getKey(), entry.getValue());
            }
        }

        boxLevelGroupedGiveaways.getChildren().clear();
        boxLevelGroupedGiveaways.appendChild(zHighCharts);
    }
}
