package com.helloingob.gifter.components;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;

import com.helloingob.gifter.ErrorLogHandler;
import com.helloingob.gifter.utilities.GUISettings;
import com.helloingob.gifter.utilities.SharedSettings;

public class DateLabelComparator implements Comparator<Object> {
    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);
    private boolean asc = true;
    private int columnIndex;

    public DateLabelComparator(boolean asc, int columnIndex) {
        this.asc = asc;
        this.columnIndex = columnIndex;
    }

    @Override
    public int compare(Object object1, Object object2) {
        String string1 = ((Label) ((Row) object1).getChildren().get(columnIndex)).getValue();
        String string2 = ((Label) ((Row) object2).getChildren().get(columnIndex)).getValue();

        Date date1 = null;
        Date date2 = null;
        try {
            date1 = GUISettings.SIMPLE_DATEFORMAT.parse(string1);
            date2 = GUISettings.SIMPLE_DATEFORMAT.parse(string2);
        } catch (ParseException e) {
            new ErrorLogHandler().error(e, logger);
        }

        return date1.compareTo(date2) * (asc ? 1 : -1);
    }
}
