package com.helloingob.gifter.utilities;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class GUISettings {
    public final static SimpleDateFormat SIMPLE_DATEFORMAT = new SimpleDateFormat("dd.MM.yy HH:mm");
    public final static DecimalFormat WINCHANCE_FORMAT = new DecimalFormat("00.00' %'");
    public final static DecimalFormat PRICE_FORMAT = new DecimalFormat("00.00' €'");
    public final static String LOGIN_EVENT = "onLoggedIn";
    public final static String SHOW_USERPANEL_EVENT = "onShowUserpanel";
    public final static String SHOW_ADMINPANEL_EVENT = "onShowAdminpanel";
    public final static String SHOW_WON_GIVEAWAYS_EVENT = "onShowWonGiveaways";
    public final static String USER_RELOAD_EVENT = "onReloadUserList";
    public final static int NOTIFICATION_DURATION = 3000;
    public final static String DEFAULT_PASSWORD = "123";
    public final static String DEFAULT_STEAM_IMAGE = "https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/fe/fef49e7fa7e1997310d705b2a6158ff8dc1cdfeb_full.jpg";
}