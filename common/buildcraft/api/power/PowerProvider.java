/** 
 * Copyright (c) SpaceToad, 2011
 * http://www.mod-buildcraft.com
 * 
 * BuildCraft is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.api.power;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import buildcraft.api.core.Orientations;
import buildcraft.api.core.SafeTimeTracker;

public abstract class PowerProvider implements IPowerProvider {
	
	// For energy-surge explosions
	// Maximum ramp-up rate for most devices is 1 MJ/t/t
	protected double lastReceived = -1;
	protected double lastLastReceived = -1;
	protected double rampUpRate = 0;

	protected int latency;
	protected int minEnergyReceived;
	protected int maxEnergyReceived;
	protected int maxEnergyStored;
	protected int minActivationEnergy;
	protected float energyStored = 0;

	protected int powerLoss = 1;
	protected int powerLossRegularity = 100;

	public SafeTimeTracker timeTracker = new SafeTimeTracker();
	public SafeTimeTracker energyLossTracker = new SafeTimeTracker();

	public int[] powerSources = { 0, 0, 0, 0, 0, 0 };

	@Override public SafeTimeTracker getTimeTracker() { return this.timeTracker; }
	
	@Override public int getLatency() { return this.latency; }
	@Override public int getMinEnergyReceived() { return this.minEnergyReceived; }
	@Override public int getMaxEnergyReceived() { return this.maxEnergyReceived; }
	@Override public int getMaxEnergyStored() { return this.maxEnergyStored; }
	@Override public int getActivationEnergy() { return this.minActivationEnergy; }
	@Override public float getEnergyStored() { return this.energyStored; }
	
	@Override 
	public void configure(int latency, int minEnergyReceived, int maxEnergyReceived, int minActivationEnergy, int maxStoredEnergy) {
		this.latency = latency;
		this.minEnergyReceived = minEnergyReceived;
		this.maxEnergyReceived = maxEnergyReceived;
		this.maxEnergyStored = maxStoredEnergy;
		this.minActivationEnergy = minActivationEnergy;
	}

	@Override 
	public void configurePowerPerdition(int powerLoss, int powerLossRegularity) {
		this.powerLoss = powerLoss;
		this.powerLossRegularity = powerLossRegularity;
	}

	@Override
	public boolean update(IPowerReceptor receptor) {
		if (!preConditions(receptor)) {
			return false;
		}

		TileEntity tile = (TileEntity) receptor;
		boolean result = false;
		
		
		if(lastLastReceived >= 0)
			rampUpRate = (lastReceived - lastLastReceived) * 0.05 + rampUpRate * 0.95;
		else
			rampUpRate = 0;
		lastLastReceived = lastReceived;
		lastReceived = 0;

		if (energyStored >= minActivationEnergy) {
			if (latency == 0) {
				receptor.doWork();
				result = true;
			} else {
				if (timeTracker.markTimeIfDelay(tile.worldObj, latency)) {
					receptor.doWork();
					result = true;
				}
			}
		}

		if (powerLoss > 0 && energyLossTracker.markTimeIfDelay(tile.worldObj, powerLossRegularity)) {

			energyStored -= powerLoss;
			if (energyStored < 0) {
				energyStored = 0;
			}
		}

		for (int i = 0; i < 6; ++i) {
			if (powerSources[i] > 0) {
				powerSources[i]--;
			}
		}

		return result;
	}

	@Override
	public boolean preConditions(IPowerReceptor receptor) {
		return true;
	}

	@Override
	public float useEnergy(float min, float max, boolean doUse) {
		float result = 0;

		if (energyStored >= min) {
			if (energyStored <= max) {
				result = energyStored;
				if (doUse) {
					energyStored = 0;
				}
			} else {
				result = max;
				if (doUse) {
					energyStored -= max;
				}
			}
		}

		return result;
	}

	@Override 
	public void readFromNBT(NBTTagCompound tag) {
		latency = tag.getInteger("latency");
		minEnergyReceived = tag.getInteger("minEnergyReceived");
		maxEnergyReceived = tag.getInteger("maxEnergyReceived");
		maxEnergyStored = tag.getInteger("maxStoreEnergy");
		minActivationEnergy = tag.getInteger("minActivationEnergy");

		try {
			energyStored = tag.getFloat("storedEnergy");
		} catch (Throwable c) {
			energyStored = 0;
		}
		
		if(tag.hasKey("lastReceived")) {
			lastReceived = tag.getDouble("lastReceived");
			lastLastReceived = tag.getDouble("lastLastReceived");
		}
	}

	@Override 
	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("latency", latency);
		tag.setInteger("minEnergyReceived", minEnergyReceived);
		tag.setInteger("maxEnergyReceived", maxEnergyReceived);
		tag.setInteger("maxStoreEnergy", maxEnergyStored);
		tag.setInteger("minActivationEnergy", minActivationEnergy);
		tag.setFloat("storedEnergy", energyStored);
		tag.setDouble("lastReceived", lastReceived);
		tag.setDouble("lastLastReceived", lastLastReceived);
	}

	@Override
	public void receiveEnergy(float quantity, Orientations from) {
		powerSources[from.ordinal()] = 2;

		energyStored += quantity;

		if (energyStored > maxEnergyStored) {
			energyStored = maxEnergyStored;
		}
		
		lastReceived += quantity;
	}

	@Override
	public boolean isPowerSource(Orientations from) {
		return powerSources[from.ordinal()] != 0;
	}
	
	@Override
	public double getPowerRamp() {
		return rampUpRate;
	}
}
