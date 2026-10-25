package co.edu.eia.ecocampus.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import co.edu.eia.ecocampus.modelo.Campana;
import co.edu.eia.ecocampus.modelo.Reporte;
import co.edu.eia.ecocampus.modelo.Ruta;

public class EcoCampusGeneralDAO {

    // ==================== REPORTES ====================
    public void guardarReporte(Reporte reporte) throws PersistenciaException {
        String sql = "MERGE INTO reportes WITH (HOLDLOCK) AS target "
                + "USING (SELECT ? AS id) AS source ON (target.id = source.id) "
                + "WHEN MATCHED THEN UPDATE SET descripcion = ?, fecha = ? "
                + "WHEN NOT MATCHED THEN INSERT (id, descripcion, fecha) VALUES (?, ?, ?);";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, reporte.getId());
            pstmt.setString(2, reporte.getDescripcion());
            pstmt.setString(3, reporte.getFecha());
            pstmt.setString(4, reporte.getId());
            pstmt.setString(5, reporte.getDescripcion());
            pstmt.setString(6, reporte.getFecha());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new PersistenciaException("Error al guardar el reporte en SQL Server", e);
        }
    }

    public List<Reporte> cargarReportes() throws PersistenciaException {
        List<Reporte> lista = new ArrayList<>();
        String sql = "SELECT id, descripcion, fecha FROM reportes";

        try (Connection conn = ConexionBD.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Reporte(
                        rs.getString("id"),
                        rs.getString("descripcion"),
                        rs.getString("fecha")
                ));
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error al cargar reportes desde SQL Server", e);
        }
        return lista;
    }

    // ==================== RUTAS ====================
    public void guardarRuta(Ruta ruta) throws PersistenciaException {
        String sql = "MERGE INTO rutas WITH (HOLDLOCK) AS target "
                + "USING (SELECT ? AS id) AS source ON (target.id = source.id) "
                + "WHEN MATCHED THEN UPDATE SET nombre = ?, origen = ?, destino = ? "
                + "WHEN NOT MATCHED THEN INSERT (id, nombre, origen, destino) VALUES (?, ?, ?, ?);";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ruta.getId());
            pstmt.setString(2, ruta.getNombre());
            pstmt.setString(3, ruta.getOrigen());
            pstmt.setString(4, ruta.getDestino());
            pstmt.setString(5, ruta.getId());
            pstmt.setString(6, ruta.getNombre());
            pstmt.setString(7, ruta.getOrigen());
            pstmt.setString(8, ruta.getDestino());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new PersistenciaException("Error al guardar la ruta en SQL Server", e);
        }
    }

    public List<Ruta> cargarRutas() throws PersistenciaException {
        List<Ruta> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, origen, destino FROM rutas";

        try (Connection conn = ConexionBD.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Ruta(
                        rs.getString("id"),
                        rs.getString("nombre"),
                        rs.getString("origen"),
                        rs.getString("destino")
                ));
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error al cargar rutas desde SQL Server", e);
        }
        return lista;
    }

    // ==================== CAMPAÑAS ====================
    public void guardarCampana(Campana campana) throws PersistenciaException {
        String sql = "MERGE INTO campanas WITH (HOLDLOCK) AS target "
                + "USING (SELECT ? AS id) AS source ON (target.id = source.id) "
                + "WHEN MATCHED THEN UPDATE SET nombre = ?, metaPuntos = ? "
                + "WHEN NOT MATCHED THEN INSERT (id, nombre, metaPuntos) VALUES (?, ?, ?);";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, campana.getId());
            pstmt.setString(2, campana.getNombre());
            pstmt.setInt(3, campana.getMetaPuntos());
            pstmt.setString(4, campana.getId());
            pstmt.setString(5, campana.getNombre());
            pstmt.setInt(6, campana.getMetaPuntos());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new PersistenciaException("Error al guardar la campaña en SQL Server", e);
        }
    }

    public List<Campana> cargarCampanas() throws PersistenciaException {
        List<Campana> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, metaPuntos FROM campanas";

        try (Connection conn = ConexionBD.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Campana(
                        rs.getString("id"),
                        rs.getString("nombre"),
                        rs.getInt("metaPuntos")
                ));
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error al cargar campañas desde SQL Server", e);
        }
        return lista;
    }
}