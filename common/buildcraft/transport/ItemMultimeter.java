package buildcraft.transport;

import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.core.DefaultProps;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class ItemMultimeter extends Item {
	public ItemMultimeter(int idm256) {
		super(idm256);
		
		setUnlocalizedName(DefaultProps.ICON_PREFIX + "multimeter");
		setCreativeTab(CreativeTabs.tabTools);
	}
	
	private String displayRounded(double d) {
		return String.format("%.2f", d);
	}
	
	private void measurePower(PipeTransportPower t, EntityPlayer pl) {
		final String CYAN = "\u00A73", WHITE = "\u00A7r";
		
		String powerReqStr = "";
		String powerInStr = "";
		String powerOutStr = "";
		double totalIn = 0;
		double totalOut = 0;
		double maxIn = 0;
		double maxOut = 0;
		for(int k = 0; k < 6; k++) {
			if(t.powerQuery[k] > 0) {
				if(powerReqStr.length() > 0)
					powerReqStr += ", ";
				powerReqStr += ForgeDirection.VALID_DIRECTIONS[k].name() + ":" + CYAN + t.powerQuery[k] + WHITE;
			}
			if(t.statsLastReceivedPower[k] > 0) {
				if(powerInStr.length() > 0)
					powerInStr += ", ";
				powerInStr += ForgeDirection.VALID_DIRECTIONS[k].name() + ":" + CYAN + displayRounded(t.statsLastReceivedPower[k]) + WHITE;
				totalIn += t.statsLastReceivedPower[k];
				maxIn = Math.max(t.statsLastReceivedPower[k], maxIn);
			}
			if(t.statsLastSentPower[k] > 0) {
				if(powerOutStr.length() > 0)
					powerOutStr += ", ";
				powerOutStr += ForgeDirection.VALID_DIRECTIONS[k].name() + ":" + CYAN + displayRounded(t.statsLastSentPower[k]) + WHITE;
				totalOut += t.statsLastSentPower[k];
			}
			maxOut = Math.max(t.statsLastSentPower[k], maxOut);
		}
		
		double inPct = maxIn / t.MAX_POWER * 100;
		double outPct = maxOut / t.MAX_POWER * 100;
		
		pl.sendChatToPlayer(ChatMessageComponent.createFromText("=== CONDUCTIVE PIPE MEASUREMENT ==="));
		pl.sendChatToPlayer(ChatMessageComponent.createFromText("Power requests: " + powerReqStr));
		pl.sendChatToPlayer(ChatMessageComponent.createFromText("Power input: " + powerInStr));
		pl.sendChatToPlayer(ChatMessageComponent.createFromText("Power output: " + powerOutStr));
		pl.sendChatToPlayer(ChatMessageComponent.createFromText("Total in: " + CYAN + displayRounded(totalIn) + WHITE + ", " + CYAN + displayRounded(inPct) + "%"));
		pl.sendChatToPlayer(ChatMessageComponent.createFromText("Total out: " + CYAN + displayRounded(totalOut) + WHITE + ", " + CYAN + displayRounded(outPct) + "%"));
		pl.sendChatToPlayer(ChatMessageComponent.createFromText("Overload power: " + CYAN + displayRounded(t.excessPower) + WHITE + ", " + CYAN + displayRounded(t.excessPower / t.MAX_EXCESS_POWER * 100) + "%"));
	}
	
	private void measureItems(PipeTransportItems t, EntityPlayer pl) {
		final String CYAN = "\u00A73", WHITE = "\u00A7r";
		
		pl.sendChatToPlayer(ChatMessageComponent.createFromText("=== ITEM PIPE MEASUREMENT ==="));
		pl.sendChatToPlayer(ChatMessageComponent.createFromText("Num items: " + CYAN + t.travelingEntities.size()));
	}
	
	private void measurePower(IPowerProvider t, IPowerReceptor r, EntityPlayer pl) {
		final String CYAN = "\u00A73", WHITE = "\u00A7r";
		
		pl.sendChatToPlayer(ChatMessageComponent.createFromText("=== MACHINE POWER MEASUREMENT ==="));
		pl.sendChatToPlayer(ChatMessageComponent.createFromText("Power request: " + CYAN + r.powerRequest()));
		pl.sendChatToPlayer(ChatMessageComponent.createFromText("Power ramp rate: " + CYAN + t.getPowerRamp()));
		pl.sendChatToPlayer(ChatMessageComponent.createFromText("Stored energy: " + CYAN + t.getEnergyStored() + WHITE + "/" + CYAN + t.getMaxEnergyStored()));
		pl.sendChatToPlayer(ChatMessageComponent.createFromText("Activation energy: " + CYAN + t.getActivationEnergy()));
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te instanceof TileGenericPipe) {
			PipeTransport transport = ((TileGenericPipe)te).pipe.transport;
			if(transport instanceof PipeTransportPower) {
				PipeTransportPower power = (PipeTransportPower)transport;
				if(!world.isRemote)
					measurePower(power, player);
				
				return true;
				
			} else if(transport instanceof PipeTransportItems) {
				if(world.isRemote)
					measureItems((PipeTransportItems)transport, player);
				return true;
			}
		}
		
		if(te instanceof IPowerReceptor) {
			IPowerProvider pp = ((IPowerReceptor)te).getPowerProvider();
			if(pp != null && !world.isRemote)
				measurePower(pp, (IPowerReceptor)te, player);
			
			return true;
		}
		
		return false;
	}
}
