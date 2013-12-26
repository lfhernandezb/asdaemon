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
import java.util.Properties;
import java.util.Queue;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

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
		m_selector = SelectorProvider.provider().openSelector();
		
		m_readBuffer = ByteBuffer.allocate(8192);
		this.m_readBuffer.clear();
		
		this.m_readBuffer.rewind();

		
		m_key = null;
		m_str_input = "";
		m_html_input = "";
		m_usuario = null;
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
		JSONObject jo_data, jo_data_o, jo_output;
		String str_tipo, str_resultado, str_descripcion; //, pais, region, ciudad, comuna; //, str_output;
		long token;
		ArrayList<Comuna> listaComuna;
		ArrayList<AbstractMap.SimpleEntry<String, String>> listParameters;
		
		str_tipo = "";
		//str_output = "";
		token = 0;
		
		str_resultado = "0";
		str_descripcion = "Exito";
		
		jo_data_o = new JSONObject();
		
		jo_output = new JSONObject();
		
        try {
        	listParameters = new ArrayList<AbstractMap.SimpleEntry<String, String>>();
        	
        	str_tipo = m_jo.get("tipo").toString();
        	
        	jo_output.put("tipo", str_tipo);
        	
        	jo_data = m_jo.getJSONObject("data");
        	
        	// todos los requests traen token
        	
        	token = jo_data.getLong("token");
        	
        	jo_output.put("token", token);
        	
        	logger.debug("Tipo Req: " + str_tipo);
        	
        	if (str_tipo.equals("MSG_REGISTRO")) {
        		
        		m_usuario = new Usuario();
        		
        		try {
					String alias = jo_data.get("alias").toString();
					logger.debug("alias: " + alias);
					
					m_usuario.set_alias(alias);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo alias no presente" + ": " + e.getMessage());
				}
        		
        		try {
					String contrasena = jo_data.get("contrasena").toString();
					logger.debug("contrasena: " + contrasena);
					
					m_usuario.set_contrasena(contrasena);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo contrasena no presente" + ": " + e.getMessage());
				}

        		try {
					String nombre = jo_data.get("nombre").toString();
					logger.debug("nombre: " + nombre);
					
					m_usuario.set_nombre(nombre);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo nombre no presente" + ": " + e.getMessage());
				}

        		try {
					String apellido_paterno = jo_data.get("apellido_paterno").toString();
					logger.debug("apellido_paterno: " + apellido_paterno);
					
					m_usuario.set_apellido_paterno(apellido_paterno);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo apellido_paterno no presente" + ": " + e.getMessage());
				}

        		try {
					String apellido_materno = jo_data.get("apellido_materno").toString();
					logger.debug("apellido_materno: " + apellido_materno);
					
					m_usuario.set_apellido_materno(apellido_materno);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo apellido_materno no presente" + ": " + e.getMessage());
				}
				
        		try {
					String movil = jo_data.get("movil").toString();
					logger.debug("movil: " + movil);
					
					m_usuario.set_movil(movil);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo movil no presente" + ": " + e.getMessage());
				}
				/*
        		try {
					String rut = jo_data.get("rut").toString();
					logger.debug("rut: " + rut);
					
					u.set_rut(rut);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo rut no presente" + ": " + e.getMessage());
				}
				*/
        		try {
					String email = jo_data.get("email").toString();
					logger.debug("email: " + email);
					
					m_usuario.set_correo(email);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo email no presente" + ": " + e.getMessage());
				}

        		try {
					String email_opcional = jo_data.get("email_opcional").toString();
					logger.debug("email_opcional: " + email_opcional);
					
					m_usuario.set_correo_opcional(email_opcional);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.debug("Atributo email_opcional no presente" + ": " + e.getMessage());
				}

				try {
					String fecha_nacimiento = jo_data.get("fecha_nacimiento").toString();
					logger.debug("fecha_nacimiento: " + fecha_nacimiento);
					
					m_usuario.set_fecha_nacimiento(fecha_nacimiento);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo fecha_nacimiento no presente. " .concat(e.getMessage()));
				}

				try {
					Boolean b = false;
					String hombre = jo_data.get("hombre").toString();
					logger.debug("hombre: " + hombre);
					
					if (hombre.equals("true")) {
						b = true;
					}
					
					m_usuario.set_hombre(b);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo hombre no presente. " .concat(e.getMessage()));
				}

				try {
					Boolean b = false;
					String contactar_contactos_de_contactos = jo_data.get("contactar_contactos_de_contactos").toString();
					logger.debug("contactar_contactos_de_contactos: " + contactar_contactos_de_contactos);
					
					if (contactar_contactos_de_contactos.equals("true")) {
						b = true;
					}
					
					m_usuario.set_contactar_contactos_de_contactos(b);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo contactar_contactos_de_contactos no presente. " .concat(e.getMessage()));
				}

				try {
					Boolean b = false;
					String contactar_desconocidos = jo_data.get("contactar_desconocidos").toString();
					logger.debug("contactar_desconocidos: " + contactar_desconocidos);
					
					if (contactar_desconocidos.equals("true")) {
						b = true;
					}
					
					m_usuario.set_contactar_desconocidos(b);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo contactar_desconocidos no presente. " .concat(e.getMessage()));
				}

				try {
					Boolean b = false;
					String publicar_informacion = jo_data.get("publicar_informacion").toString();
					logger.debug("publicar_informacion: " + publicar_informacion);
					
					if (publicar_informacion.equals("true")) {
						b = true;
					}
					
					m_usuario.set_publicar_informacion(b);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo publicar_informacion no presente. " .concat(e.getMessage()));
				}
				/*
				try {
					String direccion = jo_data.get("direccion").toString();
					logger.debug("direccion: " + direccion);
					
					u.set_direccion(direccion);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo direccion no presente" + ": " + e.getMessage());
				}
				*/
				/*
        		try {
					String foto = jo_data.get("foto").toString();
					logger.debug("foto: " + foto);
					
					u.set_foto(foto);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo foto no presente" + ": " + e.getMessage());
				}
				*/
        		try {
					String antecedentes_emergencia = jo_data.get("antecedentes_emergencia").toString();
					logger.debug("antecedentes_emergencia: " + antecedentes_emergencia);
					
					m_usuario.set_antecedentes_emergencia(antecedentes_emergencia);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo antecedentes_emergencia no presente" + ": " + e.getMessage());
				}
				/*
				try {
					comuna = jo_data.get("comuna").toString();
					logger.debug("comuna: " + comuna);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo comuna no presente" + ": " + e.getMessage());
				}

				try {
					ciudad = jo_data.get("ciudad").toString();
					logger.debug("ciudad: " + ciudad);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo ciudad no presente" + ": " + e.getMessage());
				}

        		try {
        			region = jo_data.get("region").toString();
					logger.debug("region: " + region);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo region no presente" + ": " + e.getMessage());
				}

        		try {
					pais = jo_data.get("pais").toString();
					logger.debug("pais: " + pais);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo pais no presente" + ": " + e.getMessage());
				}
				*/
				/*
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
				*/
				// ya existe usuario con el alias especificado?
				if (Usuario.getByAlias(m_conn, m_usuario.get_alias()) != null) {
					throw new Exception("Ya existe usuario con el alias '" + m_usuario.get_alias() + "'");
				}
				
				// ya existe usuario con el email especificado?
				if (Usuario.getByEmail(m_conn, m_usuario.get_correo()) != null) {
					throw new Exception("Ya existe usuario con el email '" + m_usuario.get_correo() + "'");
				}

				// ya existe usuario con el movil especificado?
				if (Usuario.getByMovil(m_conn, m_usuario.get_movil()) != null) {
					throw new Exception("Ya existe usuario con el movil '" + m_usuario.get_movil() + "'");
				}
				
				// creo la clave para validacion
				m_usuario.set_clave_validacion(Util.generateRandomWord(6));
				// envio correo a usuario
				
				Util.sendMail(m_usuario.get_correo(), "Clave de Verificaci&oacute;n", "Su clave de verificaci&oacute;n es: " + m_usuario.get_clave_validacion());
								
			    // se guarda con valido = false, el default para la columna
			    m_usuario.insert(m_conn);
				/*
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
				*/
							
				//m_usuario = u;
        	}
        	else if (str_tipo.equals("MSG_ACTUALIZA_CONTACTOS")) {
        		Integer id_usuario;
        		
        		try {
        			id_usuario = Integer.decode(jo_data.get("id_usuario").toString());
					logger.debug("id_usuario: " + id_usuario);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo id_usuario no presente" + ": " + e.getMessage());
				}
        		
        		
        		try {
					
        			m_usuario = Usuario.getById(m_conn, id_usuario.toString());
					logger.debug("Usuario.getById ok");
					if (m_usuario == null) {
						throw new Exception("No existe el usuario con id " + id_usuario.toString());
					}

        		} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Excepcion del tipo " + e.getClass() + " Info: " + e.getMessage());
				}

				try {
					JSONArray ja = jo_data.getJSONArray("contactos");
					
					// borro los contactos actuales
					Contacto.delete(m_conn, m_usuario.get_id());
					
					for (int i = 0; i < ja.length(); i++) {
						String relacion = ja.getJSONObject(i).getString("relacion");
						String correo = ja.getJSONObject(i).getString("correo");
						String accion = ja.getJSONObject(i).getString("accion");
						
						Usuario uc = Usuario.getByEmail(m_conn, correo);
						
						Contacto ct = new Contacto();
						
						ct.set_id_usuario(m_usuario.get_id());
						ct.set_relacion(relacion);
						ct.set_correo(correo);
						
						if (uc != null) {
							
							ct.set_id_usuario_contacto(uc.get_id());
						}
						else {
							// si el contacto no esta registrado, no tengo id_usuario... me quedo con correo
						}
						
						ct.insert(m_conn);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new Exception(e.getMessage());
				}
				/*
				str_output =
    				"{" +
    				"\"tipo\": \"MSG_ACTUALIZA_CONTACTOS\"," +
    				"\"token\":\"" + String.valueOf(token) + "\"," + 
    				"\"resultado\":\"0\"," +
    				"\"descripcion\":\"Exito\"," + 
    				"}";
				*/
        	}
        	else if (str_tipo.equals("MSG_POSICION")) {
        		Integer id_usuario;
        		Double latitud, longitud;
        		String fecha;
        		
        		try {
        			id_usuario = Integer.decode(jo_data.get("id_usuario").toString());
					logger.debug("id_usuario: " + id_usuario);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo id_usuario no presente" + ": " + e.getMessage());
				}
        		
        		
        		try {
					
        			m_usuario = Usuario.getById(m_conn, id_usuario.toString());
					logger.debug("Usuario.getById ok");
					if (m_usuario == null) {
						throw new Exception("No existe el usuario con id " + id_usuario.toString());
					}

        		} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Excepcion del tipo " + e.getClass() + " Info: " + e.getMessage());
				}
        		
        		try {
					latitud = Double.valueOf(jo_data.get("latitud").toString());
					logger.debug("latitud: " + latitud);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo latitud no presente" + ": " + e.getMessage());
				}

        		try {
					longitud = Double.valueOf(jo_data.get("longitud").toString());
					logger.debug("longitud: " + longitud);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo longitud no presente" + ": " + e.getMessage());
				}

				// si bien viene la fecha/hora en el requerimiento, la seteamos a la fecha/hora actual
        		try {
					fecha = jo_data.get("fecha").toString();
					logger.debug("fecha: " + fecha);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo fecha no presente" + ": " + e.getMessage());
				}
				
				Usuario.setPosicion(m_conn, m_usuario.get_id(), latitud, longitud, "DATE_FORMAT(now(), '%d-%m-%Y %H:%i:%s')");
				
				logger.debug(UsuarioPosicion.getByIdUsuario(m_conn, m_usuario.get_id()).toString());
				/*
				str_output =
    				"{" +
    				"\"tipo\": \"MSG_POSICION\"," +
    				"\"token\":\"" + String.valueOf(token) + "\"," + 
    				"\"resultado\":\"0\"," +
    				"\"descripcion\":\"Exito\"," +
    				"\"frecuencia\":\"" + m_ini.get("General", "position_frequency") + "\"" +
    				"}";
    			*/
				
				jo_output.put("frecuencia", m_ini.get("General", "position_frequency"));
				
        	}
        	else if (str_tipo.equals("MSG_POLEA_MENSAJE")) {
        		Integer id_usuario;
        		Double latitud, longitud;
        		String fecha, mensaje, encontrado;
        		
        		mensaje = "";
        		encontrado = "";
        		
        		try {
        			id_usuario = Integer.decode(jo_data.get("id_usuario").toString());
					logger.debug("id_usuario: " + id_usuario);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo id_usuario no presente" + ": " + e.getMessage());
				}
        		
        		
        		try {
					
        			m_usuario = Usuario.getById(m_conn, id_usuario.toString());
					logger.debug("Usuario.getById ok");
					if (m_usuario == null) {
						throw new Exception("No existe el usuario con id " + id_usuario.toString());
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
				/*
				str_output =
    				"{" +
    				"\"tipo\": \"MSG_POLEA_MENSAJE\"," +
    				"\"token\":\"" + String.valueOf(token) + "\"," + 
    				"\"resultado\":\"0\"," +
    				"\"descripcion\":\"Exito\"," +
    				"\"encontrado\":\"" + encontrado + "\"," +
    				"\"mensaje\":\"" + StringEscapeUtils.escapeJava(mensaje) + "\"" +
    				"}";
    			*/
				jo_output.put("encontrado", encontrado);
				jo_output.put("mensaje", StringEscapeUtils.escapeJava(mensaje));
				
        	}
        	else if (str_tipo.equals("MSG_PANICO_ALARMA")) {
        		Integer id_usuario;
        		//String str_output;
        		Double latitud, longitud;
        		Short tipo_incidente, tipo_alarma;
        		Incidente i;
        		Chat ch;
        		ChatUsuario chu;
        		LatLonPoint origen, norte, sur, este, oeste;
        		ArrayList<Usuario> lu;
        		//String str_guardianes, str_mensaje_a_encolar;
        		String str_tipo_amigo;
        		MensajeUsuario mu;
        		JSONArray ja_guardianes;
        		JSONObject jo_l_data = new JSONObject();
        		
            	//str_output = "";
        		
        		try {
        			id_usuario = Integer.decode(jo_data.get("id_usuario").toString());
					logger.debug("id_usuario: " + id_usuario);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo id_usuario no presente" + ": " + e.getMessage());
				}
        		
        		
        		try {
					
        			m_usuario = Usuario.getById(m_conn, id_usuario.toString());
					logger.debug("Usuario.getById ok");
					if (m_usuario == null) {
						throw new Exception("No existe el usuario con id " + id_usuario.toString());
					}

        		} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Excepcion del tipo " + e.getClass() + " Info: " + e.getMessage());
				}
				
        		try {
					latitud = Double.valueOf(jo_data.get("latitud").toString());
					logger.debug("latitud: " + latitud);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo latitud no presente" + ": " + e.getMessage());
				}

        		try {
					longitud = Double.valueOf(jo_data.get("longitud").toString());
					logger.debug("longitud: " + longitud);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo longitud no presente" + ": " + e.getMessage());
				}

        		try {
					tipo_incidente = Short.decode(jo_data.get("tipo_incidente").toString());
					logger.debug("tipo_incidente: " + tipo_incidente);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo tipo_incidente no presente" + ": " + e.getMessage());
				}

        		try {
					tipo_alarma = Short.decode(jo_data.get("tipo_alarma").toString());
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
				/*
				str_output =
    				"{" +
    				"\"tipo\": \"MSG_PANICO_ALARMA\"," +
    				"\"token\":\"" + String.valueOf(token) + "\"," + 
    				"\"resultado\":\"0\"," +
    				"\"descripcion\":\"Exito\"," + 
    				"\"data\":{" +
    				"    \"id_incidente\":\"" + i.get_id().toString() + "\"," +
    				"    \"listado_guardianes\": [";
				*/
				//str_guardianes = "";
				
				jo_l_data.put("id_incidente", i.get_id().toString());
				
				ja_guardianes = new JSONArray();
				
				for (Usuario us : lu) {
					
					JSONObject jo_guardian = new JSONObject();
					/*
					if (!str_guardianes.equals("")) {
						str_guardianes += ",";
					}
					*/
					//logger.debug("Usuario en cercania incidente: " + us);
					
					// es contacto? pertenece a la comunidad del usuario originador?
					listParameters.clear();
					
					listParameters.add(new AbstractMap.SimpleEntry<String, String>("id_usuario", id_usuario.toString()));
					listParameters.add(new AbstractMap.SimpleEntry<String, String>("id_usuario_contacto", us.get_id().toString()));
					/*
					str_guardianes +=
						"{ \"id_usuario\":\"" + us.get_id().toString() + "\" , \"alias\":\"" + us.get_alias() + "\", \"latitud\":\"" + us.get_latitud().toString() + "\", \"longitud\":\"" + us.get_longitud().toString() + "\", \"tipo_amigo\":\"";
					*/
					jo_guardian.put("id_usuario", us.get_id().toString());
					jo_guardian.put("alias", us.get_alias());
					jo_guardian.put("latitud", us.get_latitud().toString());
					jo_guardian.put("longitud", us.get_longitud().toString());
					
					str_tipo_amigo = "";
					
					if (Contacto.seek(m_conn, listParameters, null, null, 0, 10000).size() > 0) {
						// es contacto
						logger.debug("Contacto");
						
						//str_guardianes += "CONTACTO_DIRECTO";
						str_tipo_amigo = "CONTACTO_DIRECTO";
					}
					else {
						listParameters.clear();
						
						listParameters.add(new AbstractMap.SimpleEntry<String, String>("id_usuario", id_usuario.toString()));
						listParameters.add(new AbstractMap.SimpleEntry<String, String>("en_comunidad_con", us.get_id().toString()));

						if (ComunidadUsuario.seek(m_conn, listParameters, null, null, 0, 10000).size() > 0) {
							// es de la comunidad del usuario que genera alarma
							logger.debug("Comunidad");
							
							//str_guardianes += "CONTACTO_COMUNIDAD";
							str_tipo_amigo = "CONTACTO_COMUNIDAD";
						}
						else {
							// es de la comunidad temporal
							logger.debug("Temporal");
							
							//str_guardianes += "CONTACTO_TEMPORAL";
							str_tipo_amigo = "CONTACTO_TEMPORAL";
						}
					}
					
					jo_guardian.put("tipo_amigo", str_tipo_amigo);
					
					// inserto en chat_usuario
					chu = new ChatUsuario();
					
					chu.set_id_chat(ch.get_id());
					chu.set_id_usuario(us.get_id());
					chu.set_fecha_ingreso("DATE_FORMAT(now(), '%d-%m-%Y %H:%i:%s')");
					chu.set_latitud_inicial(us.get_latitud());
					chu.set_longitud_inicial(us.get_longitud());
					/*	
					str_guardianes +=
						"\" }";
					*/
					
					ja_guardianes.put(jo_guardian);
				}
				
				jo_l_data.put("listado_guardianes", ja_guardianes);
				
				for (Usuario us : lu) {
					// debo notificar al usuario del evento; escribo registro en tabla mensaje_usuario
					JSONObject jo_mensaje_a_encolar = new JSONObject();
					JSONObject jo_ll_data = new JSONObject();
					/*
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
					*/
					
					jo_mensaje_a_encolar.put("tipo", "NOT_ALARMA_GUARDIAN");
					
					jo_ll_data.put("id_incidente", i.get_id().toString());
					jo_ll_data.put("tipo_incidente", i.get_tipo().toString());
					jo_ll_data.put("id_usuario_incidente", m_usuario.get_id().toString());
					jo_ll_data.put("fecha_incidente", i.get_fecha());
					jo_ll_data.put("latitud_incidente", i.get_latitud().toString());
					jo_ll_data.put("longitud_incidente", i.get_longitud().toString());
					jo_ll_data.put("alias", m_usuario.get_alias());
					jo_ll_data.put("listado_guardianes", ja_guardianes);
					
					jo_mensaje_a_encolar.put("data", jo_ll_data);
					
					mu = new MensajeUsuario();
					
					mu.set_id_usuario(us.get_id());
					mu.set_mensaje(jo_mensaje_a_encolar.toString());
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
				
				/*
  				str_output += 
  					str_guardianes +
    				"    ]" +
    				"    }" +
    				"}";
  				*/
				
				jo_output.put("data", jo_l_data);
				
  				//m_fifo_output.add(new String(str_output));
				
        	}
        	else if (str_tipo.equals("MSG_CREA_COMUNIDAD")) {
        		
        		ComunidadUsuario cu;
        		Integer id_usuario;
        		Comunidad cm;
        		String nombre, descripcion;
        		Double latitud, longitud;
        		Long cobertura;
        		JSONObject jo_l_data = new JSONObject();
        		
        		try {
        			id_usuario = Integer.decode(jo_data.get("id_usuario").toString());
					logger.debug("id_usuario: " + id_usuario);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo id_usuario no presente" + ": " + e.getMessage());
				}
        		
        		
        		try {
					
        			m_usuario = Usuario.getById(m_conn, id_usuario.toString());
					logger.debug("Usuario.getById ok");
					if (m_usuario == null) {
						throw new Exception("No existe el usuario con id " + id_usuario.toString());
					}

        		} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Excepcion del tipo " + e.getClass() + " Info: " + e.getMessage());
				}

				try {
					nombre = jo_data.get("nombre").toString();
					logger.debug("nombre: " + nombre);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo nombre no presente" + ": " + e.getMessage());
				}
        		
        		try {
					descripcion = jo_data.get("descripcion").toString();
					logger.debug("descripcion: " + descripcion);
										
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo descripcion no presente" + ": " + e.getMessage());
				}

        		try {
					latitud = Double.valueOf(jo_data.get("latitud").toString());
					logger.debug("latitud: " + latitud);
										
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo latitud no presente" + ": " + e.getMessage());
				}

        		try {
        			longitud = Double.valueOf(jo_data.get("longitud").toString());
					logger.debug("longitud: " + longitud);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo longitud no presente" + ": " + e.getMessage());
				}

        		try {
					cobertura = Long.decode(jo_data.get("cobertura").toString());
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
				/*
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
        		*/
				jo_l_data.put("id_comunidad", cm.get_id().toString());
				
				jo_output.put("data", jo_l_data);
        	}
        	else if (str_tipo.equals("MSG_AGREGA_A_COMUNIDAD")) {
        		
        		Integer id_usuario, id_usuario_a_agregar, id_comunidad;
        		Comunidad cm;
        		ComunidadUsuario cu;
        		ArrayList<ComunidadUsuario> lcu;
        		
        		try {
        			id_usuario = Integer.decode(jo_data.get("id_usuario").toString());
					logger.debug("id_usuario: " + id_usuario);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo id_usuario no presente" + ": " + e.getMessage());
				}
        		
        		
        		try {
					
        			m_usuario = Usuario.getById(m_conn, id_usuario.toString());
					logger.debug("Usuario.getById ok");
					if (m_usuario == null) {
						throw new Exception("No existe el usuario con id " + id_usuario.toString());
					}

        		} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Excepcion del tipo " + e.getClass() + " Info: " + e.getMessage());
				}
        		
        		try {
        			id_usuario_a_agregar = Integer.decode(jo_data.get("id_usuario_a_agregar").toString());
					logger.debug("id_usuario_a_agregar: " + id_usuario_a_agregar);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo id_usuario no presente" + ": " + e.getMessage());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception(e.getMessage());
				}

				try {
        			id_comunidad = Integer.decode(jo_data.get("id_comunidad").toString());
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
        		/*
				str_output =
    				"{" +
    				"\"tipo\": \"MSG_AGREGA_A_COMUNIDAD\"," +
    				"\"token\":\"" + String.valueOf(token) + "\"," + 
    				"\"resultado\":\"0\"," +
    				"\"descripcion\":\"Exito\"," + 
    				"}";
    		    */
        	}
        	else if (str_tipo.equals("MSG_VALIDACION")) {
        		/*
        		 * el usuario ingresa en la aplicacion movil la clave de validacion enviada a su correo, la que quedo en 
        		 * la columna clave_validacion de la tabla 'usuario'
        		 */
        		Integer id_usuario;
        		String str_numero_validacion;
        		
        		try {
        			id_usuario = Integer.decode(jo_data.get("id_usuario").toString());
					logger.debug("id_usuario: " + id_usuario);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo id_usuario no presente" + ": " + e.getMessage());
				}
        		
        		
        		try {
					
        			m_usuario = Usuario.getById(m_conn, id_usuario.toString());
					logger.debug("Usuario.getById ok");
					if (m_usuario == null) {
						throw new Exception("No existe el usuario con id " + id_usuario.toString());
					}

        		} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Excepcion del tipo " + e.getClass() + " Info: " + e.getMessage());
				}
        		
				try {
					str_numero_validacion = jo_data.get("numero_validacion").toString();
					logger.debug("numero_validacion: " + str_numero_validacion);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo id_comunidad no presente" + ": " + e.getMessage());
				}
				
				if (!str_numero_validacion.equals(m_usuario.get_clave_validacion())) {
					throw new Exception("La clave de validacion no es correcta");
				}
				
				// todo ok
				// marco al usuario como vaído
				
				m_usuario.set_validado(true);
				
				m_usuario.update(m_conn);
        		/*
				str_output =
    				"{" +
    				"\"tipo\": \"MSG_AGREGA_A_COMUNIDAD\"," +
    				"\"token\":\"" + String.valueOf(token) + "\"," + 
    				"\"resultado\":\"0\"," +
    				"\"descripcion\":\"Exito\"," + 
    				"}";
    		    */
        	}
        	else if (str_tipo.equals("MSG_INVITAR")) {
        		/*
        		 * el usuario de la aplicación movil invita a uno o mas usuarios a unirse a su red; si pertenecen a la comunidad
        		 * les genera mensaje a ser poleado
        		 */
        		Integer id_usuario;
        		JSONArray ja_invitados, ja_invitados_resp;
        		Contacto con;
        		
        		ja_invitados_resp = new JSONArray();
        		
        		try {
        			id_usuario = Integer.decode(jo_data.get("id_usuario").toString());
					logger.debug("id_usuario: " + id_usuario);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo id_usuario no presente" + ": " + e.getMessage());
				}
        		
        		
        		try {
					
        			m_usuario = Usuario.getById(m_conn, id_usuario.toString());
					logger.debug("Usuario.getById ok");
					if (m_usuario == null) {
						throw new Exception("No existe el usuario con id " + id_usuario.toString());
					}

        		} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Excepcion del tipo " + e.getClass() + " Info: " + e.getMessage());
				}

        		try {
					
        			ja_invitados = (JSONArray) jo_data.get("invitados");
					

        		} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Excepcion del tipo " + e.getClass() + " Info: " + e.getMessage());
				}
        		
        		for (int i = 0, size = ja_invitados.length(); i < size; i++) {
        			
        			ArrayList<Contacto> listaContactos;
        			JSONObject jo_invitado_resp;
        			String str_ya_inscrito = "false";
        			
        			JSONObject jo_invitado = ja_invitados.getJSONObject(i);
        	      
        			String i_correo = jo_invitado.getString("correo");
        			String i_relacion = jo_invitado.getString("relacion");
        	      
        			Usuario i_usuario = Usuario.getByEmail(m_conn, i_correo);
        			
        			if (i_usuario != null) {
        				// ya inscrito
        				str_ya_inscrito = "true";
 
            			// existe ya el contacto?
            			
        				listParameters.clear();
        				
        				listParameters.add(new AbstractMap.SimpleEntry<String, String>("id_usuario", m_usuario.get_id().toString()));
        				listParameters.add(new AbstractMap.SimpleEntry<String, String>("id_usuario_contacto", i_usuario.get_id().toString()));
        				
        				listaContactos = Contacto.seek(m_conn, listParameters, null, null, 0, 10);
        				
        				if (listaContactos.size() > 0) {
        					// ok, existe, actualizo relacion
        					con = listaContactos.get(0);
        					
        					con.set_relacion(i_relacion);
        					
        					con.update(m_conn);
        					
        				}
        				else {
        					// no existe contacto
        					// genero un mensaje MSG_INVITACION para el invitado, el que se ira a la aplicacion movil via poleo
        					JSONObject jo_mensaje_a_encolar;
        					JSONObject jo_l_data;
        					MensajeUsuario mu;

        					jo_mensaje_a_encolar = new JSONObject();
        					jo_l_data = new JSONObject();
        					
        					jo_mensaje_a_encolar.put("tipo", "MSG_INVITACION");
        					
        					jo_l_data.put("id_usuario", i_usuario.get_id().toString());
        					jo_l_data.put("tipo_evento", "SOLICITA_AMISTAD");
        					jo_l_data.put("id_usuario_solicitante", m_usuario.get_id().toString());
        					jo_l_data.put("nombre", m_usuario.get_nombre());
        					jo_l_data.put("apellido_paterno", m_usuario.get_apellido_paterno());
        					jo_l_data.put("apellido_materno", m_usuario.get_apellido_materno());
        					jo_l_data.put("relacion", i_relacion);
        					
        					jo_mensaje_a_encolar.put("data", jo_l_data);
        					
        					mu = new MensajeUsuario();
        					
        					mu.set_id_usuario(i_usuario.get_id());
        					mu.set_mensaje(jo_mensaje_a_encolar.toString());
        					mu.set_fecha("DATE_FORMAT(now(), '%d-%m-%Y %H:%i:%s')");
        					mu.set_leido(false);
        					
        					mu.insert(m_conn);
        				}
        			}
        			else {
        				// no inscrito
        				
        				// existe ya el contacto?
        				
    					// busco por id_usuario, correo
	    				listParameters.clear();
	    				
	    				listParameters.add(new AbstractMap.SimpleEntry<String, String>("id_usuario", m_usuario.get_id().toString()));
	    				listParameters.add(new AbstractMap.SimpleEntry<String, String>("correo", i_correo));
	    				
	    				listaContactos = Contacto.seek(m_conn, listParameters, null, null, 0, 10);
	    				
	    				if (listaContactos.size() > 0) {
	    					// ok, existe, actualizo relacion
	    					con = listaContactos.get(0);
	    					
	    					con.set_relacion(i_relacion);
	    					
	    					con.update(m_conn);
	    					
	    				}
	    				else {
	    					// dejo una solicitud pendiente
	    				}
        			}
        			        			
        			jo_invitado_resp = new JSONObject();
        			
        			jo_invitado_resp.put("correo", i_correo);
        			
    				jo_invitado_resp.put("ya_inscrito", str_ya_inscrito);
    				
    				ja_invitados_resp.put(jo_invitado_resp);
   				}
        			
        		jo_output.put("invitados", ja_invitados_resp);
        	}
        	else if (str_tipo.equals("MSG_RESP_INVITACION")) {
        		/*
        		 * el invitado notifica al servidor de su deseo de aceptar o rechazar la invitacion
        		 */
        		
        		Integer id_usuario, id_usuario_solicitante;
        		String str_respuesta, str_relacion;
        		Usuario u_solicitante;
				JSONObject jo_mensaje_a_encolar;
				JSONObject jo_l_data;
				MensajeUsuario mu;
        		
        		try {
        			id_usuario = Integer.decode(jo_data.get("id_usuario").toString());
					logger.debug("id_usuario: " + id_usuario);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo id_usuario no presente" + ": " + e.getMessage());
				}
        		
        		try {
        			id_usuario_solicitante = Integer.decode(jo_data.get("id_usuario_solicitante").toString());
					logger.debug("id_usuario_solicitante: " + id_usuario_solicitante);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo id_usuario_solicitante no presente" + ": " + e.getMessage());
				}
        		
        		try {
        			str_respuesta = jo_data.get("respuesta").toString();
					logger.debug("respuesta: " + str_respuesta);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo respuesta no presente" + ": " + e.getMessage());
				}

        		try {
        			str_relacion = jo_data.get("relacion").toString();
					logger.debug("relacion: " + str_relacion);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo relacion no presente" + ": " + e.getMessage());
				}

				try {
					
        			m_usuario = Usuario.getById(m_conn, id_usuario.toString());
					logger.debug("Usuario.getById ok");
					if (m_usuario == null) {
						throw new Exception("No existe el usuario con id " + id_usuario.toString());
					}

        		} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Excepcion del tipo " + e.getClass() + " Info: " + e.getMessage());
				}

				try {
					
					u_solicitante = Usuario.getById(m_conn, id_usuario_solicitante.toString());
					logger.debug("UsuarioSolicitante.getById ok");
					if (u_solicitante == null) {
						throw new Exception("No existe el usuario con id " + id_usuario_solicitante.toString());
					}

        		} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Excepcion del tipo " + e.getClass() + " Info: " + e.getMessage());
				}
        		
        		// genero mensaje de notificacion a usuario solicitante, que debera ser leido via poleo
				jo_mensaje_a_encolar = new JSONObject();
				jo_l_data = new JSONObject();
				
				jo_mensaje_a_encolar.put("tipo", "MSG_RESP_INVITAR");
				
				jo_l_data.put("id_usuario", m_usuario.get_id().toString());
				jo_l_data.put("id_usuario_solicitante", u_solicitante.get_id().toString());
				jo_l_data.put("respuesta", str_respuesta);
				
				jo_mensaje_a_encolar.put("data", jo_l_data);
				
				mu = new MensajeUsuario();
				
				mu.set_id_usuario(u_solicitante.get_id());
				mu.set_mensaje(jo_mensaje_a_encolar.toString());
				mu.set_fecha("DATE_FORMAT(now(), '%d-%m-%Y %H:%i:%s')");
				mu.set_leido(false);
				
				mu.insert(m_conn);
        		
        		if (str_respuesta.equals("si")) {
        			// genero registro en tabla 'contacto'
        			Contacto con = new Contacto();
        			
        			con.set_id_usuario(u_solicitante.get_id());
        			con.set_id_usuario_contacto(m_usuario.get_id());
        			con.set_correo(m_usuario.get_correo());
        			con.set_relacion(str_relacion);
        			
        			con.insert(m_conn);
        		}
        		else {
        			
        		}
        	}
        	else if (str_tipo.equals("MSG_OBTIENE_DATOS_USUARIO")) {
        		
        		Integer id_usuario;
        		JSONObject jo_l_data = new JSONObject();
        		JSONArray ja_contactos = new JSONArray();
        		
        		try {
        			id_usuario = Integer.decode(jo_data.get("id_usuario").toString());
					logger.debug("id_usuario: " + id_usuario);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Atributo id_usuario no presente" + ": " + e.getMessage());
				}
        		
        		try {
					
        			m_usuario = Usuario.getById(m_conn, id_usuario.toString());
					logger.debug("Usuario.getById ok");
					if (m_usuario == null) {
						throw new Exception("No existe el usuario con id " + id_usuario.toString());
					}

        		} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception("Excepcion del tipo " + e.getClass() + " Info: " + e.getMessage());
				}
        		
        		jo_l_data.put("id_usuario", m_usuario.get_id().toString());
        		jo_l_data.put("nombre", m_usuario.get_nombre());
        		jo_l_data.put("apellido_paterno", m_usuario.get_apellido_paterno());
        		jo_l_data.put("apellido_materno", m_usuario.get_apellido_materno());
        		jo_l_data.put("movil", m_usuario.get_movil());
        		jo_l_data.put("email", m_usuario.get_correo());
        		jo_l_data.put("email_opcional", m_usuario.get_correo_opcional());
        		jo_l_data.put("fecha_nacimiento", m_usuario.get_fecha_nacimiento());
        		jo_l_data.put("hombre", m_usuario.get_hombre());
        		jo_l_data.put("contactar_contactos_de_contacto", m_usuario.get_contactar_contactos_de_contactos());
        		jo_l_data.put("publicar_informacion", m_usuario.get_publicar_informacion());
        		jo_l_data.put("antecedentes_emergencia", m_usuario.get_antecedentes_emergencia());
        		
        		jo_l_data.put("contactos", ja_contactos);
        		
        		jo_output.put("data", jo_l_data);
        	}
        	else {
        		logger.debug("Tipo de mensaje desconocido: " + str_tipo);
        		
        		throw new Exception("Tipo de mensaje desconocido: " + str_tipo);
        	}
        	
        }
        catch (JSONException ex) {
            logger.debug("JSONException: " + ex.toString());
			ex.printStackTrace();
			/*
			str_output =
				"{" +
				"\"tipo\": \"" + tipo + "\"," +
				"\"token\":\"" + String.valueOf(token) + "\"," + 
				"\"resultado\":\"1\"," +
				"\"descripcion\":\"Fallo tipo JSONException, " + ex.getMessage() + "\"," + 
				"}";
			*/
			str_resultado = "1"; 
			
			str_descripcion = "Fallo tipo JSONException, " + ex.getMessage();
            
        }
        catch (SQLException ex) {
			logger.debug("SQLException: " + ex.getMessage());
			logger.debug("SQLState: " + ex.getSQLState());
			logger.debug("VendorError: " + ex.getErrorCode());
			ex.printStackTrace();
			/*
			str_output =
				"{" +
				"\"tipo\": \"" + tipo + "\"," +
				"\"token\":\"" + String.valueOf(token) + "\"," + 
				"\"resultado\":\"1\"," +
				"\"descripcion\":\"Fallo tipo SQLException, " + ex.getMessage() + "\"," + 
				"}";
			*/        		
			str_resultado = "1"; 
			
			str_descripcion = "Fallo tipo SQLException, " + ex.getMessage();
        }
        catch (Exception ex) {
			logger.debug("Exception: " + ex.getMessage());
			ex.printStackTrace();
			/*
			str_output =
				"{" +
				"\"tipo\": \"" + tipo + "\"," +
				"\"token\":\"" + String.valueOf(token) + "\"," + 
				"\"resultado\":\"1\"," +
				"\"descripcion\":\"Fallo tipo Exception, " + ex.getMessage() + "\"," + 
				"}";
			*/       		
			str_resultado = "1"; 
			
			str_descripcion = "Fallo tipo Exception, " + ex.getMessage();
        }
        
        jo_output.put("resultado", str_resultado);
        
        jo_output.put("descripcion", str_descripcion);
        
   		m_fifo_output.add(new String(jo_output.toString()));  		
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
