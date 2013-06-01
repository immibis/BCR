package buildcraft.silicon;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import buildcraft.BuildCraftSilicon;
import buildcraft.core.DefaultProps;
import buildcraft.core.GuiIds;
import buildcraft.core.proxy.CoreProxy;
import buildcraft.core.utils.Utils;
import buildcraft.factory.TileAssemblyTable;


public class BlockAssemblyTable extends BlockContainer {

	public BlockAssemblyTable(int i) {
		super(i, Material.iron);
		// TODO Auto-generated constructor stub

		setBlockBounds(0, 0, 0, 1, 9F / 16F, 1);
		setHardness(0.5F);
		setCreativeTab(CreativeTabs.tabRedstone);

	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean isACube() {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9) {
		// Drop through if the player is sneaking
		if (entityplayer.isSneaking())
			return false;

		if (!CoreProxy.proxy.isRenderWorld(world))
			entityplayer.openGui(BuildCraftSilicon.instance, GuiIds.ASSEMBLY_TABLE, world, i, j, k);
		return true;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		Utils.preDestroyBlock(world, x, y, z);
		super.breakBlock(world, x, y, z, par5, par6);
	}
	
	private Icon texTop, texBottom, texSide;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister r) {
		texTop = r.registerIcon(DefaultProps.ICON_PREFIX + "assembly-table-top");
		texBottom = r.registerIcon(DefaultProps.ICON_PREFIX + "assembly-table-bottom");
		texSide = r.registerIcon(DefaultProps.ICON_PREFIX + "assembly-table-side");
	}

	@Override
	public Icon getIcon(int i, int j) {
		if (i == 1) {
			return texTop;
		} else if (i == 0) {
			return texBottom;
		} else {
			return texSide;
		}
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new TileAssemblyTable();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addCreativeItems(ArrayList itemList) {
		itemList.add(new ItemStack(this));
	}
}
