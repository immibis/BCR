package buildcraft.transport.pipes;

import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportStructure;

public class PipeStructureCobblestone extends Pipe {

	public PipeStructureCobblestone(int itemID) {
		super(new PipeTransportStructure(), new PipeLogicCobblestone(), itemID);

	}


}
