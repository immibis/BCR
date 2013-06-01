package buildcraft.core;

import net.minecraft.item.ItemBlock;

public class ItemBlockBuildCraft extends ItemBlock {

	protected String name;

	public ItemBlockBuildCraft(int id, String name) {
		super(id);
		this.name = name;
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

}
