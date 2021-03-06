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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import buildcraft.BuildCraftFactory;
import buildcraft.api.core.Orientations;
import buildcraft.api.core.Position;
import buildcraft.api.tools.IToolWrench;
import buildcraft.core.Box;
import buildcraft.core.DefaultProps;
import buildcraft.core.proxy.CoreProxy;
import buildcraft.core.utils.Utils;


public class BlockQuarry extends BlockMachineRoot {

	private Icon textureTop, textureFront, textureSide;

	public BlockQuarry(int i) {
		super(i, Material.iron);

		setHardness(1.5F);
		setResistance(10F);
		setStepSound(soundStoneFootstep);
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityliving, ItemStack stack) {
		super.onBlockPlacedBy(world, i, j, k, entityliving, stack);

		Orientations orientation = Utils.get2dOrientation(new Position(entityliving.posX, entityliving.posY, entityliving.posZ),
				new Position(i, j, k));

		world.setBlockMetadataWithNotify(i, j, k, orientation.reverse().ordinal(), 3);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister r) {
		textureTop = r.registerIcon(DefaultProps.ICON_PREFIX + "quarry-top");
		textureFront = r.registerIcon(DefaultProps.ICON_PREFIX + "quarry-front");
		textureSide = r.registerIcon(DefaultProps.ICON_PREFIX + "quarry-side");
	}

	@Override
	public Icon getIcon(int i, int j) {
		// If no metadata is set, then this is an icon.
		if (j == 0 && i == 3) {
			return textureFront;
		}

		if (i == j) {
			return textureFront;
		}

		switch (i) {
		case 1:
			return textureTop;
		default:
			return textureSide;
		}
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new TileQuarry();
	}

	public void searchFrames(World world, int i, int j, int k) {
		int width2 = 1;
		if (!world.checkChunksExist(i - width2, j - width2, k - width2, i + width2, j + width2, k + width2))
			return;

		int blockID = world.getBlockId(i, j, k);

		if (blockID != BuildCraftFactory.frameBlock.blockID)
			return;

		int meta = world.getBlockMetadata(i, j, k);

		if ((meta & 8) == 0) {
			world.setBlockMetadataWithNotify(i, j, k, meta | 8, 2);

			Orientations[] dirs = Orientations.dirs();

			for (Orientations dir : dirs) {
				switch (dir) {
				case YPos:
					searchFrames(world, i, j + 1, k);
				case YNeg:
					searchFrames(world, i, j - 1, k);
				case ZPos:
					searchFrames(world, i, j, k + 1);
				case ZNeg:
					searchFrames(world, i, j, k - 1);
				case XPos:
					searchFrames(world, i + 1, j, k);
				case XNeg:
				default:
					searchFrames(world, i - 1, j, k);
				}
			}
		}
	}

	private void markFrameForDecay(World world, int x, int y, int z){
		if (world.getBlockId(x, y, z) == BuildCraftFactory.frameBlock.blockID){
			world.setBlockMetadataWithNotify(x, y, z, 1, 2);
		}
	}
	
	@Override
	public void breakBlock(World world, int i, int j, int k, int par5, int par6) {
		
		if (CoreProxy.proxy.isRenderWorld(world)){
			return;
		}
		
		TileEntity tile = world.getBlockTileEntity(i, j, k);
		if (tile instanceof TileQuarry){
			TileQuarry quarry = (TileQuarry)tile;
			Box box = quarry.box;
			
			//X - Axis
			for (int x = box.xMin; x <= box.xMax; x++) {
				markFrameForDecay(world, x, box.yMin, box.zMin);
				markFrameForDecay(world, x, box.yMax, box.zMin);
				markFrameForDecay(world, x, box.yMin, box.zMax);
				markFrameForDecay(world, x, box.yMax, box.zMax);
			}
			
			//Z - Axis
			for (int z = box.zMin + 1; z <= box.zMax - 1; z++) {
				markFrameForDecay(world, box.xMin, box.yMin, z);
				markFrameForDecay(world, box.xMax, box.yMin, z);
				markFrameForDecay(world, box.xMin, box.yMax, z);
				markFrameForDecay(world, box.xMax, box.yMax, z);
			}
			
			//Y - Axis
			for (int y = box.yMin + 1; y <= box.yMax -1; y++) {
				
				markFrameForDecay(world, box.xMin, y, box.zMin);
				markFrameForDecay(world, box.xMax, y, box.zMin);
				markFrameForDecay(world, box.xMin, y, box.zMax);
				markFrameForDecay(world, box.xMax, y, box.zMax);
			}
			quarry.destroy();
		}
		
		Utils.preDestroyBlock(world, i, j, k);

//		byte width = 1;
//		int width2 = width + 1;
//
//		if (world.checkChunksExist(i - width2, j - width2, k - width2, i + width2, j + width2, k + width2)) {
//
//			boolean frameFound = false;
//			for (int z = -width; z <= width; ++z) {
//
//				for (int y = -width; y <= width; ++y) {
//
//					for (int x = -width; x <= width; ++x) {
//
//						int blockID = world.getBlockId(i + z, j + y, k + x);
//
//						if (blockID == BuildCraftFactory.frameBlock.blockID) {
//							searchFrames(world, i + z, j + y, k + x);
//							frameFound = true;
//							break;
//						}
//					}
//					if (frameFound)
//						break;
//				}
//				if (frameFound)
//					break;
//			}
//		}

		super.breakBlock(world, i, j, k, par5, par6);
	}
	
	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9) {
		TileQuarry tile = (TileQuarry) world.getBlockTileEntity(i, j, k);

		// Drop through if the player is sneaking
		if (entityplayer.isSneaking())
			return false;

		// Restart the quarry if its a wrench
		Item equipped = entityplayer.getCurrentEquippedItem() != null ? entityplayer.getCurrentEquippedItem().getItem() : null;
		if (equipped instanceof IToolWrench && ((IToolWrench) equipped).canWrench(entityplayer, i, j, k)) {

			tile.reinitalize();
			((IToolWrench) equipped).wrenchUsed(entityplayer, i, j, k);
			return true;

		}

		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addCreativeItems(ArrayList itemList) {
		itemList.add(new ItemStack(this));
	}
}
