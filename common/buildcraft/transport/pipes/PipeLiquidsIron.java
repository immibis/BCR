/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.transport.pipes;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import buildcraft.api.core.Orientations;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportLiquids;

public class PipeLiquidsIron extends Pipe {

	private int baseTexture = 7 * 16 + 3;
	private int plainTexture = 1 * 16 + 3;

	public PipeLiquidsIron(int itemID) {
		super(new PipeTransportLiquids(), new PipeLogicIron(), itemID);
	}
	
	private static Icon texBlocked, tex;
	
	@Override
	public void registerIcons(IconRegister r) {
		tex = r.registerIcon(getDefaultIconPath());
		texBlocked = r.registerIcon(getDefaultIconPath() + "-blocked");
	}
	
	@Override
	public Icon getTexture(Orientations direction) {
		if (direction == Orientations.Unknown)
			return tex;
		else {
			int metadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

			if (metadata == direction.ordinal())
				return tex;
			else
				return texBlocked;
		}
	}


}
