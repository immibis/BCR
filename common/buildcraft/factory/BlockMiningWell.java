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

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import buildcraft.api.core.Orientations;
import buildcraft.api.core.Position;
import buildcraft.core.DefaultProps;
import buildcraft.core.utils.Utils;


public class BlockMiningWell extends BlockMachineRoot {

	private Icon textureFront, textureSides, textureBack, textureTop;

	public BlockMiningWell(int i) {
		super(i, Material.ground);

		setHardness(1.5F);
		setResistance(10F);
		setStepSound(soundStoneFootstep);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister r) {
		textureFront = r.registerIcon(DefaultProps.ICON_PREFIX + "well-front");
		textureBack = r.registerIcon(DefaultProps.ICON_PREFIX + "well-back");
		textureTop = r.registerIcon(DefaultProps.ICON_PREFIX + "well-top");
		textureSides = r.registerIcon(DefaultProps.ICON_PREFIX + "well-side");
	}

	@Override
	public Icon getIcon(int i, int j) {
		if (j == 0 && i == 3) {
			return textureFront;
		}

		if (i == 1) {
			return textureTop;
		} else if (i == 0) {
			return textureBack;
		} else if (i == j) {
			return textureFront;
		} else if (j >= 0 && j < 6 && Orientations.values()[j].reverse().ordinal() == i) {
			return textureBack;
		} else {
			return textureSides;
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityliving, ItemStack stack) {
		Orientations orientation = Utils.get2dOrientation(new Position(entityliving.posX, entityliving.posY, entityliving.posZ),
				new Position(i, j, k));

		world.setBlockMetadataWithNotify(i, j, k, orientation.reverse().ordinal(), 3);
	}

	
	
	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new TileMiningWell();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addCreativeItems(ArrayList itemList) {
		itemList.add(new ItemStack(this));
	}
}
