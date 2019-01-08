package work.torp.sonar;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import work.torp.sonar.commands.Sonar;

import work.torp.sonar.database.Database;
import work.torp.sonar.scheduled.TimedCleanup;
import work.torp.sonar.alerts.Alert;
import work.torp.sonar.database.SQLite;
import work.torp.sonar.Main;
import work.torp.sonar.classes.SonarUsage;

public class Main extends JavaPlugin {
	
	// Main
	private static Main instance;
    public static Main getInstance() {
		return instance;
	}
    
    // Database
	private Database db;
    public Database getDatabase() {
        return this.db;
    }
    
    // DebugFile
	private boolean debugfile;
    public boolean getDebugFile() {
        return this.debugfile;
    }
    public void setDebugFile(boolean debugfile) {
    	this.debugfile = debugfile;
    }
    
    // Sonar Usage
    private List<SonarUsage> sonarUsage;
    public List<SonarUsage> GetSonarUsage() {
    	return sonarUsage;
    }
    public List<SonarUsage> GetSonarUsageByUUID(String uuid) {
    	List<SonarUsage> lstSU = new ArrayList<SonarUsage>();
    	try {
	    	if (sonarUsage != null) {
	    		for (SonarUsage su : sonarUsage)
	    		{
	    			if (su.uuid.equals(uuid))
	    			{
			    		lstSU.add(su);
	    			}
	    		}
	    	}
    	} catch (Exception ex) {
			Alert.DebugLog("Main", "GetSonarUsageByUUID", "Unexpected Error - " + ex.getMessage());  
		}
    	return lstSU;
    }
    public List<SonarUsage> GetSonarUsageByUUIDSecs(String uuid, Integer secs) {
    	List<SonarUsage> lstSU = new ArrayList<SonarUsage>();
    	try {
	    	if (sonarUsage != null) {
	    		for (SonarUsage su : sonarUsage)
	    		{
	    			if (su.uuid.equals(uuid))
	    			{
	    		    	long milliseconds = new Timestamp(System.currentTimeMillis()).getTime() - su.timestamp.getTime();
	    			    int seconds = (int) milliseconds / 1000;
	    			    if (seconds < secs)
	    			    {
	    			    	lstSU.add(su);
	    			    }
	    			}
	    		}
	    	}
    	} catch (Exception ex) {
			Alert.DebugLog("Main", "GetSonarUsageByUUID", "Unexpected Error - " + ex.getMessage());  
		}
    	return lstSU;
    }
    public void AddSonarUsage(SonarUsage su)
    {
    	try {
	    	if (sonarUsage == null) {
	    		sonarUsage = new ArrayList<SonarUsage>();
	    	}
	    	if (su != null)
	    	{ 		
	    		sonarUsage.add(su);
	    	}
    	} catch (Exception ex) {
			Alert.DebugLog("Main", "AddSonarUsage", "Unexpected Error - " + ex.getMessage());  
		}
    }
    public void RemoveSonarUsage(SonarUsage su)
    {
    	try {
	    	if (sonarUsage != null) {
	    		List<SonarUsage> lstSU = sonarUsage;
	    		for (SonarUsage su1 : lstSU)
	    		{
	    			if (su1.uuid.equals(su.uuid))
	    			{
	    				sonarUsage.remove(su1);
	    			}
	    		}
	    	} else {
	    		sonarUsage = new ArrayList<SonarUsage>();
	    	}
    	} catch (Exception ex) {
			Alert.DebugLog("Main", "RemoveSonarUsage", "Unexpected Error - " + ex.getMessage());  
		}
    }
    
    // Config
    int sonarRadius = 10;
    int sonarMaxHr = 3;
    public void getSonarConfig()
    {
    	String sSonarRadius = Main.getInstance().getConfig().getString("sonar_radius"); // Get the value assigned to sonar_radius
    	if (sSonarRadius != null) {
    		Alert.VerboseLog("Get Configuration", "sonar_radius found: " + sSonarRadius);
    		int iSRSecs = sonarRadius;
    		try{
    			iSRSecs = Integer.parseInt(sSonarRadius);
    			sonarRadius = iSRSecs;
    		} 
    		catch (NumberFormatException ex) {
    			Alert.Log("Get Configuration", "sonar_radius invalid, must be a number. Using default");	
    		}
    	} else {
    		Alert.Log("Get Configuration", "sonar_radius not found. Using default");
    	}
    	
    	String sSonarMaxHr = Main.getInstance().getConfig().getString("sonar_max_hr"); // Get the value assigned to sonar_max_hr
    	if (sSonarMaxHr != null) {
    		Alert.VerboseLog("Get Configuration", "sonar_max_hr found: " + sSonarMaxHr);
    		int iSMSecs = sonarMaxHr;
    		try{
    			iSMSecs = Integer.parseInt(sSonarMaxHr);
    			sonarMaxHr = iSMSecs;
    		} 
    		catch (NumberFormatException ex) {
    			Alert.Log("Get Configuration", "sonar_max_hr invalid, must be a number. Using default");	
    		}
    	} else {
    		Alert.Log("Get Configuration", "sonar_max_hr not found. Using default");
    	}
    }
    public int getSonarRadius() {
    	return this.sonarRadius;
    }
    public int getSonarMaxHr() {
    	return this.sonarMaxHr;
    }
    
    // Scheduled
    public void startCleanup() {
    	try {
	        BukkitTask task = new BukkitRunnable() {       	
	            public void run() {
	            	TimedCleanup.Run();
	            }
	        }.runTaskTimer(getInstance(), 0, 600 * 20); // Every 10 mins
	        Alert.DebugLog("Main", "startCleanup", "startCleanup running with id " + task.getTaskId());
    	} catch (Exception ex) {
			Alert.DebugLog("Main", "startCleanup", "Unexpected Error - " + ex.getMessage());  
		}
    }
    
    // On Enable/Disable
    @Override
	public void onEnable() {
    	
    	instance = this;
		saveDefaultConfig();
		Alert.Log("Main", "Starting Sonar");
		
		this.db = new SQLite(this); // New SQLite
        this.db.load(); // Run load
        this.db.initialize(); // Run initialize
        
        
    	getSonarConfig(); // Get Config
    
    	this.getDatabase().getSonarUsage();  // Get list from db
    	
    	startCleanup(); // Run cleanup
    	
    	getCommand("sonar").setExecutor(new Sonar()); // Register my command
    }
    
    @Override
	public void onDisable() {
    	// Save list to db
    	if (sonarUsage != null) {
    		this.getDatabase().addSonarUsage(sonarUsage);
    	}
    }
}
