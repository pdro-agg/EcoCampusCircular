package co.edu.eia.ecocampus.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EliminacionDAO {

    /**
     * Elimina un registro de cualquier tabla (personas, puntos_ecologicos, reportes, rutas, campanas)
     * utilizando su ID principal.
     * 
     * @param tabla Nombre de la tabla en SQL Server
     * @param id Identificador único del registro a eliminar
     * @throws PersistenciaException Si ocurre un error durante la ejecución de la consulta T-SQL
     */
    public void eliminarPorId(String tabla, String id) throws PersistenciaException {
        String sql = "DELETE FROM " + tabla + " WHERE id = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new PersistenciaException("No se encontró ningún registro con el ID «" + id + "» en " + tabla);
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error al eliminar el registro en la tabla " + tabla + ": " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un operador de la tabla personas asegurando que corresponda al tipo de vinculación.
     */
    public void eliminarOperador(String idOperador) throws PersistenciaException {
        String sql = "DELETE FROM personas WHERE id = ? AND tipoVinculacion = 'Operador'";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, idOperador);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new PersistenciaException("Error al eliminar el operador de la base de datos", e);
        }
    }

    /**
     * Elimina un punto ecológico por su ID.
     */
    public void eliminarPuntoEcologico(String idPunto) throws PersistenciaException {
        eliminarPorId("puntos_ecologicos", idPunto);
    }

    /**
     * Elimina un reporte por su ID.
     */
    public void eliminarReporte(String idReporte) throws PersistenciaException {
        eliminarPorId("reportes", idReporte);
    }

    /**
     * Elimina una ruta por su ID.
     */
    public void eliminarRuta(String idRuta) throws PersistenciaException {
        eliminarPorId("rutas", idRuta);
    }

    /**
     * Elimina una campaña por su ID.
     */
    public void eliminarCampana(String idCampana) throws PersistenciaException {
        eliminarPorId("campanas", idCampana);
    }

    /**
     * Descuenta eco-puntos a un usuario basándose en el peso/kilos o cantidad a restar.
     * Utiliza la cláusula CASE de T-SQL para prevenir que los puntos queden en números negativos.
     * 
     * @param idUsuario Identificación de la persona
     * @param puntosADescontar Cantidad de puntos a restar (calculados por peso)
     */
    public void descontarEcoPuntos(String idUsuario, int puntosADescontar) throws PersistenciaException {
        String sql = "UPDATE personas SET ecoPuntos = CASE "
                + "WHEN (ecoPuntos - ?) < 0 THEN 0 "
                + "ELSE (ecoPuntos - ?) "
                + "END WHERE id = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, puntosADescontar);
            pstmt.setInt(2, puntosADescontar);
            pstmt.setString(3, idUsuario);
            
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new PersistenciaException("No se encontró al usuario con ID «" + idUsuario + "»");
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error al descontar eco-puntos en SQL Server: " + e.getMessage(), e);
        }
    }
}