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
import buildcraft.transport.PipeTransportPower;

public class PipePowerStone extends Pipe {

	public PipePowerStone(int itemID) {
		super(new PipeTransportPower(PipeTransportPower.MAX_POWER_STONE), new PipeLogicStone(), itemID);
	}
	
	private static Icon icon;
	
	@Override
	public void registerIcons(IconRegister r) {
		icon = r.registerIcon(getDefaultIconPath());
	}
	
	@Override
	public Icon getTexture(Orientations direction) {
		return icon;
	}
}
