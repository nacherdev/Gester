package com.example.gester.dao;

import com.example.gester.dao.models.Cita;
import com.example.gester.dao.models.Servicio;
import com.example.gester.dao.models.Usuario;
import com.example.gester.dao.models.Notificacion;
import com.example.gester.BuildConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Dao {

    private String dbName;
    private String dbUser;
    private String dbPass;

    public Dao() { }

    public Dao(String dbName, String dbUser, String dbPass) {
        this.dbName = dbName.trim();
        this.dbUser = dbUser.trim();
        this.dbPass = dbPass.trim();
    }

    public String getDbName() { return dbName; }
    public void setDbName(String dbName) { this.dbName = dbName; }
    public String getDbUser() { return dbUser; }
    public void setDbUser(String dbUser) { this.dbUser = dbUser; }
    public String getDbPass() { return dbPass; }
    public void setDbPass(String dbPass) { this.dbPass = dbPass; }

    public Connection conectar() throws SQLException {
        String url = "jdbc:mysql://nacherdev.es:3306/" + dbName
                + "?connectTimeout=5000&socketTimeout=5000&autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true";
        return DriverManager.getConnection(url, dbUser, dbPass);
    }

    public int existeBaseDeDatos(String name) {
        String url = "jdbc:mysql://nacherdev.es:3306/?connectTimeout=5000&socketTimeout=5000&useSSL=false&allowPublicKeyRetrieval=true";

        try (Connection c = DriverManager.getConnection(url, BuildConfig.DB_ADMIN_USER, BuildConfig.DB_ADMIN_PASS);
             Statement stmt = c.createStatement()) {

            try {
                stmt.execute("USE " + name);
                return 0;
            } catch (SQLException e) {
                int errorCode = e.getErrorCode();
                String sqlState = e.getSQLState();
                if (errorCode == 1049 || "42000".equals(sqlState) || "3D000".equals(sqlState)) {
                    return -3;
                }
                throw e;
            }

        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.startsWith("08")) {
                return -2;
            } else {
                return -4;
            }
        }
    }

    public int probarConexion() {
        int res = existeBaseDeDatos(this.dbName);

        if (res == -2 || res == -3) {
            return res;
        }

        try (Connection c = conectar()) {
            return 0;
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.startsWith("28")) {
                return -1;
            } else if (sqlState != null && sqlState.startsWith("08")) {
                return -2;
            } else {
                return -4;
            }
        }
    }

    public static boolean crearBaseDeDatos(String dbName, String dbUser, String dbPass) {
        String url = "jdbc:mysql://nacherdev.es:3306/?connectTimeout=5000&socketTimeout=5000&useSSL=false&allowPublicKeyRetrieval=true";
        try (Connection c = DriverManager.getConnection(url, "usuario_creador", "KeriPayosen67");
             Statement st = c.createStatement()) {

            st.execute("DROP DATABASE IF EXISTS " + dbName);
            st.execute("CREATE DATABASE " + dbName);

            st.execute("DROP USER IF EXISTS '" + dbUser + "'@'%'");
            st.execute("CREATE USER '" + dbUser + "'@'%' IDENTIFIED BY '" + dbPass + "'");

            st.execute("GRANT ALL PRIVILEGES ON " + dbName + ".* TO '" + dbUser + "'@'%'");
            st.execute("FLUSH PRIVILEGES");

            String sqlUsuarios = "CREATE TABLE " + dbName + ".usuarios (\n" +
                    "\tid int PRIMARY KEY auto_increment,\n" +
                    "    nombre varchar(100) NOT NULL,\n" +
                    "    apellidos varchar(255) NOT NULL,\n" +
                    "    DNI varchar(9) NOT NULL UNIQUE,\n" +
                    "    fecha_nacimiento varchar(100) NOT NULL\n" +
                    ");";
            st.execute(sqlUsuarios);

            String sqlServicios = "CREATE TABLE " + dbName + ".servicios (\n" +
                    "\tid int primary key auto_increment,\n" +
                    "    nombre_servicio varchar(100) NOT NULL,\n" +
                    "    duracion int NOT NULL,\n" +
                    "    precio double(10,2) NOT NULL\n" +
                    ");";
            st.execute(sqlServicios);

            String sqlCitas = "CREATE TABLE " + dbName + ".citas (\n" +
                    "\tid int PRIMARY key auto_increment,\n" +
                    "    id_usuario int NOT NULL,\n" +
                    "    id_servicio int NOT NULL,\n" +
                    "    fecha varchar(100) NOT NULL,\n" +
                    "    hora varchar(100) not null,\n" +
                    "    estado boolean default true,\n" +
                    "    fecha_creacion varchar(100),\n" +
                    "    constraint fk_usuario_cita FOREIGN KEY (id_usuario) REFERENCES " + dbName + ".usuarios(id),\n" +
                    "    constraint fk_servicio_cita FOREIGN KEY (id_servicio) REFERENCES " + dbName + ".servicios(id)\n" +
                    ");";
            st.execute(sqlCitas);

            String sqlNotificaciones = "CREATE TABLE " + dbName + ".notificaciones (\n" +
                    "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                    "    titulo VARCHAR(100) NOT NULL,\n" +
                    "    mensaje TEXT NOT NULL,\n" +
                    "    fecha_envio DATETIME NOT NULL default current_timestamp\n" +
                    ");";
            st.execute(sqlNotificaciones);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Cita> todasLasCitas() {
        ArrayList<Cita> listaCitas = new ArrayList<>();
        String sql = "SELECT c.*, " +
                "u.nombre, u.apellidos, u.DNI, u.fecha_nacimiento, " +
                "s.nombre_servicio, s.duracion, s.precio " +
                "FROM citas c " +
                "INNER JOIN usuarios u ON c.id_usuario = u.id " +
                "INNER JOIN servicios s ON c.id_servicio = s.id";

        try (Connection con = conectar();
             Statement st = con != null ? con.createStatement() : null;
             ResultSet rs = st != null ? st.executeQuery(sql) : null) {

            if (rs == null) return listaCitas;

            while (rs.next()) {
                Usuario u = new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("DNI"),
                        rs.getString("fecha_nacimiento")
                );

                Servicio s = new Servicio(
                        rs.getInt("id_servicio"),
                        rs.getString("nombre_servicio"),
                        rs.getInt("duracion"),
                        rs.getDouble("precio")
                );

                listaCitas.add(new Cita(
                        rs.getInt("id"),
                        u,
                        s,
                        rs.getString("fecha"),
                        rs.getString("hora"),
                        rs.getBoolean("estado"),
                        rs.getString("fecha_creacion")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaCitas;
    }

    public ArrayList<Cita> getCitasActivas() {
        ArrayList<Cita> listaCitas = new ArrayList<>();
        String sql = "SELECT c.*, " +
                "u.nombre, u.apellidos, u.DNI, u.fecha_nacimiento, " +
                "s.nombre_servicio, s.duracion, s.precio " +
                "FROM citas c " +
                "INNER JOIN usuarios u ON c.id_usuario = u.id " +
                "INNER JOIN servicios s ON c.id_servicio = s.id" +
                " WHERE estado = 1";

        try (Connection con = conectar();
             Statement st = con != null ? con.createStatement() : null;
             ResultSet rs = st != null ? st.executeQuery(sql) : null) {

            if (rs == null) return listaCitas;

            while (rs.next()) {
                Usuario u = new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("DNI"),
                        rs.getString("fecha_nacimiento")
                );

                Servicio s = new Servicio(
                        rs.getInt("id_servicio"),
                        rs.getString("nombre_servicio"),
                        rs.getInt("duracion"),
                        rs.getDouble("precio")
                );

                listaCitas.add(new Cita(
                        rs.getInt("id"),
                        u,
                        s,
                        rs.getString("fecha"),
                        rs.getString("hora"),
                        rs.getBoolean("estado"),
                        rs.getString("fecha_creacion")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaCitas;
    }

    public ArrayList<Usuario> todosLosUsuarios() {
        ArrayList<Usuario> listaUsuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";

        try (Connection con = conectar();
             Statement st = con != null ? con.createStatement() : null;
             ResultSet rs = st != null ? st.executeQuery(sql) : null) {

            if (rs == null) return listaUsuarios;

            while (rs.next()) {
                listaUsuarios.add(new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("DNI"),
                        rs.getString("fecha_nacimiento")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaUsuarios;
    }

    public ArrayList<Servicio> todosLosServicios() {
        ArrayList<Servicio> listaServicios = new ArrayList<>();
        String sql = "SELECT * FROM servicios";

        try (Connection con = conectar();
             Statement st = con != null ? con.createStatement() : null;
             ResultSet rs = st != null ? st.executeQuery(sql) : null) {

            if (rs == null) return listaServicios;

            while (rs.next()) {
                listaServicios.add(new Servicio(
                        rs.getInt("id"),
                        rs.getString("nombre_servicio"),
                        rs.getInt("duracion"),
                        rs.getDouble("precio")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaServicios;
    }

    public int buscarIdUsuarioPorDni(String dni) {
        if (dni != null) {
            dni = dni.trim();
        }
        String sql = "SELECT id FROM usuarios WHERE DNI = ?";
        try (Connection con = conectar();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (ps == null) return -1;
            ps.setString(1, dni);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean crearUsuario(String nombre, String apellidos, String DNI, String fechaNacimiento) {
        String sqlInsert = "INSERT INTO usuarios (nombre, apellidos ,DNI, fecha_nacimiento) VALUES (?, ?, ?, ?)";

        try (Connection con = conectar();
             PreparedStatement psIn = con != null ? con.prepareStatement(sqlInsert) : null) {

            if (psIn == null) return false;
            psIn.setString(1, nombre);
            psIn.setString(2, apellidos);
            psIn.setString(3, DNI);
            psIn.setString(4, fechaNacimiento);
            return psIn.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int buscarIdCita(int idUsuario, String fecha, String hora) {
        String sql = "SELECT id FROM citas WHERE id_usuario = ? AND fecha = ? AND hora = ?";

        try (Connection con = conectar();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (ps == null) return -1;
            ps.setInt(1, idUsuario);
            ps.setString(2, fecha);
            ps.setString(3, hora);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean crearCita(int idUsuario, int idServicio, String fecha, String hora, String fechaCreacion) {
        String sqlInsert = "INSERT INTO citas (id_usuario, id_servicio, fecha, hora, fecha_creacion) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = conectar();
             PreparedStatement psIn = con != null ? con.prepareStatement(sqlInsert) : null) {

            if (psIn == null) return false;
            psIn.setInt(1, idUsuario);
            psIn.setInt(2, idServicio);
            psIn.setString(3, fecha);
            psIn.setString(4, hora);
            psIn.setString(5, fechaCreacion);

            return psIn.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Cita> obtenerCitasPorFecha(String fecha) {
        ArrayList<Cita> lista = new ArrayList<>();
        String sql = "SELECT c.id AS id_cita, c.fecha, c.hora, c.estado, c.fecha_creacion, " +
                "u.id AS id_usuario, u.nombre AS nombre_usuario, u.apellidos AS apellidos_usuario, u.DNI, u.fecha_nacimiento, " +
                "s.id AS id_servicio, s.nombre_servicio, s.duracion, s.precio " +
                "FROM citas c " +
                "INNER JOIN usuarios u ON c.id_usuario = u.id " +
                "INNER JOIN servicios s ON c.id_servicio = s.id " +
                "WHERE c.fecha = ?";

        try (Connection con = conectar();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (ps == null) return lista;
            ps.setString(1, fecha);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuario usuario = new Usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("nombre_usuario"),
                            rs.getString("apellidos_usuario"),
                            rs.getString("DNI"),
                            rs.getString("fecha_nacimiento")
                    );

                    Servicio servicio = new Servicio(
                            rs.getInt("id_servicio"),
                            rs.getString("nombre_servicio"),
                            rs.getInt("duracion"),
                            rs.getDouble("precio")
                    );

                    Cita cita = new Cita(
                            rs.getInt("id_cita"),
                            usuario,
                            servicio,
                            rs.getString("fecha"),
                            rs.getString("hora"),
                            rs.getBoolean("estado"),
                            rs.getString("fecha_creacion")
                    );

                    lista.add(cita);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public ArrayList<String> obtenerHorasOcupadasPorFecha(String fecha) {
        ArrayList<String> horasOcupadas = new ArrayList<>();
        String sql = "SELECT hora AS hora FROM citas WHERE fecha = ? AND estado = 1";

        try (Connection con = conectar();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (ps == null) return horasOcupadas;
            ps.setString(1, fecha);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    horasOcupadas.add(rs.getString("hora"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return horasOcupadas;
    }

    public boolean registrarNotificacion(String titulo, String mensaje) {
        String sql = "INSERT INTO notificaciones (titulo, mensaje) VALUES (?, ?)";
        try (Connection con = conectar();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (ps == null) return false;
            ps.setString(1, titulo);
            ps.setString(2, mensaje);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Notificacion> obtenerNotificaciones() {
        ArrayList<Notificacion> lista = new ArrayList<>();
        String sql = "SELECT id, titulo, mensaje, DATE_FORMAT(fecha_envio, '%Y-%m-%d %H:%i') AS fecha FROM notificaciones ORDER BY fecha_envio DESC";

        try (Connection con = conectar();
             Statement st = con != null ? con.createStatement() : null;
             ResultSet rs = st != null ? st.executeQuery(sql) : null) {

            if (rs == null) return lista;

            while (rs.next()) {
                lista.add(new Notificacion(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("mensaje"),
                        rs.getString("fecha")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean eliminarNotificacion(int id) {
        String sql = "DELETE FROM notificaciones WHERE id = ?";
        try (Connection con = conectar();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (ps == null) return false;
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarTodasLasNotificacion() {
        String sql = "TRUNCATE TABLE notificaciones";
        try (Connection con = conectar();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {
            if (ps != null) {
                ps.executeUpdate();
                return true;
            } else return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void verificarCitasProximas() {
        String sqlInsert = "INSERT INTO notificaciones (titulo, mensaje) " +
                "SELECT 'Cita Próxima', CONCAT('Recordatorio: El cliente ', u.nombre, ' tiene una cita mañana a las ', SUBSTRING(c.hora, 1, 5)) " +
                "FROM citas c " +
                "INNER JOIN usuarios u ON c.id_usuario = u.id " +
                "WHERE c.fecha = DATE_ADD(CURDATE(), INTERVAL 1 DAY) AND c.estado = 1 " +
                "AND NOT EXISTS ( " +
                "    SELECT 1 FROM notificaciones n " +
                "    WHERE n.titulo = 'Cita Próxima' " +
                "    AND n.mensaje LIKE CONCAT('%', u.nombre, '%') " +
                "    AND n.mensaje LIKE CONCAT('%', SUBSTRING(c.hora, 1, 5), '%') " +
                "    AND DATE(n.fecha_envio) = CURDATE() " +
                ")";
        try (Connection con = conectar();
             Statement st = con != null ? con.createStatement() : null) {

            if (st != null) {
                st.executeUpdate(sqlInsert);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Cita obtenerCitaPorId(int idCita) {
        String sql = "SELECT c.id, c.id_usuario, c.id_servicio, c.fecha, c.hora, c.estado, c.fecha_creacion, " +
                "u.nombre AS usr_nombre, u.apellidos AS usr_apellidos, u.DNI AS usr_dni, u.fecha_nacimiento AS usr_nacimiento, " +
                "s.nombre_servicio AS srv_nombre, s.duracion AS srv_duracion, s.precio AS srv_precio " +
                "FROM citas c " +
                "JOIN usuarios u ON c.id_usuario = u.id " +
                "JOIN servicios s ON c.id_servicio = s.id " +
                "WHERE c.id = ?";
        try (Connection con = conectar();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (ps == null) return null;
            ps.setInt(1, idCita);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("usr_nombre"),
                            rs.getString("usr_apellidos"),
                            rs.getString("usr_dni"),
                            rs.getString("usr_nacimiento")
                    );
                    Servicio servicio = new Servicio(
                            rs.getInt("id_servicio"),
                            rs.getString("srv_nombre"),
                            rs.getInt("srv_duracion"),
                            rs.getDouble("srv_precio")
                    );
                    return new Cita(
                            rs.getInt("id"),
                            usuario,
                            servicio,
                            rs.getString("fecha"),
                            rs.getString("hora"),
                            rs.getBoolean("estado"),
                            rs.getString("fecha_creacion")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean actualizarCita(int idCita, int idServicio, String fecha, String hora) {
        String sql = "UPDATE citas SET id_servicio = ?, fecha = ?, hora = ? WHERE id = ?";
        try (Connection con = conectar();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (ps == null) return false;
            ps.setInt(1, idServicio);
            ps.setString(2, fecha);
            ps.setString(3, hora);
            ps.setInt(4, idCita);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarCita(int idCita) {
        String sql = "UPDATE citas SET estado = 0 WHERE id = ?";
        try (Connection con = conectar();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (ps == null) return false;
            ps.setInt(1, idCita);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Usuario buscarPorDni(String dni) {
        if (dni != null) {
            dni = dni.trim().toUpperCase();
        }
        String sql = "SELECT * FROM usuarios WHERE DNI = ?";

        try (Connection con = conectar();
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (ps == null) return null;
            ps.setString(1, dni);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String fechaBBDD = rs.getString("fecha_nacimiento");
                    String fechaFormateada = "";

                    if (fechaBBDD != null && fechaBBDD.contains("-")) {
                        String[] partes = fechaBBDD.split("-");
                        if (partes.length == 3) {
                            fechaFormateada = partes[2] + "/" + partes[1] + "/" + partes[0];
                        } else {
                            fechaFormateada = fechaBBDD;
                        }
                    } else {
                        fechaFormateada = fechaBBDD;
                    }

                    return new Usuario(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("apellidos"),
                            rs.getString("DNI"),
                            fechaFormateada
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}