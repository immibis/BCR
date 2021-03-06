/**
 * Copyright (c) SpaceToad, 2011
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.transport;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import buildcraft.BuildCraftCore;
import buildcraft.api.core.Orientations;
import buildcraft.api.core.SafeTimeTracker;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.core.DefaultProps;
import buildcraft.core.IMachine;
import buildcraft.core.proxy.CoreProxy;
import buildcraft.core.utils.Utils;
import buildcraft.transport.network.PacketPowerUpdate;

public class PipeTransportPower extends PipeTransport {

	public short[] displayPower = new short[] { 0, 0, 0, 0, 0, 0 };

	public int[] powerQuery = new int[6];
	public int[] nextPowerQuery = new int[6];
	public long currentDate;
	
	private int freezeTicks;

	private int transferQuery[] = { 0, 0, 0, 0, 0, 0 };

	private double[] internalPower = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
	private double[] internalNextPower = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
	
	// power built-up from overload conditions, used to send out destabilizing power spikes
	public double excessPower;
	private final double MIN_POWER_SPIKE_SIZE = 300; // MJ
	private final double MAX_POWER_SPIKE_SIZE = 600; // MJ
	public final double MAX_EXCESS_POWER = 5000; // MJ; must be > MIN_POWER_SPIKE_SIZE*6
	private int overloadExplodeTicks; // not saved in NBT
	private final int MAX_OVERLOAD_EXPLODE_TICKS = 5;
	
	// Used for measurement purposes only
	public double[] statsLastReceivedPower = new double[6];
	public double[] statsLastSentPower = new double[6];
	
	public static final int MAX_REQUEST = 100000;
	public final int MAX_POWER;
	
	public static final int MAX_POWER_GOLD = 1000;
	public static final int MAX_POWER_STONE = 100;
	public static final int MAX_POWER_MAX = 1000;
	
	public PipeTransportPower(int maxPower) {
		this.MAX_POWER = maxPower;
		for (int i = 0; i < 6; ++i)
			powerQuery[i] = 0;
	}

	SafeTimeTracker tracker = new SafeTimeTracker();

	@Override
	public boolean isPipeConnected(TileEntity tile) {
		return tile instanceof TileGenericPipe || tile instanceof IMachine || tile instanceof IPowerReceptor;
	}

	@Override
	public void updateEntity() {
		if (CoreProxy.proxy.isRenderWorld(worldObj))
			return;
		
		if(freezeTicks > 0) {
			--freezeTicks;
			return;
		}

		step();

		TileEntity tiles[] = new TileEntity[6];

		// Get the nearby connected tiles
		for (int i = 0; i < 6; ++i)
			if (Utils.checkPipesConnections(container.getTile(Orientations.values()[i]), container))
				tiles[i] = container.getTile(Orientations.values()[i]);

		for(int k = 0; k < displayPower.length; k++) {
			displayPower[k] = 0;
			statsLastSentPower[k] = 0;
		}
		
		double[] powerSent = new double[6];
		double[] powerUsed = new double[6];
		
		// Default power split: For each side, split the power received from that side
		// among the remaining sides in proportion to their power request. If the total request is 0,
		// split evenly among remaining sides. If there are no sides to send to, store it as overload build-up.
		
		double totalPowerIn = 0;

		for (int i = 0; i < 6; ++i) {
			statsLastReceivedPower[i] = internalPower[i];
			totalPowerIn += internalPower[i];
			if (internalPower[i] > 0) {
				double div = 0;

				int numTiles = 0;
				
				// Count the total powerQuery from each output.
				for (int j = 0; j < 6; ++j)
					if (j != i)
						if (tiles[j] instanceof TileGenericPipe || tiles[j] instanceof IPowerReceptor) {
							div += powerQuery[j];
							if(container.pipe.outputOpen(Orientations.values()[j])) ++numTiles;
						}
				
				if(div > 0) {
					// Divide the energy received from the input in the last tick.
					double totalWatt = internalPower[i];
					for (int j = 0; j < 6; ++j)
						if (j != i && powerQuery[j] > 0)
							powerSent[j] += (totalWatt / div * powerQuery[j]);
					powerUsed[i] += totalWatt;
					
				} else if(numTiles > 0) {
					// no power requests, split evenly among directions
					double totalWatt = internalPower[i];
					for(int j = 0; j < 6; ++j)
						if (j != i && container.pipe.outputOpen(Orientations.values()[j]) && (tiles[j] instanceof TileGenericPipe || tiles[j] instanceof IPowerReceptor))
							powerSent[j] += totalWatt / numTiles;
					powerUsed[i] += totalWatt;
					
				} else {
					/* reflect power
					powerSent[i] += internalPower[i];
					powerUsed[i] += internalPower[i];
					*/
					
					// absorb power
					excessPower += internalPower[i];
					powerUsed[i] += internalPower[i];
				}
			}
		}
		
		// At low power transfer levels, overload power dissipates harmlessly
		if(totalPowerIn < 2)
			excessPower = 0;

		if(container.pipe instanceof IPipeTransportPowerHook)
			((IPipeTransportPowerHook)container.pipe).alterPowerSplit(internalPower, powerUsed, powerQuery, powerSent);

		double powerSpikeSize = 0;
		if(excessPower >= MIN_POWER_SPIKE_SIZE) {
			int numTiles = 0;
			for(int j = 0; j < 6; j++)
				if(container.pipe.outputOpen(Orientations.values()[j]) && (tiles[j] instanceof TileGenericPipe || tiles[j] instanceof IPowerReceptor))
					++numTiles;
			if(numTiles > 0 && excessPower >= MIN_POWER_SPIKE_SIZE * numTiles) {
				//System.out.println(container.xCoord+","+container.yCoord+","+container.zCoord+" spike "+excessPower+"/"+numTiles);
				powerSpikeSize = Math.min(excessPower / numTiles, MAX_POWER_SPIKE_SIZE);
				excessPower -= powerSpikeSize * numTiles;
			}
		}
		
		if(excessPower >= MAX_EXCESS_POWER) {
			if(++overloadExplodeTicks >= MAX_OVERLOAD_EXPLODE_TICKS) {
				// explosion power 1.0 is not very big, but enough to destroy a few nearby pipes
				worldObj.newExplosion(null, xCoord+0.5, yCoord+0.5, zCoord+0.5, 1.0f, false, true);
				excessPower = 0;
			}
		} else
			overloadExplodeTicks = 0;
		
		for (int j = 0; j < 6; ++j) {
			if (powerUsed[j] != 0) {
				internalPower[j] -= powerUsed[j];
				displayPower[j] += powerUsed[j] / 2F;
			}
			
			double watts = powerSent[j] + (container.pipe.outputOpen(Orientations.values()[j]) ? powerSpikeSize : 0);
			
			if (watts != 0) {
				statsLastSentPower[j] += watts;

				if (tiles[j] instanceof TileGenericPipe) {
					TileGenericPipe nearbyTile = (TileGenericPipe) tiles[j];

					PipeTransportPower nearbyTransport = (PipeTransportPower) nearbyTile.pipe.transport;

					nearbyTransport.receiveEnergy(Orientations.values()[j].reverse(), watts);

					displayPower[j] += watts / 2F;

				} else if (tiles[j] instanceof IPowerReceptor) {
					IPowerReceptor pow = (IPowerReceptor) tiles[j];

					pow.getPowerProvider().receiveEnergy((float) watts, Orientations.values()[j].reverse());

					displayPower[j] += watts / 2F;
				}
			}
		}

		// Update nextPowerQuery (via requestEnergy).
		// Send a power request to myself from the machines on each side,
		// but not pipes, as pipes send their own power requests.
		for (int i = 0; i < 6; ++i)
			if (tiles[i] instanceof IPowerReceptor && !(tiles[i] instanceof TileGenericPipe)) {
				IPowerReceptor receptor = (IPowerReceptor) tiles[i];
				int request = receptor.powerRequest();

				if (request > 0)
					requestEnergy(Orientations.values()[i], request);
			}

		// Send power requests to adjacent pipes.
		// The power requested _by us_ on side i
		// is the power requested _of us_ on all other sides.
		// This is why loops cause power suckage, as
		// our power requests tend to infinity if we are part of a loop,
		// and then the rest of the network sends all its power here.
		
		// Calculate the power requests.
		for (int i = 0; i < 6; ++i) {
			int newTQ = 0;

			for (int j = 0; j < 6; ++j)
				if (j != i)
					newTQ += powerQuery[j];
			
			//transferQuery[i] = (transferQuery[i] + newTQ + 1) / 2;
			transferQuery[i] = newTQ;
		}

		// Send the power requests.
		for (int i = 0; i < 6; ++i)
			if (transferQuery[i] != 0 && container.pipe.inputOpen(Orientations.dirs()[i]))
				if (tiles[i] != null) {
					TileEntity entity = tiles[i];

					if (entity instanceof TileGenericPipe) {
						TileGenericPipe nearbyTile = (TileGenericPipe) entity;

						if (nearbyTile.pipe == null)
							continue;

						PipeTransportPower nearbyTransport = (PipeTransportPower) nearbyTile.pipe.transport;
						nearbyTransport.requestEnergy(Orientations.values()[i].reverse(), transferQuery[i]);
					}
				}

		// Send updates to clients.
		if (!worldObj.isRemote && tracker.markTimeIfDelay(worldObj, 2 * BuildCraftCore.updateFactor)) {
				PacketPowerUpdate packet = new PacketPowerUpdate(xCoord, yCoord, zCoord);
				packet.displayPower = displayPower;
				CoreProxy.proxy.sendToPlayers(packet, worldObj, xCoord, yCoord, zCoord,
						DefaultProps.NETWORK_UPDATE_RANGE);
			}

	}

	public void step() {
		if (currentDate != worldObj.getWorldTime()) {
			currentDate = worldObj.getWorldTime();

			powerQuery = nextPowerQuery;
			nextPowerQuery = new int[] { 0, 0, 0, 0, 0, 0 };

			internalPower = internalNextPower;
			internalNextPower = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		}
	}

	public void receiveEnergy(Orientations from, double val) {
		step();
		if (this.container.pipe instanceof IPipeTransportPowerHook
			&& ((IPipeTransportPowerHook) this.container.pipe).receiveEnergy(from, val))
			return;
		else {
			internalNextPower[from.ordinal()] += val;

			if (internalNextPower[from.ordinal()] >= MAX_POWER) {
				double excessPowerHere = internalNextPower[from.ordinal()] - MAX_POWER;
				internalNextPower[from.ordinal()] -= excessPowerHere;
				excessPower += excessPowerHere;
			}
		}
	}

	public void requestEnergy(Orientations from, int i) {
		if(i <= 0) return;
		if(i > MAX_REQUEST) i = MAX_REQUEST;
		
		if(!this.container.pipe.outputOpen(from))
			return;
		
		step();
		if (this.container.pipe instanceof IPipeTransportPowerHook
			&& ((IPipeTransportPowerHook) this.container.pipe).requestEnergy(from, i))
			return;
		else {
			step();
			nextPowerQuery[from.ordinal()] += i;
		}
	}

	@Override
	public void initialize() {
		currentDate = worldObj.getWorldTime();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		for (int i = 0; i < 6; ++i) {
			powerQuery[i] = nbttagcompound.getInteger("powerQuery[" + i + "]");
			nextPowerQuery[i] = nbttagcompound.getInteger("nextPowerQuery[" + i + "]");
			internalPower[i] = nbttagcompound.getDouble("internalPower[" + i + "]");
			internalNextPower[i] = nbttagcompound.getDouble("internalNextPower[" + i + "]");
			transferQuery[i] = nbttagcompound.getInteger("transferQuery["+i+"]");
			displayPower[i] = nbttagcompound.getShort("displayPower["+i+"]");
		}
		
		currentDate = nbttagcompound.getLong("currentDate");
		excessPower = nbttagcompound.getDouble("excessPower");

		// Don't run for half a second after loading.
		// Seems to fix storage loops becoming unstable when loading.
		freezeTicks = 10;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		for (int i = 0; i < 6; ++i) {
			nbttagcompound.setInteger("powerQuery[" + i + "]", powerQuery[i]);
			nbttagcompound.setInteger("nextPowerQuery[" + i + "]", nextPowerQuery[i]);
			nbttagcompound.setDouble("internalPower[" + i + "]", internalPower[i]);
			nbttagcompound.setDouble("internalNextPower[" + i + "]", internalNextPower[i]);
			nbttagcompound.setInteger("transferQuery["+i+"]", transferQuery[i]);
			nbttagcompound.setShort("displayPower["+i+"]", displayPower[i]);
		}
		
		nbttagcompound.setLong("currentDate", currentDate);
		nbttagcompound.setDouble("excessPower", excessPower);
	}

	public boolean isTriggerActive(ITrigger trigger) {
		return false;
	}

	@Override
	public boolean allowsConnect(PipeTransport with) {
		return with instanceof PipeTransportPower;
	}

	/**
	 * Client-side handler for receiving power updates from the server;
	 * @param packetPower
	 */
	public void handlePowerPacket(PacketPowerUpdate packetPower) {
		displayPower = packetPower.displayPower;
	}

}
