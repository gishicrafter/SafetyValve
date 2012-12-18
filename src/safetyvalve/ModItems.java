package safetyvalve;

import cpw.mods.fml.common.registry.LanguageRegistry;
import safetyvalve.pipes.PipePowerIron;
import safetyvalve.pipes.PipePowerSafetyValve;
import safetyvalve.pipes.PipePowerVoid;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.Pipe;
import net.minecraft.src.Item;

public class ModItems {

	public static Item pipePowerVoid = null;
	public static Item pipePowerIron = null;
	public static Item pipePowerSafetyValve = null;
	
	public static void registerItems()
	{
		pipePowerVoid = registerPipe(ModConfiguration.pipePowerVoid, PipePowerVoid.class, "Void Conduction Pipe");
		pipePowerIron = registerPipe(ModConfiguration.pipePowerIron, PipePowerIron.class, "Iron Conduction Pipe");
		pipePowerSafetyValve = registerPipe(ModConfiguration.pipePowerSafetyValve, PipePowerSafetyValve.class, "Safety Valve for Conduction Pipes");
	}
	
	private static Item registerPipe(int itemID, Class<? extends Pipe> klass, String description)
	{
		Item item = null;
		
		if(itemID > 0){
			item = BlockGenericPipe.registerPipe(itemID, klass);
			item.setItemName(klass.getSimpleName());
			LanguageRegistry.addName(item, description);
		}
		
		return item;
	}
}
