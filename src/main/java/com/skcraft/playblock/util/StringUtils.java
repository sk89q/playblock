package com.skcraft.playblock.util;

import net.minecraft.util.text.translation.I18n;

public class StringUtils {

    public static String translate(String unlocalized, boolean prefix) {
        return I18n.translateToLocal((prefix ? "playblock." : "") + unlocalized);
    }

    public static String translate(String unlocalized) {
        return translate(unlocalized, true);
    }

}
