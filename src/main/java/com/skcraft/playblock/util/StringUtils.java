package com.skcraft.playblock.util;

import net.minecraft.util.StatCollector;

public class StringUtils {

    public static String translate(String unlocalized, boolean prefix) {
        return StatCollector.translateToLocal((prefix ? "playblock." : "") + unlocalized);
    }

    public static String translate(String unlocalized) {
        return translate(unlocalized, true);
    }

}
