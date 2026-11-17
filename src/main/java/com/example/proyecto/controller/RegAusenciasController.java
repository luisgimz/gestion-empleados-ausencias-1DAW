package com.example.proyecto.controller;

import com.example.proyecto.service.RegistroAusenciaService;
import com.example.proyecto.service.TipoAusenciaService;
import com.example.proyecto.model.RegistroAusencia;
import com.example.proyecto.model.TipoAusencia;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RegAusenciasController {

    @FXML private TableView<RegistroAusencia> tablaRegistros;
    @FXML private TableColumn<RegistroAusencia, String> colId;
    @FXML private TableColumn<RegistroAusencia, String> colIdSol;
    @FXML private TableColumn<RegistroAusencia, String> colTipoSol;
    @FXML private TableColumn<RegistroAusencia, String> colNomEmp;
    @FXML private TableColumn<RegistroAusencia, String> colNomAdmin;
    @FXML private TableColumn<RegistroAusencia, String> colFechaReg;
    @FXML private TableColumn<RegistroAusencia, String> colEstadoSol;

    @FXML private ComboBox<String> cmbFiltroTipo;
    @FXML private TextField txtFiltro;
    @FXML private DatePicker dpFiltro;

    private RegistroAusenciaService registroAusenciaService;
    private TipoAusenciaService tipoService;
    private List<RegistroAusencia> todosLosRegistros = new ArrayList<>();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final String FILTRO_EMPLEADO = "Nombre empleado";
    private static final String FILTRO_ADMIN    = "Nombre administrador";
    private static final String FILTRO_TIPO     = "Tipo ausencia";
    private static final String FILTRO_FECHA    = "Fecha registro";

    @FXML
    public void initialize() {
        try {
            registroAusenciaService = new RegistroAusenciaService();
            tipoService  = new TipoAusenciaService();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        colId.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().getId())));
        colIdSol.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().getSolicitud().getId())));
        colTipoSol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getTipoSolicitud().getNombre()));
        colNomEmp.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEmpleadoSolicitud().getNombre() + " " +
                        c.getValue().getEmpleadoSolicitud().getApellidos()));
        colNomAdmin.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getAdmin() != null
                        ? c.getValue().getAdmin().getNombre() + " " + c.getValue().getAdmin().getApellidos()
                        : "Admin eliminado"
        ));
        colFechaReg.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaRegistro() != null ?
                        c.getValue().getFechaRegistro().format(fmt) : ""));
        colEstadoSol.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().getSolicitud().getEstado())));

        cmbFiltroTipo.getItems().addAll(
                FILTRO_EMPLEADO,
                FILTRO_ADMIN,
                FILTRO_TIPO,
                FILTRO_FECHA
        );

        cargarTabla();
        tablaRegistros.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void onFiltroTipoChanged() {
        String seleccion = cmbFiltroTipo.getValue();
        if (seleccion == null) return;

        boolean esFecha = seleccion.equals(FILTRO_FECHA);

        txtFiltro.setVisible(!esFecha);
        txtFiltro.setManaged(!esFecha);
        dpFiltro.setVisible(esFecha);
        dpFiltro.setManaged(esFecha);

        txtFiltro.clear();
        dpFiltro.setValue(null);
    }

    @FXML
    private void aplicarFiltro() {
        String seleccion = cmbFiltroTipo.getValue();
        if (seleccion == null) {
            tablaRegistros.getItems().setAll(todosLosRegistros);
            return;
        }

        List<RegistroAusencia> filtrados = todosLosRegistros.stream()
                .filter(r -> {
                    switch (seleccion) {

                        case FILTRO_EMPLEADO: {
                            String texto = txtFiltro.getText().trim().toLowerCase();
                            if (texto.isEmpty()) return true;
                            String nombre = (r.getEmpleadoSolicitud().getNombre() + " " +
                                    r.getEmpleadoSolicitud().getApellidos()).toLowerCase();
                            return nombre.contains(texto);
                        }

                        case FILTRO_ADMIN: {
                            String texto = txtFiltro.getText().trim().toLowerCase();
                            if (texto.isEmpty()) return true;
                            if (r.getAdmin() == null) return "admin eliminado".contains(texto);
                            String nombre = (r.getAdmin().getNombre() + " " +
                                    r.getAdmin().getApellidos()).toLowerCase();
                            return nombre.contains(texto);
                        }

                        case FILTRO_TIPO: {
                            String texto = txtFiltro.getText().trim().toLowerCase();
                            if (texto.isEmpty()) return true;
                            return r.getTipoSolicitud().getNombre().toLowerCase().contains(texto);
                        }

                        case FILTRO_FECHA: {
                            LocalDate fecha = dpFiltro.getValue();
                            if (fecha == null) return true;
                            if (r.getFechaRegistro() == null) return false;
                            return r.getFechaRegistro().toLocalDate().equals(fecha);
                        }

                        default:
                            return true;
                    }
                })
                .collect(Collectors.toList());

        tablaRegistros.getItems().setAll(filtrados);
    }

    @FXML
    private void limpiarFiltro() {
        cmbFiltroTipo.setValue(null);
        txtFiltro.clear();
        dpFiltro.setValue(null);
        txtFiltro.setVisible(true);
        txtFiltro.setManaged(true);
        dpFiltro.setVisible(false);
        dpFiltro.setManaged(false);
        tablaRegistros.getItems().setAll(todosLosRegistros);
    }

    @FXML
    public void cargarTabla() {
        if (registroAusenciaService == null) return;
        todosLosRegistros = registroAusenciaService.obtenerTodos();
        tablaRegistros.getItems().setAll(todosLosRegistros);
    }

    @FXML
    private void verDetalles() {
        RegistroAusencia seleccionado = tablaRegistros.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertHelper.warning("Selecciona un registro primero.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/proyecto/detalle-registro.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(
                    getClass().getResource("/com/example/proyecto/css/styles.css").toExternalForm());

            DetalleRegistroController ctrl = loader.getController();
            ctrl.initData(seleccionado);

            Stage stage = new Stage();
            stage.setTitle("Detalle Registro");
            stage.setScene(scene);
            stage.getIcons().add(new Image(
                    GestorEscenas.class.getResourceAsStream("/com/example/proyecto/images/app-icon.png")));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();


            /*
              FXMLLoader loader = new FXMLLoader(
                    GestorEscenas.class.getResource("/com/example/proyecto/detalle-ausencia-pendiente.fxml"));
            VBox root = loader.load();

            DetalleAusenciaPendienteController ctrl = loader.getController();
            ctrl.initData(solicitud, nombreEmpleado);

            Stage stage = new Stage();
            stage.setTitle("Detalle Ausencia Pendiente");
            Scene scene = new Scene(root);
            aplicarCSS(scene);
            stage.getIcons().add(new Image(
                    GestorEscenas.class.getResourceAsStream("/com/example/proyecto/images/app-icon.png")));
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            return stage;
            * */
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}