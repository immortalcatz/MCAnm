package com.github.worldsender.mcanm;

import org.apache.logging.log4j.Logger;

import com.github.worldsender.mcanm.test.CubeEntity;

import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

@Mod(
		modid = Reference.core_modid,
		name = Reference.core_modname,
		version = Reference.core_modversion,
		guiFactory = "com.github.worldsender.mcanm.client.config.MCAnmGuiFactory")
public class MCAnm {
	/**
	 * Enables various visual outputs, e.g. the bones of models are rendered.
	 */
	public static final boolean isDebug = false;

	@Mod.Instance(Reference.core_modid)
	public static MCAnm instance;

	public static Logger logger;

	@SidedProxy(
			modId = Reference.core_modid,
			clientSide = "com.github.worldsender.mcanm.client.ClientProxy",
			serverSide = "com.github.worldsender.mcanm.server.ServerProxy")
	public static Proxy proxy;

	private Configuration config;
	public static Property enableReload;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent pre) {
		logger = pre.getModLog();
		config = new Configuration(pre.getSuggestedConfigurationFile());
		config.load();
		enableReload = config.get(Configuration.CATEGORY_GENERAL, Reference.config_reload_enabled, true)
				.setLanguageKey(Reference.gui_config_reload_enabled);
		config.save();
		proxy.register();
		FMLCommonHandler.instance().bus().register(this);
		logger.info("Successfully loaded MC Animations");
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		if (!isDebug)
			return;
		int id = 0;
		EntityRegistry.registerModEntity(CubeEntity.class, "Cube", id, this, 80, 1, true);
	}

	@SubscribeEvent
	public void onConfigChange(OnConfigChangedEvent occe) {
		if (!occe.modID.equals(Reference.core_modid))
			return;
		if (config.hasChanged())
			config.save();
	}
}
