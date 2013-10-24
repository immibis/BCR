package buildcraft.core.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import buildcraft.api.core.Orientations;

/**
 * This class is responsible for abstracting an ISidedInventory as a normal
 * IInventory
 * 
 * @author Krapht
 * 
 */
public class SidedInventoryAdapter implements IInventory {

	private final ISidedInventory _sidedInventory;
	private final Orientations _side;
	private final int[] _slots;

	public SidedInventoryAdapter(ISidedInventory sidedInventory, Orientations side) {
		_sidedInventory = sidedInventory;
		_side = side;
		_slots = _sidedInventory.getAccessibleSlotsFromSide(side.toDirection().ordinal());
	}

	@Override
	public int getSizeInventory() {
		return _slots.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return _sidedInventory.getStackInSlot(_slots[i]);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return _sidedInventory.decrStackSize(_slots[i], j);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		_sidedInventory.setInventorySlotContents(_slots[i], itemstack);
	}

	@Override
	public String getInvName() {
		return _sidedInventory.getInvName();
	}

	@Override
	public int getInventoryStackLimit() {
		return _sidedInventory.getInventoryStackLimit();
	}

	@Override
	public void onInventoryChanged() {
		_sidedInventory.onInventoryChanged();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return _sidedInventory.isUseableByPlayer(entityplayer);
	}

	@Override
	public void openChest() {
		_sidedInventory.openChest();
	}

	@Override
	public void closeChest() {
		_sidedInventory.closeChest();
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return _sidedInventory.getStackInSlotOnClosing(_slots[slot]);
	}

	@Override
	public boolean isInvNameLocalized() {
		return _sidedInventory.isInvNameLocalized();
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return _sidedInventory.isItemValidForSlot(_slots[i], itemstack);
	}
}
