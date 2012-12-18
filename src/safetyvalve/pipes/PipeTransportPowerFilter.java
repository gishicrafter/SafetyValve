package safetyvalve.pipes;

import java.util.Arrays;

import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.BuildCraftCore;
import buildcraft.api.core.SafeTimeTracker;
import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.core.DefaultProps;
import buildcraft.core.proxy.CoreProxy;
import buildcraft.core.utils.Utils;
import buildcraft.transport.PipeTransportPower;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.network.PacketPowerUpdate;

public class PipeTransportPowerFilter extends PipeTransportPower {

	private static final int MAX_POWER_INTERNAL = 10000;
	private static final int OVERLOAD_LIMIT = 7500;
	private static final short MAX_DISPLAY = 100;
	private static final float DISPLAY_POWER_FACTOR = 0.1f;
	
	private boolean needsInit = true;
	
	private TileEntity[] tiles = new TileEntity[6];
	
	SafeTimeTracker tracker = new SafeTimeTracker();
	
	private void updateTiles() {
		for (int i = 0; i < 6; ++i) {
			TileEntity tile = container.getTile(ForgeDirection.VALID_DIRECTIONS[i]);
			if (Utils.checkPipesConnections(tile, container)) {
				tiles[i] = tile;
			} else {
				tiles[i] = null;
			}
		}
	}
	
	private void init() {
		if (needsInit) {
			needsInit = false;
			updateTiles();
		}
	}
	
	@Override
	public void updateEntity() {
		if(!(this.container.pipe instanceof IPipeTransportPowerFilter))
		{
			super.updateEntity();
			return;
		}
		if (CoreProxy.proxy.isRenderWorld(worldObj))
			return;

		step();
		
		init();
		
		IPipeTransportPowerFilter filter = (IPipeTransportPowerFilter) this.container.pipe;

		// Send the power to nearby pipes who requested it

		Arrays.fill(displayPower, (short)0);
		
		int divideQuery[] = new int[6];
		
		for (int i = 0; i < 6; i++) {
			divideQuery[i] = filter.filterQueryToDivide(ForgeDirection.VALID_DIRECTIONS[i], powerQuery[i]);
		}

		for (int i = 0; i < 6; ++i) {
			if (internalPower[i] > 0) {
				double div = 0;

				for (int j = 0; j < 6; ++j) {
					if (j != i && divideQuery[j] > 0)
						if (tiles[j] instanceof TileGenericPipe || tiles[j] instanceof IPowerReceptor)
							div += divideQuery[j];
				}

				double totalWatt = internalPower[i];

				for (int j = 0; j < 6; ++j) {
					if (j != i && divideQuery[j] > 0) {
						double watts = (totalWatt / div * divideQuery[j]);

						if (tiles[j] instanceof TileGenericPipe) {
							TileGenericPipe nearbyTile = (TileGenericPipe) tiles[j];

							PipeTransportPower nearbyTransport = (PipeTransportPower) nearbyTile.pipe.transport;

							nearbyTransport.receiveEnergy(ForgeDirection.VALID_DIRECTIONS[j].getOpposite(), watts);

							displayPower[j] += (short)Math.ceil(watts * DISPLAY_POWER_FACTOR);
							displayPower[i] += (short)Math.ceil(watts * DISPLAY_POWER_FACTOR);

							internalPower[i] -= watts;
						} else if (tiles[j] instanceof IPowerReceptor) {
							IPowerReceptor pow = (IPowerReceptor) tiles[j];

							IPowerProvider prov = pow.getPowerProvider();

							if(prov != null) {
								prov.receiveEnergy((float) watts, ForgeDirection.VALID_DIRECTIONS[j].getOpposite());

							    displayPower[j] += (short)Math.ceil(watts * DISPLAY_POWER_FACTOR);
							    displayPower[i] += (short)Math.ceil(watts * DISPLAY_POWER_FACTOR);

								internalPower[i] -= watts;
							}
						}
					}
				}
				
			}
		}
		
		double highestPower = 0;
		for(int i = 0; i < 6; i++){
			if(internalPower[i] > highestPower){
				highestPower = internalPower[i];
			}
			displayPower[i] = (short)Math.max(displayPower[i], Math.ceil(internalPower[i] * DISPLAY_POWER_FACTOR));
			displayPower[i] = (short)Math.min(displayPower[i], MAX_DISPLAY);
			internalPower[i] = filter.disposePower(ForgeDirection.VALID_DIRECTIONS[i], internalPower[i]);
		}
		overload = highestPower > OVERLOAD_LIMIT;

		// Compute the tiles requesting energy that are not pipes

		for (int i = 0; i < 6; ++i) {
			if (tiles[i] instanceof IPowerReceptor && !(tiles[i] instanceof TileGenericPipe)) {
				IPowerReceptor receptor = (IPowerReceptor) tiles[i];
				int request = receptor.powerRequest();

				if (request > 0)
					requestEnergy(ForgeDirection.VALID_DIRECTIONS[i], request);
			}
		}

		// Sum the amount of energy requested on each side

		int transferQuery[] = { 0, 0, 0, 0, 0, 0 };

		for (int i = 0; i < 6; ++i) {
			transferQuery[i] = 0;

			for (int j = 0; j < 6; ++j) {
				if (j != i)
					transferQuery[i] += powerQuery[j];
			}
			
			transferQuery[i] = filter.filterQueryToTransfer(ForgeDirection.VALID_DIRECTIONS[i], transferQuery[i]);
		}

		// Transfer the requested energy to nearby pipes

		for (int i = 0; i < 6; ++i) {
			if (transferQuery[i] != 0) {
				if (tiles[i] != null) {
					TileEntity entity = tiles[i];

					if (entity instanceof TileGenericPipe) {
						TileGenericPipe nearbyTile = (TileGenericPipe) entity;

						if (nearbyTile.pipe == null) {
							continue;
						}

						PipeTransportPower nearbyTransport = (PipeTransportPower) nearbyTile.pipe.transport;
						nearbyTransport.requestEnergy(ForgeDirection.VALID_DIRECTIONS[i].getOpposite(), transferQuery[i]);
					}
				}
			}
		}

		if (tracker.markTimeIfDelay(worldObj, 2 * BuildCraftCore.updateFactor)) {
			PacketPowerUpdate packet = new PacketPowerUpdate(xCoord, yCoord, zCoord);
			packet.displayPower = displayPower;
			packet.overload = overload;
			CoreProxy.proxy.sendToPlayers(packet.getPacket(), worldObj, xCoord, yCoord, zCoord,
					DefaultProps.PIPE_CONTENTS_RENDER_DIST);
		}

	}


}
