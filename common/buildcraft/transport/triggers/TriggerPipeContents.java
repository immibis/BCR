/**
 * Copyright (c) SpaceToad, 2011
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.transport.triggers;

import buildcraft.api.gates.ITriggerParameter;
import buildcraft.api.gates.Trigger;
import buildcraft.api.liquids.ILiquidTank;
import buildcraft.api.liquids.LiquidManager;
import buildcraft.api.liquids.LiquidStack;
import buildcraft.core.DefaultProps;
import buildcraft.transport.EntityData;
import buildcraft.transport.ITriggerPipe;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.PipeTransportLiquids;
import buildcraft.transport.PipeTransportPower;

public class TriggerPipeContents extends Trigger implements ITriggerPipe {

	public enum Kind {
		Empty, ContainsItems, ContainsLiquids, ContainsEnergy,
		ContainsEnergy25, ContainsEnergy50, ContainsEnergy75
	};

	Kind kind;

	public TriggerPipeContents(int id, Kind kind) {
		super(id);
		this.kind = kind;
	}

	@Override
	public int getIndexInTexture() {
		switch (kind) {
		case Empty:
			return 3 * 16 + 0;
		case ContainsItems:
			return 3 * 16 + 1;
		case ContainsLiquids:
			return 3 * 16 + 2;
		case ContainsEnergy:
			return 3 * 16 + 3;
		case ContainsEnergy25:
			return 3 * 16 + 5;
		case ContainsEnergy50:
			return 3 * 16 + 6;
		case ContainsEnergy75:
			return 3 * 16 + 7;
		}
		return 3 * 16 + 0;
	}

	@Override
	public boolean hasParameter() {
		switch (kind) {
		case ContainsItems:
		case ContainsLiquids:
			return true;
		default:
			return false;
		}
	}

	@Override
	public String getDescription() {

		switch (kind) {
		case Empty:
			return "Pipe Empty";
		case ContainsItems:
			return "Items Traversing";
		case ContainsLiquids:
			return "Liquid Traversing";
		case ContainsEnergy:
			return "Power Traversing";
		case ContainsEnergy25:
			return "Power Flow >25%";
		case ContainsEnergy50:
			return "Power Flow >50%";
		case ContainsEnergy75:
			return "Power Flow >75%";
		}

		return "";
	}

	@Override
	public boolean isTriggerActive(Pipe pipe, ITriggerParameter parameter) {
		if (pipe.transport instanceof PipeTransportItems) {
			PipeTransportItems transportItems = (PipeTransportItems) pipe.transport;

			if (kind == Kind.Empty)
				return transportItems.travelingEntities.isEmpty();
			else if (kind == Kind.ContainsItems)
				if (parameter != null && parameter.getItem() != null) {
					for (EntityData data : transportItems.travelingEntities.values())
						if (data.item.getItemStack().itemID == parameter.getItem().itemID
								&& data.item.getItemStack().getItemDamage() == parameter.getItem().getItemDamage())
							return true;
				} else
					return !transportItems.travelingEntities.isEmpty();
		} else if (pipe.transport instanceof PipeTransportLiquids) {
			PipeTransportLiquids transportLiquids = (PipeTransportLiquids) pipe.transport;

			LiquidStack searchedLiquid = null;

			if (parameter != null && parameter.getItem() != null)
				searchedLiquid = LiquidManager.getLiquidForFilledItem(parameter.getItem());

			if (kind == Kind.Empty) {
				for (ILiquidTank b : transportLiquids.getTanks())
					if (b.getLiquid() != null && b.getLiquid().amount != 0)
						return false;

				return true;
			} else {
				for (ILiquidTank b : transportLiquids.getTanks())
					if (b.getLiquid() != null && b.getLiquid().amount != 0)
						if (searchedLiquid == null || searchedLiquid.isLiquidEqual(b.getLiquid()))
							return true;

				return false;
			}
		} else if (pipe.transport instanceof PipeTransportPower) {
			PipeTransportPower transportPower = (PipeTransportPower) pipe.transport;

			if (kind == Kind.Empty) {
				for (short s : transportPower.displayPower)
					if (s != 0)
						return false;

				return true;
			} else if(kind == Kind.ContainsEnergy) {
				for (short s : transportPower.displayPower)
					if (s != 0)
						return true;

				return false;
			} else {
				int minLevel;
				switch(kind) {
				case ContainsEnergy25: minLevel = transportPower.MAX_POWER / 4; break;
				case ContainsEnergy50: minLevel = transportPower.MAX_POWER / 2; break;
				case ContainsEnergy75: minLevel = transportPower.MAX_POWER * 3 / 4; break;
				default: return false;
				}
				
				for (short s : transportPower.displayPower)
					if (s > minLevel)
						return true;

				return false;
			}
		}

		return false;
	}

	@Override
	public String getTextureFile() {
		return DefaultProps.TEXTURE_TRIGGERS;
	}

}
