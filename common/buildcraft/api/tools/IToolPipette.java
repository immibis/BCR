package buildcraft.api.tools;

import net.minecraft.item.ItemStack;
import buildcraft.api.liquids.LiquidStack;

public interface IToolPipette {
	
	/**
	 * @param pipette ItemStack of the pipette.
	 * @return Capacity of the pipette.
	 */
	int getCapacity(ItemStack pipette);
	/**
	 * @param pipette
	 * @return true if the pipette can pipette.
	 */
	boolean canPipette(ItemStack pipette);
	/**
	 * Fills the pipette with the given liquid stack.
	 * @param pipette
	 * @param liquid
	 * @param doFill
	 * @return Amount of liquid used in filling the pipette.
	 */
	int fill(ItemStack pipette, LiquidStack liquid, boolean doFill);
	/**
	 * Drains liquid from the pipette
	 * @param pipette
	 * @param maxDrain
	 * @param doDrain
	 * @return Liquid stack representing the liquid and amount drained from the pipette.
	 */
	LiquidStack drain(ItemStack pipette, int maxDrain, boolean doDrain);
}
