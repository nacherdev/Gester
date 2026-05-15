package com.example.gester.dao;

import com.example.gester.dao.models.Cita;
import com.example.gester.dao.models.Servicio;
import com.example.gester.dao.models.Usuario;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Dao {

    private Connection con = null;
    private String dbName;
    private String dbUser;
    private String dbPass;

    public Dao(String dbName, String dbUser, String dbPass) {
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPass() {
        return dbPass;
    }

    public void setDbPass(String dbPass) {
        this.dbPass = dbPass;
    }

    public void conectar() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://nacherdev.es:3306/" +dbName, dbUser, dbPass);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Cita> historialDeCitas() {
        conectar();
        ArrayList<Cita> listaCitas = new ArrayList<>();

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
            throw new RuntimeException("Error en la base de datos: " + e.getMessage());
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return listaCitas;
    }

    public ArrayList<Usuario> todosLosUsuarios() {
        conectar();
        ArrayList<Usuario> listaUsuarios = new ArrayList<>();

        String sql = "SELECT * FROM usuarios";

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while( rs.next() ) {
                listaUsuarios.add(new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("DNI"),
                        rs.getString("fecha_nacimiento")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return listaUsuarios;
    }

}
