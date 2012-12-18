package safetyvalve.pipes;

import safetyvalve.CommonProxy;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.BuildCraftTransport;
import buildcraft.transport.IPipeTransportPowerHook;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportPower;
import buildcraft.transport.pipes.PipeLogicIron;

public class PipePowerIron extends Pipe implements IPipeTransportPowerHook {
	private static final int MAX_POWER_INTERNAL = 10000;
	
	private int baseTexture = 2;
	private int plainTexture = 3;
	
	public PipePowerIron(int itemID)
	{
		super(new PipeTransportPower(), new PipeLogicIron(), itemID);
	}

	@Override
	public void receiveEnergy(ForgeDirection from, double val) {
		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		int dir = from.ordinal();
		PipeTransportPower transport = (PipeTransportPower) this.transport;
		if(meta != dir){
			if (BuildCraftTransport.usePipeLoss) transport.internalNextPower[dir] += val * (1 - transport.powerResistance);
			else transport.internalNextPower[dir] += val;
			
			if (transport.internalNextPower[dir] >= MAX_POWER_INTERNAL) {
				worldObj.createExplosion(null, xCoord, yCoord, zCoord, 3, false);
				worldObj.setBlockWithNotify(xCoord, yCoord, zCoord, 0);
			}
		}else{
			for(int i = 0; i < 6; ++i){
				if(i != dir){
					transport.internalNextPower[i] += val/5;
					if (transport.internalNextPower[i] >= MAX_POWER_INTERNAL) {
						worldObj.createExplosion(null, xCoord, yCoord, zCoord, 3, false);
						worldObj.setBlockWithNotify(xCoord, yCoord, zCoord, 0);
					}
				}
			}
		}
	}

	@Override
	public void requestEnergy(ForgeDirection from, int i) {
		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		int dir = from.ordinal();
		PipeTransportPower transport = (PipeTransportPower) this.transport;
		if(meta == dir){
			transport.nextPowerQuery[dir] += i;
		}else{
			transport.nextPowerQuery[dir] = 0;
		}
	}

	@Override
	public String getTextureFile() {
		return CommonProxy.PIPES_PNG;
	}

	@Override
	public int getTextureIndex(ForgeDirection direction) {
		if(direction == ForgeDirection.UNKNOWN || worldObj.getBlockMetadata(xCoord, yCoord, zCoord) == direction.ordinal()){
			return baseTexture;
		}else{
			return plainTexture;
		}
	}

}
