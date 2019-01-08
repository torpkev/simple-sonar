package work.torp.sonar.database;
import java.util.logging.Level;

import work.torp.sonar.Main;

public class DatabaseError {
    public static void execute(Main plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQL statement: ", ex);
    }
    public static void close(Main plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Failed to close SQL connection: ", ex);
    }
}