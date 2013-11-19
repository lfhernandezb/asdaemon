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
public class ChatUsuario {
	private Integer _id;
	private Integer _id_chat;
	private Integer _id_usuario;
	private String _fecha_ingreso;
	private Double _latitud_inicial;
	private Double _longitud_inicial;
	
	private final static String _str_sql = 
			"  SELECT chu.id_chat_usuario AS id, chu.id_chat_FK AS id_chat, chu.id_usuario_FK AS id_usuario, DATE_FORMAT(chu.fecha_ingreso, '%d-%m-%Y %H:%i:%s') AS fecha_ingreso, chu.latitud_inicial, chu.longitud_inicial" +
		 	"  FROM chat_usuario chu";	 	

	public ChatUsuario() {
		_id = null;
		_id_chat = null;
		_id_usuario = null;
		_fecha_ingreso = null;
		_latitud_inicial = null;
		_longitud_inicial = null;
	}

	/**
	 * @return the _id
	 */
	public Integer get_id() {
		return _id;
	}

	/**
	 * @return the _id_chat
	 */
	public Integer get_id_chat() {
		return _id_chat;
	}

	/**
	 * @return the _id_usuario
	 */
	public Integer get_id_usuario() {
		return _id_usuario;
	}

	/**
	 * @return the _fecha_ingreso
	 */
	public String get_fecha_ingreso() {
		return _fecha_ingreso;
	}

	/**
	 * @return the _latitud_inicial
	 */
	public Double get_latitud_inicial() {
		return _latitud_inicial;
	}

	/**
	 * @return the _longitud_inicial
	 */
	public Double get_longitud_inicial() {
		return _longitud_inicial;
	}

	/**
	 * @param _id the _id to set
	 */
	public void set_id(Integer _id) {
		this._id = _id;
	}
	
	/**
	 * @param _id_chat the _id_chat to set
	 */
	public void set_id_chat(Integer _id_chat) {
		this._id_chat = _id_chat;
	}

	/**
	 * @param _id_usuario the _id_usuario to set
	 */
	public void set_id_usuario(Integer _id_usuario) {
		this._id_usuario = _id_usuario;
	}

	/**
	 * @param _fecha_ingreso the _fecha_ingreso to set
	 */
	public void set_fecha_ingreso(String _fecha_ingreso) {
		this._fecha_ingreso = _fecha_ingreso;
	}

	/**
	 * @param _latitud_inicial the _latitud_inicial to set
	 */
	public void set_latitud_inicial(Double _latitud_inicial) {
		this._latitud_inicial = _latitud_inicial;
	}

	/**
	 * @param _longitud_inicial the _longitud_inicial to set
	 */
	public void set_longitud_inicial(Double _longitud_inicial) {
		this._longitud_inicial = _longitud_inicial;
	}

	public static ChatUsuario fromRS(ResultSet p_rs) throws SQLException {
		ChatUsuario ret = new ChatUsuario();
		
		try {
			ret.set_id(p_rs.getInt("id"));
			ret.set_id_chat(p_rs.getInt("id_chat"));
			ret.set_id_usuario(p_rs.getInt("id_usuario"));
			ret.set_fecha_ingreso(p_rs.getString("fecha_ingreso"));
			ret.set_latitud_inicial(p_rs.getDouble("latitud_inicial"));
			ret.set_longitud_inicial(p_rs.getDouble("longitud_inicial"));
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
	
	public static ChatUsuario getByParameter(Connection p_conn, String p_key, String p_value) throws Exception {
		ChatUsuario ret = null;
		
		String str_sql = _str_sql +
			"  WHERE chu." + p_key + " = " + p_value +
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
	
	public static ChatUsuario getById(Connection p_conn, String p_id) throws Exception {
		return getByParameter(p_conn, "id_chat_usuario", p_id);
	}
	
    public static ArrayList<ChatUsuario> seek(Connection p_conn, ArrayList<AbstractMap.SimpleEntry<String, String>> p_parameters, String p_order, String p_direction, int p_offset, int p_limit) throws SQLException {
    	Statement stmt = null;
    	ResultSet rs = null;
    	String str_sql;
    	ArrayList<ChatUsuario> ret;
    	
    	str_sql = "";
    	
		try {
			ArrayList<String> array_clauses = new ArrayList<String>();
			
			ret = new ArrayList<ChatUsuario>();
			
			str_sql = _str_sql;
			
			for (AbstractMap.SimpleEntry<String, String> p : p_parameters) {
				if (p.getKey().equals("id_usuario")) {
					array_clauses.add("chu.id_usuario_FK = " + p.getValue());
				}
				else if (p.getKey().equals("id_chat")) {
					array_clauses.add("chu.id_chat = " + p.getValue());
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
			"  UPDATE chat_usuario" +
			"  SET " +
			"  id_chat_FK = " + (_id_chat != null ? "'" + _id_chat + "'" : "null") + "," +
			"  id_usuario_FK = " + (_id_usuario != null ? "'" + _id_usuario + "'" : "null") + "," +
			"  fecha_ingreso = " + (_fecha_ingreso != null ? "STR_TO_DATE(" + _fecha_ingreso + ", '%d-%m-%Y %H:%i:%s')" : "null") + "," +
			"  latitud_inicial = " + (_latitud_inicial != null ? "'" + _latitud_inicial + "'" : "null") + "," +
			"  longitud_inicial = " + (_longitud_inicial != null ? "'" + _longitud_inicial + "'" : "null") +
			"  WHERE id_chat_usuario = " + Integer.toString(this._id);
		
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
			"  INSERT INTO chat_usuario" +
			"  (" +
			"  id_chat," +
			"  id_usuario_FK," +
			"  fecha_ingreso," +
			"  latitud_inicial," +
			"  longitud_inicial" +
			"  )" +
			"  VALUES" +
			"  (" +
			"  " + (_id_chat != null ? "'" + _id_chat + "'" : "null") + "," +
			"  " + (_id_usuario != null ? "'" + _id_usuario + "'" : "null") + "," +
			"  " + (_fecha_ingreso != null ? "STR_TO_DATE(" + _fecha_ingreso + ", '%d-%m-%Y %H:%i:%s')" : "null") + "," +
			"  " + (_latitud_inicial != null ? "'" + _latitud_inicial + "'" : "null") + "," +
			"  " + (_longitud_inicial != null ? "'" + _longitud_inicial + "'" : "null") +
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