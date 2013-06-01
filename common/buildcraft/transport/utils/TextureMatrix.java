package buildcraft.transport.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.util.Icon;

import buildcraft.api.core.Orientations;


public class TextureMatrix {
	
	private final Icon[] _textureIndexes = new Icon[Orientations.values().length];
	
	private boolean dirty = false;
	
	public Icon getTextureIndex(Orientations direction){
		return _textureIndexes[direction.ordinal()];
	}
	
	public void setTextureIndex(Orientations direction, Icon icon){
		if (_textureIndexes[direction.ordinal()] != icon){
			_textureIndexes[direction.ordinal()] = icon;
			dirty = true;
		}
	}

	public boolean isDirty() {
		return dirty;
	}

	public void clean() {
		dirty = false;
	}

	public void writeData(DataOutputStream data) throws IOException {
		for(int i = 0; i < Orientations.values().length; i++){
			//data.writeInt(_textureIndexes[i]);
		}
	}

	public void readData(DataInputStream data) throws IOException {
		for (int i = 0; i < Orientations.values().length; i++){
			//_textureIndexes[i] = data.readInt();
		}
	}
}
