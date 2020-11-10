package fr.zetioz.conditionalgui;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import fr.zetioz.conditionalgui.utils.FilesManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class ConditionalGUIMain extends JavaPlugin
{	
	private static ConditionalGUIMain plugin;
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
		registerEvents(plugin, new ConditionalGUIGUIHandler(), new ConditionalGUIJoinHandler(), new ConditionalGUIDeathHandler(), new ConditionalGUIMinedBlocksHandler());
		
		getCommand("lprankup").setExecutor(new ConditionalGUICommandHandler());
		
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
	
	public static ConditionalGUIMain getPlugin()
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
	
	private void registerEvents(ConditionalGUIMain plugin, Listener... listeners)
	{
		for(Listener listener : listeners)
		{
			Bukkit.getPluginManager().registerEvents(listener, plugin);
		}
	}
}
