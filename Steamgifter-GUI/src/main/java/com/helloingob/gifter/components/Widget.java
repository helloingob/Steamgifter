package com.helloingob.gifter.components;

import java.util.Optional;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menupopup;

import com.helloingob.gifter.utilities.MenuCreator;

public class Widget extends Div {
    private static final long serialVersionUID = 1L;

    public Widget() {
        super();
        this.setClass("widget-box");
    }

    public void setContent(String title, String text) {
        Label label = new Label(title + System.getProperty("line.separator") + text);
        label.setMultiline(true);
        this.appendChild(label);
    }

    public void addMenupopup(String steamLink, String giveawayLink, String title) {
        this.addEventListener(Events.ON_RIGHT_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                Menupopup menupopup = MenuCreator.createMenupopup(Optional.ofNullable(steamLink), giveawayLink, title, null);
                menupopup.setPage(event.getTarget().getPage());
                menupopup.open(event.getTarget());
            }
        });
    }
}
