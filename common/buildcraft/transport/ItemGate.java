package buildcraft.transport;

import java.util.List;

import buildcraft.core.DefaultProps;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemGate extends Item {

	private int series;

	public ItemGate(int i, int series) {
		super(i);

		this.series = series;

		setHasSubtypes(true);
		setMaxDamage(0);
		setCreativeTab(CreativeTabs.tabRedstone);
	}
	
	private Icon[] icons;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister r) {
		icons = new Icon[7];
		for(int k = 0; k < icons.length; k++)
			icons[k] = r.registerIcon(DefaultProps.ICON_PREFIX + (series > 0 ? "gate-a" : "gate-") + k);
	}

	@SuppressWarnings({ "all" })
	@Override
	public Icon getIconFromDamage(int i) {
		return i < 0 || i >= icons.length ? icons[0] : icons[i];
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append(itemstack.getItemDamage()).toString();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List itemList) {
		itemList.add(new ItemStack(this, 1, 0));
		itemList.add(new ItemStack(this, 1, 1));
		itemList.add(new ItemStack(this, 1, 2));
		itemList.add(new ItemStack(this, 1, 3));
		itemList.add(new ItemStack(this, 1, 4));
		itemList.add(new ItemStack(this, 1, 5));
		itemList.add(new ItemStack(this, 1, 6));
	}
}
