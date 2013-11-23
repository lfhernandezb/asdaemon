import java.io.File;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import org.ini4j.Wini;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

public class Server {
    private static Server ctrlComm;
    //public static int SERVER_PORT = 6000;
    boolean activeServer = true;
    private Wini m_ini;
    Vector<AttendantThread> vDaemons = new Vector<AttendantThread>();
    BoneCP m_connectionPool;
    
    private Server() {
    	
    }
    public static Server getInstance() {
          if (ctrlComm == null) {
            ctrlComm = new Server();
          }
          return ctrlComm;
    }
    public void initServer() throws Exception {
    	String config_file_name;

    	m_ini = new Wini();
    	
    	config_file_name = System.getProperty("config_file");
    	
    	File f = new File(config_file_name);
    	
    	if (!f.exists()) {
    		throw new Exception("Config file does not exists");
    	}
    	
    	m_ini.load(new File(config_file_name));
    	
    	new ThreadServer().start();
    }
    public void stopServer() {
         activeServer = false;
         for (Iterator<AttendantThread> iter = vDaemons.iterator(); iter.hasNext();) {
        	 AttendantThread item = (AttendantThread) iter.next();
             item.m_bKeepWorking = false;
        }
         
         m_connectionPool.shutdown(); // shutdown connection pool.
    }
    private void serverTCP() throws Exception {
    	ServerSocketChannel serverSocketChannel;
    	Selector selector;
    	
		try {
			// load the database driver (make sure this is in your classpath!)
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		try {
			// setup the connection pool
			BoneCPConfig config = new BoneCPConfig();
			config.setJdbcUrl("jdbc:mysql://" + m_ini.get("DB", "host") + "/" + m_ini.get("DB", "database")); // jdbc url specific to your database, eg jdbc:mysql://127.0.0.1/yourdb
			config.setUsername(m_ini.get("DB", "user")); 
			config.setPassword(m_ini.get("DB", "password"));
			config.setMinConnectionsPerPartition(5);
			config.setMaxConnectionsPerPartition(10);
			config.setPartitionCount(1);
			m_connectionPool = new BoneCP(config); // setup the connection pool
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
    	
    	
        try {
        	// Create a new selector
        	selector = SelectorProvider.provider().openSelector();
        	
        	// Create a new non-blocking server socket channel
        	serverSocketChannel = ServerSocketChannel.open();
        	serverSocketChannel.configureBlocking(false);
        	
        	// Bind the server socket to the specified address and port
        	serverSocketChannel.socket().bind(new InetSocketAddress(m_ini.get("ServerListen", "host"), m_ini.get("ServerListen", "port", int.class)), m_ini.get("General", "max_clients", int.class));
            
            // Register the server socket channel, indicating an interest in 
            // accepting new connections
        	serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);            

			AttendantThread process = null;

			while (activeServer) {
				
		        // Wait for an event one of the registered channels
		        selector.select();
				
		        // Iterate over the set of keys for which events are available
		        Iterator selectedKeys = selector.selectedKeys().iterator();
		        
		        while (selectedKeys.hasNext()) {

		        	SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();
					
					if (!key.isValid()) {
						continue;
					}
					
					// Check what event is available and deal with it
					if (key.isAcceptable()) {
						java.sql.Connection connection;
						
					    // For an accept to be pending the channel must be a server socket channel.
					    ServerSocketChannel ssc = (ServerSocketChannel) key.channel();

					    // Accept the connection and make it non-blocking
					    SocketChannel socketChannel = ssc.accept();
					    //Socket socket = socketChannel.socket();
					    socketChannel.configureBlocking(false);
					    
					    
					    
						try {
							connection = m_connectionPool.getConnection(); // fetch a connection
							
							if (connection != null){
								System.out.println("Connection successful!");

								process= new AttendantThread(socketChannel, connection);
			    			    
			    			    process.start();
			    			    
			    			    vDaemons.add(process);

			    			    //socketChannel.close();
							}
							
						} catch (SQLException e) {
							e.printStackTrace();
						}
					    
					    
					    

					}
		        }
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    //Now the thread that initialized the server.
    private class ThreadServer extends Thread {
        
        public ThreadServer() {
        	
        }
        
        public void run() {
	        try {
	              serverTCP();
	        } catch (Exception ex) {
	             ex.printStackTrace();
	             stopServer();
	        }
        }
    }
}