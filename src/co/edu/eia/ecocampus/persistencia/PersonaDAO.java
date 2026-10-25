package co.edu.eia.ecocampus.persistencia;

import co.edu.eia.ecocampus.modelo.Persona;
import co.edu.eia.ecocampus.modelo.UsuarioCampus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {

    public void guardar(Persona persona) throws PersistenciaException {
        // Sentencia MERGE compatible con MS SQL Server (Insert / Update)
        String sql = "MERGE INTO personas AS target "
                + "USING (SELECT ? AS id, ? AS nombre, ? AS correo, ? AS tipoVinculacion) AS source "
                + "ON (target.id = source.id) "
                + "WHEN MATCHED THEN "
                + "    UPDATE SET target.nombre = source.nombre, target.correo = source.correo, target.tipoVinculacion = source.tipoVinculacion "
                + "WHEN NOT MATCHED THEN "
                + "    INSERT (id, nombre, correo, tipoVinculacion) VALUES (source.id, source.nombre, source.correo, source.tipoVinculacion);";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String vinculo = (persona instanceof UsuarioCampus) ? ((UsuarioCampus) persona).getTipoVinculacion() : "General";

            pstmt.setString(1, persona.getId());
            pstmt.setString(2, persona.getNombre());
            pstmt.setString(3, persona.getCorreo());
            pstmt.setString(4, vinculo);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al guardar la persona en SQL Server", e);
        }
    }

    public List<Persona> cargarTodos() throws PersistenciaException {
        List<Persona> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, correo, tipoVinculacion FROM personas";

        try (Connection conn = ConexionBD.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String id = rs.getString("id");
                String nombre = rs.getString("nombre");
                String correo = rs.getString("correo");
                String vinculo = rs.getString("tipoVinculacion");

                if (vinculo == null) vinculo = "Estudiante";

                lista.add(new UsuarioCampus(id, nombre, correo, vinculo));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al cargar las personas desde SQL Server", e);
        }
        return lista;
    }
    public void eliminar(String id) throws PersistenciaException {
        String sql = "DELETE FROM personas WHERE id = ?";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new PersistenciaException("Error al eliminar el usuario de la base de datos", e);
        }
    }
}