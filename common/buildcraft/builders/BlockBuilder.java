/**
 * Copyright (c) SpaceToad, 2011
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.builders;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import buildcraft.BuildCraftBuilders;
import buildcraft.api.core.Orientations;
import buildcraft.api.core.Position;
import buildcraft.api.tools.IToolWrench;
import buildcraft.core.DefaultProps;
import buildcraft.core.GuiIds;
import buildcraft.core.proxy.CoreProxy;
import buildcraft.core.utils.Utils;


public class BlockBuilder extends BlockContainer {
	
	private Icon texSide, texTop, texFront;

	public BlockBuilder(int i) {
		super(i, Material.iron);
		setHardness(0.7F);
		setCreativeTab(CreativeTabs.tabRedstone);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister r) {
		texSide = r.registerIcon(DefaultProps.ICON_PREFIX + "builder-side");
		texTop = r.registerIcon(DefaultProps.ICON_PREFIX + "builder-top");
		texFront = r.registerIcon(DefaultProps.ICON_PREFIX + "builder-front");
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new TileBuilder();
	}


	@Override
	public Icon getIcon(int i, int j) {
		if (j == 0 && i == 3) {
			return texFront;
		}

		if (i == j) {
			return texFront;
		}

		switch (i) {
		case 1:
			return texTop;
		default:
			return texSide;
		}
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9) {

		// Drop through if the player is sneaking
		if (entityplayer.isSneaking())
			return false;

		Item equipped = entityplayer.getCurrentEquippedItem() != null ? entityplayer.getCurrentEquippedItem().getItem() : null;
		if (equipped instanceof IToolWrench && ((IToolWrench) equipped).canWrench(entityplayer, i, j, k)) {

			int meta = world.getBlockMetadata(i, j, k);

			switch (Orientations.values()[meta]) {
			case XNeg:
				world.setBlockMetadataWithNotify(i, j, k, Orientations.ZPos.ordinal(), 3);
				break;
			case XPos:
				world.setBlockMetadataWithNotify(i, j, k, Orientations.ZNeg.ordinal(), 3);
				break;
			case ZNeg:
				world.setBlockMetadataWithNotify(i, j, k, Orientations.XNeg.ordinal(), 3);
				break;
			case ZPos:
			default:
				world.setBlockMetadataWithNotify(i, j, k, Orientations.XPos.ordinal(), 3);
				break;
			}

			world.markBlockForUpdate(i, j, k);
			((IToolWrench) equipped).wrenchUsed(entityplayer, i, j, k);
			return true;
		} else {

			if (!CoreProxy.proxy.isRenderWorld(world))
				entityplayer.openGui(BuildCraftBuilders.instance, GuiIds.BUILDER, world, i, j, k);
			return true;

		}
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityliving, ItemStack stack) {
		super.onBlockPlacedBy(world, i, j, k, entityliving, stack);
		Orientations orientation = Utils.get2dOrientation(new Position(entityliving.posX, entityliving.posY, entityliving.posZ),
				new Position(i, j, k));

		world.setBlockMetadataWithNotify(i, j, k, orientation.reverse().ordinal(), 3);
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
