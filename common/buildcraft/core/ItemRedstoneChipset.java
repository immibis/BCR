package buildcraft.core;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemRedstoneChipset extends Item {

	public ItemRedstoneChipset(int i) {
		super(i);

		setHasSubtypes(true);
		setMaxDamage(0);
		setCreativeTab(CreativeTabs.tabRedstone);
	}
	
	private Icon texRS, texIron, texGold, texDiamond, texEnder;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister r) {
		texRS = r.registerIcon(DefaultProps.ICON_PREFIX + "chipset-redstone");
		texIron = r.registerIcon(DefaultProps.ICON_PREFIX + "chipset-iron");
		texGold = r.registerIcon(DefaultProps.ICON_PREFIX + "chipset-gold");
		texDiamond = r.registerIcon(DefaultProps.ICON_PREFIX + "chipset-diamond");
		texEnder = r.registerIcon(DefaultProps.ICON_PREFIX + "chipset-ender");
	}
	

	@SuppressWarnings({ "all" })
	@Override
	public Icon getIconFromDamage(int i) {
		switch (i) {
		case 0:
			return texRS;
		case 1:
			return texIron;
		case 2:
			return texGold;
		case 3:
			return texDiamond;
		default:
			return texEnder;
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append(itemstack.getItemDamage()).toString();
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List itemList) {
		super.getSubItems(par1, par2CreativeTabs, itemList);
		for (int i = 0; i < 5; i++) {
			itemList.add(new ItemStack(this, 1, i));
		}
	}
}
