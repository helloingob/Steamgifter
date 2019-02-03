package com.helloingob.gifter.utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;

public class MenuCreator {

    public static Menupopup createMenupopup(Optional<String> steamLink, String giveawayLink, String title, Integer giveawayPk) {
        Menupopup menupopup = new Menupopup();
        if (steamLink.isPresent()) {
            menupopup.appendChild(createSteamMenuitem(steamLink.get()));
        }

        if (giveawayLink != null) {
            menupopup.appendChild(createGiveawayMenuitem(giveawayLink));
        }
        menupopup.appendChild(createYoutubeMenuitem(title));

        if (giveawayPk != null) {
            menupopup.appendChild(createDetailMenuitem(giveawayPk));
        }

        return menupopup;
    }

    public static Menuitem createSteamMenuitem(String steamLink) {
        Menuitem menuitem = new Menuitem("Show in Steam Store");
        menuitem.setIconSclass("z-icon-steam-square fa-lg");
        menuitem.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                Executions.getCurrent().sendRedirect(steamLink, "_blank");
            }
        });
        return menuitem;
    }

    public static Menuitem createYoutubeMenuitem(String title) {
        Menuitem menuitem = new Menuitem("Search Gameplay Video");
        menuitem.setIconSclass("z-icon-youtube-square fa-lg");
        menuitem.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                Executions.getCurrent().sendRedirect("https://www.youtube.com/results?search_query=" + title + " gameplay", "_blank");
            }
        });
        return menuitem;
    }

    public static Menuitem createGiveawayMenuitem(String giveawayLink) {
        Menuitem menuitem = new Menuitem("Show in Steamgifts.com");
        menuitem.setIconSclass("z-icon-gift fa-lg");
        menuitem.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                Executions.getCurrent().sendRedirect(giveawayLink, "_blank");
            }
        });
        return menuitem;
    }

    private static Component createDetailMenuitem(Integer giveawayPk) {
        Menuitem menuitem = new Menuitem("Giveaway Details");
        menuitem.setIconSclass("z-icon-info-circle fa-lg");
        menuitem.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("pk", giveawayPk);
                Executions.createComponents("giveaway_detail.zul", null, map);
            }
        });
        return menuitem;
    }

    public static Menuitem createLoginMenuitem() {
        Menuitem menuitem = new Menuitem("Login");
        menuitem.setIconSclass("z-icon-sign-in fa-lg");
        return menuitem;
    }

    public static Menuitem createLogoutMenuitem() {
        Menuitem menuitem = new Menuitem("Logout");
        menuitem.setIconSclass("z-icon-sign-out fa-lg");
        return menuitem;
    }
}
