package buildcraft.transport.pipes;

import java.util.Arrays;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.core.Orientations;
import buildcraft.transport.IPipeTransportPowerHook;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportPower;

public class PipePowerDiamond extends Pipe implements IPipeTransportPowerHook {
	public PipePowerDiamond(int itemID) {
		super(new PipeTransportPower(), new PipeLogicIron(), itemID);
	}
	
	// logic.outputOpen returns true for the un-filled-texture side, false for others
	
	private static Icon icon, iconBlocked;

	@Override
	public void registerIcons(IconRegister r) {
		icon = r.registerIcon(getDefaultIconPath());
		iconBlocked = r.registerIcon(getDefaultIconPath() + "-blocked");
	}

	@Override
	public Icon getTexture(Orientations direction) {
		if(direction == Orientations.Unknown)
			return icon;
		return logic.outputOpen(direction) ? icon : iconBlocked;
	}
	
	// Normally PipeLogicIron blocks inputs and outputs; override that here
	@Override
	public boolean outputOpen(Orientations to) {
		return transport.outputOpen(to);
	}
	@Override
	public boolean inputOpen(Orientations from) {
		// The active output side can't receive power.
		return transport.inputOpen(from) && from.ordinal() != ((PipeLogicIron)logic).getOutputDirection();
	}

	@Override
	public boolean receiveEnergy(Orientations from, double val) {
		return false;
	}

	@Override
	public boolean requestEnergy(Orientations from, int i) {
		return false;
	}

	@Override
	public void alterPowerSplit(double[] received, double[] used, int[] requested, double[] sent) {
		
		// Sent activeDir all the power it wants (if possible). Split the remaining power evenly.
		
		int activeDir = ((PipeLogicIron)logic).getOutputDirection();
		
		double totalIn = 0;
		for(int k = 0; k < 6; k++) {
			if(k == activeDir) {
				// shouldn't receive any power from here, but just in case
				used[k] = 0;
			} else {
				totalIn += received[k];
				used[k] = received[k]; // diamond pipes always use all power from every available input
			}
		}
		
		Arrays.fill(sent, 0);
		
		if(totalIn <= requested[activeDir]) {
			sent[activeDir] = totalIn;
			return;
		}
			
		sent[activeDir] = requested[activeDir];
		double percentLeft = 1 - sent[activeDir]/totalIn;
		
		// split remaining power evenly, but never back to where it came from
		for(ForgeDirection from : ForgeDirection.VALID_DIRECTIONS) {
			double excessReceivedHere = received[from.ordinal()] * percentLeft;
			
			int numSides = 0;
			int sideMask = 0;
			for(int k = 0; k < 6; k++)
				if(k != activeDir && k != from.ordinal() && container.isPipeConnected(Orientations.dirs()[k])) {
					numSides++;
					sideMask |= 1 << k;
				}
			
			if(numSides > 0) {
				double excessPerSide = excessReceivedHere / numSides;
				for(int k = 0; k < 6; k++)
					if((sideMask & (1 << k)) != 0)
						sent[k] += excessPerSide;
			
			} else {
				sent[activeDir] += excessReceivedHere;
			}
		}
	}
}
