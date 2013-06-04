/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.transport.pipes;

import java.util.Arrays;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import buildcraft.api.core.Orientations;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportPower;

public class PipePowerGold extends Pipe {

	public PipePowerGold(int itemID) {
		super(new PipeTransportPower(), new PipeLogicGold(), itemID);

		((PipeTransportPower) transport).powerResitance = 0.001;
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

	private static boolean DEBUG_MODE = Boolean.getBoolean("bcr.debug.powerPipe");
	
	@Override
	public boolean blockActivated(World world, int i, int j, int k, EntityPlayer pl) {
		if(!DEBUG_MODE)
			return super.blockActivated(world, i, j, k, pl);
		
		PipeTransportPower t = (PipeTransportPower)container.pipe.transport;
		
		if(!world.isRemote) {
			pl.sendChatToPlayer("====== Gold power pipe at "+i+","+j+","+k);
			//pl.sendChatToPlayer("Stored power: " + Arrays.toString(t.internalPower));
			//pl.sendChatToPlayer("Next stored power: " + Arrays.toString(t.internalPower));
			pl.sendChatToPlayer("Power query: " + Arrays.toString(t.powerQuery));
			//pl.sendChatToPlayer("Next power query: " + Arrays.toString(t.nextPowerQuery));
			pl.sendChatToPlayer("Power display: " + Arrays.toString(t.displayPower));
			pl.sendChatToPlayer("Power resistance: " + t.powerResitance);
		}
		
		return true;
	}

}
