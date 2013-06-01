package buildcraft.core.inventory;

import net.minecraft.item.ItemStack;
import buildcraft.api.core.Orientations;

public interface ITransactor {

	ItemStack add(ItemStack stack, Orientations orientation, boolean doAdd);

}
