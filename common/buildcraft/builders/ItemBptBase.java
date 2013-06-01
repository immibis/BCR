/** 
 * Copyright (c) SpaceToad, 2011-2012
 * http://www.mod-buildcraft.com
 * 
 * BuildCraft is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.builders;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import buildcraft.BuildCraftBuilders;
import buildcraft.core.blueprints.BptBase;

public abstract class ItemBptBase extends Item {

	public ItemBptBase(int i) {
		super(i);

		maxStackSize = 1;
		this.setCreativeTab(CreativeTabs.tabMisc);
	}

	@SuppressWarnings({ "all" })
	@Override
	@SideOnly(Side.CLIENT)
	public abstract Icon getIconFromDamage(int i);
	
	@SuppressWarnings({ "all" })
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer ply, List list, boolean extinfo) {
		BptBase bpt = BuildCraftBuilders.getBptRootIndex().getBluePrint(itemstack.getItemDamage());
		if (bpt != null) {
			list.add(bpt.getName());
		}
	}

	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {}

}
