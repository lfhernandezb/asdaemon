import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;

public class ASDaemon implements Daemon {
	private static final Logger logger = Logger.getLogger(ASDaemon.class.getName());
    private static Server servComm;

	@Override
	public void init(DaemonContext arg0) throws DaemonInitException, Exception {
		// TODO Auto-generated method stub
		Layout layout;
		ConsoleAppender cnslAppndr;
		
		//System.out.println("Daemon init");
		
		//PropertyConfigurator.configure(System.getProperty("log4j.configuration"));
		/*		
	    layout = new PatternLayout("%d [%t] %-5p %m%n");
	    cnslAppndr = new ConsoleAppender(layout);
	    logger.addAppender(cnslAppndr);
	    cnslAppndr.activateOptions();
	    
	    layout = new PatternLayout("%d [%t] %-5p %m%n");
	    FileAppender fileAppender = new FileAppender(layout, "/var/log/asdaemon.log");
	    logger.addAppender(fileAppender);
	    fileAppender.activateOptions();
		*/
		
		logger.debug("Daemon init");
		/*
		for( int i = 0; i < arg0.getArguments().length - 1; i++) {
		    System.out.println(arg0.getArguments()[i]);
		}
		*/
		/*
		// get the system properties object
		Properties p = System.getProperties();

		// get an enumeration of all property names that exist
		Enumeration<?> enumeration = p.propertyNames();
		while(enumeration.hasMoreElements())
		{
			// for each name, display its name=value pair
			String name = (String)enumeration.nextElement();
			String value = p.getProperty(name);
			logger.debug("system property: ["+name+"] = ["+value+"]");
		}
		//System.out.println(arg0.getArguments());
		
		*/
	}
    public static void initService() {
         try {
        	   servComm = Server.getInstance();
               servComm.initServer();
         } catch (Exception ex) {
           logger.debug("Server abort: " + ex.getMessage());
           System.exit(0);
         }
    }
    public static void stopService() {
        if (servComm != null) {
            servComm.stopServer();
            servComm = null;
            logger.debug("Service stopped " + new Date());
        }
    }
    
    @Override
    public void start() throws Exception {
    	logger.debug("Daemon start");
    	
        initService();
    }
    
    @Override
    public void stop() throws Exception {
    	logger.debug("Daemon stop");
        stopService();
    }
    
    @Override
    public void destroy() { 
    	logger.debug("Daemon destroy");
    }
 
}