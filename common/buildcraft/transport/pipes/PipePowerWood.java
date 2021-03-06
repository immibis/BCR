/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.transport.pipes;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import buildcraft.api.core.Orientations;
import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerFramework;
import buildcraft.core.utils.Utils;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportPower;
import buildcraft.transport.TileGenericPipe;

public class PipePowerWood extends Pipe implements IPowerReceptor {

	private IPowerProvider powerProvider;

	private int baseTexture = 7 * 16 + 6;
	private int plainTexture = 1 * 16 + 15;

	public PipePowerWood(int itemID) {
		super(new PipeTransportPower(PipeTransportPower.MAX_POWER_STONE), new PipeLogicWood(), itemID);

		powerProvider = PowerFramework.currentFramework.createPowerProvider();
		powerProvider.configure(50, 1, 1000, 1, 1000);
		powerProvider.configurePowerPerdition(1, 100);
	}

	@Override
	public void setPowerProvider(IPowerProvider provider) {
		provider = powerProvider;
	}

	@Override
	public IPowerProvider getPowerProvider() {
		return powerProvider;
	}

	@Override
	public void doWork() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		for (Orientations o : Orientations.dirs())
			if (Utils.checkPipesConnections(container, container.getTile(o))) {
				TileEntity tile = container.getTile(o);

				if (tile instanceof TileGenericPipe) {
					if (((TileGenericPipe) tile).pipe == null) {
						System.out.println("PipePowerWood.pipe was null, this used to cause a NPE crash)");
						continue; // Null pointer protection
					}

					PipeTransportPower pow = (PipeTransportPower) ((TileGenericPipe) tile).pipe.transport;

					float energyToRemove = 0;

					if (powerProvider.getEnergyStored() > 40)
						energyToRemove = powerProvider.getEnergyStored() / 40 + 4;
					else if (powerProvider.getEnergyStored() > 10)
						energyToRemove = powerProvider.getEnergyStored() / 10;
					else
						energyToRemove = 1;

					float energyUsed = powerProvider.useEnergy(1, energyToRemove, true);

					pow.receiveEnergy(o.reverse(), energyUsed);

					if (worldObj.isRemote) return;
					((PipeTransportPower) transport).displayPower[o.ordinal()] += energyUsed;
				}

			}
	}

	@Override
	public int powerRequest() {
		return getPowerProvider().getMaxEnergyReceived();
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
