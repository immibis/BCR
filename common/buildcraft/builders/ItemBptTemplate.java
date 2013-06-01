package buildcraft.builders;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import buildcraft.BuildCraftBuilders;
import buildcraft.core.DefaultProps;
import buildcraft.core.blueprints.BptBase;

public class ItemBptTemplate extends ItemBptBase {

	public ItemBptTemplate(int i) {
		super(i);
	}

	private Icon tex, texBlank;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister r) {
		tex = r.registerIcon(DefaultProps.ICON_PREFIX + "template");
		texBlank = r.registerIcon(DefaultProps.ICON_PREFIX + "template-blank");
	}

	@Override
	public Icon getIconFromDamage(int i) {
		BptBase bpt = BuildCraftBuilders.getBptRootIndex().getBluePrint(i);
		if (bpt == null) {
			return texBlank;
		} else {
			return tex;
		}
	}
}
