package safetyvalve;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import buildcraft.BuildCraftTransport;
import buildcraft.core.DefaultProps;
import buildcraft.core.utils.Localization;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "SafetyValve", name="Safety Valve", version="@THIS_MOD_VERSION@", dependencies="required-after:BuildCraft|Transport")
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class SafetyValve {
	
	@Instance
	public static SafetyValve instance;
	
	@SidedProxy(clientSide="safetyvalve.ClientProxy",serverSide="safetyvalve.CommonProxy")
	public static CommonProxy proxy;
	
	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		ModConfiguration.loadConfiguration(event.getSuggestedConfigurationFile());
	}
	
	@Init
	public void load(FMLInitializationEvent event) {
		proxy.registerTextures();
		ModItems.registerItems();
		Localization.addLocalization("/safetyvalve/lang/", DefaultProps.DEFAULT_LANGUAGE);
		proxy.registerRenderers();
	}
	
	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
		registerRecipes();
		
	}
	
	private void registerRecipes()
	{
		Item pipePowerVoidForRecipe = null;
		Item pipePowerIronForRecipe = null;
		
		if(ModItems.pipePowerVoid != null){
			GameRegistry.addRecipe(new ItemStack(ModItems.pipePowerVoid, 1), new Object[]{"r", "p", Character.valueOf('r'), Item.redstone, Character.valueOf('p'), BuildCraftTransport.pipeItemsVoid});
			pipePowerVoidForRecipe = ModItems.pipePowerVoid;
		}else if(ModConfiguration.pipePowerVoidAlternative > 0 && ModConfiguration.pipePowerVoidAlternative < Item.itemsList.length - 256){
			pipePowerVoidForRecipe = Item.itemsList[ModConfiguration.pipePowerVoidAlternative+256];
		}
		
		if(ModItems.pipePowerIron != null){
			GameRegistry.addRecipe(new ItemStack(ModItems.pipePowerIron, 1), new Object[]{"r", "p", Character.valueOf('r'), Item.redstone, Character.valueOf('p'), BuildCraftTransport.pipeItemsIron});
			pipePowerIronForRecipe = ModItems.pipePowerIron;
		}else if(ModConfiguration.pipePowerIronAlternative > 0 && ModConfiguration.pipePowerIronAlternative < Item.itemsList.length - 256){
			pipePowerIronForRecipe = Item.itemsList[ModConfiguration.pipePowerIronAlternative+256];	
		}
		
		if(ModItems.pipePowerSafetyValve != null && pipePowerVoidForRecipe != null && pipePowerIronForRecipe != null){
			GameRegistry.addRecipe(new ItemStack(ModItems.pipePowerSafetyValve, 1), new Object[]{"v", "l", "i", Character.valueOf('v'), pipePowerVoidForRecipe, Character.valueOf('l'), Block.lever, Character.valueOf('i'), pipePowerIronForRecipe});
		}
	}
}
