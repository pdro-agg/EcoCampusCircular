package co.edu.eia.ecocampus.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionBD {

    private static final String SERVIDOR = "localhost";
    private static final String PUERTO = "1433";
    private static final String BASE_DATOS = "EcoCampusDB";
    private static final String USUARIO = "sa"; 
    private static final String CLAVE = "PAGirard2007*"; // La clave que le asignaste a sa

    // Se remueve integratedSecurity=true para autenticar mediante usuario/clave SQL
    private static final String URL = "jdbc:sqlserver://" + SERVIDOR + ":" + PUERTO 
            + ";databaseName=" + BASE_DATOS 
            + ";encrypt=true;trustServerCertificate=true;";

    public static Connection obtenerConexion() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(URL, USUARIO, CLAVE);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC de MS SQL Server no encontrado en el Build Path.", e);
        }
    }

    public static void inicializarTablas() {
        String sqlPersonas = "IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'personas') "
                + "CREATE TABLE personas ("
                + "id VARCHAR(50) PRIMARY KEY, "
                + "nombre VARCHAR(100) NOT NULL, "
                + "correo VARCHAR(100) NOT NULL, "
                + "tipoVinculacion VARCHAR(50) NOT NULL"
                + ");";

        String sqlPuntos = "IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'puntos_ecologicos') "
                + "CREATE TABLE puntos_ecologicos ("
                + "id VARCHAR(50) PRIMARY KEY, "
                + "nombre VARCHAR(100), "
                + "ubicacion VARCHAR(100) NOT NULL, "
                + "capacidad INT NOT NULL"
                + ");";

        try (Connection conn = obtenerConexion();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(sqlPersonas);
            stmt.execute(sqlPuntos);
            
            System.out.println(">>> CONECTADO A SQL SERVER: " + BASE_DATOS);

        } catch (SQLException e) {
            System.err.println("Error al conectar e inicializar SQL Server: " + e.getMessage());
        }
    }
}