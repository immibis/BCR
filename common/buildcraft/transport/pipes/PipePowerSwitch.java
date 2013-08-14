package buildcraft.transport.pipes;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import buildcraft.api.core.Orientations;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportPower;

public class PipePowerSwitch extends Pipe {

	public PipePowerSwitch(int itemID) {
		super(new PipeTransportPower(PipeTransportPower.MAX_POWER_GOLD), new PipeLogicGold(), itemID);
	}
	
	private static Icon iconOpen, iconBlocked;
	
	@Override
	public void registerIcons(IconRegister r) {
		iconOpen = r.registerIcon(getDefaultIconPath()+"-open");
		iconBlocked = r.registerIcon(getDefaultIconPath()+"-blocked");
	}
	
	private boolean isOpen() {
		return worldObj != null && worldObj.getBlockMetadata(xCoord, yCoord, zCoord) != 0;
	}
	
	@Override
	public Icon getTexture(Orientations direction) {
		return isOpen() ? iconOpen : iconBlocked;
	}
	
	@Override
	public boolean isPipeConnected(TileEntity tile) {
		return super.isPipeConnected(tile) && isOpen();
	}
	
	@Override
	public void onNeighborBlockChange(int blockId) {
		super.onNeighborBlockChange(blockId);
		
		boolean nowOpen = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
		if(nowOpen != isOpen()) {
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, nowOpen?1:0, 3);
		}
	}
}