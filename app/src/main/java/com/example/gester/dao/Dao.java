package com.example.gester.dao;

import com.example.gester.dao.models.Cita;
import com.example.gester.dao.models.Servicio;
import com.example.gester.dao.models.Usuario;
import com.example.gester.dao.models.Notificacion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Dao {

    private Connection con = null;
    private String dbName;
    private String dbUser;
    private String dbPass;

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

    public boolean conectar() {
        try {
            if (con != null && !con.isClosed()) {
                return true;
            }
            Class.forName("com.mysql.jdbc.Driver");

            String url = "jdbc:mysql://nacherdev.es:3306/" + dbName
                    + "?connectTimeout=5000&socketTimeout=5000&autoReconnect=true";

            con = DriverManager.getConnection(url, dbUser, dbPass);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean desconectar() {
        if (con != null) {
            try {
                con.close();
                return true;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    public ArrayList<Cita> todasLasCitas() {
        conectar();
        ArrayList<Cita> listaCitas = new ArrayList<>();
        if (!conectar()) return listaCitas;

        String sql = "SELECT c.*, " +
                "u.nombre, u.apellidos, u.DNI, u.fecha_nacimiento, " +
                "s.nombre_servicio, s.duracion, s.precio " +
                "FROM citas c " +
                "INNER JOIN usuarios u ON c.id_usuario = u.id " +
                "INNER JOIN servicios s ON c.id_servicio = s.id";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

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
        } finally {
            desconectar();
        }
        return listaCitas;
    }

    public ArrayList<Usuario> todosLosUsuarios() {
        conectar();
        ArrayList<Usuario> listaUsuarios = new ArrayList<>();
        if (!conectar()) return listaUsuarios;

        String sql = "SELECT * FROM usuarios";

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while ( rs.next() ) {
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
        } finally {
            desconectar();
        }
        return listaUsuarios;
    }

    public ArrayList<Servicio> todosLosServicios() {
        ArrayList<Servicio> listaServicios = new ArrayList<>();
        if (!conectar()) return listaServicios;

        String sql = "SELECT * FROM servicios";

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
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
        } finally {
            try {
                if (con != null && !con.isClosed()) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listaServicios;
    }

    public int buscarIdUsuarioPorDni(String dni) {
        if (dni != null) {
            dni = dni.trim();
        }
        conectar();
        String sql = "SELECT id FROM usuarios WHERE DNI = ?";
        try (PreparedStatement ps = con.prepareStatement(sql);) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            desconectar();
        }

    }

    public boolean crearUsuario(String nombre, String apellidos, String DNI, String fechaNacimiento) {
        conectar();
        String sqlInsert = "INSERT INTO usuarios (nombre, apellidos ,DNI, fecha_nacimiento) VALUES (?, ?, ?, ?)";

        try (PreparedStatement psIn = con.prepareStatement(sqlInsert);) {
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
        conectar();
        String sql = "SELECT id FROM citas WHERE id_usuario = ? AND fecha = ? AND hora = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

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
        } finally {
            desconectar();
        }
    }

    public boolean crearCita(int idUsuario, int idServicio, String fecha, String hora, String fechaCreacion) {
        conectar();

        String sqlInsert = "INSERT INTO citas (id_usuario, id_servicio, fecha, hora, fecha_creacion) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement psIn = con.prepareStatement(sqlInsert)) {
            psIn.setInt(1, idUsuario);
            psIn.setInt(2, idServicio);
            psIn.setString(3, fecha);
            psIn.setString(4, hora);
            psIn.setString(5, fechaCreacion);

            int filasAfectadas = psIn.executeUpdate();

            return filasAfectadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            desconectar();
        }
    }

    public ArrayList<Cita> obtenerCitasPorFecha(String fecha) {
        conectar();
        ArrayList<Cita> lista = new ArrayList<>();
        if (!conectar()) return lista;

        String sql = "SELECT c.id AS id_cita, c.fecha, c.hora, c.estado, c.fecha_creacion, " +
                "u.id AS id_usuario, u.nombre AS nombre_usuario, u.apellidos AS apellidos_usuario, u.DNI, u.fecha_nacimiento, " +
                "s.id AS id_servicio, s.nombre_servicio, s.duracion, s.precio " +
                "FROM citas c " +
                "INNER JOIN usuarios u ON c.id_usuario = u.id " +
                "INNER JOIN servicios s ON c.id_servicio = s.id " +
                "WHERE c.fecha = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
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
        } finally {
            desconectar();
        }

        return lista;
    }

    public ArrayList<String> obtenerHorasOcupadasPorFecha(String fecha) {
        conectar();
        ArrayList<String> horasOcupadas = new ArrayList<>();
        if (!conectar()) return horasOcupadas;

        String sql = "SELECT hora AS hora FROM citas WHERE fecha = ? AND estado = 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, fecha);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    horasOcupadas.add(rs.getString("hora"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            desconectar();
        }
        return horasOcupadas;
    }

    public boolean registrarNotificacion(String titulo, String mensaje) {
        if (!conectar()) return false;
        String sql = "INSERT INTO notificaciones (titulo, mensaje) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, titulo);
            ps.setString(2, mensaje);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            desconectar();
        }
    }

    public ArrayList<Notificacion> obtenerNotificaciones() {
        ArrayList<Notificacion> lista = new ArrayList<>();
        if (!conectar()) return lista;
        String sql = "SELECT id, titulo, mensaje, DATE_FORMAT(fecha_envio, '%Y-%m-%d %H:%i') AS fecha FROM notificaciones ORDER BY fecha_envio DESC";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
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
        } finally {
            desconectar();
        }
        return lista;
    }

    public boolean eliminarNotificacion(int id) {
        if (!conectar()) return false;
        String sql = "DELETE FROM notificaciones WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            desconectar();
        }
    }

    public void verificarCitasProximas() {
        if (!conectar()) return;
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
        try (Statement st = con.createStatement()) {
            st.executeUpdate(sqlInsert);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            desconectar();
        }
    }

    public Cita obtenerCitaPorId(int idCita) {
        if (!conectar()) return null;
        String sql = "SELECT c.id, c.id_usuario, c.id_servicio, c.fecha, c.hora, c.estado, c.fecha_creacion, " +
                "u.nombre AS usr_nombre, u.apellidos AS usr_apellidos, u.DNI AS usr_dni, u.fecha_nacimiento AS usr_nacimiento, " +
                "s.nombre_servicio AS srv_nombre, s.duracion AS srv_duracion, s.precio AS srv_precio " +
                "FROM citas c " +
                "JOIN usuarios u ON c.id_usuario = u.id " +
                "JOIN servicios s ON c.id_servicio = s.id " +
                "WHERE c.id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
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
        } finally {
            desconectar();
        }
        return null;
    }

    public boolean actualizarCita(int idCita, int idServicio, String fecha, String hora) {
        if (!conectar()) return false;
        String sql = "UPDATE citas SET id_servicio = ?, fecha = ?, hora = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idServicio);
            ps.setString(2, fecha);
            ps.setString(3, hora);
            ps.setInt(4, idCita);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            desconectar();
        }
    }

    public boolean eliminarCita(int idCita) {
        if (!conectar()) return false;
        String sql = "DELETE FROM citas WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCita);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            desconectar();
        }
    }

    public Usuario buscarPorDni(String dni) {
        if (dni != null) {
            dni = dni.trim().toUpperCase();
        }
        conectar();

        if (!conectar()) return null;

        String sql = "SELECT id, nombre, apellidos, DNI, fecha_nacimiento FROM usuarios WHERE DNI = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
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
        } finally {
            desconectar();
        }
        return null;
    }
}