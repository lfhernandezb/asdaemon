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
public class ComunidadUsuario {
	private Integer _id;
	private Integer _id_comunidad;
	private Integer _id_usuario;
	private Boolean _es_lider;
	private Boolean _es_administrador;
	private String _fecha_ingreso;
	
	private final static String _str_sql = 
			"  SELECT cu.id_comunidad_usuario AS id, cu.id_comunidad_FK AS id_comunidad, cu.id_usuario_FK AS id_usuario, 0+cu.es_lider AS es_lider, 0+cu.es_administrador AS es_administrador, DATE_FORMAT(cu.fecha_ingreso, '%d-%m-%Y %H:%i:%s') AS fecha_ingreso" +
		 	"  FROM comunidad_usuario cu";	 	

	public ComunidadUsuario() {
		_id = null;
		_id_comunidad = null;
		_id_usuario = null;
		_es_lider = null;
		_es_administrador = null;
		_fecha_ingreso = null;
	}

	/**
	 * @return the _id
	 */
	public Integer get_id() {
		return _id;
	}

	/**
	 * @return the _id_comunidad
	 */
	public Integer get_id_comunidad() {
		return _id_comunidad;
	}

	/**
	 * @return the _id_usuario
	 */
	public Integer get_id_usuario() {
		return _id_usuario;
	}
	
	/**
	 * @return the _es_lider
	 */
	public Boolean get_es_lider() {
		return _es_lider;
	}	

	/**
	 * @return the _es_administrador
	 */
	public Boolean get_es_administrador() {
		return _es_administrador;
	}

	/**
	 * @return the _fecha_ingreso
	 */
	public String get_fecha_ingreso() {
		return _fecha_ingreso;
	}

	/**
	 * @param _id the _id to set
	 */
	public void set_id(int _id) {
		this._id = _id;
	}
	
	/**
	 * @param _id_comunidad the _id_comunidad to set
	 */
	public void set_id_comunidad(int _id_comunidad) {
		this._id_comunidad = _id_comunidad;
	}

	/**
	 * @param _id_usuario the _id_usuario to set
	 */
	public void set_id_usuario(Integer _id_usuario) {
		this._id_usuario = _id_usuario;
	}

	/**
	 * @param _es_administrador the _es_administrador to set
	 */
	public void set_es_lider(Boolean _es_lider) {
		this._es_lider = _es_lider;
	}

	/**
	 * @param _es_administrador the _es_administrador to set
	 */
	public void set_es_administrador(Boolean _es_administrador) {
		this._es_administrador = _es_administrador;
	}

	/**
	 * @param _id_usuario the _fecha_ingreso to set
	 */
	public void set_fecha_ingreso(String _fecha_ingreso) {
		this._fecha_ingreso = _fecha_ingreso;
	}

	public static ComunidadUsuario fromRS(ResultSet p_rs) throws SQLException {
		ComunidadUsuario ret = new ComunidadUsuario();
		
		try {
			ret.set_id(p_rs.getInt("id"));
			ret.set_id_comunidad(p_rs.getInt("id_comunidad"));
			ret.set_id_usuario(p_rs.getInt("id_usuario"));
			ret.set_es_lider(p_rs.getBoolean("es_lider"));
			ret.set_es_administrador(p_rs.getBoolean("es_administrador"));
			ret.set_fecha_ingreso(p_rs.getString("fecha_ingreso"));
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
			"  SELECT r.id_repuesto as id, p.es_administrador as plataforma, f.es_administrador as fabricante, m.es_administrador as modelo, re.es_administrador as radio_estacion, c.es_administrador as es_administrador, rg.es_administrador as region" +
		 	"  FROM repuesto r" +
			"  JOIN plataforma p ON p.id_plataforma = r.id_plataforma_FK" +
			"  JOIN radio_estacion re ON re.id_radio_estacion = r.id_radio_estacion_FK" +
		    "  JOIN es_administrador c ON c.id_comunidad = re.id_comunidad_FK" +
		    "  JOIN region rg ON rg.id_region = c.id_region_FK" +
			"  JOIN modelo m ON m.id_modelo = r.id_modelo_FK" +
			"  JOIN fabricante f ON f.id_fabricante = m.id_fabricante_FK" +
			"  WHERE f.es_administrador LIKE "%p_param%"" +
			"  OR m.es_administrador LIKE "%p_param%"" +
			"  OR r.es_administrador LIKE "%p_param%"" +
			"  OR p.es_administrador LIKE "%p_param%"" +
			"  AND r.ubicacion IS NULL";
		
		// echo str_sql . "<br>";
		
		return fc.getLink().QueryArray(str_sql, MYSQL_ASSOC);
	}
	*/
	
	public static ComunidadUsuario getByParameter(Connection p_conn, String p_key, String p_value) throws Exception {
		ComunidadUsuario ret = null;
		
		String str_sql = _str_sql +
			"  WHERE cu." + p_key + " = " + p_value +
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
	
	public static ComunidadUsuario getByIdUsuario(Connection p_conn, String p_id_usuario) throws Exception {
		return getByParameter(p_conn, "id_comunidad_FK", "'" + p_id_usuario + "'");
	}
	
	public static ComunidadUsuario getById(Connection p_conn, String p_id) throws Exception {
		return getByParameter(p_conn, "id_comunidad_usuario", p_id);
	}
	
    public static ArrayList<ComunidadUsuario> seek(Connection p_conn, ArrayList<AbstractMap.SimpleEntry<String, String>> p_parameters, String p_order, String p_direction, int p_offset, int p_limit) throws SQLException {
    	Statement stmt = null;
    	ResultSet rs = null;
    	String str_sql;
    	ArrayList<ComunidadUsuario> ret;
    	
    	str_sql = "";
    	
		try {
			ArrayList<String> array_clauses = new ArrayList<String>();
			
			ret = new ArrayList<ComunidadUsuario>();
			
			str_sql = _str_sql;
			
			for (AbstractMap.SimpleEntry<String, String> p : p_parameters) {
				if (p.getKey().equals("id_comunidad")) {
					array_clauses.add("cu.id_comunidad_FK = " + p.getValue());
				}
				else if (p.getKey().equals("id_usuario")) {
					array_clauses.add("cu.id_usuario_FK = " + p.getValue());
				}
				else if (p.getKey().equals("es_administrador")) {
					array_clauses.add("cu.es_administrador = b'1'");
				}
				else if (p.getKey().equals("es_liser")) {
					array_clauses.add("cu.es_lider = b'1'");
				}
				else if (p.getKey().equals("en_comunidad_con")) {
					array_clauses.add("cu.id_comunidad_FK IN (SELECT id_comunidad_FK FROM comunidad_usuario WHERE id_usuario_FK = " + p.getValue() + ")");
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
			"  UPDATE comunidad_usuario" +
			"  SET " +
			"  id_comunidad_FK = " + (_id_comunidad != null ? _id_comunidad : "null") + "," +
			"  id_usuario_FK = " + (_id_usuario != null ? _id_usuario : "null") + "," +
			"  es_lider = " + (_es_lider != null ? "b'" + (_es_lider ? 1 : 0) + "'" : "null") + "," +
			"  es_administrador = " + (_es_administrador != null ? "b'" + (_es_administrador ? 1 : 0) + "'" : "null") + "," +
			"  fecha_ingreso = " + (_fecha_ingreso != null ? "STR_TO_DATE(" + _fecha_ingreso + ", '%d-%m-%Y %H:%i:%s')" : "null") +
			"  WHERE id_comunidad_usuario = " + Integer.toString(this._id);
		
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
			"  INSERT INTO comunidad_usuario" +
			"  (" +
			"  id_comunidad_FK," +
			"  id_usuario_FK," +
			"  es_lider," +
			"  es_administrador," +
			"  fecha_ingreso" +
			"  )" +
			"  VALUES" +
			"  (" +
			"  " + (_id_comunidad != null ? _id_comunidad.toString() : "null") + "," +
			"  " + (_id_usuario != null ? _id_usuario.toString() : "null") + "," +
			"  " + (_es_lider != null ? "b'" + (_es_lider ? 1 : 0) + "'" : "null") + "," +
			"  " + (_es_administrador != null ? "b'" + (_es_administrador ? 1 : 0) + "'" : "null") + "," +
			"  " + (_fecha_ingreso != null ? "STR_TO_DATE(" + _fecha_ingreso + ", '%d-%m-%Y %H:%i:%s')" : "null") +
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
	
	public static int delete(Connection p_conn, Integer p_id_comunidad) throws Exception {

    	int ret = -1;
		Statement stmt = null;

    	String str_sql =
			"  DELETE FROM comunidad_usuario";
    	
    	if (p_id_comunidad != null) {
    		str_sql +=
    			"  WHERE id_comunidad_FK = " + p_id_comunidad.toString();
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