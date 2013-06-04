package buildcraft.transport.pipes;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import buildcraft.api.core.Orientations;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportPower;

public class PipePowerIron extends Pipe {
	public PipePowerIron(int itemID) {
		super(new PipeTransportPower(), new PipeLogicIron(), itemID);

		((PipeTransportPower) transport).powerResitance = 0.001;
	}
	
	private static Icon icon, iconBlocked;
	
	@Override
	public void registerIcons(IconRegister r) {
		icon = r.registerIcon(getDefaultIconPath());
		iconBlocked = r.registerIcon(getDefaultIconPath() + "-blocked");
	}
	
	@Override
	public Icon getTexture(Orientations direction) {
		if (direction == Orientations.Unknown)
			return icon;
		else {
			int metadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

			if (metadata == direction.ordinal())
				return icon;
			else
				return iconBlocked;
		}
	}
}
