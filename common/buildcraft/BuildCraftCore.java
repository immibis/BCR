/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft;

import java.io.File;
import java.util.TreeMap;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.event.ForgeSubscribe;
import buildcraft.api.core.BuildCraftAPI;
import buildcraft.api.gates.Action;
import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.Trigger;
import buildcraft.api.liquids.LiquidData;
import buildcraft.api.liquids.LiquidManager;
import buildcraft.api.liquids.LiquidStack;
import buildcraft.api.power.PowerFramework;
import buildcraft.core.*;
import buildcraft.core.blueprints.BptItem;
import buildcraft.core.network.EntityIds;
import buildcraft.core.network.PacketHandler;
import buildcraft.core.network.PacketUpdate;
import buildcraft.core.proxy.CoreProxy;
import buildcraft.core.triggers.*;
import buildcraft.core.triggers.ActionMachineControl.Mode;
import buildcraft.core.utils.Localization;
import buildcraft.transport.triggers.TriggerRedstoneInput;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(name="BuildCraft", version=Version.VERSION, useMetadata = false, modid = "BuildCraft|Core")
@NetworkMod(channels = {DefaultProps.NET_CHANNEL_NAME}, packetHandler = PacketHandler.class, clientSideRequired = true, serverSideRequired = true)
public class BuildCraftCore {
	public static enum RenderMode {
		Full, NoDynamic
	};

	public static RenderMode render = RenderMode.Full;

	public static boolean debugMode = false;
	public static boolean modifyWorld = false;
	public static boolean trackNetworkUsage = false;

	public static boolean dropBrokenBlocks = true; // Set to false to prevent the filler from dropping broken blocks.

	public static int updateFactor = 10;

	public static BuildCraftConfiguration mainConfiguration;

	public static TreeMap<BlockIndex, PacketUpdate> bufferedDescriptions = new TreeMap<BlockIndex, PacketUpdate>();

	public static final int trackedPassiveEntityId = 156;

	public static boolean continuousCurrentModel;

	public static Item woodenGearItem;
	public static Item stoneGearItem;
	public static Item ironGearItem;
	public static Item goldGearItem;
	public static Item diamondGearItem;
	public static Item wrenchItem;

	public static Icon redLaserTexture;
	public static Icon blueLaserTexture;
	public static Icon stripesLaserTexture;
	public static int transparentTexture;

	public static int blockByEntityModel;
	public static int legacyPipeModel;
	public static int markerModel;
	public static int oilModel;

	public static Trigger triggerMachineActive = new TriggerMachine(DefaultProps.TRIGGER_MACHINE_ACTIVE, true);
	public static Trigger triggerMachineInactive = new TriggerMachine(DefaultProps.TRIGGER_MACHINE_INACTIVE, false);
	public static Trigger triggerEmptyInventory = new TriggerInventory(DefaultProps.TRIGGER_EMPTY_INVENTORY, TriggerInventory.State.Empty);
	public static Trigger triggerContainsInventory = new TriggerInventory(DefaultProps.TRIGGER_CONTAINS_INVENTORY, TriggerInventory.State.Contains);
	public static Trigger triggerSpaceInventory = new TriggerInventory(DefaultProps.TRIGGER_SPACE_INVENTORY, TriggerInventory.State.Space);
	public static Trigger triggerFullInventory = new TriggerInventory(DefaultProps.TRIGGER_FULL_INVENTORY, TriggerInventory.State.Full);
	public static Trigger triggerEmptyLiquid = new TriggerLiquidContainer(DefaultProps.TRIGGER_EMPTY_LIQUID, TriggerLiquidContainer.State.Empty);
	public static Trigger triggerContainsLiquid = new TriggerLiquidContainer(DefaultProps.TRIGGER_CONTAINS_LIQUID, TriggerLiquidContainer.State.Contains);
	public static Trigger triggerSpaceLiquid = new TriggerLiquidContainer(DefaultProps.TRIGGER_SPACE_LIQUID, TriggerLiquidContainer.State.Space);
	public static Trigger triggerFullLiquid = new TriggerLiquidContainer(DefaultProps.TRIGGER_FULL_LIQUID, TriggerLiquidContainer.State.Full);
	public static Trigger triggerRedstoneActive = new TriggerRedstoneInput(DefaultProps.TRIGGER_REDSTONE_ACTIVE, true);
	public static Trigger triggerRedstoneInactive = new TriggerRedstoneInput(DefaultProps.TRIGGER_REDSTONE_INACTIVE, false);

	public static Action actionRedstone = new ActionRedstoneOutput(DefaultProps.ACTION_REDSTONE);
	public static Action actionOn = new ActionMachineControl(DefaultProps.ACTION_ON, Mode.On);
	public static Action actionOff = new ActionMachineControl(DefaultProps.ACTION_OFF, Mode.Off);
	public static Action actionLoop = new ActionMachineControl(DefaultProps.ACTION_LOOP, Mode.Loop);

	public static boolean loadDefaultRecipes = true;
	public static boolean forcePneumaticPower = true;
	public static boolean consumeWaterSources = true;

	public static BptItem[] itemBptProps = new BptItem[Item.itemsList.length];

	public static Logger bcLog = Logger.getLogger("Buildcraft");

	@Instance("BuildCraft|Core")
	public static BuildCraftCore instance;

	@ForgeSubscribe
	public void registerTextures(TextureStitchEvent evt) {
		if(evt.map.textureType == 0) {
			redLaserTexture = evt.map.registerIcon(DefaultProps.ICON_PREFIX + "laser-red");
			blueLaserTexture = evt.map.registerIcon(DefaultProps.ICON_PREFIX + "laser-blue");
			stripesLaserTexture = evt.map.registerIcon(DefaultProps.ICON_PREFIX + "laser-stripes");
		}
	}
	
	@PreInit
	public void loadConfiguration(FMLPreInitializationEvent evt) {
		
		Version.versionCheck();
		
		bcLog.setParent(FMLLog.getLogger());
		bcLog.info("Starting BuildCraft " + Version.getVersion());
		bcLog.info("Copyright (c) SpaceToad, 2011");
		bcLog.info("http://www.mod-buildcraft.com");

		mainConfiguration = new BuildCraftConfiguration(new File(evt.getModConfigurationDirectory(), "buildcraft/main.conf"));
		try
		{
			mainConfiguration.load();

			transparentTexture = 0 * 16 + 0;

			Property continuousCurrent = BuildCraftCore.mainConfiguration.get("current.continuous", Configuration.CATEGORY_GENERAL, DefaultProps.CURRENT_CONTINUOUS);
			continuousCurrent.comment = "set to true for allowing machines to be driven by continuous current";
			continuousCurrentModel = continuousCurrent.getBoolean(DefaultProps.CURRENT_CONTINUOUS);

			Property trackNetwork = BuildCraftCore.mainConfiguration.get("trackNetworkUsage", Configuration.CATEGORY_GENERAL, false);
			trackNetworkUsage = trackNetwork.getBoolean(false);

			Property dropBlock = BuildCraftCore.mainConfiguration.get("dropBrokenBlocks", Configuration.CATEGORY_GENERAL, true);
			dropBlock.comment = "set to false to prevent fillers from dropping blocks.";
			dropBrokenBlocks = dropBlock.getBoolean(true);

			Property powerFrameworkClass = BuildCraftCore.mainConfiguration.get("power.framework", Configuration.CATEGORY_GENERAL, "buildcraft.energy.PneumaticPowerFramework");

			Property factor = BuildCraftCore.mainConfiguration.get("network.updateFactor", Configuration.CATEGORY_GENERAL, 10);
			factor.comment = "increasing this number will decrease network update frequency, useful for overloaded servers";
			updateFactor = factor.getInt(10);

			String powerFrameworkClassName = "buildcraft.energy.PneumaticPowerFramework";
			if (!forcePneumaticPower)
			{
				powerFrameworkClassName = powerFrameworkClass.getString();
			}
			try {
				PowerFramework.currentFramework = (PowerFramework) Class.forName(powerFrameworkClassName).getConstructor().newInstance();
			} catch (Throwable e) {
				bcLog.throwing("BuildCraftCore", "loadConfiguration", e);
				PowerFramework.currentFramework = new RedstonePowerFramework();
			}

			Property wrenchId = BuildCraftCore.mainConfiguration.get("wrench.id", Configuration.CATEGORY_ITEM, DefaultProps.WRENCH_ID);

			wrenchItem = (new ItemWrench(wrenchId.getInt(DefaultProps.WRENCH_ID))).setUnlocalizedName(DefaultProps.ICON_PREFIX + "wrench");

			Property woodenGearId = BuildCraftCore.mainConfiguration.get("woodenGearItem.id", Configuration.CATEGORY_ITEM, DefaultProps.WOODEN_GEAR_ID);
			Property stoneGearId = BuildCraftCore.mainConfiguration.get("stoneGearItem.id", Configuration.CATEGORY_ITEM, DefaultProps.STONE_GEAR_ID);
			Property ironGearId = BuildCraftCore.mainConfiguration.get("ironGearItem.id", Configuration.CATEGORY_ITEM, DefaultProps.IRON_GEAR_ID);
			Property goldenGearId = BuildCraftCore.mainConfiguration.get("goldenGearItem.id", Configuration.CATEGORY_ITEM, DefaultProps.GOLDEN_GEAR_ID);
			Property diamondGearId = BuildCraftCore.mainConfiguration.get("diamondGearItem.id", Configuration.CATEGORY_ITEM, DefaultProps.DIAMOND_GEAR_ID);
			Property modifyWorld = BuildCraftCore.mainConfiguration.get("modifyWorld", Configuration.CATEGORY_GENERAL, true);
			modifyWorld.comment = "set to false if BuildCraft should not generate custom blocks (e.g. oil)";

			BuildCraftCore.modifyWorld = modifyWorld.getBoolean(true);

			woodenGearItem = (new Item(woodenGearId.getInt() - 256)).setUnlocalizedName(DefaultProps.ICON_PREFIX + "gear-wood");
			stoneGearItem = (new Item(stoneGearId.getInt() - 256)).setUnlocalizedName(DefaultProps.ICON_PREFIX + "gear-stone");
			ironGearItem = (new Item(ironGearId.getInt() - 256)).setUnlocalizedName(DefaultProps.ICON_PREFIX + "gear-iron");
			goldGearItem = (new Item(goldenGearId.getInt() - 256)).setUnlocalizedName(DefaultProps.ICON_PREFIX + "gear-gold");
			diamondGearItem = (new Item(diamondGearId.getInt() - 256)).setUnlocalizedName(DefaultProps.ICON_PREFIX + "gear-diamond");
		}
		finally
		{
			mainConfiguration.save();
		}
	}

	@Init
	public void initialize(FMLInitializationEvent evt) {
		
		MinecraftForge.EVENT_BUS.register(this);
		
		//MinecraftForge.registerConnectionHandler(new ConnectionHandler());
		LiquidManager.liquids.add(new LiquidData(new LiquidStack(Block.waterStill, LiquidManager.BUCKET_VOLUME), new LiquidStack(Block.waterMoving, LiquidManager.BUCKET_VOLUME), new ItemStack(Item.bucketWater), new ItemStack(Item.bucketEmpty)));
		LiquidManager.liquids.add(new LiquidData(new LiquidStack(Block.waterStill, LiquidManager.BUCKET_VOLUME), new LiquidStack(Block.waterMoving, LiquidManager.BUCKET_VOLUME), new ItemStack(Item.potion), new ItemStack(Item.glassBottle)));
		LiquidManager.liquids.add(new LiquidData(new LiquidStack(Block.lavaStill, LiquidManager.BUCKET_VOLUME), new LiquidStack(Block.lavaMoving, LiquidManager.BUCKET_VOLUME), new ItemStack(Item.bucketLava), new ItemStack(Item.bucketEmpty)));

		BuildCraftAPI.softBlocks[Block.tallGrass.blockID] = true;
		BuildCraftAPI.softBlocks[Block.snow.blockID] = true;
		BuildCraftAPI.softBlocks[Block.waterMoving.blockID] = true;
		BuildCraftAPI.softBlocks[Block.waterStill.blockID] = true;

		ActionManager.registerTriggerProvider(new DefaultTriggerProvider());
		ActionManager.registerActionProvider(new DefaultActionProvider());

		if (BuildCraftCore.loadDefaultRecipes)
		{
			loadRecipes();
		}
		EntityRegistry.registerModEntity(EntityRobot.class, "bcRobot", EntityIds.ROBOT, instance, 50, 1, true);
		EntityRegistry.registerModEntity(EntityPowerLaser.class, "bcLaser", EntityIds.LASER, instance, 50, 1, true);
		EntityRegistry.registerModEntity(EntityEnergyLaser.class, "bcEnergyLaser", EntityIds.ENERGY_LASER, instance, 50, 1, true);
		EntityList.classToStringMapping.remove(EntityRobot.class);
		EntityList.classToStringMapping.remove(EntityPowerLaser.class);
		EntityList.classToStringMapping.remove(EntityEnergyLaser.class);
		EntityList.stringToClassMapping.remove("BuildCraft|Core.bcRobot");
		EntityList.stringToClassMapping.remove("BuildCraft|Core.bcLaser");
		EntityList.stringToClassMapping.remove("BuildCraft|Core.bcEnergyLaser");

		CoreProxy.proxy.initializeRendering();
		CoreProxy.proxy.initializeEntityRendering();

		Localization.addLocalization("/lang/buildcraft/", DefaultProps.DEFAULT_LANGUAGE);

	}

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandBuildCraft());
	}
	
	public void loadRecipes() {
		GameRegistry.addRecipe(new ItemStack(wrenchItem), "I I", " G ", " I ", Character.valueOf('I'), Item.ingotIron, Character.valueOf('G'), stoneGearItem);
		GameRegistry.addRecipe(new ItemStack(woodenGearItem), " S ", "S S", " S ", Character.valueOf('S'), Item.stick);
		GameRegistry.addRecipe(new ItemStack(stoneGearItem), " I ", "IGI", " I ", Character.valueOf('I'), Block.cobblestone, Character.valueOf('G'), woodenGearItem);
		GameRegistry.addRecipe(new ItemStack(ironGearItem), " I ", "IGI", " I ", Character.valueOf('I'), Item.ingotIron, Character.valueOf('G'), stoneGearItem);
		GameRegistry.addRecipe(new ItemStack(goldGearItem), " I ", "IGI", " I ", Character.valueOf('I'), Item.ingotGold, Character.valueOf('G'), ironGearItem);
		GameRegistry.addRecipe(new ItemStack(diamondGearItem), " I ", "IGI", " I ", Character.valueOf('I'),Item.diamond, Character.valueOf('G'), goldGearItem);
	}
}
