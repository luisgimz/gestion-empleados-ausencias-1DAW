package com.example.proyecto.controller;

import com.example.proyecto.model.Empleado;
import com.example.proyecto.model.PuestoTrabajo;
import com.example.proyecto.model.Usuario;
import com.example.proyecto.service.EmpleadoService;
import com.example.proyecto.service.PuestoTrabajoService;
import com.example.proyecto.service.UsuarioService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

public class EditarEmpleadoController {

    @FXML private TextField txtDni;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtEmailCorporativo;
    @FXML private TextField txtEmailPersonal;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtNumeroSs;
    @FXML private TextField txtDiasVacaciones;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private ComboBox<PuestoTrabajo> cmbPuesto;
    @FXML private Label lblDepartamento;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private DatePicker dpFechaAlta;
    @FXML private DatePicker dpFechaBaja;
    @FXML private CheckBox chkUsuarioActivo;
    @FXML private ImageView imgFotoPreview;

    private Usuario usuario;
    private Empleado empleado;
    private EmpleadoService empleadoService;
    private UsuarioService usuarioService;
    private PuestoTrabajoService puestoTrabajoService;
    private byte[] nuevaFoto;

    public EditarEmpleadoController() {
        try {
            empleadoService      = new EmpleadoService();
            usuarioService       = new UsuarioService();
            puestoTrabajoService = new PuestoTrabajoService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        cmbEstado.getItems().addAll("Activo", "Teletrabajo", "Suspendido", "Despedido");
        dpFechaBaja.setPromptText("Sin fecha de baja");
        dpFechaBaja.setStyle("-fx-prompt-text-fill: gray;");
        txtDiasVacaciones.setEditable(false);
        txtDiasVacaciones.setStyle(
                "-fx-control-inner-background: #e0e0e0;" +
                        "-fx-opacity: 1;" +
                        "-fx-text-fill: #555;"
        );

        // Cargar puestos
        ArrayList<PuestoTrabajo> puestos = puestoTrabajoService.obtenerTodos();
        cmbPuesto.setItems(FXCollections.observableArrayList(puestos));
        cmbPuesto.setConverter(new StringConverter<>() {
            @Override
            public String toString(PuestoTrabajo p) {
                return p != null ? p.getNombre() : "";
            }
            @Override
            public PuestoTrabajo fromString(String s) { return null; }
        });

        cmbPuesto.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getDepartamento() != null) {
                lblDepartamento.setText(newVal.getDepartamento().getNombre());
            } else {
                lblDepartamento.setText("-");
            }
        });
    }

    public void initData(Empleado emp, Usuario user) {
        this.empleado = emp;
        this.usuario  = user;

        txtDni.setText(emp.getDni());
        txtDni.setDisable(true);
        txtNombre.setText(emp.getNombre());
        txtApellidos.setText(emp.getApellidos());
        txtEmailCorporativo.setText(emp.getEmailCorporativo());
        txtEmailPersonal.setText(emp.getEmailPersonal() != null ? emp.getEmailPersonal() : "");
        txtTelefono.setText(emp.getTelefono() != null ? emp.getTelefono() : "");
        txtDireccion.setText(emp.getDireccion() != null ? emp.getDireccion() : "");
        txtNumeroSs.setText(emp.getNumeroSs());
        txtDiasVacaciones.setText(String.valueOf(emp.getDiasVacacionesDisponibles()));
        cmbEstado.setValue(emp.getEstadoLaboral());
        dpFechaNacimiento.setValue(emp.getFechaNacimiento());
        dpFechaAlta.setValue(emp.getFechaAlta());
        dpFechaBaja.setValue(emp.getFechaBaja());
        chkUsuarioActivo.setSelected(user.isActivo());

        if (emp.getPuestoActual() != null) {
            cmbPuesto.getItems().stream()
                    .filter(p -> p.getId() == emp.getPuestoActual().getId())
                    .findFirst()
                    .ifPresent(cmbPuesto::setValue);
        }

        if (emp.getFoto() != null && emp.getFoto().length > 0) {
            imgFotoPreview.setImage(new Image(new ByteArrayInputStream(emp.getFoto())));
        } else {
            imgFotoPreview.setImage(new Image(
                    getClass().getResourceAsStream("/com/example/proyecto/images/default-avatar.png")));
        }
    }

    @FXML
    private void seleccionarFoto() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar foto de perfil");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.jpeg", "*.png", "*.gif"));
        chooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Downloads"));

        File file = chooser.showOpenDialog(imgFotoPreview.getScene().getWindow());
        if (file != null) {
            try {
                nuevaFoto = Files.readAllBytes(file.toPath());
                imgFotoPreview.setImage(new Image(new ByteArrayInputStream(nuevaFoto)));
            } catch (Exception e) {
                AlertHelper.error("No se pudo cargar la imagen.");
            }
        }
    }

    @FXML
    private void guardarCambios() {
        if (txtNombre.getText().trim().isEmpty() ||
                txtApellidos.getText().trim().isEmpty() ||
                txtEmailCorporativo.getText().trim().isEmpty() ||
                cmbEstado.getValue() == null) {
            AlertHelper.warning("Nombre, apellidos, email corporativo y estado son obligatorios.");
            return;
        }

        if (cmbPuesto.getValue() == null) {
            AlertHelper.warning("El puesto de trabajo es obligatorio.");
            return;
        }

        if (dpFechaNacimiento.getValue() == null) {
            AlertHelper.warning("La fecha de nacimiento es obligatoria.");
            return;
        }

        empleado.setNombre(txtNombre.getText().trim());
        empleado.setApellidos(txtApellidos.getText().trim());
        empleado.setEmailCorporativo(txtEmailCorporativo.getText().trim());
        empleado.setEmailPersonal(txtEmailPersonal.getText().trim());
        empleado.setTelefono(txtTelefono.getText().trim());
        empleado.setDireccion(txtDireccion.getText().trim());
        empleado.setNumeroSs(txtNumeroSs.getText().trim());
        empleado.setEstadoLaboral(cmbEstado.getValue());
        empleado.setFechaNacimiento(dpFechaNacimiento.getValue());
        empleado.setFechaAlta(dpFechaAlta.getValue());
        empleado.setFechaBaja(dpFechaBaja.getValue());

        try {
            boolean ok = empleadoService.actualizarEmpleado(empleado);

            if (ok) {
                if (cmbPuesto.getValue() != null) {
                    boolean okPuesto = empleadoService.actualizarPuesto(
                            empleado.getDni(),
                            cmbPuesto.getValue().getId()
                    );
                    if (!okPuesto) {
                        AlertHelper.error("Datos guardados pero error al actualizar el puesto.");
                    }
                }

                if (nuevaFoto != null) {
                    boolean okFoto = empleadoService.actualizarFoto(empleado.getDni(), nuevaFoto);
                    if (!okFoto) {
                        AlertHelper.error("Datos guardados pero error al guardar la foto.");
                    }
                }

                boolean nuevoEstado = chkUsuarioActivo.isSelected();
                String mensajeExtra = "";

                if (usuario.isActivo() != nuevoEstado) {
                    usuario.setActivo(nuevoEstado);
                    usuarioService.actualizarActivoUsuario(usuario.getUsername(), nuevoEstado);
                    mensajeExtra = nuevoEstado
                            ? "\nSe ha activado el Usuario"
                            : "\nSe ha desactivado el Usuario";
                }

                AlertHelper.info("Empleado actualizado correctamente." + mensajeExtra);
                ((Stage) txtNombre.getScene().getWindow()).close();

            } else {
                AlertHelper.error("Error al guardar. Inténtalo de nuevo.");
            }

        } catch (IllegalArgumentException e) {
            AlertHelper.warning(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.error("Error inesperado.");
        }
    }

    @FXML
    private void abrirCambiarPassword() {
        GestorEscenas.cargarCambiarPassword(usuario, empleado.getNombre());
    }

    @FXML
    private void cancelar() {
        ((Stage) txtNombre.getScene().getWindow()).close();
    }
}