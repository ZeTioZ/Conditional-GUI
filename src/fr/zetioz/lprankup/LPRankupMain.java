package fr.zetioz.lprankup;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import fr.zetioz.lprankup.utils.FilesManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class LPRankupMain extends JavaPlugin
{	
	private static LPRankupMain plugin;
	private static FilesManager filesManager;
	private static Economy econ = null;
    private static Permission perms = null;
	
	@Override
	public void onEnable()
	{
		plugin = this;
		filesManager = new FilesManager();
		filesManager.createSimpleYaml("configs");
		filesManager.createSimpleYaml("messages");
		filesManager.createSimpleYaml("database");
		registerEvents(plugin, new LPRankupGUIHandler(), new LPRankupJoinHandler(), new LPRankupDeathHandler(), new LPRankupMinedBlocksHandler());
		
		getCommand("lprankup").setExecutor(new LPRankupCommandHandler());
		
        if (!setupEconomy() ) {
            getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
	}
	
	@Override
	public void onDisable()
	{
		filesManager.saveSimpleYaml("database");
		
		plugin = null;
		filesManager = null;
	}
	
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
	
	public static LPRankupMain getPlugin()
	{
		return plugin;
	}
	
	public static FilesManager getFilesManager()
	{
		return filesManager;
	}
	
    public static Economy getEconomy() {
        return econ;
    }
    
    public static Permission getPermissions() {
        return perms;
    }
	
	private void registerEvents(LPRankupMain plugin, Listener... listeners)
	{
		for(Listener listener : listeners)
		{
			Bukkit.getPluginManager().registerEvents(listener, plugin);
		}
	}
}
