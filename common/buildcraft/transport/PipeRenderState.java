package buildcraft.transport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.util.Icon;

import buildcraft.core.network.IClientState;
import buildcraft.transport.utils.ConnectionMatrix;
import buildcraft.transport.utils.FacadeMatrix;
import buildcraft.transport.utils.TextureMatrix;
import buildcraft.transport.utils.WireMatrix;


public class PipeRenderState implements IClientState {

	private boolean hasGate = false;
	private int gateID = 0, gateMetadata = 0;
	private boolean gateState = false;
	
	public final ConnectionMatrix pipeConnectionMatrix = new ConnectionMatrix();
	public final TextureMatrix textureMatrix = new TextureMatrix();
	public final WireMatrix wireMatrix = new WireMatrix();
	
	public final FacadeMatrix facadeMatrix = new FacadeMatrix();
	
	
	
	
	private boolean dirty = false;
	
	
	/*This is a placeholder for the pipe renderer to set to a value that the BlockGenericPipe->TileGenericPipe will
	 * then return the the WorldRenderer */
	public Icon currentTextureIndex;
	
	public PipeRenderState() {
//		for (Orientations direction : Orientations.dirs()){
//			facadeMatrix.setConnected(direction, true);
//			facadeMatrix.setTextureFile(direction, "/terrain.png");
//			facadeMatrix.setTextureIndex(direction, direction.ordinal());
//		}
	}
	
	public void setHasGate(boolean value){
		if (hasGate != value){
			hasGate = value;
			dirty = true;
		}
	}
	
	public boolean hasGate(){
		return hasGate;
	}
	
	public void setGateID(int value){
		if (gateID != value){
			gateID = value;
			dirty = true;
		}
	}
	
	public int getGateID(){
		return gateID;
	}
	
	public void setGateMetadata(int value){
		if (gateMetadata != value){
			gateMetadata = value;
			dirty = true;
		}
	}
	
	public int getGateMetadata(){
		return gateMetadata;
	}
	
	public void setGateState(boolean value) {
		if(gateState != value) {
			gateState = value;
			dirty = true;
		}
	}
	
	public boolean getGateState() {
		return gateState;
	}

	public void clean(){
		dirty = false;
		pipeConnectionMatrix.clean();
		textureMatrix.clean();
		wireMatrix.clean();
		facadeMatrix.clean();
	}

	public boolean isDirty(){
		return dirty || pipeConnectionMatrix.isDirty() || textureMatrix.isDirty() || wireMatrix.isDirty() || facadeMatrix.isDirty();
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.writeBoolean(hasGate);
		data.writeInt(gateID);
		data.writeInt(gateMetadata);
		data.writeBoolean(gateState);
		pipeConnectionMatrix.writeData(data);
		textureMatrix.writeData(data);
		wireMatrix.writeData(data);
		facadeMatrix.writeData(data);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		hasGate = data.readBoolean();
		gateID = data.readInt();
		gateMetadata = data.readInt();
		gateState = data.readBoolean();
		pipeConnectionMatrix.readData(data);
		textureMatrix.readData(data);
		wireMatrix.readData(data);
		facadeMatrix.readData(data);
	}
	
}
