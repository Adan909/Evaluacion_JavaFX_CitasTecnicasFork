package ni.edu.uam.evaluacion_javafx_citastecnicas.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionDB {
    // aquí se configuran las credenciales locales para poder ejecutar la conexión, revisar documentación
    private static final String URL = "jdbc:postgresql://localhost:5432/gestion_citas";
    private static final String USER = "postgres";
    private static final String PASSWORD = "password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // metodo para probar la conexión con un SELECT 1, para revisar si existen registros
    public static boolean probarConexion() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            return stmt.execute("SELECT 1");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}