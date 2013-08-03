package com.skcraft.playblock.projector;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.skcraft.playblock.PlayBlock;

/**
 * The projector block.
 */
public class ProjectorBlock extends Block {

    public static final String INTERNAL_NAME = "playblock.ProjectorBlock";
    private Icon icon;

    public ProjectorBlock(int id, Material material) {
        super(id, material);
        setHardness(0.5F);
        setStepSound(Block.soundGlassFootstep);
        setLightValue(1.0F);
        setUnlocalizedName(ProjectorBlock.INTERNAL_NAME);
        setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z,
            EntityLiving entityLiving, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, entityLiving, stack);

        int p = MathHelper
                .floor_double(Math
                        .abs(((180 + entityLiving.rotationYaw) % 360) / 360) * 4 + 0.5);
        world.setBlockMetadataWithNotify(x, y, z, p % 4, 2);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z,
            EntityPlayer player, int side, float vx, float vy, float cz) {
        
        // Be sure rather than crash the world
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if (tileEntity == null || !(tileEntity instanceof ProjectorTileEntity)
                || player.isSneaking()) {
            return false;
        }
        
        ProjectorTileEntity projector = (ProjectorTileEntity) tileEntity;
            
        // Show the GUI if it's the client
        if (world.isRemote) {
            PlayBlock.getRuntime().showProjectorGui(player, projector);
        } else {
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
        return new ProjectorTileEntity();
    }
    
    @Override
    public void registerIcons(IconRegister iconRegister) {
        //TODO Add an actual texture...
        icon = iconRegister.registerIcon("texture_name_here");
    }
}