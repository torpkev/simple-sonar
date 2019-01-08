package work.torp.sonar.alerts;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import work.torp.sonar.Main;

public class Alert {
	public static void Sender(String message, CommandSender sender, boolean includeHeader) {
		String header = "";
		if (includeHeader)
		{
			header = ChatColor.BLUE + "[Sonar] ";
		}
		if (sender != null)
		{
			if (message != null) {
				sender.sendMessage(header + ChatColor.WHITE + message);
			}
		}
	}
	public static void Player(String message, Player player, boolean includeHeader) {
		String header = "";
		if (includeHeader)
		{
			header = ChatColor.BLUE + "[Sonar] ";
		}
		if (player != null)
		{
			Player p = Bukkit.getPlayer(player.getName());
			if (p != null) {
				if (message != null) {
					player.sendMessage(header + ChatColor.WHITE + message);
				}
			}
		}
	}
	public static void Log(String function, String message)
	{
		ConsoleCommandSender clogger = Main.getInstance().getServer().getConsoleSender();
		clogger.sendMessage(ChatColor.DARK_PURPLE + "[Sonar]" + ChatColor.GOLD + "[" + function + "] " + ChatColor.WHITE + message);
	}
	public static void VerboseLog(String function, String message)
	{
		String vlog = Main.getInstance().getConfig().getString("verbose_logging"); // Get the value assigned to verbose logging
		if (vlog != null)
		{
			if (vlog.toLowerCase() == "true") {
				ConsoleCommandSender clogger = Main.getInstance().getServer().getConsoleSender();
				clogger.sendMessage(ChatColor.DARK_RED + "[Sonar.Verbose]" + ChatColor.GOLD + "[" + function + "] " + ChatColor.WHITE + message);
			}	
		}
	}
	public static void DebugLog(String function, String subfunction, String message)
	{
		String elog = Main.getInstance().getConfig().getString("debug_logging"); // Get the value assigned to debug logging
		if (Main.getInstance().getDebugFile()) {
			elog = "true";
		}
		if (elog != null)
		{
			if (elog.toLowerCase() == "true") {
				ConsoleCommandSender clogger = Main.getInstance().getServer().getConsoleSender();
				clogger.sendMessage(ChatColor.LIGHT_PURPLE + "[Sonar.Debug]" + ChatColor.AQUA + "[" + function + "." + subfunction + "] " + ChatColor.WHITE + message);
				if (Main.getInstance().getDebugFile()) {
					Alert.LogToFile("[" + function + "." + subfunction + "] " + message);
				}
			}	
		}
	}
	public static void LogToFile(String message)
    {
        try
        {
            File dataFolder = Main.getInstance().getDataFolder();
            if(!dataFolder.exists())
            {
                dataFolder.mkdir();
            }
 
            File saveTo = new File(Main.getInstance().getDataFolder(), "Sonar_debug.log");
            if (!saveTo.exists())
            {
                saveTo.createNewFile();
            }
 
            FileWriter fw = new FileWriter(saveTo, true);
            PrintWriter pw = new PrintWriter(fw);
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());         
            pw.println(timeStamp + " - " + message);
            pw.flush();
            pw.close();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
 
    }
}
