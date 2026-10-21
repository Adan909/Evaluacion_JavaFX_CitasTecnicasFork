package ni.edu.uam.evaluacion_javafx_citastecnicas.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ni.edu.uam.evaluacion_javafx_citastecnicas.dao.CitaDAO;
import ni.edu.uam.evaluacion_javafx_citastecnicas.dao.ClienteDAO;
import ni.edu.uam.evaluacion_javafx_citastecnicas.model.Cita;
import ni.edu.uam.evaluacion_javafx_citastecnicas.model.Cliente;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CitaController implements Initializable {

    @FXML private Button btnActualizar, btnEliminar, btnFiltrar, btnGuardar, btnLimpiar, btnLimpiarFiltros, btnVolver;
    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private ComboBox<String> cmbEstado, cmbFiltroEstado, cmbTipoServicio;
    @FXML private TableColumn<Cita, String> colCliente, colEstado;
    @FXML private TableColumn<Cita, LocalDate> colFecha;
    @FXML private TableColumn<Cita, LocalTime> colHora;
    @FXML private TableColumn<Cita, Integer> colId;
    @FXML private TableColumn<Cita, String> colServicio;
    @FXML private DatePicker dpFecha, dpFiltroFecha;
    @FXML private Label lblContador, lblErrorCliente, lblErrorDescripcion, lblErrorEstado, lblErrorFecha, lblErrorServicio, lblTotal;
    @FXML private Spinner<Integer> spnHora, spnMinuto;
    @FXML private TableView<Cita> tblCitas;
    @FXML private TextArea txtDescripcion;

    private CitaDAO citaDAO = new CitaDAO();
    private ClienteDAO clienteDAO = new ClienteDAO();
    private ObservableList<Cita> listaCitas = FXCollections.observableArrayList();
    private Cita citaSeleccionada;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarColumnas();
        cargarCombos();
        cargarCitas();
        configurarEventos();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colServicio.setCellValueFactory(new PropertyValueFactory<>("tipo_servicio"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Personalizar columna Cliente para mostrar Nombres y Apellidos
        colCliente.setCellValueFactory(cellData -> {
            Cliente c = cellData.getValue().getCliente();
            return new SimpleStringProperty(c.getNombres() + " " + c.getApellidos());
        });
    }

    private void cargarCombos() {
        Thread hiloCombos = new Thread(() -> {
            try {
                var clientesDesdeBD = clienteDAO.obtenerTodos().stream()
                        .filter(Cliente::getActivo)
                        .collect(Collectors.toList());

                Platform.runLater(() -> {
                    ObservableList<Cliente> clientes = FXCollections.observableArrayList(clientesDesdeBD);
                    cmbCliente.setItems(clientes);

                    // Convertidor para mostrar el nombre completo en el ComboBox
                    cmbCliente.setConverter(new StringConverter<Cliente>() {
                        @Override
                        public String toString(Cliente cliente) {
                            return cliente == null ? "" : cliente.getNombres() + " " + cliente.getApellidos();
                        }
                        @Override
                        public Cliente fromString(String string) { return null; }
                    });
                });
            } catch (SQLException e) {
                Platform.runLater(() -> {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error BD", "No se pudieron cargar los clientes: " + e.getMessage());
                });
            }
        });
        hiloCombos.setDaemon(true);
        hiloCombos.start();
    }

    private void cargarCitas() {
        Thread hiloCitas = new Thread(() -> {
            try {
                var citasDesdeBD = citaDAO.obtenerTodas();
                Platform.runLater(() -> {
                    listaCitas.setAll(citasDesdeBD);
                    tblCitas.setItems(listaCitas);
                    actualizarContador();
                });
            } catch (SQLException e) {
                Platform.runLater(() -> {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error BD", "No se pudieron cargar las citas: " + e.getMessage());
                });
            }
        });
        hiloCitas.setDaemon(true);
        hiloCitas.start();
    }

    private void configurarEventos() {
        // Listener para la tabla
        tblCitas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                citaSeleccionada = newSelection;

                // Buscar el cliente en el combo y seleccionarlo (por ID)
                cmbCliente.getItems().stream()
                        .filter(c -> c.getId().equals(citaSeleccionada.getCliente().getId()))
                        .findFirst().ifPresent(cmbCliente.getSelectionModel()::select);

                dpFecha.setValue(citaSeleccionada.getFecha());
                spnHora.getValueFactory().setValue(citaSeleccionada.getHora().getHour());
                spnMinuto.getValueFactory().setValue(citaSeleccionada.getHora().getMinute());
                cmbTipoServicio.setValue(citaSeleccionada.getTipo_servicio());
                cmbEstado.setValue(citaSeleccionada.getEstado());
                txtDescripcion.setText(citaSeleccionada.getDescripcion());
            }
        });

        // Listener para contador de descripción
        txtDescripcion.textProperty().addListener((obs, oldV, newV) -> {
            if (newV.length() > 255) {
                txtDescripcion.setText(oldV);
            } else {
                lblContador.setText(newV.length() + "/255");
            }
        });
    }

    @FXML
    void onGuardar(ActionEvent event) {
        if (!validarFormulario()) return;

        LocalTime horaSeleccionada = LocalTime.of(spnHora.getValue(), spnMinuto.getValue());
        Cita nuevaCita = new Cita(null, cmbCliente.getValue(), dpFecha.getValue(), horaSeleccionada,
                cmbTipoServicio.getValue(), txtDescripcion.getText().trim(), cmbEstado.getValue());

        Thread hiloGuardar = new Thread(() -> {
            try {
                if (citaDAO.insertar(nuevaCita)) {
                    Platform.runLater(() -> {
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Cita registrada correctamente.");
                        onLimpiar(null);
                        cargarCitas();
                    });
                }
            } catch (SQLException e) {
                Platform.runLater(() -> {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error BD", "Error al guardar la cita: " + e.getMessage());
                });
            }
        });
        hiloGuardar.setDaemon(true);
        hiloGuardar.start();
    }

    @FXML
    void onActualizar(ActionEvent event) {
        if (citaSeleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Seleccione una cita de la tabla para actualizar.");
            return;
        }
        if (!validarFormulario()) return;

        citaSeleccionada.setCliente(cmbCliente.getValue());
        citaSeleccionada.setFecha(dpFecha.getValue());
        citaSeleccionada.setHora(LocalTime.of(spnHora.getValue(), spnMinuto.getValue()));
        citaSeleccionada.setTipo_servicio(cmbTipoServicio.getValue());
        citaSeleccionada.setEstado(cmbEstado.getValue());
        citaSeleccionada.setDescripcion(txtDescripcion.getText().trim());

        Cita citaAActualizar = citaSeleccionada;

        Thread hiloActualizar = new Thread(() -> {
            try {
                if (citaDAO.actualizar(citaAActualizar)) {
                    Platform.runLater(() -> {
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Cita actualizada correctamente.");
                        onLimpiar(null);
                        cargarCitas();
                    });
                }
            } catch (SQLException e) {
                Platform.runLater(() -> {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error BD", "Error al actualizar la cita: " + e.getMessage());
                });
            }
        });
        hiloActualizar.setDaemon(true);
        hiloActualizar.start();
    }

    @FXML
    void onEliminar(ActionEvent event) {
        if (citaSeleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Seleccione una cita de la tabla para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar esta cita programada para el " + citaSeleccionada.getFecha() + "?");
        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            int idEliminar = citaSeleccionada.getId();
            Thread hiloEliminar = new Thread(() -> {
                try {
                    if (citaDAO.eliminar(idEliminar)) {
                        Platform.runLater(() -> {
                            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Cita eliminada correctamente.");
                            onLimpiar(null);
                            cargarCitas();
                        });
                    }
                } catch (SQLException e) {
                    Platform.runLater(() -> {
                        mostrarAlerta(Alert.AlertType.ERROR, "Error BD", "Error al eliminar la cita: " + e.getMessage());
                    });
                }
            });
            hiloEliminar.setDaemon(true);
            hiloEliminar.start();
        }
    }

    @FXML
    void onFiltrar(ActionEvent event) {
        String estado = cmbFiltroEstado.getValue();
        LocalDate fecha = dpFiltroFecha.getValue();

        ObservableList<Cita> filtradas = FXCollections.observableArrayList(listaCitas.stream()
                .filter(c -> (estado == null || estado.equals("Todos") || c.getEstado().equals(estado)))
                .filter(c -> (fecha == null || c.getFecha().equals(fecha)))
                .collect(Collectors.toList()));

        tblCitas.setItems(filtradas);
    }

    @FXML
    void onLimpiarFiltros(ActionEvent event) {
        cmbFiltroEstado.setValue("Todos");
        dpFiltroFecha.setValue(null);
        tblCitas.setItems(listaCitas);
    }

    @FXML
    void onLimpiar(ActionEvent event) {
        cmbCliente.setValue(null);
        dpFecha.setValue(null);
        spnHora.getValueFactory().setValue(8);
        spnMinuto.getValueFactory().setValue(0);
        cmbTipoServicio.setValue(null);
        cmbEstado.setValue(null);
        txtDescripcion.clear();
        citaSeleccionada = null;
        tblCitas.getSelectionModel().clearSelection();
        ocultarErrores();
    }

    @FXML
    void onVolver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ni/edu/uam/evaluacion_javafx_citastecnicas/fxml/menu-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("TecnoAsistencia - Menú Principal");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validarFormulario() {
        boolean valido = true;
        ocultarErrores();

        if (cmbCliente.getValue() == null) { lblErrorCliente.setVisible(true); lblErrorCliente.setManaged(true); valido = false; }
        if (dpFecha.getValue() == null) { lblErrorFecha.setVisible(true); lblErrorFecha.setManaged(true); valido = false; }
        if (cmbTipoServicio.getValue() == null) { lblErrorServicio.setVisible(true); lblErrorServicio.setManaged(true); valido = false; }
        if (cmbEstado.getValue() == null) { lblErrorEstado.setVisible(true); lblErrorEstado.setManaged(true); valido = false; }
        if (txtDescripcion.getText().trim().isEmpty()) { lblErrorDescripcion.setVisible(true); lblErrorDescripcion.setManaged(true); valido = false; }

        return valido;
    }

    private void ocultarErrores() {
        lblErrorCliente.setVisible(false); lblErrorCliente.setManaged(false);
        lblErrorFecha.setVisible(false); lblErrorFecha.setManaged(false);
        lblErrorServicio.setVisible(false); lblErrorServicio.setManaged(false);
        lblErrorEstado.setVisible(false); lblErrorEstado.setManaged(false);
        lblErrorDescripcion.setVisible(false); lblErrorDescripcion.setManaged(false);
    }

    private void actualizarContador() {
        lblTotal.setText("Total de citas registradas: " + listaCitas.size());
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}