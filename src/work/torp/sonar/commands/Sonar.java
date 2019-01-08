package work.torp.sonar.commands;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatColor;
import work.torp.sonar.Main;
import work.torp.sonar.alerts.Alert;
import work.torp.sonar.classes.SonarUsage;
import work.torp.sonar.helper.Convert;

public class Sonar implements CommandExecutor {
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		boolean ret = false;
		
		if (sender instanceof Player) { // Check if the sender is console or a player
			Player player = (Player) sender; // Cast the sender to a player			
			if (player.hasPermission("sonar.command"))
			{
				int sonarusage = 0;
				List<SonarUsage> lstSU = Main.getInstance().GetSonarUsageByUUIDSecs(player.getUniqueId().toString(), 3600);
				if (lstSU != null)
				{
					for (SonarUsage su : lstSU)
					{
						Alert.VerboseLog("Sonar.GetSonarUsage", "User: " + player.getName() + "/" + player.getUniqueId().toString() + " found with timestamp: " + su.timestamp.getTime());
						sonarusage++;
					}
				}
				Alert.VerboseLog("Sonar.GetSonarUsage", "User: " + player.getName() + "/" + player.getUniqueId().toString() + " has " + Integer.toString(sonarusage) + " sonar usages in the past hour");
				if (sonarusage >= Main.getInstance().getSonarMaxHr() && !player.isOp())
				{
					Alert.Player("You have exhausted all of your Sonar abilities for the current hour", player, true);
					return true;
				} else {
					if (!player.isOp())
					{
						SonarUsage su = new SonarUsage();
						su.uuid = player.getUniqueId().toString();
						su.timestamp = new Timestamp(System.currentTimeMillis());
						Main.getInstance().AddSonarUsage(su);
						Alert.VerboseLog("Sonar", "Added a sonar attempt for " + player.getName());
					}
					
					Location loc = player.getLocation();	
					
					int sonar_radius = Main.getInstance().getSonarRadius();
					Alert.VerboseLog("Sonar", "Check radius of " + Integer.toString(sonar_radius) + " for " + player.getName() + " - " + Convert.LocationToReadableString(loc));
					
					Collection<Entity> nearbyEntities = loc.getWorld().getNearbyEntities(loc, sonar_radius, sonar_radius, sonar_radius);				
					List<String> lentities = new ArrayList<String>();			
					int ient = 0;			
					for (Entity e : nearbyEntities) {
						if (e.getName() != player.getName())
						{
							if (e instanceof Creature) {
								String sentity = "";
								Location eloc = e.getLocation();
								int edist = (int) Math.round(loc.distance(eloc));
								if (e instanceof Monster) {
									sentity = ChatColor.RED + e.getName() + ChatColor.WHITE + " - " + Integer.toString(edist) + " blocks away";
								} else {
									sentity = ChatColor.BLUE + e.getName() + ChatColor.WHITE + " - " + Integer.toString(edist) + " blocks away";
								}
								lentities.add(sentity);
								ient++;
							}
							if (e instanceof Player) {
								String splayer = "";
								Location ploc = e.getLocation();
								int pdist = (int) Math.round(loc.distance(ploc));
								splayer = ChatColor.DARK_PURPLE + e.getName() + ChatColor.WHITE + " - " + Integer.toString(pdist) + " blocks away";
								lentities.add(splayer);
								ient++;
							}
						}
					}
					if (ient > 0)
					{
						Alert.Player(ChatColor.WHITE + "Mobs within sonar range:", player, false);
						for (String s : lentities) {
							Alert.Player(s, player, false);
						}
					} else {
						Alert.Player("No mobs in sonar range", player, false);
					}	
				}
				ret = true;
			} else {
				Alert.Player("You do not have permission to use this command", player, true);
				ret = true;
			}
		}
		
		return ret;
	}
}
