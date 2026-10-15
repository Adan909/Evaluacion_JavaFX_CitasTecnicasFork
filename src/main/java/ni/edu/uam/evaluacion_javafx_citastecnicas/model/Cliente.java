package ni.edu.uam.evaluacion_javafx_citastecnicas.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Cliente {
    private Integer id;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String correo;
    private Boolean activo;
}
