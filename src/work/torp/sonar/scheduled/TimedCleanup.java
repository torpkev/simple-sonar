package work.torp.sonar.scheduled;

import java.sql.Timestamp;
import java.util.List;
import work.torp.sonar.Main;
import work.torp.sonar.classes.SonarUsage;

public class TimedCleanup {
	public static void Run() {
		List<SonarUsage> lstSU = Main.getInstance().GetSonarUsage();
		if (lstSU != null)
		{
			for (SonarUsage su : lstSU)
			{
				long milliseconds = new Timestamp(System.currentTimeMillis()).getTime() - su.timestamp.getTime();
			    int seconds = (int) milliseconds / 1000;
			    if (seconds >= 3700)
			    {
			    	Main.getInstance().RemoveSonarUsage(su);
			    }
			}
		}
	}
}