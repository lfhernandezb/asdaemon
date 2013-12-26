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
public class Contacto {
	private Integer _id;
	private Integer _id_usuario;
	private Integer _id_usuario_contacto;
	private String _correo;
	private String _relacion;
	
	private final static String _str_sql = 
			"  SELECT ct.id_contacto AS id, ct.id_usuario_FK AS id_usuario, ct.id_usuario_contacto_FK AS id_usuario_contacto, ct.correo, ct.relacion" +
		 	"  FROM contacto ct";	 	

	public Contacto() {
		_id = null;
		_id_usuario = null;
		_id_usuario_contacto = null;
		_correo = null;
		_relacion = null;
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
	 * @return the _id_usuario_contacto
	 */
	public Integer get_id_usuario_contacto() {
		return _id_usuario_contacto;
	}
	
	/**
	 * @return the _correo
	 */
	public String get_correo() {
		return _correo;
	}	

	/**
	 * @return the _relacion
	 */
	public String get_relacion() {
		return _relacion;
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
	 * @param _id_usuario_contacto the _id_usuario_contacto to set
	 */
	public void set_id_usuario_contacto(Integer _id_usuario_contacto) {
		this._id_usuario_contacto = _id_usuario_contacto;
	}

	/**
	 * @param _relacion the _relacion to set
	 */
	public void set_correo(String _correo) {
		this._correo = _correo;
	}

	/**
	 * @param _relacion the _relacion to set
	 */
	public void set_relacion(String _relacion) {
		this._relacion = _relacion;
	}

	public static Contacto fromRS(ResultSet p_rs) throws SQLException {
		Contacto ret = new Contacto();
		
		try {
			ret.set_id(p_rs.getInt("id"));
			ret.set_id_usuario(p_rs.getInt("id_usuario"));
			ret.set_id_usuario_contacto(p_rs.getInt("id_usuario_contacto"));
			ret.set_correo(p_rs.getString("correo"));
			ret.set_relacion(p_rs.getString("relacion"));
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
			"  SELECT r.id_repuesto as id, p.descripcion as plataforma, f.descripcion as fabricante, m.descripcion as modelo, re.descripcion as radio_estacion, c.descripcion as descripcion, rg.descripcion as region" +
		 	"  FROM repuesto r" +
			"  JOIN plataforma p ON p.id_plataforma = r.id_plataforma_FK" +
			"  JOIN radio_estacion re ON re.id_radio_estacion = r.id_radio_estacion_FK" +
		    "  JOIN descripcion c ON c.id_usuario = re.id_usuario_FK" +
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
	
	public static Contacto getByParameter(Connection p_conn, String p_key, String p_value) throws Exception {
		Contacto ret = null;
		
		String str_sql = _str_sql +
			"  WHERE ct." + p_key + " = " + p_value +
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
	
	public static Contacto getByIdUsuarioIdUsuarioContacto(Connection p_conn, String p_id_usuario_contacto) throws Exception {
		return getByParameter(p_conn, "id_usuario_FK", "'" + p_id_usuario_contacto + "'");
	}
	
	public static Contacto getById(Connection p_conn, String p_id) throws Exception {
		return getByParameter(p_conn, "id_contacto", p_id);
	}
	
    public static ArrayList<Contacto> seek(Connection p_conn, ArrayList<AbstractMap.SimpleEntry<String, String>> p_parameters, String p_order, String p_direction, int p_offset, int p_limit) throws SQLException {
    	Statement stmt = null;
    	ResultSet rs = null;
    	String str_sql;
    	ArrayList<Contacto> ret;
    	
    	str_sql = "";
    	
		try {
			ArrayList<String> array_clauses = new ArrayList<String>();
			
			ret = new ArrayList<Contacto>();
			
			str_sql = _str_sql;
			
			for (AbstractMap.SimpleEntry<String, String> p : p_parameters) {
				if (p.getKey().equals("id_usuario")) {
					array_clauses.add("ct.id_usuario_FK = " + p.getValue());
				}
				else if (p.getKey().equals("id_usuario_contacto")) {
					array_clauses.add("ct.id_usuario_contacto_FK = " + p.getValue());
				}
				else if (p.getKey().equals("correo")) {
					array_clauses.add("ct.correo = '" + p.getValue() + "'");
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
			"  UPDATE contacto" +
			"  SET " +
			"  id_usuario_FK = " + (_id_usuario != null ? _id_usuario : "null") + "," +
			"  id_usuario_contacto_FK = " + (_id_usuario_contacto != null ? _id_usuario_contacto : "null") + "," +
			"  correo = " + (_correo != null ? "'" + _correo + "'" : "null") + "," +
			"  relacion = " + (_relacion != null ? "'" + _relacion + "'" : "null") +
			"  WHERE id_contacto = " + Integer.toString(this._id);
		
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
			"  INSERT INTO contacto" +
			"  (" +
			"  id_usuario_FK," +
			"  id_usuario_contacto_FK," +
			"  correo," +
			"  relacion" +
			"  )" +
			"  VALUES" +
			"  (" +
			"  " + (_id_usuario != null ? _id_usuario.toString() : "null") + "," +
			"  " + (_id_usuario_contacto != null ? _id_usuario_contacto.toString() : "null") + "," +
			"  " + (_correo != null ? "'" + _correo + "'" : "null") + "," +
			"  " + (_relacion != null ? "'" + _relacion + "'" : "null") +
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
			"  DELETE FROM contacto";
    	
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