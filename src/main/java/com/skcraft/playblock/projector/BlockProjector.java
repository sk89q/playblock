package com.skcraft.playblock.projector;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.skcraft.playblock.GuiHandler;
import com.skcraft.playblock.PlayBlock;
import com.skcraft.playblock.PlayBlockCreativeTab;

/**
 * The projector block.
 */
public class BlockProjector extends Block {

    public static final String INTERNAL_NAME = "playblock.projector";

    public BlockProjector() {
        super(Material.iron);
        setHardness(0.5F);
        setStepSound(Block.soundTypeGlass);
        setLightLevel(1.0F);
        setBlockName(INTERNAL_NAME);
        setBlockTextureName("playblock:projector");
        setCreativeTab(PlayBlockCreativeTab.tab);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, entityLiving, stack);

        int p = MathHelper.floor_double(Math.abs(((180 + entityLiving.rotationYaw) % 360) / 360) * 4 + 0.5);
        world.setBlockMetadataWithNotify(x, y, z, p % 4, 2);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float vx, float vy, float cz) {

        // Be sure rather than crash the world
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity == null || !(tileEntity instanceof TileEntityProjector) || player.isSneaking()) {
            return false;
        }

        TileEntityProjector projector = (TileEntityProjector) tileEntity;

        // Show the GUI if it's the client
        player.openGui(PlayBlock.instance, GuiHandler.PROJECTOR, world, x, y, z);

        if(!world.isRemote) {
            projector.getAccessList().allow(player);
        }

        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEntityProjector();
    }

}
