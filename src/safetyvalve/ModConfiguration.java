package safetyvalve;

import java.io.File;

import gishicrafter.util.ConfigHelper;

public class ModConfiguration {
	
	@ConfigHelper.Item
	public static int pipePowerVoid = 17400;
	
	@ConfigHelper.Value
	public static int pipePowerVoidAlternative = 0;

	@ConfigHelper.Item
	public static int pipePowerIron = 17401;
	
	@ConfigHelper.Value
	public static int pipePowerIronAlternative = 0;

	@ConfigHelper.Item
	public static int pipePowerSafetyValve = 17402;
	
	@ConfigHelper.Value
	public static int pipePowerSafetyValveAlternative = 0;

	public static void loadConfiguration(File file)
	{
		ConfigHelper helper = new ConfigHelper(file);
		helper.loadTo(ModConfiguration.class);
	}
}
