/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.transport.pipes;

import java.util.LinkedList;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import buildcraft.api.core.Orientations;
import buildcraft.api.core.Position;
import buildcraft.api.transport.IPipedItem;
import buildcraft.transport.IPipeTransportItemsHook;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportItems;

public class PipeItemsDiamond extends Pipe implements IPipeTransportItemsHook {

	public PipeItemsDiamond(int itemID) {
		super(new PipeTransportItems(), new PipeLogicDiamond(), itemID);
	}
	
	private Icon iconSide[] = new Icon[7];
	
	@Override
	public void registerIcons(IconRegister r) {
		super.registerIcons(r);
		for(int k = 0; k < 6; k++)
			iconSide[k] = r.registerIcon(getDefaultIconPath() + "-" + k);
		iconSide[6] = getTextureForItem();
	}
	
	@Override
	public Icon getTexture(Orientations direction) {
		return iconSide[direction.ordinal()];
	}

	@Override
	public LinkedList<Orientations> filterPossibleMovements(LinkedList<Orientations> possibleOrientations, Position pos,
			IPipedItem item) {
		LinkedList<Orientations> filteredOrientations = new LinkedList<Orientations>();
		LinkedList<Orientations> defaultOrientations = new LinkedList<Orientations>();

		// Filtered outputs
		for (Orientations dir : possibleOrientations) {
			boolean foundFilter = false;

			// NB: if there's several of the same match, the probability
			// to use that filter is higher, this is why there are
			// no breaks here.
			PipeLogicDiamond diamondLogic = (PipeLogicDiamond)logic;
			for (int slot = 0; slot < 9; ++slot) {
				ItemStack stack = diamondLogic.getStackInSlot(dir.ordinal() * 9 + slot);

				if (stack != null)
					foundFilter = true;

				if (stack != null && stack.itemID == item.getItemStack().itemID)
					if ((Item.itemsList[item.getItemStack().itemID].isDamageable()))
						filteredOrientations.add(dir);
					else if (stack.getItemDamage() == item.getItemStack().getItemDamage())
						filteredOrientations.add(dir);
			}
			if (!foundFilter)
				defaultOrientations.add(dir);
		}
		if (filteredOrientations.size() != 0)
			return filteredOrientations;
		else
			return defaultOrientations;
	}

	@Override
	public void entityEntered(IPipedItem item, Orientations orientation) {

	}

	@Override
	public void readjustSpeed(IPipedItem item) {
		((PipeTransportItems) transport).defaultReajustSpeed(item);
	}

}
