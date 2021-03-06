/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.event.ForgeSubscribe;
import buildcraft.core.DefaultProps;
import buildcraft.core.Version;
import buildcraft.core.proxy.CoreProxy;
import buildcraft.factory.*;
import buildcraft.factory.network.PacketHandlerFactory;
import buildcraft.silicon.TileLaser;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(name="BuildCraft Factory", version=Version.VERSION, useMetadata = false, modid = "BuildCraft|Factory", dependencies = DefaultProps.DEPENDENCY_CORE)
@NetworkMod(channels = {DefaultProps.NET_CHANNEL_NAME}, packetHandler = PacketHandlerFactory.class, clientSideRequired = true, serverSideRequired = true)
public class BuildCraftFactory {

	public static BlockQuarry quarryBlock;
	public static BlockMiningWell miningWellBlock;
	public static BlockAutoWorkbench autoWorkbenchBlock;
	public static BlockFrame frameBlock;
	public static BlockPlainPipe plainPipeBlock;
	public static BlockPump pumpBlock;
	public static BlockTank tankBlock;
	public static BlockRefinery refineryBlock;
	public static BlockHopper hopperBlock;
	public static boolean hopperDisabled;

	public static Icon drillTexture;
	public static Icon quarryHeadTexture;
	public static Icon pumpShaftTexture;

	public static boolean allowMining = true;

	@Instance("BuildCraft|Factory")
	public static BuildCraftFactory instance;

	@ForgeSubscribe
	public void registerTextures(TextureStitchEvent evt) {
		if(evt.map.textureType == 0) {
			drillTexture = evt.map.registerIcon(DefaultProps.ICON_PREFIX + "quarry-drill");
			quarryHeadTexture = evt.map.registerIcon(DefaultProps.ICON_PREFIX + "quarry-head");
			pumpShaftTexture = evt.map.registerIcon(DefaultProps.ICON_PREFIX + "pump-shaft");
		}
	}
	
	@PostInit
	public void postInit(FMLPostInitializationEvent evt) {
		FactoryProxy.proxy.initializeNEIIntegration();
	}
	@Init
	public void load(FMLInitializationEvent evt) {
		MinecraftForge.EVENT_BUS.register(this);
		
		NetworkRegistry.instance().registerGuiHandler(instance, new GuiHandler());

//		EntityRegistry.registerModEntity(EntityMechanicalArm.class, "bcMechanicalArm", EntityIds.MECHANICAL_ARM, instance, 50, 1, true);

		CoreProxy.proxy.registerTileEntity(TileQuarry.class, "Machine");
		CoreProxy.proxy.registerTileEntity(TileMiningWell.class, "MiningWell");
		CoreProxy.proxy.registerTileEntity(TileAutoWorkbench.class, "AutoWorkbench");
		CoreProxy.proxy.registerTileEntity(TilePump.class, "net.minecraft.src.buildcraft.factory.TilePump");
		CoreProxy.proxy.registerTileEntity(TileTank.class, "net.minecraft.src.buildcraft.factory.TileTank");
		CoreProxy.proxy.registerTileEntity(TileRefinery.class, "net.minecraft.src.buildcraft.factory.Refinery");
		CoreProxy.proxy.registerTileEntity(TileLaser.class, "net.minecraft.src.buildcraft.factory.TileLaser");
		CoreProxy.proxy.registerTileEntity(TileAssemblyTable.class, "net.minecraft.src.buildcraft.factory.TileAssemblyTable");

		if (!hopperDisabled) {
			CoreProxy.proxy.registerTileEntity(TileHopper.class, "net.minecraft.src.buildcraft.factory.TileHopper");
		}

		FactoryProxy.proxy.initializeTileEntities();
		FactoryProxy.proxy.initializeEntityRenders();

		new BptBlockAutoWorkbench(autoWorkbenchBlock.blockID);
		new BptBlockFrame(frameBlock.blockID);
		new BptBlockRefinery(refineryBlock.blockID);
		new BptBlockTank(tankBlock.blockID);

		if (BuildCraftCore.loadDefaultRecipes)
			loadRecipes();
	}

	@PreInit
	public void initialize(FMLPreInitializationEvent evt) {
		allowMining = BuildCraftCore.mainConfiguration.get("mining.enabled", Configuration.CATEGORY_GENERAL, true).getBoolean(true);

		Property minigWellId = BuildCraftCore.mainConfiguration.getBlock("miningWell.id", DefaultProps.MINING_WELL_ID);
		Property plainPipeId = BuildCraftCore.mainConfiguration.getBlock("drill.id", DefaultProps.DRILL_ID);
		Property autoWorkbenchId = BuildCraftCore.mainConfiguration.getBlock("autoWorkbench.id", DefaultProps.AUTO_WORKBENCH_ID);
		Property frameId = BuildCraftCore.mainConfiguration.getBlock("frame.id", DefaultProps.FRAME_ID);
		Property quarryId = BuildCraftCore.mainConfiguration.getBlock("quarry.id", DefaultProps.QUARRY_ID);
		Property pumpId = BuildCraftCore.mainConfiguration.getBlock("pump.id", DefaultProps.PUMP_ID);
		Property tankId = BuildCraftCore.mainConfiguration.getBlock("tank.id", DefaultProps.TANK_ID);
		Property refineryId = BuildCraftCore.mainConfiguration.getBlock("refinery.id", DefaultProps.REFINERY_ID);
		Property hopperId = BuildCraftCore.mainConfiguration.getBlock("hopper.id", DefaultProps.HOPPER_ID);
		Property hopperDisable = BuildCraftCore.mainConfiguration.get("hopper.disabled", "Block Savers", false);

		BuildCraftCore.mainConfiguration.save();

		miningWellBlock = new BlockMiningWell(minigWellId.getInt());
		miningWellBlock.setUnlocalizedName(DefaultProps.ICON_PREFIX + "miningwell");
		CoreProxy.proxy.registerBlock(miningWellBlock);

		plainPipeBlock = new BlockPlainPipe(plainPipeId.getInt());
		plainPipeBlock.setUnlocalizedName(DefaultProps.ICON_PREFIX + "drill-pipe");
		CoreProxy.proxy.registerBlock(plainPipeBlock);

		autoWorkbenchBlock = new BlockAutoWorkbench(autoWorkbenchId.getInt());
		autoWorkbenchBlock.setUnlocalizedName(DefaultProps.ICON_PREFIX + "autoworkbench");
		CoreProxy.proxy.registerBlock(autoWorkbenchBlock);

		frameBlock = new BlockFrame(frameId.getInt());
		frameBlock.setUnlocalizedName(DefaultProps.ICON_PREFIX + "frame");
		CoreProxy.proxy.registerBlock(frameBlock);

		quarryBlock = new BlockQuarry(quarryId.getInt());
		quarryBlock.setUnlocalizedName(DefaultProps.ICON_PREFIX + "quarry");
		CoreProxy.proxy.registerBlock(quarryBlock);

		tankBlock = new BlockTank(tankId.getInt());
		tankBlock.setUnlocalizedName(DefaultProps.ICON_PREFIX + "tank");
		CoreProxy.proxy.registerBlock(tankBlock);

		pumpBlock = new BlockPump(pumpId.getInt());
		pumpBlock.setUnlocalizedName(DefaultProps.ICON_PREFIX + "pump");
		CoreProxy.proxy.registerBlock(pumpBlock);

		refineryBlock = new BlockRefinery(refineryId.getInt());
		refineryBlock.setUnlocalizedName(DefaultProps.ICON_PREFIX + "refinery");
		CoreProxy.proxy.registerBlock(refineryBlock);

		hopperDisabled = hopperDisable.getBoolean(false);
		if (!hopperDisabled) {
			hopperBlock = new BlockHopper(hopperId.getInt());
			hopperBlock.setUnlocalizedName(DefaultProps.ICON_PREFIX + "hopper");
			CoreProxy.proxy.registerBlock(hopperBlock);
		}

		BuildCraftCore.mainConfiguration.save();
	}

	public static void loadRecipes() {

		if (allowMining) {
			CoreProxy.proxy.addCraftingRecipe(new ItemStack(miningWellBlock, 1),
					new Object[] { "ipi", "igi", "iPi", Character.valueOf('p'), Item.redstone, Character.valueOf('i'),
							Item.ingotIron, Character.valueOf('g'), BuildCraftCore.ironGearItem, Character.valueOf('P'),
							Item.pickaxeIron });

			CoreProxy.proxy.addCraftingRecipe(new ItemStack(quarryBlock), new Object[] { "ipi", "gig", "dDd", Character.valueOf('i'),
					BuildCraftCore.ironGearItem, Character.valueOf('p'), Item.redstone, Character.valueOf('g'),
					BuildCraftCore.goldGearItem, Character.valueOf('d'), BuildCraftCore.diamondGearItem, Character.valueOf('D'),
					Item.pickaxeDiamond, });
		}

		CoreProxy.proxy.addCraftingRecipe(new ItemStack(autoWorkbenchBlock), new Object[] { " g ", "gwg", " g ", Character.valueOf('w'),
				Block.workbench, Character.valueOf('g'), BuildCraftCore.woodenGearItem });

		CoreProxy.proxy.addCraftingRecipe(new ItemStack(pumpBlock), new Object[] { "T ", "W ", Character.valueOf('T'), tankBlock,
				Character.valueOf('W'), miningWellBlock, });

		CoreProxy.proxy.addCraftingRecipe(new ItemStack(tankBlock), new Object[] { "ggg", "g g", "ggg", Character.valueOf('g'),
				Block.glass, });

		CoreProxy.proxy.addCraftingRecipe(new ItemStack(refineryBlock), new Object[] { "   ", "RTR", "TGT", Character.valueOf('T'),
				tankBlock, Character.valueOf('G'), BuildCraftCore.diamondGearItem, Character.valueOf('R'),
				Block.torchRedstoneActive, });
		if (!hopperDisabled) {
			CoreProxy.proxy.addCraftingRecipe(new ItemStack(hopperBlock), new Object[] { "ICI", "IGI", " I ", Character.valueOf('I'),
					Item.ingotIron, Character.valueOf('C'), Block.chest, Character.valueOf('G'), BuildCraftCore.stoneGearItem });
		}

	}
}
