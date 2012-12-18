package safetyvalve.pipes;

import net.minecraftforge.common.ForgeDirection;

public interface IPipeTransportPowerFilter {
	int filterQueryToDivide(ForgeDirection direction, int query);
	int filterQueryToTransfer(ForgeDirection direction, int query);
	double disposePower(ForgeDirection direction, double watts);
}
