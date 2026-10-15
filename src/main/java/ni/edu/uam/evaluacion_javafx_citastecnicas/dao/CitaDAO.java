package ni.edu.uam.evaluacion_javafx_citastecnicas.dao;

import ni.edu.uam.evaluacion_javafx_citastecnicas.model.Cita;
import ni.edu.uam.evaluacion_javafx_citastecnicas.model.Cliente;
import ni.edu.uam.evaluacion_javafx_citastecnicas.config.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CitaDAO {

    public boolean insertar(Cita cita) throws SQLException {
        String sql = "INSERT INTO cita_tecnica (cliente_id, fecha, hora, tipo_servicio, descripcion, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cita.getCliente().getId());
            pstmt.setDate(2, Date.valueOf(cita.getFecha()));
            pstmt.setTime(3, Time.valueOf(cita.getHora()));
            pstmt.setString(4, cita.getTipo_servicio());
            pstmt.setString(5, cita.getDescripcion());
            pstmt.setString(6, cita.getEstado());
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Cita> obtenerTodas() throws SQLException {
        List<Cita> citas = new ArrayList<>();
        // Hacemos JOIN para traer los datos del cliente, necesario para el TableView
        String sql = "SELECT c.*, cl.nombres, cl.apellidos, cl.telefono, cl.correo, cl.activo " +
                "FROM cita_tecnica c INNER JOIN cliente cl ON c.cliente_id = cl.id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Cliente cliente = new Cliente(
                        rs.getInt("cliente_id"),
                        rs.getString("nombres"),
                        rs.getString("apellidos"),
                        rs.getString("telefono"),
                        rs.getString("correo"),
                        rs.getBoolean("activo")
                );
                citas.add(new Cita(
                        rs.getInt("id"),
                        cliente,
                        rs.getDate("fecha").toLocalDate(),
                        rs.getTime("hora").toLocalTime(),
                        rs.getString("tipo_servicio"),
                        rs.getString("descripcion"),
                        rs.getString("estado")
                ));
            }
        }
        return citas;
    }

    public boolean actualizar(Cita cita) throws SQLException {
        String sql = "UPDATE cita_tecnica SET cliente_id=?, fecha=?, hora=?, tipo_servicio=?, descripcion=?, estado=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cita.getCliente().getId());
            pstmt.setDate(2, Date.valueOf(cita.getFecha()));
            pstmt.setTime(3, Time.valueOf(cita.getHora()));
            pstmt.setString(4, cita.getTipo_servicio());
            pstmt.setString(5, cita.getDescripcion());
            pstmt.setString(6, cita.getEstado());
            pstmt.setInt(7, cita.getId());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM cita_tecnica WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
}