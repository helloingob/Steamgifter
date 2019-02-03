package com.helloingob.gifter;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Box;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import com.helloingob.gifter.dao.UserDAO;
import com.helloingob.gifter.dao.WonGiveawayDAO;
import com.helloingob.gifter.to.UserTO;
import com.helloingob.gifter.to.WonGiveawayTO;
import com.helloingob.gifter.utilities.GUIHelper;
import com.helloingob.gifter.utilities.GUISettings;
import com.helloingob.gifter.utilities.MenuCreator;
import com.helloingob.gifter.utilities.TimeHelper;
import com.helloingob.gifter.utilities.manager.NotificationManager;
import com.helloingob.gifter.utilities.manager.SessionManager;

public class IndexController extends GenericForwardComposer<Component> {
    private static final long serialVersionUID = 1L;

    private Hlayout hlLastGiveaway;
    private Label lblLastWonGame;
    private Box boxUserImage;

    private Menupopup mnpMenu;

    private Tab tabDashboard;
    private Tab tabUserPanel;
    private Tab tabAdminPanel;
    private Tab tabWonGiveaways;

    private Vlayout vlDashBaordContent;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        init();
    }

    private void init() {
        initLastWonGame();
        vlDashBaordContent.appendChild(new DashboardContent().getTable());
        vlDashBaordContent.appendChild(new DashboardContent().getWidgets());
        updateUserSpecificComponents();
        showUserEditNotification();
        refreshLastLogin();
    }

    private void initLastWonGame() {
        Optional<WonGiveawayTO> optWonGiveaway = new WonGiveawayDAO().getLastWonGiveaway();
        if (optWonGiveaway.isPresent()) {
            WonGiveawayTO wonGiveaway = optWonGiveaway.get();
            String formattedDate = new SimpleDateFormat("dd.MM.yy").format(wonGiveaway.getEndDate());
            lblLastWonGame.setValue("Last won giveaway: " + wonGiveaway.getTitle() + " (" + formattedDate + ", " + GUIHelper.convertDateDifferenceToString(TimeHelper.getCurrentTimestamp().getTime(), wonGiveaway.getEndDate().getTime()) + ")");
            Menupopup menu = MenuCreator.createMenupopup(wonGiveaway.getSteamLink(), wonGiveaway.getGiveawayLink(), wonGiveaway.getTitle(), wonGiveaway.getPk());
            menu.setPage(getPage());
            hlLastGiveaway.addEventListener(Events.ON_RIGHT_CLICK, new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    menu.open(hlLastGiveaway);
                }
            });

            Image userImage = GUIHelper.createImage(wonGiveaway.getUser().getImageLink(), wonGiveaway.getUserName());
            userImage.setWidth("25px");
            userImage.setHeight("25px");
            userImage.setTooltiptext(wonGiveaway.getUserName());
            boxUserImage.appendChild(userImage);

        } else {
            hlLastGiveaway.setVisible(false);
        }
    }

    private void updateUserSpecificComponents() {
        updateMenu();
        toggleTabVisibility();
    }

    private void updateMenu() {
        mnpMenu.getChildren().clear();

        if (SessionManager.getUser().isPresent()) {
            Menuitem mniLogout = MenuCreator.createLogoutMenuitem();
            mniLogout.addEventListener(Events.ON_CLICK, createLogoutMenuListener());
            mnpMenu.appendChild(mniLogout);

        } else {
            Menuitem mniLogin = MenuCreator.createLoginMenuitem();
            mniLogin.addEventListener(Events.ON_CLICK, createLoginMenuListener());
            mnpMenu.appendChild(mniLogin);
        }
    }

    private EventListener<Event> createLoginMenuListener() {
        return new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                Window win = new Window();
                win = (Window) Executions.createComponents("login.zul", null, null);
                win.setPosition("center");
                win.setClosable(true);
                win.setBorder("normal");
                win.setMaximizable(false);
                win.setSizable(false);
                win.doModal();

                win.addEventListener(GUISettings.LOGIN_EVENT, new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        UserTO user = SessionManager.getUser().get();
                        NotificationManager.showInfo("Welcome, " + user.getLoginName(), tabUserPanel, NotificationManager.BEFORE_CENTER);
                        Events.postEvent(GUISettings.SHOW_USERPANEL_EVENT, tabUserPanel.getLinkedPanel().getFirstChild().getFirstChild(), null);
                        if (user.getIsAdmin()) {
                            Events.postEvent(GUISettings.SHOW_ADMINPANEL_EVENT, tabAdminPanel.getLinkedPanel().getFirstChild().getFirstChild(), null);
                        }
                        updateUserSpecificComponents();
                    }
                });
            }
        };
    }

    private void toggleTabVisibility() {
        if (SessionManager.getUser().isPresent()) {
            tabUserPanel.setVisible(true);
            if (SessionManager.getUser().get().getIsAdmin()) {
                tabAdminPanel.setVisible(true);
            } else {
                tabAdminPanel.setVisible(false);
            }
        } else {
            if (tabUserPanel.isSelected() || tabAdminPanel.isSelected()) {
                tabDashboard.setSelected(true);
            }
            tabUserPanel.setVisible(false);
            tabAdminPanel.setVisible(false);
        }
    }

    private EventListener<Event> createLogoutMenuListener() {
        return new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                SessionManager.setUser(null);
                updateUserSpecificComponents();
            }
        };
    }

    private void refreshLastLogin() {
        if (SessionManager.getUser().isPresent()) {
            UserTO user = SessionManager.getUser().get();
            user.setLastLogin(TimeHelper.getCurrentTimestamp());
            new UserDAO().updateLastLogin(user);
        }
    }

    private void showUserEditNotification() {
        if (SessionManager.getUserEditNotification().isPresent()) {
            NotificationManager.showInfo(SessionManager.getUserEditNotification().get(), tabUserPanel, NotificationManager.BEFORE_CENTER);
            tabUserPanel.setSelected(true);
            SessionManager.setNotification(null);
        }
    }

    public void onClick$tabWonGiveaways() {
        Events.postEvent(GUISettings.SHOW_WON_GIVEAWAYS_EVENT, tabWonGiveaways.getLinkedPanel().getFirstChild().getFirstChild(), null);
    }
}
