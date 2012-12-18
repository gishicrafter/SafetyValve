package safetyvalve.pipes;

import safetyvalve.CommonProxy;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.transport.IPipeTransportPowerHook;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportPower;
import buildcraft.transport.pipes.PipeLogicVoid;

public class PipePowerSafetyValve extends Pipe implements IPipeTransportPowerFilter {
	
	private int baseTexture = 4;
	private int plainTexture = 5;
	
	public boolean[] isOpen = new boolean[]{false, false, false, false, false, false};

	public PipePowerSafetyValve(int itemID)
	{
		super(new PipeTransportPowerFilter(), new PipeLogicVoid(), itemID);
	}

	@Override
	public int filterQueryToDivide(ForgeDirection direction, int query) {
		return query;
	}

	@Override
	public int filterQueryToTransfer(ForgeDirection direction, int query) {
		PipeTransportPower transport = (PipeTransportPower) this.transport;
		int dir = direction.ordinal();
		if(isOpen[dir] != (transport.powerQuery[dir] <= 0)){
			isOpen[dir] = transport.powerQuery[dir] <= 0;
			container.scheduleRenderUpdate();
		}
		if(isOpen[dir]) return query > 0 ? query : 50;
		else return 0;
	}

	@Override
	public String getTextureFile() {
		return CommonProxy.PIPES_PNG;
	}

	@Override
	public int getTextureIndex(ForgeDirection direction) {
		if(direction == ForgeDirection.UNKNOWN) return baseTexture;
		int dir = direction.ordinal();
		if(isOpen[dir]) return baseTexture;
		else return plainTexture;
	}

	@Override
	public double disposePower(ForgeDirection direction, double watts) {
		return 0;
	}

}
