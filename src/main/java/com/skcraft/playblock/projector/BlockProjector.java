package com.skcraft.playblock.projector;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.skcraft.playblock.GuiHandler;
import com.skcraft.playblock.PlayBlock;
import com.skcraft.playblock.PlayBlockCreativeTab;

/**
 * The projector block.
 */
public class BlockProjector extends Block implements ITileEntityProvider {

    public static final String INTERNAL_NAME = "playblock.projector";

    public BlockProjector() {
        super(Material.IRON);
        setHardness(0.5F);
        setLightLevel(1.0F);
        setCreativeTab(PlayBlockCreativeTab.tab);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        // set direction
        //int p = MathHelper.floor_double(Math.abs(((180 + placer.rotationYaw) % 360) / 360) * 4 + 0.5);
        //world.setBlockState(new BlockPos(pos), state., 2);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        // Be sure rather than crash the world
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null || !(tileEntity instanceof TileEntityProjector) || playerIn.isSneaking()) {
            return false;
        }

        TileEntityProjector projector = (TileEntityProjector) tileEntity;

        // Show the GUI if it's the client
        playerIn.openGui(PlayBlock.instance, GuiHandler.PROJECTOR, world, pos.getX(), pos.getY(), pos.getZ());

        if (!world.isRemote) {
            projector.getAccessList().allow(playerIn);
        }

        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityProjector();
    }
}
