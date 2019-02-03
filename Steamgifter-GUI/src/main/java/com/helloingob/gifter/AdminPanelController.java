package com.helloingob.gifter;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.helloingob.gifter.ErrorLogHandler;
import com.helloingob.gifter.components.ComparableImage;
import com.helloingob.gifter.components.FontImage;
import com.helloingob.gifter.components.Searchbox;
import com.helloingob.gifter.dao.AlgorithmDAO;
import com.helloingob.gifter.dao.CachedGameDAO;
import com.helloingob.gifter.dao.ErrorLogDAO;
import com.helloingob.gifter.dao.UserDAO;
import com.helloingob.gifter.to.CachedGameTO;
import com.helloingob.gifter.to.ErrorLogTO;
import com.helloingob.gifter.to.UserTO;
import com.helloingob.gifter.utilities.GUIHelper;
import com.helloingob.gifter.utilities.GUISettings;
import com.helloingob.gifter.utilities.SharedSettings;
import com.helloingob.gifter.utilities.manager.NotificationManager;
import com.helloingob.gifter.utilities.manager.SessionManager;

public class AdminPanelController extends GenericForwardComposer<Component> {
    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);
    private static final long serialVersionUID = 1L;

    private Listbox lbxUsers;
    private Grid grdErrorLog;
    private Grid grdCachedGames;
    private Grid grdServerLog;
    private Grid grdTomcatLog;

    private Tab tabCachedGames;
    private Tab tabErrorLog;

    private final static String SERVER_INFO_LOG_FILEPATH = "/var/log/steamgifter-server-info.log";
    private final static String TOMCAT_LOG_FILEPATH = "/opt/tomcat/logs/catalina.out";

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        init();
    }

    private void init() {
        initStartListener();
        if (SessionManager.getUser().isPresent() && SessionManager.getUser().get().getIsAdmin()) {
            fillView();
        }
        initServerLogSearch();
        tabCachedGames.setContext(generateCachedGameMenupopup());
        tabErrorLog.setContext(generateErrorLogMenupopup());

        initPaging(grdErrorLog);
        initPaging(grdCachedGames);
        initPaging(grdServerLog);
        initPaging(grdTomcatLog);
    }

    private void initServerLogSearch() {
        Searchbox searchbox = new Searchbox();
        searchbox.setHflex("true");
        grdServerLog.getParent().insertBefore(searchbox, grdServerLog);
        searchbox.addSearchButtonClickListener(getSearchEventListener(searchbox));
    }

    private EventListener<Event> getSearchEventListener(Searchbox searchbox) {
        return new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                for (Component component : grdServerLog.getRows().getChildren()) {
                    Row row = (Row) component;
                    row.setVisible(false);

                    Label label = (Label) row.getChildren().get(0);
                    if (label.getValue().toLowerCase().contains(searchbox.getText().toLowerCase()) || searchbox.getText().isEmpty()) {
                        row.setVisible(true);
                    }
                }
            }
        };
    }

    private void initPaging(Grid grid) {
        grid.setMold("paging");
        grid.setAutopaging(true);
        grid.getPaginal().setAutohide(false);
    }

    private void fillView() {
        fillUserGrid();
        fillErrorGrid();
        fillCachedGamesGrid();
        fillGridFromFile(SERVER_INFO_LOG_FILEPATH, grdServerLog);
        fillGridFromFile(TOMCAT_LOG_FILEPATH, grdTomcatLog);
    }

    private void fillGridFromFile(String filename, Grid grid) {
        grid.getRows().getChildren().clear();
        grid.setSizedByContent(true);
        try {
            Files.lines(Paths.get(filename)).sorted((x, y) -> -1).forEach(line -> {
                Row row = new Row();
                row.appendChild(new Label(line));
                grid.getRows().appendChild(row);
            });
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        }
        grid.invalidate();
    }

    private void fillCachedGamesGrid() {
        grdCachedGames.getRows().getChildren().clear();

        for (CachedGameTO cachedGame : new CachedGameDAO().getList()) {
            Row row = new Row();
            Label lblLink = new Label("http://store.steampowered.com/app/" + cachedGame.getAppId());
            lblLink.setStyle("color: #314D60; text-decoration: underline; cursor: pointer;");

            lblLink.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    Executions.getCurrent().sendRedirect(lblLink.getValue(), "_blank");
                }
            });
            row.appendChild(lblLink);

            row.appendChild(new Label(GUIHelper.convertBoolean(cachedGame.getIsDlc())));
            row.appendChild(new Label(GUISettings.SIMPLE_DATEFORMAT.format(cachedGame.getDate())));

            grdCachedGames.getRows().appendChild(row);
        }
    }

    private void fillErrorGrid() {
        grdErrorLog.getRows().getChildren().clear();

        UserDAO userDao = new UserDAO();
        ErrorLogDAO errorLogDao = new ErrorLogDAO();

        for (ErrorLogTO errorLog : errorLogDao.get()) {
            Row row = new Row();
            row.appendChild(new Label(errorLog.getMessage().split("\\r?\\n")[0]));
            row.appendChild(new Label(GUISettings.SIMPLE_DATEFORMAT.format(errorLog.getDate())));

            if (errorLog.getUserPk().isPresent()) {
                row.appendChild(new Label(userDao.get(errorLog.getUserPk().get()).getLoginName()));
            } else {
                row.appendChild(new Label());
            }

            FontImage imgShowValue = new FontImage();
            imgShowValue.setIconSclass("z-icon-ellipsis-h fa-2x");
            imgShowValue.setCursor("pointer");
            imgShowValue.addEventListener(Events.ON_CLICK, createShowValueEventListener(row));

            row.setContext(generateErrorLogEntryMenupopup(errorLogDao, errorLog));

            row.appendChild(imgShowValue);
            row.setValue(errorLog);

            grdErrorLog.getRows().appendChild(row);
        }
    }

    private Menupopup generateErrorLogEntryMenupopup(ErrorLogDAO errorLogDao, ErrorLogTO errorLog) {
        Menupopup menupopup = new Menupopup();
        Menuitem menuitem = new Menuitem("Delete");
        menuitem.setIconSclass("z-icon-trash-o");
        menuitem.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                errorLogDao.delete(errorLog);
                fillErrorGrid();
            }
        });
        menupopup.appendChild(menuitem);
        menupopup.setPage(getPage());

        return menupopup;
    }

    private Menupopup generateErrorLogMenupopup() {
        Menupopup menupopup = new Menupopup();
        Menuitem menuitem = new Menuitem("Truncate log entries");
        menuitem.setIconSclass("z-icon-times");
        menuitem.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (new ErrorLogDAO().delete()) {
                    NotificationManager.showInfo("Successfully truncated log.", null, NotificationManager.CENTER);
                    fillErrorGrid();
                }
            }
        });
        menupopup.appendChild(menuitem);
        menupopup.setPage(getPage());

        return menupopup;
    }

    private Menupopup generateCachedGameMenupopup() {
        Menupopup menupopup = new Menupopup();
        Menuitem menuitem = new Menuitem("Delete duplicate appIDs");
        menuitem.setIconSclass("z-icon-recycle");
        menuitem.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                Integer count = new CachedGameDAO().deleteDuplicates();
                if (count != null && count > 0) {
                    NotificationManager.showInfo("Successfully deleted " + count + "x appID(s).", null, NotificationManager.CENTER);
                    fillCachedGamesGrid();
                } else {
                    NotificationManager.showInfo("No duplicate appID found!", null, NotificationManager.CENTER);
                }
            }
        });
        menupopup.appendChild(menuitem);
        menupopup.setPage(getPage());

        return menupopup;
    }

    private EventListener<Event> createShowValueEventListener(Row row) {
        return new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                Window win = new Window();
                Map<String, ErrorLogTO> data = new HashMap<>();
                data.put("errorLog", row.getValue());
                win = (Window) Executions.createComponents("simple_error_log.zul", null, data);
                win.setPosition("center");
                win.setClosable(true);
                win.setBorder("normal");
                win.setMaximizable(false);
                win.setSizable(false);
                win.doModal();
            }
        };
    }

    private void fillUserGrid() {
        lbxUsers.getItems().clear();
        for (UserTO user : new UserDAO().get()) {
            Listitem listitem = new Listitem();

            listitem.appendChild(new Listcell());

            Listcell lclImgUser = new Listcell();
            ComparableImage imgUser = GUIHelper.createImage(user.getImageLink(), user.getLoginName());
            imgUser.setWidth("50px");
            lclImgUser.appendChild(imgUser);
            listitem.appendChild(lclImgUser);

            listitem.appendChild(new Listcell(user.getLoginName()));
            listitem.appendChild(new Listcell(new AlgorithmDAO().get(user.getAlgorithmPk()).getName()));
            listitem.appendChild(new Listcell(GUISettings.SIMPLE_DATEFORMAT.format(user.getCreatedDate())));

            String lastSynced;
            if (user.getLastSyncedDate().isPresent()) {
                lastSynced = GUISettings.SIMPLE_DATEFORMAT.format(user.getLastSyncedDate().get());
            } else {
                lastSynced = "never";
            }
            listitem.appendChild(new Listcell(lastSynced));

            String lastLogin;
            if (user.getLastLogin().isPresent()) {
                lastLogin = GUISettings.SIMPLE_DATEFORMAT.format(user.getLastLogin().get());
            } else {
                lastLogin = "never";
            }
            listitem.appendChild(new Listcell(lastLogin));

            Listcell lcImageIsAdmin = new Listcell();
            lcImageIsAdmin.appendChild(createStatusImage(user.getIsAdmin()));
            listitem.appendChild(lcImageIsAdmin);

            Listcell lcImageIsActive = new Listcell();
            lcImageIsActive.appendChild(createStatusImage(user.getIsActive()));
            listitem.appendChild(lcImageIsActive);

            listitem.setValue(user);

            lbxUsers.appendChild(listitem);
        }
    }

    private FontImage createStatusImage(boolean status) {
        FontImage fontImage = new FontImage();
        if (status) {
            fontImage.setIconSclass("z-icon-check");
        } else {
            fontImage.setIconSclass("z-icon-times");
        }
        return fontImage;
    }

    private void initStartListener() {
        this.self.addEventListener(GUISettings.SHOW_ADMINPANEL_EVENT, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                fillView();
            }
        });
    }

    public void onClick$btnEditUser() {
        if (lbxUsers.getSelectedItem() == null) {
            NotificationManager.showWarning("Select a user to edit", lbxUsers, NotificationManager.BEFORE_CENTER);
        } else {
            Window win = new Window();
            Map<String, UserTO> data = new HashMap<>();
            data.put("user", lbxUsers.getSelectedItem().getValue());
            win = (Window) Executions.createComponents("user_form.zul", null, data);
            win.setPosition("center");
            win.setTitle("Edit User");
            win.setClosable(true);
            win.setBorder("normal");
            win.setMaximizable(false);
            win.setSizable(false);
            win.doModal();

            win.addEventListener(GUISettings.USER_RELOAD_EVENT, createReloadUserListEventListener());
        }
    }

    public void onClick$btnAddUser() {
        Window win = new Window();
        win = (Window) Executions.createComponents("user_form.zul", null, null);
        win.setPosition("center");
        win.setTitle("Add User");
        win.setClosable(true);
        win.setBorder("normal");
        win.setMaximizable(false);
        win.setSizable(false);
        win.doModal();

        win.addEventListener(GUISettings.USER_RELOAD_EVENT, createReloadUserListEventListener());
    }

    public void onClick$btnDeleteUser() {
        if (lbxUsers.getSelectedItem() == null) {
            NotificationManager.showWarning("Select a user to delete", lbxUsers, NotificationManager.BEFORE_CENTER);
        } else {
            UserTO user = lbxUsers.getSelectedItem().getValue();
            Messagebox.show("Delete " + user.getLoginName() + "?", "Caution", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws InterruptedException {
                    if (event.getName().equals("onYes")) {
                        deleteUser(user);
                    }
                }
            });
        }
    }

    protected void deleteUser(UserTO user) {
        if (new UserDAO().delete(user)) {
            fillUserGrid();
            NotificationManager.showInfo("User deleted", null, NotificationManager.CENTER);
        } else {
            NotificationManager.showWarning("Error deleting user", null, NotificationManager.CENTER);
        }
    }

    private EventListener<Event> createReloadUserListEventListener() {
        return new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                fillUserGrid();
            }
        };
    }
}
