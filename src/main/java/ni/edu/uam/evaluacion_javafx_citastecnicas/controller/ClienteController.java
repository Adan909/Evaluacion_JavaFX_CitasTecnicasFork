package ni.edu.uam.evaluacion_javafx_citastecnicas.controller;

import javafx.application.Platform;
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
import ni.edu.uam.evaluacion_javafx_citastecnicas.dao.ClienteDAO;
import ni.edu.uam.evaluacion_javafx_citastecnicas.model.Cliente;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ClienteController implements Initializable {

    @FXML private Button btnActualizar, btnBuscar, btnEliminar, btnGuardar, btnLimpiar, btnLimpiarBusqueda, btnVolver;
    @FXML private CheckBox chkActivo;
    @FXML private TableColumn<Cliente, Boolean> colActivo;
    @FXML private TableColumn<Cliente, String> colApellidos;
    @FXML private TableColumn<Cliente, String> colCorreo;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colNombres;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private Label lblErrorApellidos, lblErrorCorreo, lblErrorNombres, lblErrorTelefono, lblTotal;
    @FXML private TableView<Cliente> tblClientes;
    @FXML private TextField txtApellidos, txtBuscar, txtCorreo, txtNombres, txtTelefono;

    private ClienteDAO clienteDAO = new ClienteDAO();
    private ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();
    private Cliente clienteSeleccionado;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarColumnas();
        cargarClientes();
        configurarEventosTabla();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        // Mostrar "Sí"/"No" en lugar de "true"/"false" en la columna activo
        colActivo.setCellFactory(tc -> new TableCell<Cliente, Boolean>() {
            @Override
            protected void updateItem(Boolean activo, boolean empty) {
                super.updateItem(activo, empty);
                setText(empty || activo == null ? null : (activo ? "Activo" : "Inactivo"));
            }
        });
    }

    private void cargarClientes() {
        // 1. Creamos un nuevo hilo en segundo plano
        Thread hiloCarga = new Thread(() -> {
            try {
                // TAREA PESADA: Consultar la base de datos (se ejecuta fuera del hilo principal)
                var clientesDesdeBD = clienteDAO.obtenerTodos();

                // 2. TAREA GRÁFICA: Actualizar la tabla (obligatorio usar Platform.runLater)
                Platform.runLater(() -> {
                    listaClientes.setAll(clientesDesdeBD);
                    tblClientes.setItems(listaClientes);
                    actualizarContador();
                });

            } catch (SQLException e) {
                // Las alertas también son elementos gráficos, van en runLater
                Platform.runLater(() -> {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error BD", "No se pudieron cargar los clientes: " + e.getMessage());
                });
            }
        });

        // Convertimos el hilo en 'Daemon' para que se destruya automáticamente si cierras la aplicación
        hiloCarga.setDaemon(true);

        // 3. Iniciamos el hilo
        hiloCarga.start();
    }

    private void configurarEventosTabla() {
        tblClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                clienteSeleccionado = newSelection;
                txtNombres.setText(clienteSeleccionado.getNombres());
                txtApellidos.setText(clienteSeleccionado.getApellidos());
                txtTelefono.setText(clienteSeleccionado.getTelefono());
                txtCorreo.setText(clienteSeleccionado.getCorreo());
                chkActivo.setSelected(clienteSeleccionado.getActivo());
            }
        });
    }

    @FXML
    void onGuardar(ActionEvent event) {
        if (!validarFormulario()) return;

        Cliente nuevoCliente = new Cliente(null, txtNombres.getText().trim(), txtApellidos.getText().trim(),
                txtTelefono.getText().trim(), txtCorreo.getText().trim(), chkActivo.isSelected());

        try {
            if (clienteDAO.insertar(nuevoCliente)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Cliente registrado correctamente.");
                onLimpiar(null);
                cargarClientes();
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) { // Código común para unique violation en Postgres
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "El correo electrónico ya está registrado.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error BD", "Error al guardar el cliente: " + e.getMessage());
            }
        }
    }

    @FXML
    void onActualizar(ActionEvent event) {
        if (clienteSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Seleccione un cliente de la tabla para actualizar.");
            return;
        }
        if (!validarFormulario()) return;

        clienteSeleccionado.setNombres(txtNombres.getText().trim());
        clienteSeleccionado.setApellidos(txtApellidos.getText().trim());
        clienteSeleccionado.setTelefono(txtTelefono.getText().trim());
        clienteSeleccionado.setCorreo(txtCorreo.getText().trim());
        clienteSeleccionado.setActivo(chkActivo.isSelected());

        try {
            if (clienteDAO.actualizar(clienteSeleccionado)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Cliente actualizado correctamente.");
                onLimpiar(null);
                cargarClientes();
            }
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error BD", "Error al actualizar el cliente: " + e.getMessage());
        }
    }

    @FXML
    void onEliminar(ActionEvent event) {
        if (clienteSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Seleccione un cliente de la tabla para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar a " + clienteSeleccionado.getNombres() + "?");
        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                if (clienteDAO.eliminar(clienteSeleccionado.getId())) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Cliente eliminado correctamente.");
                    onLimpiar(null);
                    cargarClientes();
                }
            } catch (SQLException e) {
                // Violación de llave foránea (intentar eliminar cliente con citas asociadas)
                if (e.getSQLState().startsWith("23")) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Eliminación Restringida", "No se puede eliminar el cliente porque tiene citas técnicas asociadas.");
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error BD", "Error al eliminar el cliente: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    void onBuscar(ActionEvent event) {
        String texto = txtBuscar.getText().toLowerCase().trim();
        if (texto.isEmpty()) {
            cargarClientes();
            return;
        }

        ObservableList<Cliente> filtrados = FXCollections.observableArrayList(
                listaClientes.stream()
                        .filter(c -> c.getNombres().toLowerCase().contains(texto) ||
                                c.getApellidos().toLowerCase().contains(texto) ||
                                c.getCorreo().toLowerCase().contains(texto))
                        .collect(Collectors.toList())
        );
        tblClientes.setItems(filtrados);
    }

    @FXML
    void onLimpiarBusqueda(ActionEvent event) {
        txtBuscar.clear();
        tblClientes.setItems(listaClientes);
    }

    @FXML
    void onLimpiar(ActionEvent event) {
        txtNombres.clear();
        txtApellidos.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        chkActivo.setSelected(true);
        clienteSeleccionado = null;
        tblClientes.getSelectionModel().clearSelection();
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

        if (txtNombres.getText().trim().length() < 2) { lblErrorNombres.setVisible(true); lblErrorNombres.setManaged(true); valido = false; }
        if (txtApellidos.getText().trim().length() < 2) { lblErrorApellidos.setVisible(true); lblErrorApellidos.setManaged(true); valido = false; }
        if (!txtTelefono.getText().trim().matches("\\d{8}")) { lblErrorTelefono.setVisible(true); lblErrorTelefono.setManaged(true); valido = false; }
        if (!txtCorreo.getText().trim().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) { lblErrorCorreo.setVisible(true); lblErrorCorreo.setManaged(true); valido = false; }

        return valido;
    }

    private void ocultarErrores() {
        lblErrorNombres.setVisible(false); lblErrorNombres.setManaged(false);
        lblErrorApellidos.setVisible(false); lblErrorApellidos.setManaged(false);
        lblErrorTelefono.setVisible(false); lblErrorTelefono.setManaged(false);
        lblErrorCorreo.setVisible(false); lblErrorCorreo.setManaged(false);
    }

    private void actualizarContador() {
        lblTotal.setText("Total de clientes registrados: " + listaClientes.size());
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}