package buildcraft.api.filler;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import buildcraft.api.core.IBox;

public interface IFillerPattern {

	public int getId();

	public void setId(int id);

	public boolean iteratePattern(TileEntity tile, IBox box, ItemStack stackToPlace);

	public Icon getTexture();

	public String getName();

}
