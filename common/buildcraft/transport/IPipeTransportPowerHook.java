/**
 * Copyright (c) SpaceToad, 2011
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.transport;

import buildcraft.api.core.Orientations;

public interface IPipeTransportPowerHook {

	// Return true to override default behaviour
	public boolean receiveEnergy(Orientations from, double val);
	public boolean requestEnergy(Orientations from, int i);
	
	public void alterPowerSplit(Orientations from, int[] requested, double[] sent);
}
