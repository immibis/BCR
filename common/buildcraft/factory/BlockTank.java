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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import buildcraft.BuildCraftCore;
import buildcraft.api.core.Orientations;
import buildcraft.api.liquids.LiquidManager;
import buildcraft.api.liquids.LiquidStack;
import buildcraft.core.DefaultProps;
import buildcraft.core.utils.Utils;


public class BlockTank extends BlockContainer {

	public BlockTank(int i) {
		super(i, Material.glass);
		setBlockBounds(0.125F, 0F, 0.125F, 0.875F, 1F, 0.875F);
		setHardness(0.5F);
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	public boolean isACube() {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new TileTank();
	}
	
	private Icon texTop, texSide, texSideJoined;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister r) {
		texTop = r.registerIcon(DefaultProps.ICON_PREFIX + "tank-top");
		texSide = r.registerIcon(DefaultProps.ICON_PREFIX + "tank-side");
		texSideJoined = r.registerIcon(DefaultProps.ICON_PREFIX + "tank-side-joined");
	}

	@Override
	public Icon getIcon(int i, int j) {
		switch (i) {
		case 0:
		case 1:
			return texTop;
		default:
			return texSide;
		}
	}

	@SuppressWarnings({ "all" })
	public Icon getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		switch (l) {
		case 0:
		case 1:
			return texTop;
		default:
			if (iblockaccess.getBlockId(i, j - 1, k) == blockID) {
				return texSideJoined;
			} else {
				return texSide;
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9) {

		ItemStack current = entityplayer.inventory.getCurrentItem();
		if (current != null) {

			LiquidStack liquid = LiquidManager.getLiquidForFilledItem(current);

			TileTank tank = (TileTank) world.getBlockTileEntity(i, j, k);

			// Handle filled containers
			if (liquid != null) {
				int qty = tank.fill(Orientations.Unknown, liquid, true);

				if (qty != 0 && !BuildCraftCore.debugMode && !entityplayer.capabilities.isCreativeMode) {
					entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem,
							Utils.consumeItem(current));
				}

				return true;

			// Handle empty containers
			} else {

				LiquidStack available = tank.getTanks()[0].getLiquid();
                if(available != null){
                    ItemStack filled = LiquidManager.fillLiquidContainer(available, current);

                    liquid = LiquidManager.getLiquidForFilledItem(filled);
                   
                    if(liquid != null) {
			if (!BuildCraftCore.debugMode && !entityplayer.capabilities.isCreativeMode){
                        if(current.stackSize > 1) {
                            if(!entityplayer.inventory.addItemStackToInventory(filled))
                                return false;
                            else
                                entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem,
                                        Utils.consumeItem(current));
                        } else {
                            entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem,
                                    Utils.consumeItem(current));
                            entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, filled);
                        }
			}
                        tank.drain(Orientations.Unknown, liquid.amount, true);
                        return true;
                    }
                }
			}
		}

		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addCreativeItems(ArrayList itemList) {
		itemList.add(new ItemStack(this));
	}

}
