package safetyvalve.pipes;

import safetyvalve.CommonProxy;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.transport.IPipeTransportPowerHook;
import buildcraft.transport.Pipe;
import buildcraft.transport.pipes.PipeLogicVoid;

public class PipePowerVoid extends Pipe implements IPipeTransportPowerFilter {
	
	public PipePowerVoid(int itemID)
	{
		super(new PipeTransportPowerFilter(), new PipeLogicVoid(), itemID);
	}

	@Override
	public String getTextureFile() {
		return CommonProxy.PIPES_PNG;
	}

	@Override
	public int getTextureIndex(ForgeDirection direction) {
		return 1;
	}

	@Override
	public int filterQueryToDivide(ForgeDirection direction, int query) {
		return 0;
	}

	@Override
	public int filterQueryToTransfer(ForgeDirection direction, int query) {
		return 5;
	}

	@Override
	public double disposePower(ForgeDirection direction, double watts) {
		return 0;
	}

}
