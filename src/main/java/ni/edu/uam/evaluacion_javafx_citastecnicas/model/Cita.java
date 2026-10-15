package ni.edu.uam.evaluacion_javafx_citastecnicas.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data @NoArgsConstructor @AllArgsConstructor
public class Cita {
    private Integer id;
    private Cliente cliente;
    private LocalDate fecha;
    private LocalTime hora;
    private String tipo_servicio;
    private String descripcion;
    private String estado;
}
