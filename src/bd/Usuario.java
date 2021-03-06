package bd;

//import java.util.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.Statement;

public class Usuario
{
	private Integer _id;
	private Integer _id_comuna;
	private String _alias;
	private String _contrasena;
	private String _nombre;
	private String _apellido_paterno;
	private String _apellido_materno;
	private String _movil;
	private String _rut;
	private String _correo;
	private String _correo_opcional;
	private String _fecha_nacimiento;
	private Boolean _hombre;
	private Boolean _contactar_contactos_de_contactos;
	private Boolean _contactar_desconocidos;
	private Boolean _publicar_informacion;
	private String _clave_validacion;
	private String _direccion;
	private String _foto;
	private String _antecedentes_emergencia;
	private String _comuna;
	private String _ciudad;
	private String _region;
	private String _pais;
	private Double _latitud;
	private Double _longitud;
	private String _parametros;
	private Boolean _validado;
	private Boolean _borrado;
	
	private final static String _str_sql = 
			"  SELECT u.id_usuario AS id, u.id_comuna_FK AS id_comuna, u.alias, u.contrasena, u.nombre, u.apellido_paterno, u.apellido_materno, u.movil, " +
			"  u.rut, u.correo, u.correo_opcional, DATE_FORMAT(u.fecha_nacimiento, '%d-%m-%Y') AS fecha_nacimiento, 0+u.hombre AS hombre, " +
			"  0+u.contactar_contactos_de_contactos AS contactar_contactos_de_contactos, " +
			"  0+u.contactar_desconocidos AS contactar_desconocidos, " +
			"  0+u.publicar_informacion AS publicar_informacion, u.clave_validacion, " +
			"  u.direccion, u.foto, u.antecedentes_emergencia, 0+u.validado AS validado, cm.nombre AS comuna, " +
			"  ci.nombre AS ciudad," +
			"  rg.nombre AS region, p.nombre AS pais, up.latitud, up.longitud, upm.parametros" +
		 	"  FROM usuario u" +
		 	"  JOIN comuna cm ON cm.id_comuna = u.id_comuna_FK" +
		 	"  JOIN ciudad ci ON ci.id_ciudad = cm.id_ciudad_FK" +
		 	"  JOIN region rg ON rg.id_region = ci.id_region_FK" +
		 	"  JOIN pais p ON p.id_pais = rg.id_pais_FK" +
		 	"  LEFT JOIN usuario_posicion up ON up.id_usuario_FK = u.id_usuario" +
		 	"  LEFT JOIN usuario_parametro upm ON upm.id_usuario_FK = u.id_usuario" +
		 	"  LEFT JOIN comunidad_usuario cu ON cu.id_usuario_FK = u.id_usuario" +
		 	"  LEFT JOIN comunidad c ON c.id_comunidad = cu.id_comunidad_FK";	 	

	public Usuario() {
		_id = null;
		_id_comuna = null;
		_alias = null;
		_contrasena = null;
		_nombre = null;
		_apellido_paterno = null;
		_apellido_materno = null;
		_movil = null;
		_rut = null;
		_correo = null;
		_correo_opcional = null;
		_fecha_nacimiento = null;
		_hombre = null;
		_contactar_contactos_de_contactos = null;
		_contactar_desconocidos = null;
		_publicar_informacion = null;
		_clave_validacion = null;
		_direccion = null;
		_foto = null;
		_antecedentes_emergencia = null;
		_validado = null;
	}

	/**
	 * @return the _id
	 */
	public Integer get_id() {
		return _id;
	}

	/**
	 * @return the _id_comuna
	 */
	public Integer get_id_comuna() {
		return _id_comuna;
	}

	/**
	 * @return the _alias
	 */
	public String get_alias() {
		return _alias;
	}

	/**
	 * @return the _contrasena
	 */
	public String get_contrasena() {
		return _contrasena;
	}

	/**
	 * @return the _nombre
	 */
	public String get_nombre() {
		return _nombre;
	}

	/**
	 * @return the _apellido_paterno
	 */
	public String get_apellido_paterno() {
		return _apellido_paterno;
	}

	/**
	 * @return the _apellido_materno
	 */
	public String get_apellido_materno() {
		return _apellido_materno;
	}

	/**
	 * @return the _movil
	 */
	public String get_movil() {
		return _movil;
	}

	/**
	 * @return the _rut
	 */
	public String get_rut() {
		return _rut;
	}

	/**
	 * @return the _correo
	 */
	public String get_correo() {
		return _correo;
	}

	/**
	 * @return the _correo_opcional
	 */
	public String get_correo_opcional() {
		return _correo_opcional;
	}

	/**
	 * @return the _fecha_nacimiento
	 */
	public String get_fecha_nacimiento() {
		return _fecha_nacimiento;
	}

	/**
	 * @return the _hombre
	 */
	public Boolean get_hombre() {
		return _hombre;
	}

	/**
	 * @return the _contactar_contactos_de_contactos
	 */
	public Boolean get_contactar_contactos_de_contactos() {
		return _contactar_contactos_de_contactos;
	}

	/**
	 * @return the _contactar_desconocidos
	 */
	public Boolean get_contactar_desconocidos() {
		return _contactar_desconocidos;
	}

	/**
	 * @return the _publicar_informacion
	 */
	public Boolean get_publicar_informacion() {
		return _publicar_informacion;
	}

	/**
	 * @return the _clave_validacion
	 */
	public String get_clave_validacion() {
		return _clave_validacion;
	}

	/**
	 * @return the _direccion
	 */
	public String get_direccion() {
		return _direccion;
	}

	/**
	 * @return the _foto
	 */
	public String get_foto() {
		return _foto;
	}

	/**
	 * @return the _antecedentes_emergencia
	 */
	public String get_antecedentes_emergencia() {
		return _antecedentes_emergencia;
	}

	/**
	 * @return the _validado
	 */
	public Boolean get_validado() {
		return _validado;
	}

	/**
	 * @return the _comuna
	 */
	public String get_comuna() {
		return _comuna;
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
	 * @return the _parametros
	 */
	public String get_parametros() {
		return _parametros;
	}

	/**
	 * @return the _borrado
	 */
	public boolean is_borrado() {
		return _borrado;
	}

	/**
	 * @param _id the _id to set
	 */
	public void set_id(int _id) {
		this._id = _id;
	}
	
	/**
	 * @param _id_comuna the _id_comuna to set
	 */
	public void set_id_comuna(int _id_comuna) {
		this._id_comuna = _id_comuna;
	}

	/**
	 * @param _alias the _alias to set
	 */
	public void set_alias(String _alias) {
		this._alias = _alias;
	}

	/**
	 * @param _contrasena the _contrasena to set
	 */
	public void set_contrasena(String _contrasena) {
		this._contrasena = _contrasena;
	}

	/**
	 * @param _nombre the _nombre to set
	 */
	public void set_nombre(String _nombre) {
		this._nombre = _nombre;
	}

	/**
	 * @param _apellido_paterno the _apellido_paterno to set
	 */
	public void set_apellido_paterno(String _apellido_paterno) {
		this._apellido_paterno = _apellido_paterno;
	}

	/**
	 * @param _apellido_materno the _apellido_materno to set
	 */
	public void set_apellido_materno(String _apellido_materno) {
		this._apellido_materno = _apellido_materno;
	}

	/**
	 * @param _movil the _movil to set
	 */
	public void set_movil(String _movil) {
		this._movil = _movil;
	}

	/**
	 * @param _rut the _rut to set
	 */
	public void set_rut(String _rut) {
		this._rut = _rut;
	}

	/**
	 * @param _correo the _correo to set
	 */
	public void set_correo(String _correo) {
		this._correo = _correo;
	}

	/**
	 * @param _correo the _correo_opcional to set
	 */
	public void set_correo_opcional(String _correo_opcional) {
		this._correo_opcional = _correo_opcional;
	}

	/**
	 * @param _fecha_nacimiento the _fecha_nacimiento to set
	 */
	public void set_fecha_nacimiento(String _fecha_nacimiento) {
		this._fecha_nacimiento = _fecha_nacimiento;
	}

	/**
	 * @param _hombre the _hombre to set
	 */
	public void set_hombre(Boolean _hombre) {
		this._hombre = _hombre;
	}

	/**
	 * @param _contactar_contactos_de_contactos the _contactar_contactos_de_contactos to set
	 */
	public void set_contactar_contactos_de_contactos(Boolean _contactar_contactos_de_contactos) {
		this._contactar_contactos_de_contactos = _contactar_contactos_de_contactos;
	}

	/**
	 * @param _contactar_desconocidos the _contactar_desconocidos to set
	 */
	public void set_contactar_desconocidos(Boolean _contactar_desconocidos) {
		this._contactar_desconocidos = _contactar_desconocidos;
	}

	/**
	 * @param _publicar_informacion the _publicar_informacion to set
	 */
	public void set_publicar_informacion(Boolean _publicar_informacion) {
		this._publicar_informacion = _publicar_informacion;
	}

	/**
	 * @param _clave_validacion the _clave_validacion to set
	 */
	public void set_clave_validacion(String _clave_validacion) {
		this._clave_validacion = _clave_validacion;
	}

	/**
	 * @param _direccion the _direccion to set
	 */
	public void set_direccion(String _direccion) {
		this._direccion = _direccion;
	}

	/**
	 * @param _foto the _foto to set
	 */
	public void set_foto(String _foto) {
		this._foto = _foto;
	}

	/**
	 * @param _antecedentes_emergencia the _antecedentes_emergencia to set
	 */
	public void set_antecedentes_emergencia(String _antecedentes_emergencia) {
		this._antecedentes_emergencia = _antecedentes_emergencia;
	}

	/**
	 * @param _validado the _validado to set
	 */
	public void set_validado(Boolean _validado) {
		this._validado = _validado;
	}

	/**
	 * @param _comuna the _comuna to set
	 */
	public void set_comuna(String _comuna) {
		this._comuna = _comuna;
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
	 * @param _parametros the _parametros to set
	 */
	public void set_parametros(String _parametros) {
		this._parametros = _parametros;
	}

	/**
	 * @param _borrado the _borrado to set
	 */
	public void set_borrado(boolean _borrado) {
		this._borrado = _borrado;
	}

	public static Usuario fromRS(ResultSet p_rs) throws SQLException {
		Usuario ret = new Usuario();
		
		try {
			ret.set_id(p_rs.getInt("id"));
			ret.set_id_comuna(p_rs.getInt("id_comuna"));
			ret.set_alias(p_rs.getString("alias"));
			ret.set_contrasena(p_rs.getString("contrasena"));
			ret.set_alias(p_rs.getString("alias"));
			ret.set_nombre(p_rs.getString("nombre"));
			ret.set_apellido_paterno(p_rs.getString("apellido_paterno"));
			ret.set_apellido_materno(p_rs.getString("apellido_materno"));
			ret.set_movil(p_rs.getString("movil"));
			ret.set_rut(p_rs.getString("rut"));
			ret.set_correo(p_rs.getString("correo"));
			ret.set_correo_opcional(p_rs.getString("correo_opcional"));
			ret.set_fecha_nacimiento(p_rs.getString("fecha_nacimiento"));
			ret.set_hombre(p_rs.getBoolean("hombre"));
			ret.set_contactar_contactos_de_contactos(p_rs.getBoolean("contactar_contactos_de_contactos"));
			ret.set_hombre(p_rs.getBoolean("hombre"));
			ret.set_contactar_desconocidos(p_rs.getBoolean("contactar_desconocidos"));
			ret.set_publicar_informacion(p_rs.getBoolean("publicar_informacion"));
			ret.set_clave_validacion(p_rs.getString("clave_validacion"));
			ret.set_direccion(p_rs.getString("direccion"));
			ret.set_foto(p_rs.getString("foto"));
			ret.set_antecedentes_emergencia(p_rs.getString("antecedentes_emergencia"));
			ret.set_validado(p_rs.getBoolean("validado"));
			ret.set_comuna(p_rs.getString("comuna"));
			ret.set_ciudad(p_rs.getString("ciudad"));
			ret.set_region(p_rs.getString("region"));
			ret.set_pais(p_rs.getString("pais"));
			ret.set_latitud(p_rs.getDouble("latitud"));
			ret.set_longitud(p_rs.getDouble("longitud"));
			ret.set_parametros(p_rs.getString("parametros"));		
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
    
	
	public static Usuario getByParameter(Connection p_conn, String p_key, String p_value) throws Exception {
		Usuario ret = null;
		
		String str_sql = _str_sql +
			"  WHERE u." + p_key + " = " + p_value +
			"  LIMIT 0, 1";
		
		//System.out.println(str_sql);
		
		// assume that conn is an already created JDBC connection (see previous examples)
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = p_conn.createStatement();
			//System.out.println("stmt = p_conn.createStatement() ok");
			rs = stmt.executeQuery(str_sql);
			//System.out.println("rs = stmt.executeQuery(str_sql) ok");

			// Now do something with the ResultSet ....
			
			if (rs.next()) {
				//System.out.println("rs.next() ok");
				ret = fromRS(rs);
				//System.out.println("fromRS(rs) ok");
			}
		}
		catch (SQLException ex){
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage() + " sentencia: " + str_sql);
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			
			throw new Exception("Error al obtener registro");
		}
		catch (Exception e){
			// handle any errors
			throw new Exception("Excepcion del tipo " + e.getClass() + " Info: " + e.getMessage());
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
	
	public static Usuario getByAlias(Connection p_conn, String p_alias) throws Exception {
		return getByParameter(p_conn, "alias", "'" + p_alias + "'");
	}
	
	public static Usuario getByEmail(Connection p_conn, String p_correo) throws Exception {
		return getByParameter(p_conn, "correo", "'" + p_correo + "'");
	}
	
	public static Usuario getByOptEmail(Connection p_conn, String p_correo_opcional) throws Exception {
		return getByParameter(p_conn, "correo_opcional", "'" + p_correo_opcional + "'");
	}

	public static Usuario getByMovil(Connection p_conn, String p_movil) throws Exception {
		return getByParameter(p_conn, "movil", "'" + p_movil + "'");
	}

	public static Usuario getById(Connection p_conn, String p_id) throws Exception {
		return getByParameter(p_conn, "id_usuario", p_id);
	}
	
    public static ArrayList<Usuario> seek(Connection p_conn, ArrayList<AbstractMap.SimpleEntry<String, String>> p_parameters, String p_order, String p_direction, int p_offset, int p_limit) throws SQLException {
    	Statement stmt = null;
    	ResultSet rs = null;
    	String str_sql;
    	ArrayList<Usuario> ret;
    	
    	str_sql = "";
    	
		try {
			ArrayList<String> array_clauses = new ArrayList<String>();
			
			ret = new ArrayList<Usuario>();
			
			str_sql = _str_sql;
			
			for (AbstractMap.SimpleEntry<String, String> p : p_parameters) {
				if (p.getKey().equals("id_comunidad")) {
					array_clauses.add("c.id_comunidad = " + p.getValue());
				}
				else if (p.getKey().equals("id_comuna")) {
					array_clauses.add("cm.id_comuna = " + p.getValue());
				}
				else if (p.getKey().equals("latitud_mayor")) {
					array_clauses.add("up.latitud > " + p.getValue());
				}
				else if (p.getKey().equals("latitud_menor")) {
					array_clauses.add("up.latitud < " + p.getValue());
				}
				else if (p.getKey().equals("longitud_mayor")) {
					array_clauses.add("up.longitud > " + p.getValue());
				}
				else if (p.getKey().equals("longitud_menor")) {
					array_clauses.add("up.longitud < " + p.getValue());
				}
				else if (p.getKey().equals("posicion_reciente")) {
					array_clauses.add("up.fecha > DATE_ADD(now(), INTERVAL -" + p.getValue() + " MINUTE)");
				}
				else if (p.getKey().equals("id_distinto")) {
					array_clauses.add("u.id_usuario <> " + p.getValue());
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
    public static UsuarioPosicion getPosicion(Integer p_id_usuario) {
    	
    }
	*/
    public static void setPosicion(Connection p_conn, Integer p_id_usuario, Double p_latitud, Double p_longitud, String p_fecha) throws Exception {
    	
		try {
			UsuarioPosicion up = UsuarioPosicion.getByIdUsuario(p_conn, p_id_usuario);  //new UsuarioPosicion();
			
			if (up == null) {
				up = new UsuarioPosicion();
				
				up.set_id_usuario(p_id_usuario);
			}
			
			up.set_latitud(p_latitud);
			up.set_longitud(p_longitud);
			up.set_fecha(p_fecha);
			
			// esta ya la posicion del usuario?
			if (up.get_id() == null) {
				// hago insert
				up.insert(p_conn);
			}
			else {
				// hago update....
				up.update(p_conn);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
    }

    public int update(Connection p_conn) throws Exception {

    	int ret = -1;
    	Statement stmt = null;

    	String str_sql =
			"  UPDATE usuario" +
			"  SET " +
			"  alias = " + (_alias != null ? "'" + _alias + "'" : "null") + "," +
			"  contrasena = " + (_contrasena != null ? "'" + _contrasena + "'" : "null") + "," +
			"  nombre = " + (_nombre != null ? "'" + _nombre + "'" : "null") + "," +
			"  apellido_paterno = " + (_apellido_paterno != null ? "'" + _apellido_paterno + "'" : "null") + "," +
			"  apellido_materno = " + (_apellido_materno != null ? "'" + _apellido_materno + "'" : "null") + "," +
			"  movil = " + (_movil != null ? "'" + _movil + "'" : "null") + "," +
			"  rut = " + (_rut != null ? "'" + _rut + "'" : "null") + "," +
			"  correo = " + (_correo != null ? "'" + _correo + "'" : "null") + "," +
			"  correo_opcional = " + (_correo_opcional != null ? "'" + _correo_opcional + "'" : "null") + "," +
			"  fecha_nacimiento = " + (_fecha_nacimiento != null ? "STR_TO_DATE('" + _fecha_nacimiento + "', '%d-%m-%Y')" : "null") + "," +
			"  hombre = " + (_hombre != null ? "b'" + (_hombre ? 1 : 0) + "'" : "null") + "," +
			"  contactar_contactos_de_contactos = " + (_contactar_contactos_de_contactos != null ? "b'" + (_contactar_contactos_de_contactos ? 1 : 0) + "'" : "null") + "," +
			"  contactar_desconocidos = " + (_contactar_desconocidos != null ? "b'" + (_contactar_desconocidos ? 1 : 0) + "'" : "null") + "," +
			"  leido = " + (_publicar_informacion != null ? "b'" + (_publicar_informacion ? 1 : 0) + "'" : "null") + "," +
			"  clave_validacion = " + (_clave_validacion != null ? "'" + _clave_validacion + "'" : "null") + "," +
			"  direccion = " + (_direccion != null ? "'" + _direccion + "'" : "null") + "," +
			"  foto = " + (_foto != null ? "'" + _foto + "'" : "null") + "," +
			"  antecedentes_emergencia = " + (_antecedentes_emergencia != null ? "'" + _antecedentes_emergencia + "'" : "null") + "," +
			"  validado = " + (_validado != null ? "b'" + (_validado ? 1 : 0) + "'" : "null") +
			"  WHERE id_usuario = " + Integer.toString(this._id);
		
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
			"  INSERT INTO usuario" +
			"  (" +
			"  id_comuna_FK," +
			"  alias," +
			"  contrasena," +
			"  nombre," +
			"  apellido_paterno," +
			"  apellido_materno," +
			"  movil," +
			"  rut," +
			"  correo," +
			"  correo_opcional," +
			"  fecha_nacimiento," +
			"  hombre," +
			"  contactar_contactos_de_contactos," +
			"  contactar_desconocidos," +
			"  publicar_informacion," +
			"  clave_validacion," +
			"  direccion," +
			"  foto," +
			"  antecedentes_emergencia" +
			"  )" +
			"  VALUES" +
			"  (" +
			"  " + (_id_comuna != null ? "'" + _id_comuna + "'" : "null") + "," +
			"  " + (_alias != null ? "'" + _alias + "'" : "null") + "," +
			"  " + (_contrasena != null ? "'" + _contrasena + "'" : "null") + "," +
			"  " + (_nombre != null ? "'" + _nombre + "'" : "null") + "," +
			"  " + (_apellido_paterno != null ? "'" + _apellido_paterno + "'" : "null") + "," +
			"  " + (_apellido_materno != null ? "'" + _apellido_materno + "'" : "null") + "," +
			"  " + (_movil != null ? "'" + _movil + "'" : "null") + "," +
			"  " + (_rut != null ? "'" + _rut + "'" : "null") + "," +
			"  " + (_correo != null ? "'" + _correo + "'" : "null") + "," +
			"  " + (_correo_opcional != null ? "'" + _correo_opcional + "'" : "null") + "," +
			"  " + (_fecha_nacimiento != null ? "STR_TO_DATE('" + _fecha_nacimiento + "', '%d-%m-%Y')" : "null") + "," +
			"  " + (_hombre != null ? "b'" + (_hombre ? 1 : 0) + "'" : "null") + "," +
			"  " + (_contactar_contactos_de_contactos != null ? "b'" + (_contactar_contactos_de_contactos ? 1 : 0) + "'" : "null") + "," +
			"  " + (_contactar_desconocidos != null ? "b'" + (_contactar_desconocidos ? 1 : 0) + "'" : "null") + "," +
			"  " + (_publicar_informacion != null ? "b'" + (_publicar_informacion ? 1 : 0) + "'" : "null") + "," +
			"  " + (_clave_validacion != null ? "'" + _clave_validacion + "'" : "null") + "," +
			"  " + (_direccion != null ? "'" + _direccion + "'" : "null") + "," +
			"  " + (_foto != null ? "'" + _foto + "'" : "null") + "," +
			"  " + (_antecedentes_emergencia != null ? "'" + _antecedentes_emergencia + "'" : "null") +
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
			//System.out.println("Key returned from getGeneratedKeys():" + _id.toString());
			
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Usuario [_id=" + _id + ", _alias=" + _alias + ", _nombre="
				+ _nombre + ", _apellido_paterno=" + _apellido_paterno
				+ ", _movil=" + _movil + ", _correo=" + _correo + ", _latitud="
				+ _latitud + ", _longitud=" + _longitud + "]";
	}
	
}