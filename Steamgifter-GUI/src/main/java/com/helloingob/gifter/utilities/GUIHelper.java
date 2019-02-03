package com.helloingob.gifter.utilities;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;

import com.helloingob.gifter.components.ComparableImage;

public class GUIHelper {

    public static void fillBooleanCombobox(Combobox combobox, boolean defaultStatus) {
        combobox.getItems().clear();

        Comboitem cbiTrue = new Comboitem(convertBoolean(true));
        cbiTrue.setValue(true);
        Comboitem cbiFalse = new Comboitem(convertBoolean(false));
        cbiFalse.setValue(false);

        combobox.appendChild(cbiFalse);
        combobox.appendChild(cbiTrue);

        if (defaultStatus) {
            combobox.setText(convertBoolean(defaultStatus));
        } else {
            combobox.setText(convertBoolean(defaultStatus));
        }
    }

    public static String convertBoolean(boolean state) {
        if (state) {
            return "yes";
        } else {
            return "no";
        }
    }

    public static ComparableImage createImage(String imageLink, String name) {
        if (imageLink == null || imageLink.equals(GUISettings.DEFAULT_STEAM_IMAGE)) {
            BufferedImage image = new BufferedImage(150, 150, BufferedImage.TYPE_INT_RGB);

            String backgroundPath = "/resources/anonymous.png";
            String port = (Executions.getCurrent().getServerPort() == 80) ? "" : (":" + Executions.getCurrent().getServerPort());
            String urlPath = Executions.getCurrent().getScheme() + "://" + Executions.getCurrent().getServerName() + port + Executions.getCurrent().getContextPath() + backgroundPath;

            Image background = null;
            try {
                URL url = new URL(urlPath);
                background = ImageIO.read(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Graphics2D g2d = image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawImage(background, 0, 0, 150, 150, null);

            g2d.setFont(new Font("Verdana", Font.PLAIN, 20));
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
            g2d.setColor(Color.BLACK);
            FontMetrics fm = g2d.getFontMetrics();

            int x = (150 - fm.stringWidth(name)) / 2;
            g2d.drawString(name, x, 65);
            g2d.dispose();

            ComparableImage img = new ComparableImage(null);
            img.setContent(image);
            return img;
        } else {
            return new ComparableImage(imageLink);
        }

    }

    public static String convertDateDifferenceToString(long date1, long date2) {
        final long ONEHOUR = 3600000;
        final long TWENTYFOURHOURS = ONEHOUR * 24;

        long difference = date1 - date2;
        long days = difference / TWENTYFOURHOURS;

        if (days < 1) {
            long hours = difference / ONEHOUR;
            if (hours < 1) {
                return "< 1 hour ago";
            } else {
                if (hours == 1) {
                    return "1 hour ago";
                } else {
                    return hours + " hours ago";
                }
            }
        } else {
            if (days == 1) {
                return "1 day ago";
            } else {
                if (days > 1) {
                    return days + " days ago";
                }
            }
        }
        return "";
    }
}
