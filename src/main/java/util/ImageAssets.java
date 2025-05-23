package util;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ImageAssets {
    private static final Map<String, Image> imageCache = new HashMap<>();

//    public static Image get(String name) {
//        if (!imageCache.containsKey(name)) {
////            ImageIcon icon = new ImageIcon(ImageAssets.class.getResource("/assets/" + name));
////            imageCache.put(name, icon.getImage());
//        }
//        return imageCache.get(name);
//    }
public static Image get(String name) {
    if (!imageCache.containsKey(name)) {
        var url = ImageAssets.class.getResource("/" + name);
        if (url == null) {
            System.err.println(" تصویر پیدا نشد: " + name);
            return null;
        }
        ImageIcon icon = new ImageIcon(url);
        imageCache.put(name, icon.getImage());
    }
    return imageCache.get(name);
}

    public static void preloadAll() {
        get("grid_background.png");
       // get("tip_bar.png");
    }
}
