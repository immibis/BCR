package buildcraft.transport.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import buildcraft.api.core.Orientations;


public class FacadeMatrix extends ConnectionMatrix {
	private int[] facadeTypes = new int[Orientations.dirs().length];
	
	private boolean dirty = false;
	
	public FacadeMatrix() {
		for (Orientations direction : Orientations.dirs()){
			facadeTypes[direction.ordinal()] = 0;
		}
	}
	
	public int getFacadeType(Orientations direction){
		return facadeTypes[direction.ordinal()];
	}
	
	public void setFacadeType(Orientations direction, int newType){
		if (facadeTypes[direction.ordinal()] != newType){
			facadeTypes[direction.ordinal()] = newType;
			dirty = true;
		}
	}
	
	@Override
	public boolean isDirty() {
		return dirty || super.isDirty();
	}
	
	@Override
	public void clean() {
		super.clean();
		dirty = false;
	}
	
	@Override
	public void readData(DataInputStream data) throws IOException {
		super.readData(data);
		for (int i = 0; i < Orientations.dirs().length; i++){
			facadeTypes[i] = data.readInt();
		}
	}
	
	@Override
	public void writeData(DataOutputStream data) throws IOException {
		super.writeData(data);
		for (int i = 0; i < Orientations.dirs().length; i++){
			data.writeInt(facadeTypes[i]);
		}
	}
}
