
package util;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public final class ImageAssets {

    private static final Map<String, Image> imageCache   = new HashMap<>();
    private static final Map<String, Image> scaledCache  = new HashMap<>();
    private static final Map<String, String> ALIAS       = new HashMap<>();

    static {

        ALIAS.put("grid",     "grid_background.png");
        ALIAS.put("mainMenu", "mainMenuImage.png");
        ALIAS.put("info",     "information.png");
        ALIAS.put("blue",     "blue.png");
        ALIAS.put("tipBar",   "tip_bar.png");



        ALIAS.put("Pmessage1", "Pmessage1.png");
        ALIAS.put("Pmessage2", "Pmessage2.png");
        ALIAS.put("Pmessage3", "Pmessage3.png");


        ALIAS.put("Pprotected", "Pprotected.png");
        ALIAS.put("Pconf4",     "Pconf4.png");
        ALIAS.put("Pconf6",     "Pconf6.png");

        ALIAS.put("Pheavy8",    "Pheavy8.png");
        ALIAS.put("Pheavy10",   "Pheavy10.png");


        ALIAS.put("packet_message1", "Pmessage2.png");
        ALIAS.put("packet_message2", "Pmessage3.png");
        ALIAS.put("packet_message3", "Pmessage1.png");
        ALIAS.put("packet_protected","Pprotected.png");
        ALIAS.put("packet_conf1",    "Pconf4.png");
        ALIAS.put("packet_conf2",    "Pconf6.png");
        ALIAS.put("packet_heavy1",   "Pheavy8.png");
        ALIAS.put("packet_heavy2",   "Pheavy10.png");


        ALIAS.put("antivirus", "antivirus.png");
        ALIAS.put("distribute","distribute.png");
        ALIAS.put("saboteur",  "saboteur.png");

        ALIAS.put("marge",     "marge.png");
        ALIAS.put("spy",       "spy.png");
        ALIAS.put("vpn",       "vpn.png");
    }

    private ImageAssets() {}


    public static Image get(String filename) {
        if (filename == null) return null;
        Image img = imageCache.get(filename);
        if (img != null) return img;

        URL url = ImageAssets.class.getResource("/" + filename);
        if (url == null) {
            System.err.println("تصویر پیدا نشد: " + filename);
            return null;
        }
        ImageIcon icon = new ImageIcon(url);
        img = icon.getImage();
        imageCache.put(filename, img);
        return img;
    }

    public static Image getAlias(String key) {
        String filename = ALIAS.get(key);
        if (filename == null) {
            System.err.println("کلید ناشناخته: " + key);
            return null;
        }
        return get(filename);
    }

    public static Image getScaled(String filename, int width, int height) {
        if (filename == null) return null;
        String k = filename + "@" + width + "x" + height;
        Image img = scaledCache.get(k);
        if (img != null) return img;

        Image base = get(filename);
        if (base == null) return null;

        Image scaled = base.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        scaledCache.put(k, scaled);
        return scaled;
    }

    public static Image getScaledAlias(String key, int width, int height) {
        String filename = ALIAS.get(key);
        if (filename == null) {
            System.err.println("کلید ناشناخته: " + key);
            return null;
        }
        return getScaled(filename, width, height);
    }


    public static void preloadAll() {


        get(ALIAS.get("grid"));
        get(ALIAS.get("mainMenu"));
        get(ALIAS.get("info"));
        get(ALIAS.get("blue"));
        get(ALIAS.get("tipBar"));

        get(ALIAS.get("Pmessage1"));
        get(ALIAS.get("Pmessage2"));
        get(ALIAS.get("Pmessage3"));
        get(ALIAS.get("Pprotected"));
        get(ALIAS.get("Pconf4"));
        get(ALIAS.get("Pconf6"));
        get(ALIAS.get("Pheavy8"));
        get(ALIAS.get("Pheavy10"));

        get(ALIAS.get("antivirus"));
        get(ALIAS.get("distribute"));
        get(ALIAS.get("saboteur"));
        get(ALIAS.get("merge"));
        get(ALIAS.get("spy"));
        get(ALIAS.get("vpn"));
    }
}
