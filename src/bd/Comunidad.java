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
public class Comunidad {
	private Integer _id;
	private String _nombre;
	private String _descripcion;
	private String _fecha_creacion;
	private Double _latitud;
	private Double _longitud;
	private Long _cobertura;
	
	private final static String _str_sql = 
			"  SELECT cm.id_comunidad AS id, QUOTE(cm.nombre) AS nombre, QUOTE(cm.descripcion) AS descripcion, cm.fecha_creacion, cm.latitud, cm.longitud, cm.cobertura AS cobertura" +
		 	"  FROM comunidad cm";	 	

	public Comunidad() {
		_id = null;
		_nombre = null;
		_descripcion = null;
		_fecha_creacion = null;
		_latitud = null;
		_longitud = null;
		_cobertura = null;
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
	 * @return the _descripcion
	 */
	public String get_descripcion() {
		return _descripcion;
	}

	/**
	 * @return the _fecha_creacion
	 */
	public String get_fecha_creacion() {
		return _fecha_creacion;
	}

	/**
	 * @return the _latitud
	 */
	public Double get_latitud() {
		return _latitud;
	}

	/**
	 * @return the _longitud
	 */
	public Double get_longitud() {
		return _longitud;
	}

	/**
	 * @return the _cobertura
	 */
	public Long get_cobertura() {
		return _cobertura;
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
	 * @param _descripcion the _descripcion to set
	 */
	public void set_descripcion(String _descripcion) {
		this._descripcion = _descripcion;
	}

	/**
	 * @param _fecha_creacion the _fecha_creacion to set
	 */
	public void set_fecha_creacion(String _fecha_creacion) {
		this._fecha_creacion = _fecha_creacion;
	}

	/**
	 * @param _latitud the _latitud to set
	 */
	public void set_latitud(Double _latitud) {
		this._latitud = _latitud;
	}

	/**
	 * @param _longitud the _longitud to set
	 */
	public void set_longitud(Double _longitud) {
		this._longitud = _longitud;
	}

	/**
	 * @param _cobertura the _cobertura to set
	 */
	public void set_cobertura(Long _cobertura) {
		this._cobertura = _cobertura;
	}

	public static Comunidad fromRS(ResultSet p_rs) throws SQLException {
		Comunidad ret = new Comunidad();
		
		try {
			ret.set_id(p_rs.getInt("id"));
			ret.set_nombre(p_rs.getString("nombre"));
			ret.set_descripcion(p_rs.getString("descripcion"));
			ret.set_fecha_creacion(p_rs.getString("fecha_creacion"));
			ret.set_latitud(p_rs.getDouble("latitud"));
			ret.set_longitud(p_rs.getDouble("longitud"));
			ret.set_cobertura(p_rs.getLong("cobertura"));
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
	
	public static Comunidad getByParameter(Connection p_conn, String p_key, String p_value) throws Exception {
		Comunidad ret = null;
		
		String str_sql = _str_sql +
			"  WHERE cm." + p_key + " = " + p_value +
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
	
	public static Comunidad getById(Connection p_conn, String p_id) throws Exception {
		return getByParameter(p_conn, "id_comunidad", p_id);
	}
	
	public static Comunidad getByNombre(Connection p_conn, String p_nombre) throws Exception {
		return getByParameter(p_conn, "nombre", "QUOTE('" + p_nombre + "')");
	}

	public static ArrayList<Comunidad> seek(Connection p_conn, ArrayList<AbstractMap.SimpleEntry<String, String>> p_parameters, String p_order, String p_direction, int p_offset, int p_limit) throws SQLException {
    	Statement stmt = null;
    	ResultSet rs = null;
    	String str_sql;
    	ArrayList<Comunidad> ret;
    	
    	str_sql = "";
    	
		try {
			ArrayList<String> array_clauses = new ArrayList<String>();
			
			ret = new ArrayList<Comunidad>();
			
			str_sql = _str_sql;
			
			for (AbstractMap.SimpleEntry<String, String> p : p_parameters) {
				if (p.getKey().equals("descripcion")) {
					array_clauses.add("cm.descripcion = " + p.getValue());
				}
				else if (p.getKey().equals("nombre")) {
					array_clauses.add("cm.nombre = " + p.getValue());
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
			"  UPDATE comunidad" +
			"  SET" +
			"  nombre = " + (_nombre != null ? "QUOTE('" + _nombre + "')" : "null") + "," +
			"  descripcion = " + (_descripcion != null ? "QUOTE('" + _descripcion + "')" : "null") + "," +
			"  fecha_creacion = " + (_fecha_creacion != null ? "STR_TO_DATE(" + _fecha_creacion + ", '%d-%m-%Y %H:%i:%s')" : "null") +
			"  latitud = " + (_latitud != null ? "'" + _latitud + "'" : "null") + "," +
			"  longitud = " + (_longitud != null ? "'" + _longitud + "'" : "null") + "," +
			"  cobertura = " + (_cobertura != null ? "'" + _cobertura + "'" : "null") +
			"  WHERE id_comunidad = " + Integer.toString(this._id);
		
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
			"  INSERT INTO comunidad" +
			"  (" +
			"  nombre," +
			"  descripcion," +
			"  fecha_creacion," +
			"  latitud," +
			"  longitud," +
			"  cobertura" +
			"  )" +
			"  VALUES" +
			"  (" +
			"  " + (_nombre != null ? "QUOTE('" + _nombre + "')" : "null") + "," +
			"  " + (_descripcion != null ? "QUOTE('" + _descripcion + "')" : "null") + "," +
			"  " + (_fecha_creacion != null ? "STR_TO_DATE(" + _fecha_creacion + ", '%d-%m-%Y %H:%i:%s')" : "null") + "," +
			"  " + (_latitud != null ? "'" + _latitud + "'" : "null") + "," +
			"  " + (_longitud != null ? "'" + _longitud + "'" : "null") + "," +
			"  " + (_cobertura != null ? "'" + _cobertura + "'" : "null") +
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