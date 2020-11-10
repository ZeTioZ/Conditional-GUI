package fr.zetioz.conditionalgui;

import java.io.FileNotFoundException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fr.zetioz.conditionalgui.utils.Color;

public class ConditionalGUICommandHandler implements CommandExecutor
{
	private YamlConfiguration messagesFile;
	private String prefix;
	
	public ConditionalGUICommandHandler()
	{
		try
		{
			this.messagesFile = ConditionalGUIMain.getFilesManager().getSimpleYaml("messages");
			this.prefix = Color.color(messagesFile.getString("prefix"));
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if(cmd.getName().equalsIgnoreCase("conditionalgui"))
		{
			if(args.length == 0)
			{
				if(sender instanceof Player)
				{						
					new ConditionalGUIGUIHandler().openGUI((Player) sender);
				}
				else
				{
					for(String line : Color.color(messagesFile.getStringList("errors.must-be-a-player")))
					{
						sender.sendMessage(prefix + line);
					}
				}
			}
			else if(args.length == 1)
			{
				if(args[0].equalsIgnoreCase("help"))
				{
					sendHelpPage(sender);
				}
				else if(args[0].equalsIgnoreCase("reload") && sender.hasPermission("conditionalgui.reload"))
				{
					for(String line : Color.color(messagesFile.getStringList("reload-command")))
					{
						sender.sendMessage(prefix + line);
					}
					Plugin plugin = ConditionalGUIMain.getPlugin();
					Bukkit.getPluginManager().disablePlugin(plugin);
					Bukkit.getPluginManager().enablePlugin(plugin);
				}
				else if(args[0].equalsIgnoreCase("open"))
				{
					if(sender instanceof Player)
					{						
						new ConditionalGUIGUIHandler().openGUI((Player) sender);
					}
					else
					{
						for(String line : Color.color(messagesFile.getStringList("errors.must-be-a-player")))
						{
							sender.sendMessage(prefix + line);
						}
					}
				}
			}
		}
		return false;
	}
	
	public void sendHelpPage(CommandSender sender)
	{
		for(String line : Color.color(messagesFile.getStringList("help-command")))
		{
			sender.sendMessage(prefix + line);
		}
	}
}
