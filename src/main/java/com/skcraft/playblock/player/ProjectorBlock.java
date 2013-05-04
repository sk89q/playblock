package com.skcraft.playblock.player;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.skcraft.playblock.PlayBlock;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ProjectorBlock extends Block {

    public static final String INTERNAL_NAME = "playblock.ProjectorBlock";

    public ProjectorBlock(int id, int texture, Material material) {
        super(id, texture, material);
    }

    @Override
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4,
            EntityLiving par5EntityLiving) {
        super.onBlockPlacedBy(par1World, par2, par3, par4, par5EntityLiving);

        int p = MathHelper
                .floor_double(Math
                        .abs(((180 + par5EntityLiving.rotationYaw) % 360) / 360) * 4 + 0.5);
        par1World.setBlockMetadataWithNotify(par2, par3, par4, p % 4);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean onBlockActivated(World world, int x, int y, int z,
            EntityPlayer player, int side, float vx, float vy, float cz) {
        Side side1 = FMLCommonHandler.instance().getEffectiveSide();

        if (side1 == Side.CLIENT) {
            TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
            if (tileEntity == null || player.isSneaking()) {
                return false;
            }
            PlayBlock.getRuntime().showProjectorGui(player,
                    (ProjectorTileEntity) tileEntity);
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
        return new ProjectorTileEntity();
    }

}