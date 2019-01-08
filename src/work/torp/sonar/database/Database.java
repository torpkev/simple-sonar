package work.torp.sonar.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import work.torp.sonar.classes.SonarUsage;

import work.torp.sonar.Main;



public abstract class Database {
    Main plugin;
    Connection connection;

    public String SQLConnectionExecute = "Couldn't execute SQL statement: ";
    public String SQLConnectionClose = "Failed to close SQL connection: "; 
    public String NoSQLConnection = "Unable to retreive SQL connection: ";
    public String NoTableFound = "Database Error: No Table Found";
    
    public int tokens = 0;
    public Database(Main instance){
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize(){
        connection = getSQLConnection();
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM sonar_log");
            ResultSet rs = ps.executeQuery();
            close(ps,rs);
   
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, NoSQLConnection, ex);
        }
    }

    public void close(PreparedStatement ps,ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
        	DatabaseError.close(plugin, ex);
        }
    }   
    
    public void getSonarUsage() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT uuid, dtime from sonar_log;");
   
            rs = ps.executeQuery();
            while(rs.next()){
            	boolean recOK = false;
            	SonarUsage su = new SonarUsage();
            	su.uuid = rs.getString("uuid");
            	try {
            	    
            	    Date ftDateTime = dateFormat.parse(rs.getString("dtime"));
            	    Timestamp dtime = new java.sql.Timestamp(ftDateTime.getTime());
            	    su.timestamp = dtime;
            	    recOK = true;
            	} catch(Exception e) {
            	    recOK = false;
            	}
                if(recOK)
                {
                	Main.getInstance().AddSonarUsage(su);
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, SQLConnectionExecute, ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, SQLConnectionClose, ex);
            }
        }
    }   
    public boolean addSonarUsage(List<SonarUsage> lstSU) {
        if (lstSU != null)
        {
        	Connection conn = null;
            PreparedStatement ps = null;

            boolean has_error = false;
            try {
            	for (SonarUsage su : lstSU)
            	{
	                conn = getSQLConnection();
	                ps = conn.prepareStatement("INSERT INTO sonar_log (uuid, dtime) VALUES ('" + su.uuid + "', '" + su.timestamp + "');");
	       
	                ps.executeUpdate();
            	}
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, SQLConnectionExecute, ex);
                has_error = true;
            } finally {
                try {
                    if (ps != null)
                        ps.close();
                    if (conn != null)
                        conn.close();
                } catch (SQLException ex) {
                    plugin.getLogger().log(Level.SEVERE, SQLConnectionClose, ex);
                    has_error = true;
                }
            }
            if (!has_error) {
            	return true;
            } else {
            	return false;
            }	
        } else {
        	return false;
        }
    }   
}