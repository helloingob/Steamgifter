package com.helloingob.gifter.components;

import org.zkoss.zul.Image;

public class ComparableImage extends Image implements Comparable<Object> {
    private static final long serialVersionUID = 1L;

    public ComparableImage(String portImagePath) {
        super(portImagePath);
    }

    @Override
    public int compareTo(Object object) {
        if (object instanceof ComparableImage) {
            return this.getSrc().compareTo(((ComparableImage) object).getSrc());
        } else if (object instanceof FontImage) {
            return this.getSrc().compareTo(((FontImage) object).getIconSclass());
        } else {
            return this.compareTo(object);
        }
    }
}