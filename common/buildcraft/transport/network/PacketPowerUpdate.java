package buildcraft.transport.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet;

import buildcraft.core.network.PacketCoordinates;
import buildcraft.core.network.PacketIds;


public class PacketPowerUpdate extends PacketCoordinates {
	public short[] displayPower = new short[] { 0, 0, 0, 0, 0, 0 };
	
	public PacketPowerUpdate(){
	
	}
	
	public PacketPowerUpdate(int x, int y, int z) {
		super(PacketIds.PIPE_POWER, x, y, z);
	}
	
	@Override
	public void readData(DataInputStream data) throws IOException {
		super.readData(data);
		for (int i = 0; i < displayPower.length; i++){
			displayPower[i] = data.readShort();
		}
	}
	
	@Override
	public void writeData(DataOutputStream data) throws IOException {
		super.writeData(data);
		for (int i = 0; i < displayPower.length; i++){
			data.writeShort(displayPower[i]);
		}
	}
	
	@Override
	public Packet getSSPPacket() {
		return new Packet() {
			@Override
			public void writePacketData(DataOutputStream dataoutputstream) throws IOException {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public void readPacketData(DataInputStream datainputstream) throws IOException {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public void processPacket(NetHandler nethandler) {
				PacketHandlerTransport.onPacketPower(nethandler.getPlayer(), PacketPowerUpdate.this);
			}
			
			@Override
			public int getPacketSize() {
				return 0;
			}
		};
	}

}
