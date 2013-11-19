package bd;

//import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.Statement;

public class Comuna
{
	private Integer _id;
	private Integer _id_ciudad;
	private String _nombre;
	private String _ciudad;
	private String _region;
	private String _pais;
	private Integer _id_region;
	private Integer _id_pais;
	
	private final static String _str_sql = 
			"  SELECT cm.id_comuna AS id, cm.id_ciudad_FK AS id_ciudad, cm.nombre, ci.nombre AS ciudad, rg.nombre AS region, p.nombre AS pais, rg.id_region, p.id_pais" +
		 	"  FROM comuna cm" +
		 	"  JOIN ciudad ci ON ci.id_ciudad = cm.id_ciudad_FK" +
		 	"  JOIN region rg ON rg.id_region = ci.id_region_FK" +
		 	"  JOIN pais p ON p.id_pais = rg.id_pais_FK";	 	

	public Comuna() {
		_id = null;
		_id_ciudad = null;
		_nombre = null;
		_ciudad = null;
		_region = null;
		_pais = null;
		_id_region = null;
		_id_pais = null;
	}

	/**
	 * @return the _id
	 */
	public Integer get_id() {
		return _id;
	}

	/**
	 * @return the _id_ciudad
	 */
	public Integer get_id_ciudad() {
		return _id_ciudad;
	}

	/**
	 * @return the _nombre
	 */
	public String get_nombre() {
		return _nombre;
	}

	/**
	 * @return the _ciudad
	 */
	public String get_ciudad() {
		return _ciudad;
	}

	/**
	 * @return the _region
	 */
	public String get_region() {
		return _region;
	}

	/**
	 * @return the _pais
	 */
	public String get_pais() {
		return _pais;
	}

	/**
	 * @return the _id_region
	 */
	public Integer get_id_region() {
		return _id_region;
	}

	/**
	 * @return the _id_pais
	 */
	public Integer get_id_pais() {
		return _id_pais;
	}

	/**
	 * @param _id the _id to set
	 */
	public void set_id(int _id) {
		this._id = _id;
	}
	
	/**
	 * @param _id_ciudad the _id_ciudad to set
	 */
	public void set_id_ciudad(int _id_ciudad) {
		this._id_ciudad = _id_ciudad;
	}

	/**
	 * @param _nombre the _nombre to set
	 */
	public void set_nombre(String _nombre) {
		this._nombre = _nombre;
	}

	/**
	 * @param _ciudad the _ciudad to set
	 */
	public void set_ciudad(String _ciudad) {
		this._ciudad = _ciudad;
	}

	/**
	 * @param _region the _region to set
	 */
	public void set_region(String _region) {
		this._region = _region;
	}

	/**
	 * @param _pais the _pais to set
	 */
	public void set_pais(String _pais) {
		this._pais = _pais;
	}

	/**
	 * @param _id_region the _id_region to set
	 */
	public void set_id_region(Integer _id_region) {
		this._id_region = _id_region;
	}

	/**
	 * @param _id_pais the _id_pais to set
	 */
	public void set_id_pais(Integer _id_pais) {
		this._id_pais = _id_pais;
	}

	public static Comuna fromRS(ResultSet p_rs) throws SQLException {
		Comuna ret = new Comuna();
		
		try {
			ret.set_id(p_rs.getInt("id"));
			ret.set_id_ciudad(p_rs.getInt("id_ciudad"));
			ret.set_nombre(p_rs.getString("nombre"));
			ret.set_ciudad(p_rs.getString("ciudad"));
			ret.set_region(p_rs.getString("region"));
			ret.set_pais(p_rs.getString("pais"));
			ret.set_ciudad(p_rs.getString("ciudad"));
			ret.set_region(p_rs.getString("region"));
			ret.set_pais(p_rs.getString("pais"));
			ret.set_id_region(p_rs.getInt("id_region"));
			ret.set_id_pais(p_rs.getInt("id_pais"));
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
	
	public static Comuna getByParameter(Connection p_conn, String p_key, String p_value) throws Exception {
		Comuna ret = null;
		
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
	
	public static Comuna getByNombre(Connection p_conn, String p_nombre) throws Exception {
		return getByParameter(p_conn, "nombre", "'" + p_nombre + "'");
	}
	
	public static Comuna getById(Connection p_conn, String p_id) throws Exception {
		return getByParameter(p_conn, "id_comuna", p_id);
	}
	
    public static ArrayList<Comuna> seek(Connection p_conn, ArrayList<AbstractMap.SimpleEntry<String, String>> p_parameters, String p_order, String p_direction, int p_offset, int p_limit) throws SQLException {
    	Statement stmt = null;
    	ResultSet rs = null;
    	String str_sql;
    	ArrayList<Comuna> ret;
    	
    	str_sql = "";
    	
		try {
			ArrayList<String> array_clauses = new ArrayList<String>();
			
			ret = new ArrayList<Comuna>();
			
			str_sql = _str_sql;
			
			for (AbstractMap.SimpleEntry<String, String> p : p_parameters) {
				if (p.getKey().equals("id_ciudad")) {
					array_clauses.add("cm.id_ciudad = " + p.getValue());
				}
				else if (p.getKey().equals("id_region")) {
					array_clauses.add("rg.id_region = " + p.getValue());
				}
				else if (p.getKey().equals("id_pais")) {
					array_clauses.add("p.id_pais = " + p.getValue());
				}
				else if (p.getKey().equals("nombre")) {
					array_clauses.add("cm.nombre = " + p.getValue());
				}
				else if (p.getKey().equals("ciudad")) {
					array_clauses.add("ci.nombre = " + p.getValue());
				}
				else if (p.getKey().equals("pais")) {
					array_clauses.add("p.nombre = " + p.getValue());
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
	/*
	public void update(Connection p_conn) throws Exception {

    	Statement stmt = null;

    	String str_sql =
			"  UPDATE comuna" +
			"  SET alias = " + (_alias != null ? "'" + _alias + "'" : "null") + "," +
			"  contrasena = " + (_contrasena != null ? "'" + _contrasena + "'" : "null") + "," +
			"  nombre = " + (_nombre != null ? "'" + _nombre + "'" : "null") + "," +
			"  ciudad = " + (_ciudad != null ? "'" + _ciudad + "'" : "null") + "," +
			"  region = " + (_region != null ? "'" + _region + "'" : "null") + "," +
			"  pais = " + (_pais != null ? "'" + _pais + "'" : "null") + "," +
			"  rut = " + (_rut != null ? "'" + _rut + "'" : "null") + "," +
			"  email = " + (_email != null ? "'" + _email + "'" : "null") + "," +
			"  fecha_nacimiento = " + (_fecha_nacimiento != null ? "'" + _fecha_nacimiento + "'" : "null") + "," +
			"  direccion = " + (_direccion != null ? "'" + _direccion + "'" : "null") + "," +
			"  foto = " + (_foto != null ? "'" + _foto + "'" : "null") + "," +
			"  antecedentes_emergencia = " + (_antecedentes_emergencia != null ? "'" + _antecedentes_emergencia + "'" : "null") +
			"  WHERE id_comuna = " + Integer.toString(this._id);
		
		try {
			stmt = p_conn.createStatement();
			
			if (stmt.executeUpdate(str_sql) < 1) {
				throw new Exception("No hubo filas afectadas");
			}
			
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
	}
	
	public void insert(Connection p_conn) throws Exception {
		
    	Statement stmt = null;
    	ResultSet rs = null;

    	String str_sql =
			"  INSERT INTO usuario" +
			"  (" +
			"  alias," +
			"  contrasena," +
			"  nombre," +
			"  ciudad," +
			"  region," +
			"  pais," +
			"  rut," +
			"  email," +
			"  fecha_nacimiento," +
			"  direccion," +
			"  foto," +
			"  antecedentes_emergencia" +
			"  )" +
			"  VALUES" +
			"  (" +
			"  " + (_alias != null ? "'" + _alias + "'" : "null") + "," +
			"  " + (_contrasena != null ? "'" + _contrasena + "'" : "null") + "," +
			"  " + (_nombre != null ? "'" + _nombre + "'" : "null") + "," +
			"  " + (_ciudad != null ? "'" + _ciudad + "'" : "null") + "," +
			"  " + (_region != null ? "'" + _region + "'" : "null") + "," +
			"  " + (_pais != null ? "'" + _pais + "'" : "null") + "," +
			"  " + (_rut != null ? "'" + _rut + "'" : "null") + "," +
			"  " + (_email != null ? "'" + _email + "'" : "null") + "," +
			"  " + (_fecha_nacimiento != null ? "'" + _fecha_nacimiento + "'" : "null") + "," +
			"  " + (_direccion != null ? "'" + _direccion + "'" : "null") + "," +
			"  " + (_foto != null ? "'" + _foto + "'" : "null") + "," +
			"  " + (_antecedentes_emergencia != null ? "'" + _antecedentes_emergencia + "'" : "null") +
			"  )";
		
		try {
			stmt = p_conn.createStatement();
			
			if (stmt.executeUpdate(str_sql) < 1) {
				throw new Exception("No hubo filas afectadas");
			}
			
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
	}
	*/
}