import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
//import java.util.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.ini4j.Wini;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import com.mysql.jdbc.Connection;
//import com.mysql.jdbc.Statement;

import org.apache.commons.lang3.*;
import org.apache.log4j.Logger;

import com.bbn.openmap.proj.Length;
import com.bbn.openmap.proj.coords.LatLonGCT;
import com.bbn.openmap.proj.coords.LatLonPoint;

import bd.Chat;
import bd.ChatUsuario;
import bd.Comunidad;
import bd.ComunidadUsuario;
import bd.Contacto;
import bd.Incidente;
import bd.MensajeUsuario;
import bd.Usuario;
import bd.Comuna;
import bd.UsuarioPosicion;

/**
 * 
 */

/**
 * @author petete-ntbk
 *
 */
public class AttendantThread extends Thread {
	
	private static HashMap<Integer, Queue<String>> m_chat_message = new HashMap<Integer, Queue<String>>();
	private static final Logger logger = Logger.getLogger(ASDaemon.class.getName());
	
	public boolean m_bKeepWorking;
    private final SocketChannel m_socketChannel;
    private String m_str_input;
    private String m_str_output;
    private String m_html_input;
    //private String m_str_output;
	private java.sql.Connection m_conn;
	private Wini m_ini;
	//private java.sql.Statement m_stat;
	private Usuario m_usuario;
	Selector m_selector;
	// The buffer into which we'll read data when it's available
	private ByteBuffer m_readBuffer;
	SelectionKey m_key;
	public static Charset charset = Charset.forName("UTF-8");
	CharsetEncoder m_encoder = charset.newEncoder();
	CharsetDecoder m_decoder = charset.newDecoder();
	JSONObject m_jo;
	LinkedList<String> m_fifo_output;

	public AttendantThread(SocketChannel p_socketChannel, java.sql.Connection conn) throws IOException {
		// TODO Auto-generated constructor stub
		//logger.debug("inicio constructor");
		m_bKeepWorking = true;
		m_socketChannel = p_socketChannel;
		m_conn = conn;
		m_usuario = null;
		m_selector = SelectorProvider.provider().openSelector();
		
		m_readBuffer = ByteBuffer.allocate(8192);
		this.m_readBuffer.clear();
		
		this.m_readBuffer.rewind();

		
		m_key = null;
		m_str_input = "";
		m_html_input = "";
		//logger.debug("fin constructor");
		
		m_fifo_output = new LinkedList<String>();
	}
	
	public ByteBuffer str_to_bb(String msg){
		try{
		    return m_encoder.encode(CharBuffer.wrap(msg));
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	public String bb_to_str(ByteBuffer buffer){
		  String data = "";
		  try{
		    //int old_position = buffer.position();
			buffer.position(0);
		    data = m_decoder.decode(buffer).toString();
		    // reset buffer's position to its original so it is not altered:
		    //buffer.position(old_position);  
		  }catch (Exception e){
		    e.printStackTrace();
		    return "";
		  }
		  return data;
	}
	
	private void read() throws Exception {
		String resultado;
		int numRead;
		
		SocketChannel socketChannel = (SocketChannel) m_key.channel();
					
		// Attempt to read off the channel
			
			
			// Clear out our read buffer so it's ready for new data
			this.m_readBuffer.clear();
			
			//this.m_readBuffer.rewind();				

			numRead = socketChannel.read(this.m_readBuffer);
			
			//logger.debug("numread: " + Integer.toString(numRead));
			
			if (numRead == -1) {
				// Remote entity shut the socket down cleanly. Do the
				// same from our end and cancel the channel.
				m_key.channel().close();
				m_key.cancel();
				
				throw new Exception("Conection closed by client");
			}
			
			// Hand the data off to our worker thread
			// los datos llegan codificados HTML
			
			//logger.debug("m_html_input inicial: " + m_html_input);
			
			m_html_input += bb_to_str(m_readBuffer).substring(0, numRead);
			
			//logger.debug("m_html_input final: " + m_html_input);
			
			m_str_input = StringEscapeUtils.unescapeHtml3(m_html_input);
			
			logger.debug("m_str_input: " + m_str_input);
			
			// request o response?
			
			try {
				
				m_jo = new JSONObject(m_str_input);
				
				// ok, mensaje completo
				//logger.debug("mensaje completo");
								
				m_html_input = "";

				try {
					resultado = m_jo.get("resultado").toString();
					
					// response
					
					try {
						
						ProcessResponse();
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						// ignore
					}
				} catch (Exception e) {
					// no existe atributo 'resultado'.... request
					try {
						
						ProcessRequest();
						
					} catch (Exception ex) {
						// TODO Auto-generated catch block
						// ignore
					}
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				// aun no tengo un objeto JSON completo... concateno
				logger.debug("mensaje incompleto");
			}
			
	    
	}
	
	private void write() throws IOException {
		Integer numwritten;
		
		SocketChannel socketChannel = (SocketChannel) m_key.channel();
		
		//logger.debug("About to write " + m_str_output.length() + " bytes.");
		
		ByteBuffer buf = str_to_bb(StringEscapeUtils.escapeHtml3(m_str_output + "\r\n"));
		
		//logger.debug("Will try to write " + buf.remaining() + " bytes.");
		
		numwritten = socketChannel.write(buf);
		
		//logger.debug("Written " + numwritten.toString() + " bytes.");
		
		logger.debug("m_str_output: " + m_str_output);
		
	} 	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String input, resultado;
		JSONObject jo;
		ArrayList<AbstractMap.SimpleEntry<String, String>> listParameters;
		// para medir timeout de datos desde cliente
		Date t0, t1;
		
		super.run();
		
		t0 = null;
		t1 = null;

        try {
            try {
            	listParameters = new ArrayList<AbstractMap.SimpleEntry<String, String>>();
            	
                logger.debug("Client Connected.");
                
			    // Register the new SocketChannel with our Selector, indicating
			    // we'd like to be notified when there's data waiting to be read
			    m_socketChannel.register(m_selector, SelectionKey.OP_READ);                
			    
                while (m_bKeepWorking)
                {
                	if (m_key != null) {
                		m_key.interestOps(SelectionKey.OP_READ);
                	}
                	
    		        // Wait for an event one of the registered channels, or timeout
    		        if (m_selector.select(m_ini.get("General", "socket_timeout", long.class)) > 0) {
    		        	
    		        	t0 = null;
	    		        // Iterate over the set of keys for which events are available
	    		        Iterator selectedKeys = m_selector.selectedKeys().iterator();
	    		        
	    		        while (selectedKeys.hasNext()) {
	
	    		        	m_key = (SelectionKey) selectedKeys.next();
	    					selectedKeys.remove();
	    					
	    					if (!m_key.isValid()) {
	    						continue;
	    					}
	    					
	    					// Check what event is available and deal with it
	    					if (m_key.isReadable()) {
	    						this.read();
	    					}
	    		        }
                	}
                    else {
                    	//logger.debug("in not ready");
                    	// chequeo si se ha alcanzado el timeout de no recepcion de datos desde el cliente
                    	// si el timeout se cumple, se cierra la conexion
                    	if (t0 == null) {
                    		t0 = new Date();
                    	}
                    	else {
                    		t1 = new Date();
                    		
                    		if (t1.getTime() - t0.getTime() > m_ini.get("General", "client_timeout", long.class)) {
                    			// timeout
                    			logger.debug("Client timeout");
                    			break;
                    		}
                    		else {
                    			//logger.debug("Still not client timeout");
                    		}
                    	}
                    }
    		        
                    while (!m_fifo_output.isEmpty()) {
                    	
                    	logger.debug("something to send to user id " + m_usuario.get_id().toString());
                    	
                    	m_key.interestOps(SelectionKey.OP_WRITE);
                    	
        		        // Wait for an event one of the registered channels, or timeout
        		        if (m_selector.select(m_ini.get("General", "socket_timeout", long.class)) > 0) {
        				
    	    		        // Iterate over the set of keys for which events are available
    	    		        Iterator selectedKeys = m_selector.selectedKeys().iterator();
    	    		        
    	    		        while (selectedKeys.hasNext()) {
    	
    	    		        	m_key = (SelectionKey) selectedKeys.next();
    	    					selectedKeys.remove();
    	    					
    	    					if (!m_key.isValid()) {
    	    						continue;
    	    					}
    	    					
    	    					// Check what event is available and deal with it
    	    					if (m_key.isWritable()) {
    	    						m_str_output = m_fifo_output.element();
    	    						this.write();
    	    						logger.debug("Sent message to user id " + m_usuario.get_id().toString());
    	                        	m_fifo_output.removeFirst();
    	    					}
    	    		        }
                    	}
                    	

                    }
    		        
    		        /*
    		        2013/nov/22 Los mensajes asincronos seran enviados con un request 
    		        
                	if (m_usuario != null) {
                		
                    	// hay algun evento asincrono que notificar al cliente?
                    	ArrayList<MensajeUsuario> lmu;
                    	
        				listParameters.clear();
        				
        				listParameters.add(new AbstractMap.SimpleEntry<String, String>("no leido", ""));
        				listParameters.add(new AbstractMap.SimpleEntry<String, String>("id_usuario", m_usuario.get_id().toString()));
        				listParameters.add(new AbstractMap.SimpleEntry<String, String>("mensaje_reciente", m_ini.get("General", "recent")));
        				
        				lmu = MensajeUsuario.seek(m_conn, listParameters, "mu.fecha", "ASC", 0, 1);
        				
        				Iterator<MensajeUsuario> iterator = lmu.iterator();
        				
        				
        				if (iterator.hasNext()) {
        					// hay mensaje para enviar a usuario
        					MensajeUsuario mu = iterator.next();
        					
        					m_str_output = mu.get_mensaje();
        					
        					
        					
        					
        					
                        	logger.debug("something to send to user id " + m_usuario.get_id().toString());
                        	
                        	m_key.interestOps(SelectionKey.OP_WRITE);
                        	
            		        // Wait for an event one of the registered channels, or timeout
            		        if (m_selector.select(m_ini.get("General", "socket_timeout", long.class)) > 0) {
            				
        	    		        // Iterate over the set of keys for which events are available
        	    		        Iterator selectedKeys = m_selector.selectedKeys().iterator();
        	    		        
        	    		        while (selectedKeys.hasNext()) {
        	
        	    		        	m_key = (SelectionKey) selectedKeys.next();
        	    					selectedKeys.remove();
        	    					
        	    					if (!m_key.isValid()) {
        	    						break;
        	    					}
        	    					
        	    					// Check what event is available and deal with it
        	    					if (m_key.isWritable()) {
        	    						this.write();
        	    						
        	    						// write ok
        	    						
        	    						logger.debug("Sent message to user id " + m_usuario.get_id().toString());
        	                        	//m_fifo_output.removeFirst();
			        					
			        					
			            		        //logger.debug("mu.get_fecha() 1 " + mu.get_fecha());
			                        	mu.set_fecha(mu.get_fecha());
			                        	// logger.debug("mu.get_fecha() 2 " + mu.get_fecha());
			        					mu.set_leido(true);
			        					mu.update(m_conn);
        	    					}
        	    					else {
        	    						break;
        	    					}
        	    		        }
                        	}
        					
        					
        				}
                	}
                    //}
                    */                    
                    //Thread.sleep(m_ini.get("General", "socket_timeout", long.class));
                } // end while
                
                logger.debug("leaving loop");
            }
            catch(Exception ex) {
    			logger.debug("Exception: " + ex.getMessage());
    			ex.printStackTrace();
            	
            }            
            
        }
        catch (Exception e) {
            logger.debug("Client Error: " + e.toString());
        }
        finally {
        	
        	try {
				m_selector.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				logger.debug("Client Error: " + e1.toString());
				e1.printStackTrace();
			}
        	
        	m_key.cancel();
        	
            try {
				m_socketChannel.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.debug("Client Error: " + e.toString());
				e.printStackTrace();
			}
            
            m_fifo_output.clear();
            
    		// cierro conexion a la BD
    		try {

    			m_conn.close();
    			
     		} catch (SQLException ex) {
    			// TODO Auto-generated catch block
            	logger.debug("SQLException: " + ex.getMessage());
            	logger.debug("SQLState: " + ex.getSQLState());
            	logger.debug("VendorError: " + ex.getErrorCode());
    			ex.printStackTrace();
    		}
        }
        
        if (m_usuario != null) {
        	//logger.debug("Thread ending. User ID " + m_usuario.get_id().toString() + " Queued messages not sent: " + m_fifo_output.size());
        	logger.debug("Thread ending. User ID " + m_usuario.get_id().toString());
        }
        else {
        	logger.debug("Thread ending.");
        }
	}
	
	protected void ProcessResponse() throws Exception {
		
	}

	protected void ProcessRequest() throws Exception {
		JSONObject data;
		String tipo, pais, region, ciudad, comuna, str_output;
		long token;
		ArrayList<Comuna> listaComuna;
		ArrayList<AbstractMap.SimpleEntry<String, String>> listParameters;
		
		tipo = "";
		str_output = "";
		token = 0;
		
        try {
        	listParameters = new ArrayList<AbstractMap.SimpleEntry<String, String>>();
        	
        	tipo = m_jo.get("tipo").toString();
        	data = m_jo.getJSONObject("data");
        	
        	// todos los requests traen token
        	
        	token = data.getLong("token");
        	
        	logger.debug("Tipo Req: " + tipo);
        	
        	if (tipo.equals("MSG_REGISTRO")) {
        		Usuario u;
        		
        		if (m_usuario != null) {
        			throw new Exception("No es posible registrar usuario cuando ya hubo Login");
        		}
        		
        		u = new Usuario();
        		
        		try {
					String alias = data.get("alias").toString();
					logger.debug("alias: " + alias);
					
					u.set_alias(alias);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo alias no presente" + ": " + e.getMessage());
				}
        		
        		try {
					String contrasena = data.get("contrasena").toString();
					logger.debug("contrasena: " + contrasena);
					
					u.set_contrasena(contrasena);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo contrasena no presente" + ": " + e.getMessage());
				}

        		try {
					String nombre = data.get("nombre").toString();
					logger.debug("nombre: " + nombre);
					
					u.set_nombre(nombre);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo nombre no presente" + ": " + e.getMessage());
				}

        		try {
					String apellido_paterno = data.get("apellido_paterno").toString();
					logger.debug("apellido_paterno: " + apellido_paterno);
					
					u.set_apellido_paterno(apellido_paterno);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo apellido_paterno no presente" + ": " + e.getMessage());
				}

        		try {
					String apellido_materno = data.get("apellido_materno").toString();
					logger.debug("apellido_materno: " + apellido_materno);
					
					u.set_apellido_materno(apellido_materno);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo apellido_materno no presente" + ": " + e.getMessage());
				}
				
        		try {
					String movil = data.get("movil").toString();
					logger.debug("movil: " + movil);
					
					u.set_movil(movil);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo movil no presente" + ": " + e.getMessage());
				}

        		try {
					String rut = data.get("rut").toString();
					logger.debug("rut: " + rut);
					
					u.set_rut(rut);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo rut no presente" + ": " + e.getMessage());
				}

        		try {
					String email = data.get("email").toString();
					logger.debug("email: " + email);
					
					u.set_email(email);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo email no presente" + ": " + e.getMessage());
				}

        		try {
					String fecha_nacimiento = data.get("fecha_nacimiento").toString();
					logger.debug("fecha_nacimiento: " + fecha_nacimiento);
					
					u.set_fecha_nacimiento(fecha_nacimiento);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo fecha_nacimiento no presente. " .concat(e.getMessage()));
				}

        		try {
					String direccion = data.get("direccion").toString();
					logger.debug("direccion: " + direccion);
					
					u.set_direccion(direccion);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo direccion no presente" + ": " + e.getMessage());
				}

        		try {
					String foto = data.get("foto").toString();
					logger.debug("foto: " + foto);
					
					u.set_foto(foto);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo foto no presente" + ": " + e.getMessage());
				}

        		try {
					String antecedentes_emergencia = data.get("antecedentes_emergencia").toString();
					logger.debug("antecedentes_emergencia: " + antecedentes_emergencia);
					
					u.set_antecedentes_emergencia(antecedentes_emergencia);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo antecedentes_emergencia no presente" + ": " + e.getMessage());
				}

				try {
					comuna = data.get("comuna").toString();
					logger.debug("comuna: " + comuna);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo comuna no presente" + ": " + e.getMessage());
				}

				try {
					ciudad = data.get("ciudad").toString();
					logger.debug("ciudad: " + ciudad);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo ciudad no presente" + ": " + e.getMessage());
				}

        		try {
        			region = data.get("region").toString();
					logger.debug("region: " + region);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo region no presente" + ": " + e.getMessage());
				}

        		try {
					pais = data.get("pais").toString();
					logger.debug("pais: " + pais);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo pais no presente" + ": " + e.getMessage());
				}
				
				// trato de obtener el id_comuna
				
				listParameters.clear();
				
				listParameters.add(new AbstractMap.SimpleEntry<String, String>("nombre", "'" + comuna + "'"));
				listParameters.add(new AbstractMap.SimpleEntry<String, String>("ciudad", "'" + ciudad + "'"));
				listParameters.add(new AbstractMap.SimpleEntry<String, String>("region", "'" + region + "'"));
				listParameters.add(new AbstractMap.SimpleEntry<String, String>("pais", "'" + pais + "'"));
				
				listaComuna = Comuna.seek(m_conn, listParameters, null, null, 0, 10);
				
				if (listaComuna.isEmpty()) {
					throw new Exception("Comuna no encontrada en la base de datos");
				}
				
				if (listaComuna.size() > 1) {
					throw new Exception("Mas de una fila obtenida desde la base de datos");
				}
				
				u.set_id_comuna(listaComuna.get(0).get_id());
				
				// ya existe usuario con el alias especificado?
				if (Usuario.getByAlias(m_conn, u.get_alias()) != null) {
					throw new Exception("Ya existe usuario con el alias '" + u.get_alias() + "'");
				}
				
				// ya existe usuario con el email especificado?
				if (Usuario.getByEmail(m_conn, u.get_email()) != null) {
					throw new Exception("Ya existe usuario con el email '" + u.get_email() + "'");
				}

				// ya existe usuario con el movil especificado?
				if (Usuario.getByMovil(m_conn, u.get_movil()) != null) {
					throw new Exception("Ya existe usuario con el movil '" + u.get_movil() + "'");
				}

				u.insert(m_conn);
				
				str_output =
        				"{" +
        				"\"tipo\": \"MSG_REGISTRO\"," +
        				"\"token\":\"" + String.valueOf(token) + "\"," + 
        				"\"resultado\":\"0\"," +
        				"\"descripcion\":\"Exito\"," + 
        				"\"data\":{" +
        				"    \"id_usuario\":\"" + u.get_id().toString() + "\"" +
        				"}" +
        				"}";
				
				m_usuario = u;
        	}
        	else if (tipo.equals("MSG_ACTUALIZA_CONTACTOS")) {
        		Integer id_usuario;
        		
				try {
					id_usuario = Integer.decode(data.get("id_usuario").toString());
					logger.debug("id_usuario: " + id_usuario);
					
	        		if (m_usuario != null) {
	        			if (m_usuario.get_id() != id_usuario) {
	        				throw new Exception("No es posible cambio de usuario en una misma sesion");
	        			}
	        			
	        		}
	        		else {
						m_usuario = Usuario.getById(m_conn, id_usuario.toString());
						
						if (m_usuario == null) {
							throw new Exception("No existe el usuario con id " + id_usuario.toString());
						}
	        		}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new Exception(e.getMessage());
				}

				try {
					JSONArray ja = data.getJSONArray("contactos");
					
					// borro los contactos actuales
					Contacto.delete(m_conn, m_usuario.get_id());
					
					for (int i = 0; i < ja.length(); i++) {
						String descripcion = ja.getJSONObject(i).getString("descripcion");
						String movil = ja.getJSONObject(i).getString("movil");
						
						Usuario uc = Usuario.getByMovil(m_conn, movil);
						
						Contacto ct = new Contacto();
						
						ct.set_id_usuario(m_usuario.get_id());
						ct.set_descripcion(descripcion);
						ct.set_movil(movil);
						
						if (uc != null) {
							
							ct.set_id_usuario_contacto(uc.get_id().toString());
						}
						else {
							// si el contacto no esta registrado, no tengo id_usuario... me quedo con movil
						}
						
						ct.insert(m_conn);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new Exception(e.getMessage());
				}

				str_output =
    				"{" +
    				"\"tipo\": \"MSG_ACTUALIZA_CONTACTOS\"," +
    				"\"token\":\"" + String.valueOf(token) + "\"," + 
    				"\"resultado\":\"0\"," +
    				"\"descripcion\":\"Exito\"," + 
    				"}";
				
        	}
        	else if (tipo.equals("MSG_POSICION")) {
        		Integer id_usuario;
        		Double latitud, longitud;
        		String fecha;
        		
        		try {
        			id_usuario = Integer.decode(data.get("id_usuario").toString());
					logger.debug("id_usuario: " + id_usuario);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo id_usuario no presente" + ": " + e.getMessage());
				}
        		
        		
        		try {
					
	        		if (m_usuario != null) {
	        			logger.debug("m_usuario != null");
	        			if (m_usuario.get_id() != id_usuario) {
	        				throw new Exception("No es posible cambio de usuario en una misma sesion");
	        			}
	        			
	        		}
	        		else {
	        			logger.debug("m_usuario == null");
	        			logger.debug("m_conn: " + m_conn);
						m_usuario = Usuario.getById(m_conn, id_usuario.toString());
						logger.debug("Usuario.getById ok");
						if (m_usuario == null) {
							throw new Exception("No existe el usuario con id " + id_usuario.toString());
						}
	        		}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Excepcion del tipo " + e.getClass() + " Info: " + e.getMessage());
				}
        		
        		try {
					latitud = Double.valueOf(data.get("latitud").toString());
					logger.debug("latitud: " + latitud);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo latitud no presente" + ": " + e.getMessage());
				}

        		try {
					longitud = Double.valueOf(data.get("longitud").toString());
					logger.debug("longitud: " + longitud);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo longitud no presente" + ": " + e.getMessage());
				}

				// si bien viene la fecha/hora en el requerimiento, la seteamos a la fecha/hora actual
        		try {
					fecha = data.get("fecha").toString();
					logger.debug("fecha: " + fecha);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo fecha no presente" + ": " + e.getMessage());
				}
				
				Usuario.setPosicion(m_conn, m_usuario.get_id(), latitud, longitud, "DATE_FORMAT(now(), '%d-%m-%Y %H:%i:%s')");
				
				logger.debug(UsuarioPosicion.getByIdUsuario(m_conn, m_usuario.get_id()).toString());
				
				str_output =
    				"{" +
    				"\"tipo\": \"MSG_POSICION\"," +
    				"\"token\":\"" + String.valueOf(token) + "\"," + 
    				"\"resultado\":\"0\"," +
    				"\"descripcion\":\"Exito\"," +
    				"\"frecuencia\":\"" + m_ini.get("General", "position_frequency") + "\"" +
    				"}";
				
        	}
        	else if (tipo.equals("MSG_POLEA_MENSAJE")) {
        		Integer id_usuario;
        		Double latitud, longitud;
        		String fecha, mensaje, encontrado;
        		
        		mensaje = "";
        		encontrado = "";
        		
        		try {
        			id_usuario = Integer.decode(data.get("id_usuario").toString());
					logger.debug("id_usuario: " + id_usuario);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo id_usuario no presente" + ": " + e.getMessage());
				}
        		
        		
        		try {
					
	        		if (m_usuario != null) {
	        			logger.debug("m_usuario != null");
	        			if (m_usuario.get_id() != id_usuario) {
	        				throw new Exception("No es posible cambio de usuario en una misma sesion");
	        			}
	        			
	        		}
	        		else {
	        			logger.debug("m_usuario == null");
	        			logger.debug("m_conn: " + m_conn);
						m_usuario = Usuario.getById(m_conn, id_usuario.toString());
						logger.debug("Usuario.getById ok");
						if (m_usuario == null) {
							throw new Exception("No existe el usuario con id " + id_usuario.toString());
						}
	        		}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Excepcion del tipo " + e.getClass() + " Info: " + e.getMessage());
				}
        						

				// hay algun evento asincrono que notificar al cliente?
            	ArrayList<MensajeUsuario> lmu;
            	
				listParameters.clear();
				
				listParameters.add(new AbstractMap.SimpleEntry<String, String>("no leido", ""));
				listParameters.add(new AbstractMap.SimpleEntry<String, String>("id_usuario", m_usuario.get_id().toString()));
				listParameters.add(new AbstractMap.SimpleEntry<String, String>("mensaje_reciente", m_ini.get("General", "recent")));
				
				lmu = MensajeUsuario.seek(m_conn, listParameters, "mu.fecha", "ASC", 0, 1);
				
				Iterator<MensajeUsuario> iterator = lmu.iterator();
				
				
				if (iterator.hasNext()) {
					// hay mensaje para enviar a usuario
					encontrado = "S";
					MensajeUsuario mu = iterator.next();
					
					mensaje = mu.get_mensaje();
					
    		        //logger.debug("mu.get_fecha() 1 " + mu.get_fecha());
                	mu.set_fecha(mu.get_fecha());
                	// logger.debug("mu.get_fecha() 2 " + mu.get_fecha());
					mu.set_leido(true);
					mu.update(m_conn);
				}
				else {
					// no hay mensaje pendiente
					encontrado = "N";
				}

				str_output =
    				"{" +
    				"\"tipo\": \"MSG_POLEA_MENSAJE\"," +
    				"\"token\":\"" + String.valueOf(token) + "\"," + 
    				"\"resultado\":\"0\"," +
    				"\"descripcion\":\"Exito\"," +
    				"\"encontrado\":\"" + encontrado + "\"," +
    				"\"mensaje\":\"" + StringEscapeUtils.escapeJava(mensaje) + "\"" +
    				"}";
				
        	}
        	else if (tipo.equals("MSG_LOGIN")) {
        		
        	}
        	else if (tipo.equals("MSG_LOGOUT")) {
        		m_usuario = null;
        	}
        	else if (tipo.equals("MSG_PANICO_ALARMA")) {
        		Integer id_usuario;
        		//String str_output;
        		Double latitud, longitud;
        		Short tipo_incidente, tipo_alarma;
        		Incidente i;
        		Chat ch;
        		ChatUsuario chu;
        		LatLonPoint origen, norte, sur, este, oeste;
        		ArrayList<Usuario> lu;
        		String str_guardianes, str_mensaje_a_encolar;
        		MensajeUsuario mu;
        		
            	str_output = "";
        		
        		try {
					id_usuario = Integer.decode(data.get("id_usuario").toString());
					logger.debug("id_usuario: " + id_usuario);
					
	        		if (m_usuario != null) {
	        			if (m_usuario.get_id() != id_usuario) {
	        				throw new Exception("No es posible cambio de usuario en una misma sesion");
	        			}
	        			
	        		}
	        		else {
						m_usuario = Usuario.getById(m_conn, id_usuario.toString());
						
						if (m_usuario == null) {
							throw new Exception("No existe el usuario con id " + id_usuario.toString());
						}
	        		}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo id_usuario no presente" + ": " + e.getMessage());
				}
				
        		try {
					latitud = Double.valueOf(data.get("latitud").toString());
					logger.debug("latitud: " + latitud);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo latitud no presente" + ": " + e.getMessage());
				}

        		try {
					longitud = Double.valueOf(data.get("longitud").toString());
					logger.debug("longitud: " + longitud);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo longitud no presente" + ": " + e.getMessage());
				}

        		try {
					tipo_incidente = Short.decode(data.get("tipo_incidente").toString());
					logger.debug("tipo_incidente: " + tipo_incidente);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo tipo_incidente no presente" + ": " + e.getMessage());
				}

        		try {
					tipo_alarma = Short.decode(data.get("tipo_alarma").toString());
					logger.debug("tipo_alarma: " + tipo_alarma);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo tipo_alarma no presente" + ": " + e.getMessage());
				}
				
				// * Usuario.setPosicion(m_conn, id_usuario, latitud, longitud, "DATE_FORMAT(now(), '%d-%m-%Y %H:%i:%s')");
				
				// creo un registro en tabla chat
				ch = new Chat();
				
				ch.set_nombre("Incidente");
				ch.set_fecha_creacion("DATE_FORMAT(now(), '%d-%m-%Y %H:%i:%s')");
				ch.set_id_usuario_creador(m_usuario.get_id());
				
				ch.insert(m_conn);
				
				// creo un incidente
				i = new Incidente();
				
				i.set_tipo(tipo_incidente);
				i.set_id_usuario(m_usuario.get_id());
				i.set_fecha("DATE_FORMAT(now(), '%d-%m-%Y %H:%i:%s')");
				i.set_latitud(latitud);
				i.set_longitud(longitud);
				i.set_id_chat(ch.get_id());
				
				i.insert(m_conn);
				
				// leo de la bd para tener las fechas reales
				
				i = Incidente.getById(m_conn, i.get_id().toString());
				
				// en base a la posicion del incidente, busco los usuarios dentro de un cuadrado de n kilometros, tanto de las
				// comunidades propias, los contactos y la comunidad temporal
				
				origen = new LatLonPoint.Double();
				
				origen.setLatLon(latitud, longitud);
				
				// norte
				norte = origen.getPoint(Length.METER.toRadians(m_ini.get("General", "square", int.class)), 0);
				
				//logger.debug("Norte: " + norte);
				
				// este
				este = origen.getPoint(Length.METER.toRadians(m_ini.get("General", "square", int.class)), Math.PI / 2);
				
				//logger.debug("Este: " + este);
				
				// oeste
				oeste = origen.getPoint(Length.METER.toRadians(m_ini.get("General", "square", int.class)), -1 * Math.PI / 2);
				
				//logger.debug("Oeste: " + oeste);
				
				// sur
				sur = origen.getPoint(Length.METER.toRadians(m_ini.get("General", "square", int.class)), Math.PI);
				
				//logger.debug("Sur: " + sur);
				
				// busco lus usuarios con sur.latitud < latitud < norte.latitud y oeste.longitud < longitud < este.longitud
				
				listParameters.clear();
				
				listParameters.add(new AbstractMap.SimpleEntry<String, String>("latitud_mayor", Double.toString(sur.getLatitude())));
				listParameters.add(new AbstractMap.SimpleEntry<String, String>("latitud_menor", Double.toString(norte.getLatitude())));
				listParameters.add(new AbstractMap.SimpleEntry<String, String>("longitud_mayor", Double.toString(oeste.getLongitude())));
				listParameters.add(new AbstractMap.SimpleEntry<String, String>("longitud_menor", Double.toString(este.getLongitude())));
				listParameters.add(new AbstractMap.SimpleEntry<String, String>("posicion_reciente", m_ini.get("General", "recent")));
				
				listParameters.add(new AbstractMap.SimpleEntry<String, String>("id_distinto", m_usuario.get_id().toString()));
				
				lu = Usuario.seek(m_conn, listParameters, null, null, 0, 10000);
				
				str_output =
    				"{" +
    				"\"tipo\": \"MSG_PANICO_ALARMA\"," +
    				"\"token\":\"" + String.valueOf(token) + "\"," + 
    				"\"resultado\":\"0\"," +
    				"\"descripcion\":\"Exito\"," + 
    				"\"data\":{" +
    				"    \"id_incidente\":\"" + i.get_id().toString() + "\"," +
    				"    \"listado_guardianes\": [";

				str_guardianes = "";
				
				for (Usuario us : lu) {
					
					if (!str_guardianes.equals("")) {
						str_guardianes += ",";
					}
					
					//logger.debug("Usuario en cercania incidente: " + us);
					
					// es contacto? pertenece a la comunidad del usuario originador?
					listParameters.clear();
					
					listParameters.add(new AbstractMap.SimpleEntry<String, String>("id_usuario", id_usuario.toString()));
					listParameters.add(new AbstractMap.SimpleEntry<String, String>("id_usuario_contacto", us.get_id().toString()));
					
					str_guardianes +=
						"{ \"id_usuario\":\"" + us.get_id().toString() + "\" , \"alias\":\"" + us.get_alias() + "\", \"latitud\":\"" + us.get_latitud().toString() + "\", \"longitud\":\"" + us.get_longitud().toString() + "\", \"tipo_amigo\":\"";
					
					if (Contacto.seek(m_conn, listParameters, null, null, 0, 10000).size() > 0) {
						// es contacto
						logger.debug("Contacto");
						
						str_guardianes += "CONTACTO_DIRECTO";
					}
					else {
						listParameters.clear();
						
						listParameters.add(new AbstractMap.SimpleEntry<String, String>("id_usuario", id_usuario.toString()));
						listParameters.add(new AbstractMap.SimpleEntry<String, String>("en_comunidad_con", us.get_id().toString()));

						if (ComunidadUsuario.seek(m_conn, listParameters, null, null, 0, 10000).size() > 0) {
							// es de la comunidad del usuario que genera alarma
							logger.debug("Comunidad");
							
							str_guardianes += "CONTACTO_COMUNIDAD";
						}
						else {
							// es de la comunidad temporal
							logger.debug("Temporal");
							
							str_guardianes += "CONTACTO_TEMPORAL";
						}
					}
					
					// inserto en chat_usuario
					chu = new ChatUsuario();
					
					chu.set_id_chat(ch.get_id());
					chu.set_id_usuario(us.get_id());
					chu.set_fecha_ingreso("DATE_FORMAT(now(), '%d-%m-%Y %H:%i:%s')");
					chu.set_latitud_inicial(us.get_latitud());
					chu.set_longitud_inicial(us.get_longitud());
						
					str_guardianes +=
						"\" }";
					
				}
				
				for (Usuario us : lu) {
					// debo notificar al usuario del evento; escribo registro en tabla mensaje_usuario
					str_mensaje_a_encolar =
	    				"{" +
	    				"\"tipo\": \"NOT_ALARMA_GUARDIAN\"," +
	    				"\"data\":{" +
	    				"    \"id_incidente\":\"" + i.get_id().toString() + "\"," +
	    				"    \"tipo_incidente\":\"" + i.get_tipo().toString() + "\"," +
	    				"    \"id_usuario_incidente\":\"" + m_usuario.get_id().toString() + "\"," +
	    				"    \"fecha_incidente\":\"" + i.get_fecha() + "\"," +
	    				"    \"latitud_incidente\":\"" + i.get_latitud().toString() + "\"," +
	    				"    \"longitud_incidente\":\"" + i.get_longitud().toString() + "\"," +
	    				"    \"alias\":\"" + m_usuario.get_alias() + "\"," +
	    				"    \"listado_guardianes\": [" +
	    				str_guardianes +
	    				"    ]" +
	    				"    }" +
	    				"}";        		
						
					mu = new MensajeUsuario();
					
					mu.set_id_usuario(us.get_id());
					mu.set_mensaje(str_mensaje_a_encolar);
					mu.set_fecha("DATE_FORMAT(now(), '%d-%m-%Y %H:%i:%s')");
					mu.set_leido(false);
					
					mu.insert(m_conn);
				}

				// ingreso al usuario originador al chat
				chu = new ChatUsuario();
				
				chu.set_id_chat(i.get_id());
				chu.set_id_usuario(m_usuario.get_id());
				chu.set_fecha_ingreso("DATE_FORMAT(now(), '%d-%m-%Y %H:%i:%s')");
				chu.set_latitud_inicial(m_usuario.get_latitud());
				chu.set_longitud_inicial(m_usuario.get_longitud());
				
				
  				str_output += 
  					str_guardianes +
    				"    ]" +
    				"    }" +
    				"}";
  				
  				//m_fifo_output.add(new String(str_output));
				
        	}
        	else if (tipo.equals("MSG_CREA_COMUNIDAD")) {
        		
        		ComunidadUsuario cu;
        		Integer id_usuario;
        		Comunidad cm;
        		String nombre, descripcion;
        		Double latitud, longitud;
        		Long cobertura;
        		
        		try {
					id_usuario = Integer.decode(data.get("id_usuario").toString());
					logger.debug("id_usuario: " + id_usuario);
					
	        		if (m_usuario != null) {
	        			if (m_usuario.get_id() != id_usuario) {
	        				throw new Exception("No es posible cambio de usuario en una misma sesion");
	        			}
	        			
	        		}
	        		else {
						m_usuario = Usuario.getById(m_conn, id_usuario.toString());
						
						if (m_usuario == null) {
							throw new Exception("No existe el usuario con id " + id_usuario.toString());
						}
	        		}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo id_usuario no presente" + ": " + e.getMessage());
				}

				try {
					nombre = data.get("nombre").toString();
					logger.debug("nombre: " + nombre);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo nombre no presente" + ": " + e.getMessage());
				}
        		
        		try {
					descripcion = data.get("descripcion").toString();
					logger.debug("descripcion: " + descripcion);
										
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo descripcion no presente" + ": " + e.getMessage());
				}

        		try {
					latitud = Double.valueOf(data.get("latitud").toString());
					logger.debug("latitud: " + latitud);
										
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo latitud no presente" + ": " + e.getMessage());
				}

        		try {
        			longitud = Double.valueOf(data.get("longitud").toString());
					logger.debug("longitud: " + longitud);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo longitud no presente" + ": " + e.getMessage());
				}

        		try {
					cobertura = Long.decode(data.get("cobertura").toString());
					logger.debug("cobertura: " + cobertura);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo cobertura no presente" + ": " + e.getMessage());
				}
				
				// existe ya una comunidad con el nombre escogido?
				if (Comunidad.getByNombre(m_conn, nombre) != null) {
					throw new Exception("Ya existe una comunidad con el nombre '" + nombre + "'");
				}
				
				cm = new Comunidad();
				
				cm.set_nombre(nombre);
				cm.set_descripcion(descripcion);
				cm.set_latitud(latitud);
				cm.set_longitud(longitud);
				cm.set_cobertura(cobertura);
				cm.set_fecha_creacion("DATE_FORMAT(now(), '%d-%m-%Y %H:%i:%s')");
				
				cm.insert(m_conn);
				
				// creo al usuario creador como miembro de la comunidad y lider
				cu = new ComunidadUsuario();
				
				cu.set_id_comunidad(cm.get_id());
				cu.set_id_usuario(m_usuario.get_id());
				cu.set_es_lider(true);
				cu.set_es_administrador(false);
				cu.set_fecha_ingreso("DATE_FORMAT(now(), '%d-%m-%Y %H:%i:%s')");
				
				cu.insert(m_conn);
				
				str_output =
        				"{" +
        				"\"tipo\": \"MSG_CREA_COMUNIDAD\"," +
        				"\"token\":\"" + String.valueOf(token) + "\"," + 
        				"\"resultado\":\"0\"," +
        				"\"descripcion\":\"Exito\"," + 
        				"\"data\":{" +
        				"    \"id_comunidad\":\"" + cm.get_id().toString() + "\"" +
        				"}" +
        				"}";        		
        	}
        	else if (tipo.equals("MSG_AGREGA_A_COMUNIDAD")) {
        		
        		Integer id_usuario, id_usuario_a_agregar, id_comunidad;
        		Comunidad cm;
        		ComunidadUsuario cu;
        		ArrayList<ComunidadUsuario> lcu;
        		
        		try {
					id_usuario = Integer.decode(data.get("id_usuario").toString());
					logger.debug("id_usuario: " + id_usuario);
					
	        		if (m_usuario != null) {
	        			if (m_usuario.get_id() != id_usuario) {
	        				throw new Exception("No es posible cambio de usuario en una misma sesion");
	        			}
	        			
	        		}
	        		else {
						m_usuario = Usuario.getById(m_conn, id_usuario.toString());
						
						if (m_usuario == null) {
							throw new Exception("No existe el usuario con id " + id_usuario.toString());
						}
	        		}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo id_usuario no presente" + ": " + e.getMessage());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception(e.getMessage());
				}
        		
        		try {
        			id_usuario_a_agregar = Integer.decode(data.get("id_usuario_a_agregar").toString());
					logger.debug("id_usuario_a_agregar: " + id_usuario_a_agregar);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo id_usuario no presente" + ": " + e.getMessage());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception(e.getMessage());
				}

				try {
        			id_comunidad = Integer.decode(data.get("id_comunidad").toString());
					logger.debug("id_comunidad: " + id_comunidad);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo id_comunidad no presente" + ": " + e.getMessage());
				}
				
				// comunidad valida?
				cm = Comunidad.getById(m_conn, id_comunidad.toString());
				
				if (cm == null) {
					throw new Exception("Comunidad con id " + id_comunidad.toString() + " no existe");
				}
				
				// es administrador o lider el usuario actual de la comunidad?
				listParameters.clear();
				
				listParameters.add(new AbstractMap.SimpleEntry<String, String>("id_comunidad", id_comunidad.toString()));
				listParameters.add(new AbstractMap.SimpleEntry<String, String>("id_usuario", id_usuario.toString()));
				
				lcu = ComunidadUsuario.seek(m_conn, listParameters, null, null, 0, 10000);
				
				if (lcu.size() == 0) {
					throw new Exception("Usuario actual (id " + id_usuario.toString() + ") no pertenece a la comunidad con id " + id_comunidad.toString());
				}
				else if (lcu.size() > 1) {
					throw new Exception("ComunidadUsuario.seek retorno mas de una fila para id_usuario " + id_usuario.toString() + " y id_comunidad " + id_comunidad.toString());
				}
				else {
					cu = lcu.get(0);
					
					if (!cu.get_es_administrador() && !cu.get_es_lider()) {
						throw new Exception("Usuario actual (id " + id_usuario.toString() + ") no es lider o administrador de la comunidad con id " + id_comunidad.toString());
					}
				}

				// existe ya el usuario en la comunidad?
				listParameters.clear();
				
				listParameters.add(new AbstractMap.SimpleEntry<String, String>("id_comunidad", id_comunidad.toString()));
				listParameters.add(new AbstractMap.SimpleEntry<String, String>("id_usuario", id_usuario_a_agregar.toString()));
				
				if (ComunidadUsuario.seek(m_conn, listParameters, null, null, 0, 10000).size() > 0) {
					throw new Exception("Usuario con id " + id_usuario_a_agregar.toString() + " ya esta presente en comunidad con id " + id_comunidad.toString());
				}
				
				cu = null;
				
				// todo ok
				cu = new ComunidadUsuario();
				
				cu.set_id_comunidad(id_comunidad);
				cu.set_id_usuario(id_usuario_a_agregar);
				cu.set_es_lider(false);
				cu.set_es_administrador(false);
				cu.set_fecha_ingreso("DATE_FORMAT(now(), '%d-%m-%Y %H:%i:%s')");
				
				cu.insert(m_conn);
        		
				str_output =
    				"{" +
    				"\"tipo\": \"MSG_AGREGA_A_COMUNIDAD\"," +
    				"\"token\":\"" + String.valueOf(token) + "\"," + 
    				"\"resultado\":\"0\"," +
    				"\"descripcion\":\"Exito\"," + 
    				"}";        		
        	}
        	else if (tipo.equals("MSG_INICIO_CHAT")) {
        		
        	}
        	else if (tipo.equals("MSG_ENVIO_ARCHIVO")) {
        		
        	}
        	else if (tipo.equals("MSG_CREA_TEMA")) {
        		
        	}
        	else if (tipo.equals("MSG_ARCHIVO_TEMA")) {
        		
        	}
        	else {
        		logger.debug("Tipo de mensaje desconocido: " + tipo);
        	}
        	
        }
        catch (JSONException ex) {
            logger.debug("JSONException: " + ex.toString());
			ex.printStackTrace();
			str_output =
				"{" +
				"\"tipo\": \"" + tipo + "\"," +
				"\"token\":\"" + String.valueOf(token) + "\"," + 
				"\"resultado\":\"1\"," +
				"\"descripcion\":\"Fallo tipo JSONException, " + ex.getMessage() + "\"," + 
				"}";        		
            
        }
        catch (SQLException ex) {
			logger.debug("SQLException: " + ex.getMessage());
			logger.debug("SQLState: " + ex.getSQLState());
			logger.debug("VendorError: " + ex.getErrorCode());
			ex.printStackTrace();
			str_output =
				"{" +
				"\"tipo\": \"" + tipo + "\"," +
				"\"token\":\"" + String.valueOf(token) + "\"," + 
				"\"resultado\":\"1\"," +
				"\"descripcion\":\"Fallo tipo SQLException, " + ex.getMessage() + "\"," + 
				"}";        		
        }
        catch (Exception ex) {
			logger.debug("Exception: " + ex.getMessage());
			ex.printStackTrace();
			str_output =
				"{" +
				"\"tipo\": \"" + tipo + "\"," +
				"\"token\":\"" + String.valueOf(token) + "\"," + 
				"\"resultado\":\"1\"," +
				"\"descripcion\":\"Fallo tipo Exception, " + ex.getMessage() + "\"," + 
				"}";        		
        }
        
    	if (!str_output.equals("")) {
    		
    		m_fifo_output.add(new String(str_output));  		
    	}
	}
	/*
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
		
		// cierro conexion a la BD
		try {
			m_conn.close();
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
        	logger.debug("SQLException: " + ex.getMessage());
        	logger.debug("SQLState: " + ex.getSQLState());
        	logger.debug("VendorError: " + ex.getErrorCode());
			ex.printStackTrace();
		}
	}
	*/
	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub
		//logger.debug("inicio start");
		String config_file_name;
		
        try {
        	// leo archivo de configuracion
        	
        	m_ini = new Wini();
        	/*
        	URL url = ASDaemon.class.getClassLoader().getResource("./etc/config.ini");
        	
        	logger.debug("url: " + url);
        	
        	logger.debug("url.getPath(): " + url.getPath());
        	
        	logger.debug("pwd 1: " + System.getProperty("user.dir"));
        	
        	logger.debug("pwd 2: " + new File(".").getAbsolutePath());
        	
        	// m_ini.load(new File(new URL(url.getPath()).getPath()));
        	*/
        	
        	config_file_name = System.getProperty("config_file");
        	
        	File f = new File(config_file_name);
        	
        	if (!f.exists()) {
        		throw new Exception("Config file does not exists");
        	}
        	
        	m_ini.load(new File(config_file_name));
        	
        	/* pasado a Server
        	// abro conexion a la BD
        	Class.forName("com.mysql.jdbc.Driver");
        	m_conn = DriverManager.getConnection("jdbc:mysql://" + m_ini.get("DB", "host") + "/" + m_ini.get("DB", "database"), 
        			m_ini.get("DB", "user"), m_ini.get("DB", "password"));
        	*/
        	//logger.debug("m_conn: " + m_conn);
        	
        	super.start();

        } 
        catch (Exception ex) {
        	logger.debug("Exception: " + ex.getMessage());
        	ex.printStackTrace();
        }
        
        //logger.debug("fin start");
    }
}
