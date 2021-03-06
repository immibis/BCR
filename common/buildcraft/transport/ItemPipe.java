/**
 * Copyright (c) SpaceToad, 2011
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.transport;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import buildcraft.BuildCraftTransport;
import buildcraft.core.IItemPipe;

public class ItemPipe extends Item implements IItemPipe {

	Pipe dummyPipe;

	private int textureIndex = 0;

	protected ItemPipe(int i) {
		super(i);
		this.setCreativeTab(CreativeTabs.tabTransport);
	}
	
	// icon needs to be registered in the block texture sheet
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister r) {
	}
	
	@SideOnly(Side.CLIENT)
	public void registerPipeIcons(IconRegister r) {
		dummyPipe.registerIcons(r);
		if(getIconFromDamage(0) == null)
			throw new RuntimeException("icon loading failed for "+dummyPipe);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIconFromDamage(int par1) {
		return dummyPipe.getTextureForItem();
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int side, float par8, float par9, float par10) {
		int blockID = BuildCraftTransport.genericPipeBlock.blockID;

		if (world.getBlockId(i, j, k) == Block.snow.blockID)
			side = 0;
		else {
			if (side == 0)
				j--;
			if (side == 1)
				j++;
			if (side == 2)
				k--;
			if (side == 3)
				k++;
			if (side == 4)
				i--;
			if (side == 5)
				i++;
		}

		if (itemstack.stackSize == 0)
			return false;
		if (entityplayer.canPlayerEdit(i, j, k, side, itemstack)
				&& world.canPlaceEntityOnSide(blockID, i, j, k, false, side, entityplayer, itemstack)) {

			Pipe pipe = BlockGenericPipe.createPipe(itemID);
			if (BlockGenericPipe.placePipe(pipe, world, i, j, k, blockID, 0)) {

				Block.blocksList[blockID].onBlockPlacedBy(world, i, j, k, entityplayer, itemstack);
				// To move to a proxt
				// world.playSoundEffect((float)i + 0.5F, (float)j + 0.5F,
				// (float)k + 0.5F, block.stepSound.func_1145_d(),
				// (block.stepSound.getVolume() + 1.0F) / 2.0F,
				// block.stepSound.getPitch() * 0.8F);
				itemstack.stackSize--;
			}
			return true;
		} else
			return false;
	}

	public ItemPipe setTextureIndex(int textureIndex){
		this.textureIndex = textureIndex;
		return this;
	}

	public int getTextureIndex() {
		return textureIndex;
	}
}
