package com.helloingob.gifter.components;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Bandbox;

public class Searchbox extends Bandbox {
    private static final long serialVersionUID = 3615543709458733480L;

    public Searchbox() {
        super();
        init();
    }

    private void init() {
        this.setInstant(true);
        this.setPlaceholder("Search ...");
    }

    public void addSearchButtonClickListener(EventListener<Event> onSearchClick) {
        this.addEventListener(Events.ON_OPEN, onSearchClick);
        this.addEventListener(Events.ON_OK, onSearchClick);
    }
}
