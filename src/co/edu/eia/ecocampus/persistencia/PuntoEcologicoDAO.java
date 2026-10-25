package co.edu.eia.ecocampus.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import co.edu.eia.ecocampus.modelo.PuntoEcologico;

public class PuntoEcologicoDAO {

    public void guardar(PuntoEcologico punto) throws PersistenciaException {
        String sql = "INSERT OR REPLACE INTO puntos_ecologicos(id, nombre, ubicacion, capacidad) VALUES(?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, punto.getId());
            pstmt.setString(2, punto.getCodigo()); // Usamos getCodigo() como identificador/nombre
            pstmt.setString(3, punto.getUbicacion());
            pstmt.setInt(4, punto.getCapacidad());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al guardar el punto ecológico en la base de datos", e);
        }
    }

    public List<PuntoEcologico> cargarTodos() throws PersistenciaException {
        List<PuntoEcologico> lista = new ArrayList<>();
        String sql = "SELECT * FROM puntos_ecologicos";

        try (Connection conn = ConexionBD.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String codigo = rs.getString("id");
                String ubicacion = rs.getString("ubicacion");
                int capacidad = rs.getInt("capacidad");

                // Invoca el constructor correcto de 3 parámetros
                lista.add(new PuntoEcologico(codigo, ubicacion, capacidad));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al cargar los puntos ecológicos", e);
        }
        return lista;
    }
}