package com.helloingob.gifter.components.comparators;

import java.util.Comparator;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;

public class PriceLabelComparator implements Comparator<Object> {
    private boolean asc = true;
    private int columnIndex;

    public PriceLabelComparator(boolean asc, int columnIndex) {
        this.asc = asc;
        this.columnIndex = columnIndex;
    }

    @Override
    public int compare(Object object1, Object object2) {
        String string1 = ((Label) ((Row) object1).getChildren().get(columnIndex)).getValue().replace("€", "").replace(",", ".");
        String string2 = ((Label) ((Row) object2).getChildren().get(columnIndex)).getValue().replace("€", "").replace(",", ".");

        Double double1;
        if (string1.equals("-")) {
            double1 = -1.0;
        } else {
            double1 = Double.valueOf(string1);
        }

        Double double2;
        if (string2.equals("-")) {
            double2 = -1.0;
        } else {
            double2 = Double.valueOf(string2);
        }

        return double1.compareTo(double2) * (asc ? 1 : -1);
    }
}
