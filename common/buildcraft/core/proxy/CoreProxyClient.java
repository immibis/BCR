/**
 * Copyright (c) SpaceToad, 2011
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.core.proxy;

import java.io.File;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import buildcraft.BuildCraftCore;
import buildcraft.core.EntityBlock;
import buildcraft.core.EntityEnergyLaser;
import buildcraft.core.EntityPowerLaser;
import buildcraft.core.EntityRobot;
import buildcraft.core.render.RenderEnergyLaser;
import buildcraft.core.render.RenderEntityBlock;
import buildcraft.core.render.RenderLaser;
import buildcraft.core.render.RenderRobot;
import buildcraft.core.render.RenderingEntityBlocks;
import buildcraft.core.render.RenderingMarkers;
import buildcraft.core.render.RenderingOil;
import buildcraft.transport.render.TileEntityPickupFX;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class CoreProxyClient extends CoreProxy {

	/* INSTANCES */
	@Override
	public Object getClient() {
		return FMLClientHandler.instance().getClient();
	}
	
	@Override
	public World getClientWorld() {
		return FMLClientHandler.instance().getClient().theWorld;
	}

	/* ENTITY HANDLING */
	@Override
	public void removeEntity(Entity entity) {
		super.removeEntity(entity);

		if (isRenderWorld(entity.worldObj))
			((WorldClient) entity.worldObj).removeEntityFromWorld(entity.entityId);
	}

	/* WRAPPER */
	@Override
	public void feedSubBlocks(int id, CreativeTabs tab, List itemList) {
		if(Block.blocksList[id] == null)
			return;
		
		Block.blocksList[id].getSubBlocks(id, tab, itemList);
	}
	
	/* GFX */
	@Override
	public void obsidianPipePickup(World world, EntityItem item, TileEntity tile) {
		FMLClientHandler.instance().getClient().effectRenderer.addEffect(new TileEntityPickupFX(world, item, tile));
	}

	@Override
	public void initializeRendering() {
		BuildCraftCore.blockByEntityModel = RenderingRegistry.getNextAvailableRenderId();
		BuildCraftCore.legacyPipeModel = RenderingRegistry.getNextAvailableRenderId();
		BuildCraftCore.markerModel = RenderingRegistry.getNextAvailableRenderId();
		BuildCraftCore.oilModel = RenderingRegistry.getNextAvailableRenderId();

		RenderingRegistry.registerBlockHandler(new RenderingEntityBlocks());
		RenderingRegistry.registerBlockHandler(BuildCraftCore.legacyPipeModel, new RenderingEntityBlocks());
		RenderingRegistry.registerBlockHandler(new RenderingOil());
		RenderingRegistry.registerBlockHandler(new RenderingMarkers());
	}

	@Override
	public void initializeEntityRendering() {
		RenderingRegistry.registerEntityRenderingHandler(EntityBlock.class, new RenderEntityBlock());
		RenderingRegistry.registerEntityRenderingHandler(EntityPowerLaser.class, new RenderLaser());
		RenderingRegistry.registerEntityRenderingHandler(EntityEnergyLaser.class, new RenderEnergyLaser());
		RenderingRegistry.registerEntityRenderingHandler(EntityRobot.class, new RenderRobot());
	}


	/* NETWORKING */
	@Override
	public void sendToServer(Packet packet) {
		FMLClientHandler.instance().getClient().getNetHandler().getNetManager().addToSendQueue(packet);
	}

	/* FILE SYSTEM */
	@Override
	public File getBuildCraftBase() {
		return Minecraft.getMinecraft().mcDataDir;
	}

	/* BUILDCRAFT PLAYER */
	@Override
	public String playerName() {
		return FMLClientHandler.instance().getClient().thePlayer.username;
	}

	private EntityPlayer createNewPlayer(World world) {
		return new EntityPlayer(world, "[BuildCraft]") {
			@Override public void sendChatToPlayer(ChatMessageComponent var1) {}
			@Override public boolean canCommandSenderUseCommand(int var1, String var2) { return false; }
			@Override public ChunkCoordinates getPlayerCoordinates() {return new ChunkCoordinates();}
		};
	}

	@Override
	public EntityPlayer getBuildCraftPlayer(World world) {
		if (CoreProxy.buildCraftPlayer == null) {
			CoreProxy.buildCraftPlayer = createNewPlayer(world);
		}

		return CoreProxy.buildCraftPlayer;
	}

}
