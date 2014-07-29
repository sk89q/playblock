package com.skcraft.playblock.projector;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import com.skcraft.playblock.PlayBlock;
import com.skcraft.playblock.PlayBlockCreativeTab;
import com.skcraft.playblock.queue.ExposedQueue;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemRemote extends Item {

    public static final String INTERNAL_NAME = "playblock.remote";

    public ItemRemote() {
        setUnlocalizedName(ItemRemote.INTERNAL_NAME);
        setTextureName("playblock:remote");
        setCreativeTab(PlayBlockCreativeTab.tab);
    }

    @Override
    public boolean getShareTag() {
        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack item, World world, EntityPlayer player) {
        if (world.isRemote) {
            ExposedQueue queuable = getLinked(world, item);
            if (queuable == null) {
                player.addChatMessage(new ChatComponentText("Not linked."));
            } else {
                PlayBlock.getClientRuntime().showRemoteGui(player, queuable);
            }
        }

        return item;
    }

    @Override
    public boolean onItemUseFirst(ItemStack item, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity == null || !(tileEntity instanceof ExposedQueue)) {
            return false;
        }

        ExposedQueue queuable = (ExposedQueue) tileEntity;

        if (!item.hasTagCompound()) {
            item.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound tag = item.getTagCompound();
        item.getTagCompound().setInteger("dim", world.provider.dimensionId);
        item.getTagCompound().setInteger("x", x);
        item.getTagCompound().setInteger("y", y);
        item.getTagCompound().setInteger("z", z);

        player.addChatMessage(new ChatComponentText("Remote linked!"));

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack item, EntityPlayer player, List items, boolean showAdvanced) {
        super.addInformation(item, player, items, showAdvanced);

        NBTTagCompound tag = item.getTagCompound();

        if (tag != null && tag.hasKey("x")) {
            int x = item.getTagCompound().getInteger("x");
            int y = item.getTagCompound().getInteger("y");
            int z = item.getTagCompound().getInteger("z");

            items.add("Linked to " + x + ", " + y + ", " + z);
        } else {
            items.add("Right click a projector to link.");
        }
    }

    /**
     * Get the {@link ExposedQueue} from an instance of an item.
     * 
     * @param world
     *            the current world
     * @param item
     *            the item
     * @return the linked object, otherwise null
     */
    public static ExposedQueue getLinked(World world, ItemStack item) {
        if (!item.hasTagCompound()) {
            return null;
        }

        NBTTagCompound tag = item.getTagCompound();
        int dim = item.getTagCompound().getInteger("dim");
        int x = item.getTagCompound().getInteger("x");
        int y = item.getTagCompound().getInteger("y");
        int z = item.getTagCompound().getInteger("z");

        if (world.provider.dimensionId != dim) {
            return null;
        }

        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity == null || !(tileEntity instanceof ExposedQueue)) {
            return null;
        }

        return (ExposedQueue) tileEntity;
    }

}
