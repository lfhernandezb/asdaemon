/**
 * 
 */
package bd;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractMap;
import java.util.ArrayList;

/**
 * @author petete-ntbk
 *
 */
public class Chat {
	private Integer _id;
	private String _nombre;
	private String _fecha_creacion;
	private Integer _id_usuario_creador;
	private Integer _id_servidor_chat;
	private Long _id_chat_servidor;
	
	private final static String _str_sql = 
			"  SELECT ch.id_chat AS id, ch.nombre, DATE_FORMAT(ch.fecha_creacion, '%d-%m-%Y %H:%i:%s') AS fecha_creacion, ch.id_usuario_creador_FK AS id_usuario_creador, ch.id_servidor_chat_FK AS id_servidor_chat, ch.id_chat_servidor" +
		 	"  FROM chat ch";	 	

	public Chat() {
		_id = null;
		_nombre = null;
		_fecha_creacion = null;
		_id_usuario_creador = null;
		_id_servidor_chat = null;
		_id_chat_servidor = null;
	}

	/**
	 * @return the _id
	 */
	public Integer get_id() {
		return _id;
	}

	/**
	 * @return the _nombre
	 */
	public String get_nombre() {
		return _nombre;
	}

	/**
	 * @return the _fecha_creacion
	 */
	public String get_fecha_creacion() {
		return _fecha_creacion;
	}

	/**
	 * @return the _id_usuario_creador
	 */
	public Integer get_id_usuario_creador() {
		return _id_usuario_creador;
	}

	/**
	 * @return the _id_servidor_chat
	 */
	public Integer get_id_servidor_chat() {
		return _id_servidor_chat;
	}

	/**
	 * @return the _id_chat_servidor
	 */
	public Long get_id_chat_servidor() {
		return _id_chat_servidor;
	}

	/**
	 * @param _id the _id to set
	 */
	public void set_id(Integer _id) {
		this._id = _id;
	}
	
	/**
	 * @param _nombre the _nombre to set
	 */
	public void set_nombre(String _nombre) {
		this._nombre = _nombre;
	}

	/**
	 * @param _fecha_creacion the _fecha_creacion to set
	 */
	public void set_fecha_creacion(String _fecha_creacion) {
		this._fecha_creacion = _fecha_creacion;
	}

	/**
	 * @param _id_usuario_creador the _id_usuario_creador to set
	 */
	public void set_id_usuario_creador(Integer _id_usuario_creador) {
		this._id_usuario_creador = _id_usuario_creador;
	}

	/**
	 * @param _id_servidor_chat the _id_servidor_chat to set
	 */
	public void set_id_servidor_chat(Integer _id_servidor_chat) {
		this._id_servidor_chat = _id_servidor_chat;
	}

	/**
	 * @param _id_chat_servidor the _id_chat_servidor to set
	 */
	public void set_id_chat_servidor(Long _id_chat_servidor) {
		this._id_chat_servidor = _id_chat_servidor;
	}

	public static Chat fromRS(ResultSet p_rs) throws SQLException {
		Chat ret = new Chat();
		
		try {
			ret.set_id(p_rs.getInt("id"));
			ret.set_nombre(p_rs.getString("nombre"));
			ret.set_fecha_creacion(p_rs.getString("fecha_creacion"));
			ret.set_id_usuario_creador(p_rs.getInt("id_usuario_creador"));
			ret.set_id_servidor_chat(p_rs.getInt("id_servidor_chat"));
			ret.set_id_chat_servidor(p_rs.getLong("id_chat_servidor"));
		}
		catch (SQLException ex){
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			
			throw ex;
		}
						
		return ret;
	}
    
	
	/*
	public static function seek(p_param) {
		global fc;
		
		str_sql =
			"  SELECT r.id_repuesto as id, p.descripcion as plataforma, f.descripcion as fabricante, m.descripcion as modelo, re.descripcion as radio_estacion, c.descripcion as ciudad, rg.descripcion as region" +
		 	"  FROM repuesto r" +
			"  JOIN plataforma p ON p.id_plataforma = r.id_plataforma_FK" +
			"  JOIN radio_estacion re ON re.id_radio_estacion = r.id_radio_estacion_FK" +
		    "  JOIN ciudad c ON c.id_ciudad = re.id_ciudad_FK" +
		    "  JOIN region rg ON rg.id_region = c.id_region_FK" +
			"  JOIN modelo m ON m.id_modelo = r.id_modelo_FK" +
			"  JOIN fabricante f ON f.id_fabricante = m.id_fabricante_FK" +
			"  WHERE f.descripcion LIKE "%p_param%"" +
			"  OR m.descripcion LIKE "%p_param%"" +
			"  OR r.descripcion LIKE "%p_param%"" +
			"  OR p.descripcion LIKE "%p_param%"" +
			"  AND r.ubicacion IS NULL";
		
		// echo str_sql . "<br>";
		
		return fc.getLink().QueryArray(str_sql, MYSQL_ASSOC);
	}
	*/
	
	public static Chat getByParameter(Connection p_conn, String p_key, String p_value) throws Exception {
		Chat ret = null;
		
		String str_sql = _str_sql +
			"  WHERE ch." + p_key + " = " + p_value +
			"  LIMIT 0, 1";
		
		// assume that conn is an already created JDBC connection (see previous examples)
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = p_conn.createStatement();
			rs = stmt.executeQuery(str_sql);

			// Now do something with the ResultSet ....
			
			if (rs.next()) {
				ret = fromRS(rs);
			}
		}
		catch (SQLException ex){
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage() + " sentencia: " + str_sql);
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			
			throw new Exception("Error al obtener registro");
		}
		finally {
			// it is a good idea to release
			// resources in a finally{} block
			// in reverse-order of their creation
			// if they are no-longer needed
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { 
					
				} // ignore
				rs = null;
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) {
					
				} // ignore
				stmt = null;
			}
		}		
		
		return ret;		
	}
	
	public static Chat getById(Connection p_conn, String p_id) throws Exception {
		return getByParameter(p_conn, "id_chat", p_id);
	}
	
	public static Chat getByIdIncidente(Connection p_conn, String p_id_incidente) throws Exception {
		return getByParameter(p_conn, "id_incidente_FK", p_id_incidente);
	}

	public static ArrayList<Chat> seek(Connection p_conn, ArrayList<AbstractMap.SimpleEntry<String, String>> p_parameters, String p_order, String p_direction, int p_offset, int p_limit) throws SQLException {
    	Statement stmt = null;
    	ResultSet rs = null;
    	String str_sql;
    	ArrayList<Chat> ret;
    	
    	str_sql = "";
    	
		try {
			ArrayList<String> array_clauses = new ArrayList<String>();
			
			ret = new ArrayList<Chat>();
			
			str_sql = _str_sql;
			
			for (AbstractMap.SimpleEntry<String, String> p : p_parameters) {
				if (p.getKey().equals("fecha_creacion")) {
					array_clauses.add("ch.fecha_creacion = " + p.getValue());
				}
				else if (p.getKey().equals("nombre")) {
					array_clauses.add("ch.nombre = " + p.getValue());
				}
			}
								
	        boolean bFirstTime = false;
	        
	        for(String clause : array_clauses) {
	            if (!bFirstTime) {
	                 bFirstTime = true;
	                 str_sql += " WHERE ";
	            }
	            else {
	                 str_sql += " AND ";
	            }
	            str_sql += clause;
	        }
			
	        if (p_order != null && p_direction != null) {
	        	str_sql += " ORDER BY " + p_order + " " + p_direction;
	        }
	        
	        if (p_offset != -1 && p_limit != -1) {
	        	str_sql += "  LIMIT " +  Integer.toString(p_offset) + ", " + Integer.toString(p_limit);
	        }
			
	        //echo "<br>" . str_sql . "<br>";
		
			stmt = p_conn.createStatement();
			
			rs = stmt.executeQuery(str_sql);
			
			while (rs.next()) {
				ret.add(fromRS(rs));
			}
			/*
			if (ret.size() == 0) {
				ret = null;
			}
			*/
		}
		catch (SQLException ex){
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage() + " sentencia: " + str_sql);
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			
			throw ex;
		}
		finally {
			// it is a good idea to release
			// resources in a finally{} block
			// in reverse-order of their creation
			// if they are no-longer needed
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { 
					
				} // ignore
				rs = null;
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) {
					
				} // ignore
				stmt = null;
			}
		}		

		return ret;
	}

    public int update(Connection p_conn) throws Exception {

    	int ret = -1;
    	Statement stmt = null;

    	String str_sql =
			"  UPDATE chat" +
			"  SET " +
			"  nombre = " + (_nombre != null ? "'" + _nombre + "'" : "null") + "," +
			"  fecha_creacion = " + (_fecha_creacion != null ? "STR_TO_DATE(" + _fecha_creacion + ", '%d-%m-%Y %H:%i:%s')" : "null") + "," +
			"  id_usuario_creador_FK = " + (_id_usuario_creador != null ? "'" + _id_usuario_creador + "'" : "null") + "," +
			"  id_servidor_chat_FK = " + (_id_servidor_chat != null ? "'" + _id_servidor_chat + "'" : "null") + "," +
			"  id_chat_servidor = " + (_id_chat_servidor != null ? "'" + _id_chat_servidor + "'" : "null") +
			"  WHERE id_chat = " + Integer.toString(this._id);
		
		try {
			stmt = p_conn.createStatement();
			
			ret = stmt.executeUpdate(str_sql);
			/*
			if (stmt.executeUpdate(str_sql) < 1) {
				throw new Exception("No hubo filas afectadas");
			}
			*/			
		}
		catch (SQLException ex){
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage() + " sentencia: " + str_sql);
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			
			throw new Exception("Error al actualizar registro '" + str_sql + "'");
		}
		finally {
			// it is a good idea to release
			// resources in a finally{} block
			// in reverse-order of their creation
			// if they are no-longer needed
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) {
					
				} // ignore
				stmt = null;
			}
		}
		
		return ret;
	}
	
	public int insert(Connection p_conn) throws Exception {
		
    	int ret = -1;
		Statement stmt = null;
    	ResultSet rs = null;

    	String str_sql =
			"  INSERT INTO chat" +
			"  (" +
			"  nombre," +
			"  fecha_creacion," +
			"  id_usuario_creador_FK," +
			"  id_servidor_chat_FK," +
			"  id_chat_servidor" +
			"  )" +
			"  VALUES" +
			"  (" +
			"  " + (_nombre != null ? "'" + _nombre + "'" : "null") + "," +
			"  " + (_fecha_creacion != null ? "STR_TO_DATE(" + _fecha_creacion + ", '%d-%m-%Y %H:%i:%s')" : "null") + "," +
			"  " + (_id_usuario_creador != null ? "'" + _id_usuario_creador + "'" : "null") + "," +
			"  " + (_id_servidor_chat != null ? "'" + _id_servidor_chat + "'" : "null") + "," +
			"  " + (_id_chat_servidor != null ? "'" + _id_chat_servidor + "'" : "null") +
			"  )";
		
		try {
			stmt = p_conn.createStatement();
			
			ret = stmt.executeUpdate(str_sql, Statement.RETURN_GENERATED_KEYS);
			/*
			if (stmt.executeUpdate(str_sql) < 1) {
				throw new Exception("No hubo filas afectadas");
			}
			*/
			
			rs = stmt.getGeneratedKeys();

			if (rs.next()) {
				_id = rs.getInt(1);
			} else {
				// throw an exception from here
				throw new Exception("Error al obtener id");
			}

			rs.close();
			rs = null;
			System.out.println("Key returned from getGeneratedKeys():" + _id.toString());
			
		}
		catch (SQLException ex){
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage() + " sentencia: " + str_sql);
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			
			throw new Exception("Error al insertar registro '" + str_sql + "'");
		}
		finally {
			// it is a good idea to release
			// resources in a finally{} block
			// in reverse-order of their creation
			// if they are no-longer needed
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { 
					
				} // ignore
				rs = null;
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) {
					
				} // ignore
				stmt = null;
			}
		}
		
		return ret;
	}
	
}