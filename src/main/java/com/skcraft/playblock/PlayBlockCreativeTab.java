package com.skcraft.playblock;

import com.skcraft.playblock.client.ClientRuntime;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PlayBlockCreativeTab extends CreativeTabs {

    public static final PlayBlockCreativeTab tab = new PlayBlockCreativeTab();

    public PlayBlockCreativeTab() {
        super("tabPlayBlock");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getTabIconItem() {
        return ClientRuntime.itemRemote;
    }

}
