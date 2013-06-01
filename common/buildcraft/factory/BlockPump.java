/** 
 * Copyright (c) SpaceToad, 2011
 * http://www.mod-buildcraft.com
 * 
 * BuildCraft is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.factory;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import buildcraft.core.DefaultProps;
import buildcraft.core.utils.Utils;


public class BlockPump extends BlockContainer {

	public BlockPump(int i) {
		super(i, Material.iron);
		setHardness(5F);
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new TilePump();
	}
	
	private Icon texTop, texBottom, texSide;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister r) {
		texTop = r.registerIcon(DefaultProps.ICON_PREFIX + "pump-top");
		texBottom = r.registerIcon(DefaultProps.ICON_PREFIX + "pump-bottom");
		texSide = r.registerIcon(DefaultProps.ICON_PREFIX + "pump-side");
	}

	@Override
	public Icon getIcon(int i, int j) {
		switch (i) {
		case 0:
			return texTop;
		case 1:
			return texBottom;
		default:
			return texSide;
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		Utils.preDestroyBlock(world, x, y, z);
		super.breakBlock(world, x, y, z, par5, par6);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addCreativeItems(ArrayList itemList) {
		itemList.add(new ItemStack(this));
	}
}
