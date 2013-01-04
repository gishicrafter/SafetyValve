package safetyvalve;

import java.io.File;

import safetyvalve.util.ConfigHelper;

public class ModConfiguration {
	
	@ConfigHelper.Item(name="pipePowerVoid.id")
	public static int pipePowerVoid = 17400;
	
	@ConfigHelper.Value
	public static int pipePowerVoidAlternative = 0;

	@ConfigHelper.Item(name="pipePowerIron.id")
	public static int pipePowerIron = 19203;
	
	@ConfigHelper.Value
	public static int pipePowerIronAlternative = 0;

	@ConfigHelper.Item(name="pipePowerSafetyValve.id")
	public static int pipePowerSafetyValve = 17402;
	
	@ConfigHelper.Value
	public static int pipePowerSafetyValveAlternative = 0;

	public static void loadConfiguration(File file)
	{
		ConfigHelper helper = new ConfigHelper(file);
		helper.loadTo(ModConfiguration.class);
	}
}
