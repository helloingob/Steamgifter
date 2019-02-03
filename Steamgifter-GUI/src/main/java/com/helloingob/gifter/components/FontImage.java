package com.helloingob.gifter.components;

import org.zkoss.zul.Span;

public class FontImage extends Span implements Comparable<Object> {
    private static final long serialVersionUID = 1L;
    private String iconSclass = ""; // to compare images
    private String colorStyle = "";
    private String sizeStyle = "";
    private String cursorStyle = "";
    private String customStyle = "";

    public FontImage() {
        super();
    }

    public void setIconSclass(String iconSclass) {
        this.iconSclass = iconSclass;
        this.setSclass(iconSclass);
    }

    public String getIconSclass() {
        return iconSclass;
    }

    public void setColor(String hexColorCode) {
        if (!hexColorCode.startsWith("#")) {
            hexColorCode = "#" + hexColorCode;
        }
        colorStyle = "color: " + hexColorCode + ";";
        refreshStyle();
    }

    public void setSize(int size) {
        sizeStyle = "font-size: " + size + "px;";
        refreshStyle();
    }

    public void setCursor(String string) {
        cursorStyle = "cursor: " + string + ";";
        refreshStyle();
    }

    private void refreshStyle() {
        this.setStyle(colorStyle + sizeStyle + cursorStyle + customStyle);
    }

    @Override
    public int compareTo(Object object) {
        if (object instanceof FontImage) {
            return this.iconSclass.compareTo(((FontImage) object).iconSclass);
        } else if (object instanceof ComparableImage) {
            return this.iconSclass.compareTo(((ComparableImage) object).getSrc());
        } else {
            return this.compareTo(object);
        }
    }

    public void setCustomStyle(String customStyle) {
        this.customStyle = customStyle;
        refreshStyle();
    }
}
