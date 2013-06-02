/** 
 * Copyright (c) SpaceToad, 2011
 * http://www.mod-buildcraft.com
 * 
 * BuildCraft is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.energy;

import net.minecraft.item.ItemStack;
import buildcraft.core.DefaultProps;
import buildcraft.core.ItemBlockBuildCraft;

public class ItemEngine extends ItemBlockBuildCraft {

	public ItemEngine(int i) {
		super(i, "engine");
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

	@Override
	@SuppressWarnings({ "all" })
	public String getUnlocalizedName(ItemStack itemstack) {
		switch(itemstack.getItemDamage()) {
		case 0: return "tile." + DefaultProps.ICON_PREFIX + "engine.redstone";
		case 1: return "tile." + DefaultProps.ICON_PREFIX + "engine.steam";
		case 2: return "tile." + DefaultProps.ICON_PREFIX + "engine.combustion";
		default: return "";
		}
	}
}
