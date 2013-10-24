package buildcraft.transport.network;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet;

import buildcraft.api.core.Orientations;
import buildcraft.api.liquids.LiquidStack;
import buildcraft.core.network.PacketCoordinates;
import buildcraft.core.network.PacketIds;


public class PacketLiquidUpdate extends PacketCoordinates{

	public LiquidStack[] displayLiquid = new LiquidStack[Orientations.values().length];

	public PacketLiquidUpdate(int xCoord, int yCoord, int zCoord) {
		super(PacketIds.PIPE_LIQUID, xCoord, yCoord, zCoord);
	}
	
	public PacketLiquidUpdate() {

	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		super.readData(data);
		for (Orientations direction : Orientations.values()){
			int liquidId = data.readInt();
			int liquidQuantity = data.readInt();
			int liquidMeta = data.readInt();
			displayLiquid[direction.ordinal()] = new LiquidStack(liquidId, liquidQuantity, liquidMeta);
		}
		
	}
	
	@Override
	public void writeData(DataOutputStream data) throws IOException {
		super.writeData(data);
		for (Orientations direction : Orientations.values()){
			if (displayLiquid[direction.ordinal()] != null){
				data.writeInt(displayLiquid[direction.ordinal()].itemID);
				data.writeInt(displayLiquid[direction.ordinal()].amount);
				data.writeInt(displayLiquid[direction.ordinal()].itemMeta);
			} else {
				data.writeInt(0);
				data.writeInt(0);
				data.writeInt(0);
			}
			
		}
	}
	
	// Using a fake SSP packet allows the client to directly access the server's renderCache for rendering.
	@Override
	public Packet getSSPPacket() {
		return new Packet() {
			@Override
			public void writePacketData(DataOutput dataoutputstream) throws IOException {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public void readPacketData(DataInput datainputstream) throws IOException {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public void processPacket(NetHandler nethandler) {
				PacketHandlerTransport.onPacketLiquid(nethandler.getPlayer(), PacketLiquidUpdate.this);
			}
			
			@Override
			public int getPacketSize() {
				return 0;
			}
		};
	}

}
