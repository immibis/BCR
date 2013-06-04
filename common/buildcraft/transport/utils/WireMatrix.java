package buildcraft.transport.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.util.Icon;

import buildcraft.api.core.Orientations;
import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.IPipe.WireColor;


public class WireMatrix {
	
	private final boolean[] _hasWire = new boolean[IPipe.WireColor.values().length];
	private final ConnectionMatrix _wires[] = new ConnectionMatrix[IPipe.WireColor.values().length];
	private boolean _wireState[] = new boolean[IPipe.WireColor.values().length];
	private boolean dirty = false;
	
	public WireMatrix(){
		for (int i = 0; i < IPipe.WireColor.values().length; i++){
			_wires[i] = new ConnectionMatrix();
		}
	}
	
	public boolean hasWire(IPipe.WireColor color){
		return _hasWire[color.ordinal()];
	}
	
	public void setWire(IPipe.WireColor color, boolean value){
		if (_hasWire[color.ordinal()] != value){
			_hasWire[color.ordinal()] = value;
			dirty = true;
		}
	}
	
	public boolean isWireConnected(IPipe.WireColor color, Orientations direction){
		return _wires[color.ordinal()].isConnected(direction);
	}
	
	public void setWireConnected(IPipe.WireColor color, Orientations direction, boolean value){
		_wires[color.ordinal()].setConnected(direction, value);
	}
	
	public boolean getWireState(IPipe.WireColor color){
		return _wireState[color.ordinal()];
	}
	
	public void setWireState(IPipe.WireColor color, boolean state){
		if (_wireState[color.ordinal()] != state){
			_wireState[color.ordinal()] = state;
			dirty = true;
		}
	}
	
	public boolean isDirty() {
		
		for (int i = 0; i < IPipe.WireColor.values().length; i++){
			if (_wires[i].isDirty()) return true;
		}
		
		return dirty;
	}

	public void clean() {
		for (int i = 0; i < IPipe.WireColor.values().length; i++){
			_wires[i].clean();
		}
		dirty = false;
	}

	public void writeData(DataOutputStream data) throws IOException {
		
		for (int i = 0; i < IPipe.WireColor.values().length; i++){
			data.writeBoolean(_hasWire[i]);
			_wires[i].writeData(data);
			data.writeBoolean(_wireState[i]);
		}
	}

	public void readData(DataInputStream data) throws IOException {
		for (int i = 0; i < IPipe.WireColor.values().length; i++){
			_hasWire[i] = data.readBoolean();
			_wires[i].readData(data);
			_wireState[i] = data.readBoolean();
		}
	}

	public Icon getTextureIndex(WireColor c) {
		return getWireState(c) ? c.iconOn : c.iconOff;
	}
}
