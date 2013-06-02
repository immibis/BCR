package buildcraft.transport;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import buildcraft.BuildCraftTransport;
import buildcraft.api.core.Orientations;
import buildcraft.api.recipes.AssemblyRecipe;
import buildcraft.core.proxy.CoreProxy;
import buildcraft.core.utils.StringUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemFacade extends Item {

	public final static LinkedList<ItemStack> allFacades = new LinkedList<ItemStack>();
	
	public ItemFacade(int i) {
		super(i);

		setHasSubtypes(true);
		setMaxDamage(0);
		this.setCreativeTab(CreativeTabs.tabMisc);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
	}
	
	@Override
	public String getItemDisplayName(ItemStack itemstack) {
		String name = "" + StringUtil.localize(getUnlocalizedName(itemstack));
		int decodedBlockId = ItemFacade.getBlockId(itemstack.getItemDamage());
		int decodedMeta = ItemFacade.getMetaData(itemstack.getItemDamage());
		ItemStack newStack = new ItemStack(decodedBlockId, 1, decodedMeta);
		if (Item.itemsList[decodedBlockId] != null){
			name += ": " + CoreProxy.proxy.getItemDisplayName(newStack);
		} else {
			name += " < BROKEN (" + decodedBlockId + ":"+ decodedMeta +" )>";
		}
		return name; 
	}

	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List itemList) {
		//Do not call super, that would add a 0:0 facade
		for (ItemStack stack : allFacades){
			itemList.add(stack.copy());
		}
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World worldObj, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (worldObj.isRemote) return false;
		TileEntity tile = worldObj.getBlockTileEntity(x, y, z);
		if (!(tile instanceof TileGenericPipe)) return false;
		TileGenericPipe pipeTile = (TileGenericPipe)tile;

		if (player.isSneaking()) { //Strip facade
			if (!pipeTile.hasFacade(Orientations.dirs()[side])) return false;
			pipeTile.dropFacade(Orientations.dirs()[side]);
			return true;
		} else {
			if (((TileGenericPipe)tile).addFacade(Orientations.values()[side], ItemFacade.getBlockId(stack.getItemDamage()), ItemFacade.getMetaData(stack.getItemDamage()))){
				stack.stackSize--;	
				return true;
			}
			return false;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void initialize(){
		List creativeItems = getCreativeContents();
		ListIterator creativeIterator = creativeItems.listIterator();
		
		while(creativeIterator.hasNext()){
			ItemStack stack = (ItemStack) creativeIterator.next();
			if (stack.getItem() instanceof ItemBlock){
				ItemBlock itemBlock = (ItemBlock) stack.getItem();
				int blockId = itemBlock.getBlockID();
				//Block certain IDs (Bedrock, leaves and spunge)
				if (blockId == 7 || blockId == 18 || blockId == 19) continue; 
				
				Block b = Block.blocksList[blockId];
				if(b == null)
					continue;
				if(b.getIcon(0, stack.getItemDamage()) == null) continue;
				if(b.getIcon(1, stack.getItemDamage()) == null) continue;
				if(b.getIcon(2, stack.getItemDamage()) == null) continue;
				if(b.getIcon(3, stack.getItemDamage()) == null) continue;
				if(b.getIcon(4, stack.getItemDamage()) == null) continue;
				if(b.getIcon(5, stack.getItemDamage()) == null) continue;

				if (Block.blocksList[blockId] != null 
					&& Block.blocksList[blockId].isOpaqueCube() 
					&& Block.blocksList[blockId].getUnlocalizedName() != null 
					&& !Block.blocksList[blockId].hasTileEntity(0) 
					&& Block.blocksList[blockId].renderAsNormalBlock())
				{
					allFacades.add(new ItemStack(BuildCraftTransport.facadeItem, 1, ItemFacade.encode(blockId, stack.getItemDamage())));
					
					//3 Structurepipes + this block makes 6 facades
					AssemblyRecipe.assemblyRecipes.add(
							new AssemblyRecipe(
									new ItemStack[] {new ItemStack(BuildCraftTransport.pipeStructureCobblestone, 3), new ItemStack(blockId, 1, stack.getItemDamage())}, 
									8000, 
									new ItemStack(BuildCraftTransport.facadeItem, 6, ItemFacade.encode(blockId,  stack.getItemDamage()))));
				}
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List getCreativeContents(){
		List itemList = new ArrayList();
		
		for (Block block : Block.blocksList)
            if (block != null)
            	CoreProxy.proxy.feedSubBlocks(block.blockID, null, itemList);

        return itemList;
	}
	
	public static int encode(int blockId, int metaData){
		return metaData + (blockId << 4);
	}
	
	public static int getMetaData(int encoded){
		return encoded & 0x0000F;
	}
	
	public static int getBlockId(int encoded){
		return encoded >>> 4;
	}
	
	
}

