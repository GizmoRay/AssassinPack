package com.gizmoray.assassinpack;

import java.io.File;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;


public class AssassinPack extends JavaPlugin{
	public final Logger log = Logger.getLogger("Minecraft");
	public File configFile;
	private FileManager fm;
	public String value;
	public WorldEditPlugin we = null;
	public WorldGuardPlugin wg = null;
	
	@Override
	public void onEnable(){
		this.fm = new FileManager(this); this.fm.load();
		loadConfiguration();
		getConfig().options().copyDefaults(true);
		saveConfig();
		getServer().getPluginManager().registerEvents(new PluginListener(this), this);
		PluginDescriptionFile pdfFile = this.getDescription();
		this.log.info(pdfFile.getName() + " " + " v" + pdfFile.getVersion() + " : Has been enabled");
		
		
		// <editor-fold defaultstate="collapsed" desc="worldedit & worldguard">
				// ///////////////////////////////////////////////
				Plugin plTemp = Bukkit.getServer().getPluginManager()
						.getPlugin("WorldGuard");
				if (null == plTemp || !(plTemp instanceof WorldGuardPlugin)) {
					wg = null;
				} else {
					wg = (WorldGuardPlugin) plTemp;
				}
				// ///////////////////////////////////////////////
				plTemp = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
				if (null == plTemp || !(plTemp instanceof WorldEditPlugin)) {
					we = null;
				} else {
					we = (WorldEditPlugin) plTemp;
				}
				// ///////////////////////////////////////////////
				// </editor-fold>
	}
	
	public boolean isRegioned(Location testLoc) {
		boolean ret = false;
		if (!(null == wg)) {
			// do wg and we stuff here.
			RegionManager rm = wg.getRegionManager(testLoc.getWorld());
			if (null != rm) {
				// keep going! GO GO GO GO GO GO GOOOO!
				if (!rm.getApplicableRegions(testLoc).allows(
						DefaultFlag.MOB_SPAWNING)) {
					ret = true;
				}
			}
		}
		return ret;
	}

	private void loadConfiguration() {
		getConfig().addDefault("HeadshotDamage", 5);
		String WorldName = "world";
		getConfig().addDefault("WorldNames", WorldName);
		
	}
	
	@SuppressWarnings("unused")
	private WorldGuardPlugin getWorldGuard() {
		Plugin plugin = Bukkit.getServer().getPluginManager()
				.getPlugin("WorldGuard");
		if ((null == plugin) || (!(plugin instanceof WorldGuardPlugin))) {
			return null;
		}
		return (WorldGuardPlugin) plugin;
	}
}
