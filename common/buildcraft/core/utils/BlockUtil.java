/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.core.utils;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import buildcraft.BuildCraftCore;

public class BlockUtil {

	public static ArrayList<ItemStack> getItemStackFromBlock(World world, int i, int j, int k) {
		Block block = Block.blocksList[world.getBlockId(i, j, k)];

		if (block == null)
			return null;

		int meta = world.getBlockMetadata(i, j, k);

		return block.getBlockDropped(world, i, j, k, meta, 0);
	}

	public static void breakBlock(World world, int x, int y, int z) {
		int blockId = world.getBlockId(x, y, z);

		if (blockId != 0 && BuildCraftCore.dropBrokenBlocks)
			Block.blocksList[blockId].dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);

		world.setBlockToAir(x, y, z);
	}

}