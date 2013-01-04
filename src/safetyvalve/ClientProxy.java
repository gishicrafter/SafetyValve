package safetyvalve;

import buildcraft.transport.TransportProxyClient;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerTextures() {
		MinecraftForgeClient.preloadTexture(CommonProxy.PIPES_PNG);
	}

	@Override
	public void registerRenderers() {
		if(ModItems.pipePowerVoid != null)
			MinecraftForgeClient.registerItemRenderer(ModItems.pipePowerVoid.itemID, TransportProxyClient.pipeItemRenderer);
		if(ModItems.pipePowerIron != null)
			MinecraftForgeClient.registerItemRenderer(ModItems.pipePowerIron.itemID, TransportProxyClient.pipeItemRenderer);
		if(ModItems.pipePowerSafetyValve != null)
			MinecraftForgeClient.registerItemRenderer(ModItems.pipePowerSafetyValve.itemID, TransportProxyClient.pipeItemRenderer);
	}

}
