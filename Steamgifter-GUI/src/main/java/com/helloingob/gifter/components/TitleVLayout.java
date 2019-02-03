package com.helloingob.gifter.components;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vlayout;

public class TitleVLayout extends Vlayout implements Comparable<TitleVLayout> {
    private static final long serialVersionUID = 1L;
    private Label lblTitle = new Label();
    private FontImage imgKey = new FontImage();
    private Label lblKey = new Label();
    private Label lblLink = new Label();

    public TitleVLayout() {
        super();
        this.appendChild(lblTitle);

        imgKey.setIconSclass("z-icon-key");
        imgKey.setSize(10);
        lblKey.setStyle("font-size: 10px");
    }

    public void addTitle(String title) {
        lblTitle.setValue(title);
    }

    public void addSteamKey(String steamKey) {
        lblKey.setValue(steamKey);
        Hlayout hlayout = new Hlayout();
        hlayout.appendChild(imgKey);
        hlayout.appendChild(lblKey);
        this.appendChild(hlayout);
    }

    public void addLink(String steamKey) {
        lblLink.setValue(steamKey);
        lblLink.setStyle("color: #314D60; text-decoration: underline; cursor: pointer; font-size: 10px;");

        lblLink.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                Executions.getCurrent().sendRedirect(steamKey, "_blank");
            }
        });

        Hlayout hlayout = new Hlayout();
        hlayout.appendChild(imgKey);
        hlayout.appendChild(lblLink);
        this.appendChild(hlayout);
    }

    @Override
    public int compareTo(TitleVLayout layout) {
        return lblTitle.getValue().compareTo(layout.lblTitle.getValue());
    }
}