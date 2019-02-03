package com.helloingob.gifter;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;

import com.helloingob.gifter.dao.WonGiveawayDAO;
import com.helloingob.gifter.to.WonGiveawayTO;
import com.helloingob.gifter.utilities.GUISettings;
import com.helloingob.gifter.utilities.ImageCreator;

public class GiveawayDetailController extends GenericForwardComposer<Component> {
    private static final long serialVersionUID = 1L;
    private Label lblTitle;
    private Div imgContainer;
    private Rows rows;

    private final static int MAX_FILE_LENGTH = 20;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        init();
    }

    private void init() {
        WonGiveawayTO wonGiveaway = new WonGiveawayDAO().get((Integer) arg.get("pk"));
        String title = wonGiveaway.getTitle();
        if (wonGiveaway.getTitle().length() >= MAX_FILE_LENGTH) {
            title = wonGiveaway.getTitle().substring(0, MAX_FILE_LENGTH) + "...";
        }
        lblTitle.setValue(title);
        imgContainer.appendChild(ImageCreator.createGiveawayImage(wonGiveaway.getImageLink()));

        Row rowDate = new Row();
        rowDate.appendChild(new Label("Date"));
        rowDate.appendChild(new Label(GUISettings.SIMPLE_DATEFORMAT.format(wonGiveaway.getEndDate())));
        rows.appendChild(rowDate);

        Row rowWinChance = new Row();
        rowWinChance.appendChild(new Label("Winchance"));
        rowWinChance.appendChild(new Label(GUISettings.WINCHANCE_FORMAT.format(wonGiveaway.getWinChance())));
        rows.appendChild(rowWinChance);

        if (wonGiveaway.getSteamStorePrice().isPresent()) {
            Row rowPrice = new Row();
            rowPrice.appendChild(new Label("Price"));
            rowPrice.appendChild(new Label(GUISettings.PRICE_FORMAT.format(wonGiveaway.getSteamStorePrice().get())));
            rows.appendChild(rowPrice);
        }

        Row rowLevel = new Row();
        rowLevel.appendChild(new Label("Level"));
        rowLevel.appendChild(new Label(wonGiveaway.getLevelRequirement().toString()));
        rows.appendChild(rowLevel);
    }
}