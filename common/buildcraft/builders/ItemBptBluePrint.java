/** 
 * Copyright (c) SpaceToad, 2011-2012
 * http://www.mod-buildcraft.com
 * 
 * BuildCraft is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.builders;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import buildcraft.BuildCraftBuilders;
import buildcraft.core.DefaultProps;
import buildcraft.core.blueprints.BptBase;

public class ItemBptBluePrint extends ItemBptBase {

	public ItemBptBluePrint(int i) {
		super(i);
	}
	
	private Icon tex, texBlank;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister r) {
		tex = r.registerIcon(DefaultProps.ICON_PREFIX + "blueprint");
		texBlank = r.registerIcon(DefaultProps.ICON_PREFIX + "blueprint-blank");
	}

	@Override
	public Icon getIconFromDamage(int i) {
		BptBase bpt = BuildCraftBuilders.getBptRootIndex().getBluePrint(i);
		if (bpt == null) {
			return texBlank;
		} else {
			return tex;
		}
	}
}
