package ni.edu.uam.evaluacion_javafx_citastecnicas.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Cita {
    private Integer id;
    private Cliente cliente;
    private LocalDate fecha;
    private LocalTime hora;
    private String tipo_servicio;
    private String descripcion;
    private String estado;

    public Cita() {
    }

    public Cita(Integer id, Cliente cliente, LocalDate fecha, LocalTime hora, String tipo_servicio, String descripcion, String estado) {
        this.id = id;
        this.cliente = cliente;
        this.fecha = fecha;
        this.hora = hora;
        this.tipo_servicio = tipo_servicio;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public String getTipo_servicio() {
        return tipo_servicio;
    }

    public void setTipo_servicio(String tipo_servicio) {
        this.tipo_servicio = tipo_servicio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
