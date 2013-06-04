/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft;

import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.event.ForgeSubscribe;
import buildcraft.api.gates.Action;
import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.Trigger;
import buildcraft.api.recipes.AssemblyRecipe;
import buildcraft.api.transport.IExtractionHandler;
import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.PipeManager;
import buildcraft.core.DefaultProps;
import buildcraft.core.Version;
import buildcraft.transport.*;
import buildcraft.transport.blueprints.BptBlockPipe;
import buildcraft.transport.blueprints.BptItemPipeDiamond;
import buildcraft.transport.blueprints.BptItemPipeIron;
import buildcraft.transport.blueprints.BptItemPipeWooden;
import buildcraft.transport.network.PacketHandlerTransport;
import buildcraft.transport.pipes.*;
import buildcraft.transport.triggers.ActionEnergyPulser;
import buildcraft.transport.triggers.ActionSignalOutput;
import buildcraft.transport.triggers.TriggerPipeContents;
import buildcraft.transport.triggers.TriggerPipeContents.Kind;
import buildcraft.transport.triggers.TriggerPipeSignal;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(version = Version.VERSION, modid="BuildCraft|Transport", name = "Buildcraft Transport", dependencies=DefaultProps.DEPENDENCY_CORE)
@NetworkMod(channels={DefaultProps.NET_CHANNEL_NAME}, packetHandler = PacketHandlerTransport.class)
public class BuildCraftTransport {
	public static BlockGenericPipe genericPipeBlock;

	public static boolean alwaysConnectPipes;
	public static boolean usePipeLoss;
	public static int maxItemsInPipes;

	public static Item pipeWaterproof;
	public static Item pipeGate;
	public static Item pipeGateAutarchic;
	public static Item redPipeWire;
	public static Item bluePipeWire;
	public static Item greenPipeWire;
	public static Item yellowPipeWire;

	public static Item pipeItemsWood;
	public static Item pipeItemsStone;
	public static Item pipeItemsCobblestone;
	public static Item pipeItemsIron;
	public static Item pipeItemsGold;
	public static Item pipeItemsDiamond;
	public static Item pipeItemsObsidian;
	public static Item pipeItemsVoid;
	public static Item pipeItemsSandstone;

	public static Item pipeLiquidsWood;
	public static Item pipeLiquidsCobblestone;
	public static Item pipeLiquidsStone;
	public static Item pipeLiquidsIron;
	public static Item pipeLiquidsGold;
	public static Item pipeLiquidsVoid;
	public static Item pipeLiquidsSandstone;

	public static Item pipePowerWood;
	public static Item pipePowerStone;
	public static Item pipePowerIron;
	public static Item pipePowerGold;
	public static Item pipePowerDiamond;

	public static Item facadeItem;

	//public static Item pipeItemsStipes;
	public static Item pipeStructureCobblestone;
	public static int groupItemsTrigger;

	public static Trigger triggerPipeEmpty = new TriggerPipeContents(DefaultProps.TRIGGER_PIPE_EMPTY, Kind.Empty);
	public static Trigger triggerPipeItems = new TriggerPipeContents(DefaultProps.TRIGGER_PIPE_ITEMS, Kind.ContainsItems);
	public static Trigger triggerPipeLiquids = new TriggerPipeContents(DefaultProps.TRIGGER_PIPE_LIQUIDS, Kind.ContainsLiquids);
	public static Trigger triggerPipeEnergy = new TriggerPipeContents(DefaultProps.TRIGGER_PIPE_ENERGY, Kind.ContainsEnergy);
	public static Trigger triggerPipeEnergy25 = new TriggerPipeContents(DefaultProps.TRIGGER_PIPE_ENERGY_25, Kind.ContainsEnergy25);
	public static Trigger triggerPipeEnergy50 = new TriggerPipeContents(DefaultProps.TRIGGER_PIPE_ENERGY_50, Kind.ContainsEnergy50);
	public static Trigger triggerPipeEnergy75 = new TriggerPipeContents(DefaultProps.TRIGGER_PIPE_ENERGY_75, Kind.ContainsEnergy75);
	public static Trigger triggerRedSignalActive = new TriggerPipeSignal(DefaultProps.TRIGGER_RED_SIGNAL_ACTIVE, true, IPipe.WireColor.Red);
	public static Trigger triggerRedSignalInactive = new TriggerPipeSignal(DefaultProps.TRIGGER_RED_SIGNAL_INACTIVE, false, IPipe.WireColor.Red);
	public static Trigger triggerBlueSignalActive = new TriggerPipeSignal(DefaultProps.TRIGGER_BLUE_SIGNAL_ACTIVE, true, IPipe.WireColor.Blue);
	public static Trigger triggerBlueSignalInactive = new TriggerPipeSignal(DefaultProps.TRIGGER_BLUE_SIGNAL_INACTIVE, false, IPipe.WireColor.Blue);
	public static Trigger triggerGreenSignalActive = new TriggerPipeSignal(DefaultProps.TRIGGER_GREEN_SIGNAL_ACTIVE, true, IPipe.WireColor.Green);
	public static Trigger triggerGreenSignalInactive = new TriggerPipeSignal(DefaultProps.TRIGGER_GREEN_SIGNAL_INACTIVE, false, IPipe.WireColor.Green);
	public static Trigger triggerYellowSignalActive = new TriggerPipeSignal(DefaultProps.TRIGGER_YELLOW_SIGNAL_ACTIVE, true, IPipe.WireColor.Yellow);
	public static Trigger triggerYellowSignalInactive = new TriggerPipeSignal(DefaultProps.TRIGGER_YELLOW_SIGNAL_INACTIVE, false, IPipe.WireColor.Yellow);

	public static Action actionRedSignal = new ActionSignalOutput(DefaultProps.ACTION_RED_SIGNAL, IPipe.WireColor.Red);
	public static Action actionBlueSignal = new ActionSignalOutput(DefaultProps.ACTION_BLUE_SIGNAL, IPipe.WireColor.Blue);
	public static Action actionGreenSignal = new ActionSignalOutput(DefaultProps.ACTION_GREEN_SIGNAL, IPipe.WireColor.Green);
	public static Action actionYellowSignal = new ActionSignalOutput(DefaultProps.ACTION_YELLOW_SIGNAL, IPipe.WireColor.Yellow);
	public static Action actionEnergyPulser = new ActionEnergyPulser(DefaultProps.ACTION_ENERGY_PULSER);

	@Instance("BuildCraft|Transport")
	public static BuildCraftTransport instance;

	private static class PipeRecipe {
		boolean isShapeless = false; // pipe recipes come shaped and unshaped.
		ItemStack result;
		Object[] input;
	}

	private static class ExtractionHandler implements IExtractionHandler {
	      private final String[] items;
	      private final String[] liquids;

	      public ExtractionHandler(String[] items, String[] liquids){
	         this.items = items;
	         this.liquids = liquids;
	      }

	      @Override
	      public boolean canExtractItems(IPipe pipe, World world, int i, int j, int k) {
	         return testStrings(items, world, i, j, k);
	      }

	      @Override
	      public boolean canExtractLiquids(IPipe pipe, World world, int i, int j, int k) {
	         return testStrings(liquids, world, i, j, k);
	      }

	      private boolean testStrings(String[] excludedBlocks, World world, int i, int j, int k) {
	         int id = world.getBlockId(i, j, k);
	         Block block = Block.blocksList[id];
	         if(block == null)
	            return false;

	         int meta = world.getBlockMetadata(i, j, k);

	         for (String excluded : excludedBlocks) {
	            if (excluded.equals(block.getUnlocalizedName()))
	               return false;

	            String[] tokens = excluded.split(":");
	            if(tokens[0].equals(Integer.toString(id)) && (tokens.length == 1 || tokens[1].equals(Integer.toString(meta))))
	               return false;
	         }
	         return true;
	      }
	   }

	private static LinkedList<PipeRecipe> pipeRecipes = new LinkedList<PipeRecipe>();

	@PreInit
	public void preInitialize(FMLPreInitializationEvent evt)
	{
		try
		{
			Property alwaysConnect = BuildCraftCore.mainConfiguration.get("pipes.alwaysConnect", Configuration.CATEGORY_GENERAL, DefaultProps.PIPES_ALWAYS_CONNECT);
			alwaysConnect.comment = "set to false to deactivate pipe connection rules, true by default";
			alwaysConnectPipes = alwaysConnect.getBoolean(DefaultProps.PIPES_ALWAYS_CONNECT);

			/*Property pipeLoss = BuildCraftCore.mainConfiguration.get("power.usePipeLoss", Configuration.CATEGORY_GENERAL, DefaultProps.USE_PIPELOSS);
			pipeLoss.comment = "Set to false to turn off energy loss over distance on all power pipes";
			usePipeLoss = pipeLoss.getBoolean(DefaultProps.USE_PIPELOSS);*/
			
			usePipeLoss = false;

			Property exclusionItemList = BuildCraftCore.mainConfiguration.get("woodenPipe.item.exclusion", Configuration.CATEGORY_BLOCK, "");

			String[] excludedItemBlocks = exclusionItemList.getString().split(",");
			for (int j = 0; j < excludedItemBlocks.length; ++j)
			{
				excludedItemBlocks[j] = excludedItemBlocks[j].trim();
			}

			Property exclusionLiquidList = BuildCraftCore.mainConfiguration.get("woodenPipe.liquid.exclusion", Configuration.CATEGORY_BLOCK, "");

			String[] excludedLiquidBlocks = exclusionLiquidList.getString().split(",");
			for (int j = 0; j < excludedLiquidBlocks.length; ++j)
			{
				excludedLiquidBlocks[j] = excludedLiquidBlocks[j].trim();
			}

			PipeManager.registerExtractionHandler(new ExtractionHandler(excludedItemBlocks, excludedLiquidBlocks));

			Property maxItemInPipesProp = BuildCraftCore.mainConfiguration.get("pipes.maxItems", Configuration.CATEGORY_GENERAL, 100);
			maxItemInPipesProp.comment = "pipes containing more than this amount of items will explode, not dropping any item";

			maxItemsInPipes = maxItemInPipesProp.getInt();

			Property groupItemsTriggerProp = BuildCraftCore.mainConfiguration.get("pipes.groupItemsTrigger", Configuration.CATEGORY_GENERAL, 32);
			groupItemsTriggerProp.comment = "when reaching this amount of objects in a pipes, items will be automatically grouped";

			groupItemsTrigger = groupItemsTriggerProp.getInt();

			Property genericPipeId = BuildCraftCore.mainConfiguration.getBlock("pipe.id", DefaultProps.GENERIC_PIPE_ID);

			Property pipeWaterproofId = BuildCraftCore.mainConfiguration.get("pipeWaterproof.id", Configuration.CATEGORY_ITEM, DefaultProps.PIPE_WATERPROOF_ID);

			pipeWaterproof = new Item(pipeWaterproofId.getInt() - 256).setUnlocalizedName(DefaultProps.ICON_PREFIX + "waterproof");
			pipeWaterproof.setCreativeTab(CreativeTabs.tabMaterials);
			genericPipeBlock = new BlockGenericPipe(genericPipeId.getInt());
			GameRegistry.registerBlock(genericPipeBlock);

			// Fixing retro-compatiblity
			pipeItemsWood = createPipe(DefaultProps.PIPE_ITEMS_WOOD_ID, PipeItemsWood.class, Block.planks, Block.glass, Block.planks);
			pipeItemsCobblestone = createPipe(DefaultProps.PIPE_ITEMS_COBBLESTONE_ID, PipeItemsCobblestone.class, Block.cobblestone, Block.glass, Block.cobblestone);
			pipeItemsStone = createPipe(DefaultProps.PIPE_ITEMS_STONE_ID, PipeItemsStone.class, Block.stone, Block.glass, Block.stone);
			pipeItemsIron = createPipe(DefaultProps.PIPE_ITEMS_IRON_ID, PipeItemsIron.class, Item.ingotIron, Block.glass, Item.ingotIron);
			pipeItemsGold = createPipe(DefaultProps.PIPE_ITEMS_GOLD_ID, PipeItemsGold.class, Item.ingotGold, Block.glass, Item.ingotGold);
			pipeItemsDiamond = createPipe(DefaultProps.PIPE_ITEMS_DIAMOND_ID, PipeItemsDiamond.class, Item.diamond, Block.glass, Item.diamond);
			pipeItemsObsidian = createPipe(DefaultProps.PIPE_ITEMS_OBSIDIAN_ID, PipeItemsObsidian.class, Block.obsidian, Block.glass, Block.obsidian);

			pipeLiquidsWood = createPipe(DefaultProps.PIPE_LIQUIDS_WOOD_ID, PipeLiquidsWood.class, pipeWaterproof, pipeItemsWood, null);
			pipeLiquidsCobblestone = createPipe(DefaultProps.PIPE_LIQUIDS_COBBLESTONE_ID, PipeLiquidsCobblestone.class, pipeWaterproof, pipeItemsCobblestone, null);
			pipeLiquidsStone = createPipe(DefaultProps.PIPE_LIQUIDS_STONE_ID, PipeLiquidsStone.class, pipeWaterproof, pipeItemsStone, null);
			pipeLiquidsIron = createPipe(DefaultProps.PIPE_LIQUIDS_IRON_ID, PipeLiquidsIron.class, pipeWaterproof, pipeItemsIron, null);
			pipeLiquidsGold = createPipe(DefaultProps.PIPE_LIQUIDS_GOLD_ID, PipeLiquidsGold.class, pipeWaterproof, pipeItemsGold, null);
			// diamond
			// obsidian

			pipePowerWood = createPipe(DefaultProps.PIPE_POWER_WOOD_ID, PipePowerWood.class, Item.redstone, pipeItemsWood, null);
			// cobblestone
			pipePowerStone = createPipe(DefaultProps.PIPE_POWER_STONE_ID, PipePowerStone.class, Item.redstone, pipeItemsStone, null);
			pipePowerIron = createPipe(DefaultProps.PIPE_POWER_IRON_ID, PipePowerIron.class, Item.redstone, pipeItemsIron, null);
			pipePowerGold = createPipe(DefaultProps.PIPE_POWER_GOLD_ID, PipePowerGold.class, Item.redstone, pipeItemsGold, null);
			pipePowerDiamond = createPipe(DefaultProps.PIPE_POWER_DIAMOND_ID, PipePowerDiamond.class, Item.redstone, pipeItemsDiamond, null);
			// obsidian

			// Fix name and recipe (Structure pipe insteand of Signal?)
			pipeStructureCobblestone = createPipe(DefaultProps.PIPE_STRUCTURE_COBBLESTONE_ID, PipeStructureCobblestone.class, Block.gravel, pipeItemsCobblestone, null);

			// Fix the recipe
			//pipeItemsStipes = createPipe(DefaultProps.PIPE_ITEMS_STRIPES_ID, PipeItemsStripes.class, new ItemStack(Item.dyePowder, 1, 0), Block.glass, new ItemStack(Item.dyePowder, 1, 11));

			pipeItemsVoid = createPipe(DefaultProps.PIPE_ITEMS_VOID_ID, PipeItemsVoid.class, new ItemStack(Item.dyePowder, 1, 0), Block.glass, Item.redstone);

			pipeLiquidsVoid = createPipe(DefaultProps.PIPE_LIQUIDS_VOID_ID, PipeLiquidsVoid.class, pipeWaterproof, pipeItemsVoid, null);

			pipeItemsSandstone = createPipe(DefaultProps.PIPE_ITEMS_SANDSTONE_ID, PipeItemsSandstone.class, Block.sandStone, Block.glass, Block.sandStone);

			pipeLiquidsSandstone = createPipe(DefaultProps.PIPE_LIQUIDS_SANDSTONE_ID, PipeLiquidsSandstone.class, pipeWaterproof, pipeItemsSandstone, null);
		}
		finally
		{
			BuildCraftCore.mainConfiguration.save();
		}
	}
	
	@ForgeSubscribe
	public void registerIcons(TextureStitchEvent evt) {
		if(evt.map.textureType == 0) {
			for(Item i : Item.itemsList) {
				if(i instanceof ItemPipe)
					((ItemPipe)i).registerPipeIcons(evt.map);
				if(i instanceof ItemGate)
					((ItemGate)i).registerWorldIcons(evt.map);
			}
		
			IPipe.WireColor.Blue.iconOn  = evt.map.registerIcon(DefaultProps.ICON_PREFIX + "wires/blue1");
			IPipe.WireColor.Blue.iconOff = evt.map.registerIcon(DefaultProps.ICON_PREFIX + "wires/blue0");
			IPipe.WireColor.Red.iconOn  = evt.map.registerIcon(DefaultProps.ICON_PREFIX + "wires/red1");
			IPipe.WireColor.Red.iconOff = evt.map.registerIcon(DefaultProps.ICON_PREFIX + "wires/red0");
			IPipe.WireColor.Green.iconOn  = evt.map.registerIcon(DefaultProps.ICON_PREFIX + "wires/green1");
			IPipe.WireColor.Green.iconOff = evt.map.registerIcon(DefaultProps.ICON_PREFIX + "wires/green0");
			IPipe.WireColor.Yellow.iconOn  = evt.map.registerIcon(DefaultProps.ICON_PREFIX + "wires/yellow1");
			IPipe.WireColor.Yellow.iconOff = evt.map.registerIcon(DefaultProps.ICON_PREFIX + "wires/yellow0");
		}
	}
	
	@Init
	public void load(FMLInitializationEvent evt) {
		// Register connection handler
		//MinecraftForge.registerConnectionHandler(new ConnectionHandler());

		// Register gui handler
		//MinecraftForge.setGuiHandler(mod_BuildCraftTransport.instance, new GuiHandler());

		MinecraftForge.EVENT_BUS.register(this);
		
		TransportProxy.proxy.registerTileEntities();

		// dockingStationBlock = new
		// BlockDockingStation(Integer.parseInt(dockingStationId.value));
		// ModLoader.registerBlock(dockingStationBlock);
		// CoreProxy.addName(dockingStationBlock.setBlockName("dockingStation"),
		// "Docking Station");

		// ModLoader.RegisterTileEntity(TileDockingStation.class,
		// "net.minecraft.src.buildcraft.TileDockingStation");

		Property redPipeWireId = BuildCraftCore.mainConfiguration.get("redPipeWire.id", Configuration.CATEGORY_ITEM, DefaultProps.RED_PIPE_WIRE);
		redPipeWire = new Item(redPipeWireId.getInt() - 256).setUnlocalizedName(DefaultProps.ICON_PREFIX + "wire-red").setCreativeTab(CreativeTabs.tabRedstone);
		AssemblyRecipe.assemblyRecipes.add(new AssemblyRecipe(new ItemStack[] { new ItemStack(Item.dyePowder, 1, 1),
				new ItemStack(Item.redstone, 1), new ItemStack(Item.ingotIron, 1) }, 500, new ItemStack(redPipeWire, 8)));

		Property bluePipeWireId = BuildCraftCore.mainConfiguration.get("bluePipeWire.id", Configuration.CATEGORY_ITEM, DefaultProps.BLUE_PIPE_WIRE);
		bluePipeWire = new Item(bluePipeWireId.getInt() - 256).setUnlocalizedName(DefaultProps.ICON_PREFIX + "wire-blue").setCreativeTab(CreativeTabs.tabRedstone);
		AssemblyRecipe.assemblyRecipes.add(new AssemblyRecipe(new ItemStack[] { new ItemStack(Item.dyePowder, 1, 4),
				new ItemStack(Item.redstone, 1), new ItemStack(Item.ingotIron, 1) }, 500, new ItemStack(bluePipeWire, 8)));

		Property greenPipeWireId = BuildCraftCore.mainConfiguration.get("greenPipeWire.id", Configuration.CATEGORY_ITEM, DefaultProps.GREEN_PIPE_WIRE);
		greenPipeWire = new Item(greenPipeWireId.getInt() - 256).setUnlocalizedName(DefaultProps.ICON_PREFIX + "wire-green").setCreativeTab(CreativeTabs.tabRedstone);
		AssemblyRecipe.assemblyRecipes.add(new AssemblyRecipe(new ItemStack[] { new ItemStack(Item.dyePowder, 1, 2),
				new ItemStack(Item.redstone, 1), new ItemStack(Item.ingotIron, 1) }, 500, new ItemStack(greenPipeWire, 8)));

		Property yellowPipeWireId = BuildCraftCore.mainConfiguration.get("yellowPipeWire.id", Configuration.CATEGORY_ITEM, DefaultProps.YELLOW_PIPE_WIRE);
		yellowPipeWire = new Item(yellowPipeWireId.getInt() - 256).setUnlocalizedName(DefaultProps.ICON_PREFIX + "wire-yellow").setCreativeTab(CreativeTabs.tabRedstone);
		AssemblyRecipe.assemblyRecipes.add(new AssemblyRecipe(new ItemStack[] { new ItemStack(Item.dyePowder, 1, 11),
				new ItemStack(Item.redstone, 1), new ItemStack(Item.ingotIron, 1) }, 500, new ItemStack(yellowPipeWire, 8)));

		Property pipeGateId = BuildCraftCore.mainConfiguration.get("pipeGate.id", Configuration.CATEGORY_ITEM, DefaultProps.GATE_ID);
		pipeGate = new ItemGate(pipeGateId.getInt() - 256, 0);
		pipeGate.setUnlocalizedName("pipeGate");

		Property pipeGateAutarchicId = BuildCraftCore.mainConfiguration.get("pipeGateAutarchic.id", Configuration.CATEGORY_ITEM, DefaultProps.GATE_AUTARCHIC_ID);
		pipeGateAutarchic = new ItemGate(pipeGateAutarchicId.getInt() - 256, 1);
		pipeGateAutarchic.setUnlocalizedName("pipeGateAutarchic");

		Property pipeFacadeId = BuildCraftCore.mainConfiguration.get("pipeFacade.id", Configuration.CATEGORY_ITEM, DefaultProps.PIPE_FACADE_ID);
		facadeItem = new ItemFacade(pipeFacadeId.getInt() - 256);
		facadeItem.setUnlocalizedName(DefaultProps.ICON_PREFIX + "facade");
		ItemFacade.initialize();

		BuildCraftCore.mainConfiguration.save();

		new BptBlockPipe(genericPipeBlock.blockID);

		BuildCraftCore.itemBptProps[pipeItemsWood.itemID] = new BptItemPipeWooden();
		BuildCraftCore.itemBptProps[pipeLiquidsWood.itemID] = new BptItemPipeWooden();
		BuildCraftCore.itemBptProps[pipeItemsIron.itemID] = new BptItemPipeIron();
		BuildCraftCore.itemBptProps[pipeLiquidsIron.itemID] = new BptItemPipeIron();
		BuildCraftCore.itemBptProps[pipeItemsDiamond.itemID] = new BptItemPipeDiamond();

		ActionManager.registerTriggerProvider(new PipeTriggerProvider());

		if (BuildCraftCore.loadDefaultRecipes)
		{
			loadRecipes();
		}

		TransportProxy.proxy.registerRenderers();
		NetworkRegistry.instance().registerGuiHandler(instance, new GuiHandler());
	}

   public void loadRecipes() {

		// Add base recipe for pipe waterproof.
		GameRegistry.addShapelessRecipe(new ItemStack(pipeWaterproof, 1), new ItemStack(Item.dyePowder, 1, 2));

		// Add pipe recipes
		for (PipeRecipe pipe : pipeRecipes)
		{
			if (pipe.isShapeless)
			{
				GameRegistry.addShapelessRecipe(pipe.result, pipe.input);
			}
			else
			{
				GameRegistry.addRecipe(pipe.result, pipe.input);
			}
		}
	}

	private static Item createPipe(int defaultID, Class<? extends Pipe> clas, Object ingredient1, Object ingredient2, Object ingredient3) {
		String name = Character.toLowerCase(clas.getSimpleName().charAt(0)) + clas.getSimpleName().substring(1);

		Property prop = BuildCraftCore.mainConfiguration.get(name + ".id", Configuration.CATEGORY_ITEM, defaultID);

		int id = prop.getInt(defaultID);
		ItemPipe res = BlockGenericPipe.registerPipe(id - 256, clas);
		res.setUnlocalizedName(clas.getSimpleName());

		// Add appropriate recipe to temporary list
		PipeRecipe recipe = new PipeRecipe();

		if (ingredient1 != null && ingredient2 != null && ingredient3 != null) {
			recipe.result = new ItemStack(res, 8);
			recipe.input = new Object[] { "ABC", Character.valueOf('A'), ingredient1, Character.valueOf('B'), ingredient2, Character.valueOf('C'), ingredient3 };

			pipeRecipes.add(recipe);
		} else if (ingredient1 != null && ingredient2 != null) {
			recipe.isShapeless = true;
			recipe.result = new ItemStack(res, 1);
			recipe.input = new Object[] { ingredient1, ingredient2 };

			pipeRecipes.add(recipe);
		}

		return res;
	}
}
