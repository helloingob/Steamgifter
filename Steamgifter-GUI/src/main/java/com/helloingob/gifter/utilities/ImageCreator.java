package com.helloingob.gifter.utilities;

import java.util.Optional;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Image;

import com.helloingob.gifter.components.ComparableImage;
import com.helloingob.gifter.components.FontImage;

public class ImageCreator {
    public static Component createGiveawayImage(Optional<String> imageLink) {
        if (imageLink.isPresent()) {
            Image image = new ComparableImage(imageLink.get());
            image.setClass("won-giveaway-image");
            return image;
        } else {
            FontImage emptyImage = new FontImage();
            emptyImage.setIconSclass("z-icon-picture-o fa-3x");
            return emptyImage;
        }
    }
}
