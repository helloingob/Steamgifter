package com.helloingob.gifter.utilities.manager;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;

import com.helloingob.gifter.utilities.GUISettings;

public class NotificationManager {
    public static String LEFT = "start_center";
    public static String RIGHT = "end_center";
    public static String CENTER = "top_center";
    public static String BEFORE_CENTER = "before_center";

    public static void showInfo(String msg, Component component, String position) {
        showNotification(msg, Clients.NOTIFICATION_TYPE_INFO, component, position);
    }

    public static void showWarning(String msg, Component component, String position) {
        showNotification(msg, Clients.NOTIFICATION_TYPE_WARNING, component, position);
    }

    private static void showNotification(String msg, String type, Component component, String position) {
        Clients.showNotification(msg, type, component, position, GUISettings.NOTIFICATION_DURATION);
    }

}
