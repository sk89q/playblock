package com.skcraft.playblock;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PlayBlockCreativeTab extends CreativeTabs {

    public static final PlayBlockCreativeTab tab = new PlayBlockCreativeTab();

    public PlayBlockCreativeTab() {
        super("tabPlayBlock");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getTabIconItem() {
        return SharedRuntime.itemRemote;
    }

}
