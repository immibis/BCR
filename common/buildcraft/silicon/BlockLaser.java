/**
 * Copyright (c) SpaceToad, 2011
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.silicon;

import java.util.ArrayList;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import buildcraft.api.core.Orientations;
import buildcraft.core.DefaultProps;


public class BlockLaser extends BlockContainer {

	public BlockLaser(int i) {
		super(i, Material.iron);
		setHardness(0.5F);
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	public int getRenderType() {
		return SiliconProxyClient.laserBlockModel;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean isACube() {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new TileLaser();
	}

	@Override
	public String getTextureFile() {
		return DefaultProps.TEXTURE_BLOCKS;
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int i, int j) {
		if (i == Orientations.values()[j].reverse().ordinal()) {
			return 16 * 2 + 15;
		} else if (i == j) {
			return 16 * 2 + 14;
		} else {
			return 16 * 2 + 13;
		}

	}


	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int par5,	float par6, float par7, float par8, int par9) {
		super.onBlockPlaced(world, x, y, z, par5, par6, par7, par8, par9);
		int i1 = par9;
		if (par5 <= 6) {
			i1 = par5;
		}
		return i1;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addCreativeItems(ArrayList itemList) {
		itemList.add(new ItemStack(this));
	}
}
