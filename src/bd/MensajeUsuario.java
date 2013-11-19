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
public class MensajeUsuario {
	private Integer _id;
	private Integer _id_usuario;
	private String _mensaje;
	private Boolean _leido;
	private String _fecha;
	
	private final static String _str_sql = 
			"  SELECT mu.id_mensaje_usuario AS id, mu.id_usuario_FK AS id_usuario, mu.mensaje, 0+mu.leido AS leido, QUOTE(DATE_FORMAT(mu.fecha, '%d-%m-%Y %H:%i:%s')) AS fecha" +
		 	"  FROM mensaje_usuario mu";	 	

	public MensajeUsuario() {
		_id = null;
		_id_usuario = null;
		_mensaje = null;
		_leido = null;
		_fecha = null;
	}

	/**
	 * @return the _id
	 */
	public Integer get_id() {
		return _id;
	}

	/**
	 * @return the _id_usuario
	 */
	public Integer get_id_usuario() {
		return _id_usuario;
	}

	/**
	 * @return the _mensaje
	 */
	public String get_mensaje() {
		return _mensaje;
	}
	
	/**
	 * @return the _leido
	 */
	public Boolean get_leido() {
		return _leido;
	}	

	/**
	 * @return the _fecha
	 */
	public String get_fecha() {
		return _fecha;
	}

	/**
	 * @param _id the _id to set
	 */
	public void set_id(int _id) {
		this._id = _id;
	}
	
	/**
	 * @param _id_usuario the _id_usuario to set
	 */
	public void set_id_usuario(int _id_usuario) {
		this._id_usuario = _id_usuario;
	}

	/**
	 * @param _mensaje the _mensaje to set
	 */
	public void set_mensaje(String _mensaje) {
		this._mensaje = _mensaje;
	}

	/**
	 * @param _fecha the _fecha to set
	 */
	public void set_leido(Boolean _leido) {
		this._leido = _leido;
	}

	/**
	 * @param _fecha the _fecha to set
	 */
	public void set_fecha(String _fecha) {
		this._fecha = _fecha;
	}

	public static MensajeUsuario fromRS(ResultSet p_rs) throws SQLException {
		MensajeUsuario ret = new MensajeUsuario();
		
		try {
			ret.set_id(p_rs.getInt("id"));
			ret.set_id_usuario(p_rs.getInt("id_usuario"));
			ret.set_mensaje(p_rs.getString("mensaje"));
			ret.set_leido(p_rs.getBoolean("leido"));
			ret.set_fecha(p_rs.getString("fecha"));
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
			"  SELECT r.id_repuesto as id, p.fecha as plataforma, f.fecha as fabricante, m.fecha as modelo, re.fecha as radio_estacion, c.fecha as fecha, rg.fecha as region" +
		 	"  FROM repuesto r" +
			"  JOIN plataforma p ON p.id_plataforma = r.id_plataforma_FK" +
			"  JOIN radio_estacion re ON re.id_radio_estacion = r.id_radio_estacion_FK" +
		    "  JOIN fecha c ON c.id_usuario = re.id_usuario_FK" +
		    "  JOIN region rg ON rg.id_region = c.id_region_FK" +
			"  JOIN modelo m ON m.id_modelo = r.id_modelo_FK" +
			"  JOIN fabricante f ON f.id_fabricante = m.id_fabricante_FK" +
			"  WHERE f.fecha LIKE "%p_param%"" +
			"  OR m.fecha LIKE "%p_param%"" +
			"  OR r.fecha LIKE "%p_param%"" +
			"  OR p.fecha LIKE "%p_param%"" +
			"  AND r.ubicacion IS NULL";
		
		// echo str_sql . "<br>";
		
		return fc.getLink().QueryArray(str_sql, MYSQL_ASSOC);
	}
	*/
	
	public static MensajeUsuario getByParameter(Connection p_conn, String p_key, String p_value) throws Exception {
		MensajeUsuario ret = null;
		
		String str_sql = _str_sql +
			"  WHERE mu." + p_key + " = " + p_value +
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
	
	public static MensajeUsuario getByIdUsuario(Connection p_conn, Integer p_id_usuario) throws Exception {
		return getByParameter(p_conn, "id_usuario_FK", p_id_usuario.toString());
	}
	
	public static MensajeUsuario getById(Connection p_conn, Integer p_id) throws Exception {
		return getByParameter(p_conn, "id_mensaje_usuario", p_id.toString());
	}
	
    public static ArrayList<MensajeUsuario> seek(Connection p_conn, ArrayList<AbstractMap.SimpleEntry<String, String>> p_parameters, String p_order, String p_direction, int p_offset, int p_limit) throws SQLException {
    	Statement stmt = null;
    	ResultSet rs = null;
    	String str_sql;
    	ArrayList<MensajeUsuario> ret;
    	
    	str_sql = "";
    	
		try {
			ArrayList<String> array_clauses = new ArrayList<String>();
			
			ret = new ArrayList<MensajeUsuario>();
			
			str_sql = _str_sql;
			
			for (AbstractMap.SimpleEntry<String, String> p : p_parameters) {
				if (p.getKey().equals("id_usuario")) {
					array_clauses.add("mu.id_usuario_FK = " + p.getValue());
				}
				else if (p.getKey().equals("fecha")) {
					array_clauses.add("mu.fecha = " + p.getValue());
				}
				else if (p.getKey().equals("mensaje_reciente")) {
					array_clauses.add("mu.fecha > DATE_ADD(now(), INTERVAL -" + p.getValue() + " MINUTE)");
				}
				else if (p.getKey().equals("leido")) {
					array_clauses.add("mu.leido = b'1'");
				}
				else if (p.getKey().equals("no leido")) {
					array_clauses.add("mu.leido = b'0'");
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
			"  UPDATE mensaje_usuario" +
			"  SET " +
			"  id_usuario_FK = " + (_id_usuario != null ? _id_usuario : "null") + "," +
			"  mensaje = " + (_mensaje != null ? "'" + _mensaje + "'" : "null") + "," +
			"  leido = " + (_leido != null ? "b'" + (_leido ? 1 : 0) + "'" : "null") + "," +
			"  fecha = " + (_fecha != null ? "STR_TO_DATE(" + _fecha + ", '%d-%m-%Y %H:%i:%s')" : "null") +
			"  WHERE id_mensaje_usuario = " + Integer.toString(this._id);
		
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
			
			throw new Exception("Error al obtener registros");
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
			"  INSERT INTO mensaje_usuario" +
			"  (" +
			"  id_usuario_FK," +
			"  mensaje," +
			"  leido," +
			"  fecha" +
			"  )" +
			"  VALUES" +
			"  (" +
			"  " + (_id_usuario != null ? _id_usuario.toString() : "null") + "," +
			"  " + (_mensaje != null ? "'" + _mensaje + "'" : "null") + "," +
			"  " + (_leido != null ? "b'" + (_leido ? 1 : 0) + "'" : "null") + "," +
			"  " + (_fecha != null ? "STR_TO_DATE(" + _fecha + ", '%d-%m-%Y %H:%i:%s')" : "null") +
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
			
			throw new Exception("Error al obtener registros");
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
	
	public static int delete(Connection p_conn, Integer p_id_usuario) throws Exception {

    	int ret = -1;
		Statement stmt = null;

    	String str_sql =
			"  DELETE FROM mensaje_usuario";
    	
    	if (p_id_usuario != null) {
    		str_sql +=
    			"  WHERE id_usuario_FK = " + p_id_usuario.toString();
    	}
		
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
			
			throw new Exception("Error al borrar registros");
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
}