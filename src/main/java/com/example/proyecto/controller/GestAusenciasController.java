package com.example.proyecto.controller;

import com.example.proyecto.service.RegistroAusenciaService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.example.proyecto.model.SolicitudAusencia;
import com.example.proyecto.model.RegistroAusencia;
import com.example.proyecto.service.SolicitudAusenciaService;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class GestAusenciasController {

    @FXML private TableView<SolicitudAusencia> tablaSolicitudes;
    @FXML private TableColumn<SolicitudAusencia, String> colNombre;
    @FXML private TableColumn<SolicitudAusencia, String> colTipo;
    @FXML private TableColumn<SolicitudAusencia, String> colFechaInicio;
    @FXML private TableColumn<SolicitudAusencia, String> colFechaFin;
    @FXML private TableColumn<SolicitudAusencia, String> colDias;
    @FXML private TableColumn<SolicitudAusencia, String> colEstado;
    @FXML private TableColumn<SolicitudAusencia, String> colFechaSol;

    @FXML private TableView<RegistroAusencia> tablaAusentes;
    @FXML private TableColumn<RegistroAusencia, String> colAuNombre;
    @FXML private TableColumn<RegistroAusencia, String> colAuDep;
    @FXML private TableColumn<RegistroAusencia, String> colAuTipo;
    @FXML private TableColumn<RegistroAusencia, String> colAuFechaInicio;
    @FXML private TableColumn<RegistroAusencia, String> colAuFechaFin;

    // Filtros solicitudes
    @FXML private ComboBox<String> cmbFiltroSol;
    @FXML private TextField txtFiltroSol;
    @FXML private DatePicker dpFiltroSol;

    // Filtros ausentes
    @FXML private ComboBox<String> cmbFiltroAu;
    @FXML private TextField txtFiltroAu;
    @FXML private DatePicker dpFiltroAu;

    private SolicitudAusenciaService solicitudService;
    private RegistroAusenciaService registroAusenciaService;

    private List<SolicitudAusencia> todasLasSolicitudes = new ArrayList<>();
    private List<RegistroAusencia> todosLosAusentes     = new ArrayList<>();

    private static final String FILTRO_NOMBRE    = "Nombre";
    private static final String FILTRO_TIPO      = "Tipo solicitud";
    private static final String FILTRO_FECHA_INI = "Fecha inicio";
    private static final String FILTRO_FECHA_FIN = "Fecha fin";

    public GestAusenciasController() {
        try {
            solicitudService = new SolicitudAusenciaService();
            registroAusenciaService = new RegistroAusenciaService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {

        colNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getEmpleado().getNombre() + " " + c.getValue().getEmpleado().getApellidos()
        ));
        colTipo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getTipoAusencia().getNombre()
        ));
        colFechaInicio.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getFechaInicio() != null ? c.getValue().getFechaInicio().toString() : ""
        ));
        colFechaFin.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getFechaFin() != null ? c.getValue().getFechaFin().toString() : ""
        ));
        colDias.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(c.getValue().getDiasSolicitados())
        ));
        colEstado.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getEstado()
        ));
        colFechaSol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getFechaSolicitud() != null ? c.getValue().getFechaSolicitud().toString() : ""
        ));

        colAuNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getEmpleadoSolicitud().getNombre() + " " + c.getValue().getEmpleadoSolicitud().getApellidos()
        ));
        colAuDep.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getEmpleadoSolicitud().getPuestoActual() != null
                        ? c.getValue().getEmpleadoSolicitud().getPuestoActual().getDepartamento().getNombre()
                        : ""
        ));
        colAuTipo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getTipoSolicitud().getNombre()
        ));
        colAuFechaInicio.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getSolicitud().getFechaInicio() != null
                        ? c.getValue().getSolicitud().getFechaInicio().toString() : ""
        ));
        colAuFechaFin.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getSolicitud().getFechaFin() != null
                        ? c.getValue().getSolicitud().getFechaFin().toString() : ""
        ));

        cmbFiltroSol.getItems().addAll(FILTRO_NOMBRE, FILTRO_TIPO, FILTRO_FECHA_INI, FILTRO_FECHA_FIN);
        cmbFiltroAu.getItems().addAll(FILTRO_NOMBRE, FILTRO_TIPO, FILTRO_FECHA_INI, FILTRO_FECHA_FIN);

        dpFiltroSol.setVisible(false);
        dpFiltroSol.setManaged(false);
        dpFiltroAu.setVisible(false);
        dpFiltroAu.setManaged(false);

        cargarSolicitudes();
        cargarAusentes();
        tablaSolicitudes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaAusentes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    @FXML
    private void onFiltroSolChanged() {
        String sel = cmbFiltroSol.getValue();
        if (sel == null) return;
        boolean esFecha = sel.equals(FILTRO_FECHA_INI) || sel.equals(FILTRO_FECHA_FIN);
        txtFiltroSol.setVisible(!esFecha);
        txtFiltroSol.setManaged(!esFecha);
        dpFiltroSol.setVisible(esFecha);
        dpFiltroSol.setManaged(esFecha);
        txtFiltroSol.clear();
        dpFiltroSol.setValue(null);
    }

    @FXML
    private void aplicarFiltroSol() {
        String sel = cmbFiltroSol.getValue();
        if (sel == null) { tablaSolicitudes.getItems().setAll(todasLasSolicitudes); return; }

        List<SolicitudAusencia> filtradas = todasLasSolicitudes.stream()
                .filter(s -> switch (sel) {
                    case FILTRO_NOMBRE -> {
                        String txt = txtFiltroSol.getText().trim().toLowerCase();
                        yield txt.isEmpty() || (s.getEmpleado().getNombre() + " " + s.getEmpleado().getApellidos()).toLowerCase().contains(txt);
                    }
                    case FILTRO_TIPO -> {
                        String txt = txtFiltroSol.getText().trim().toLowerCase();
                        yield txt.isEmpty() || s.getTipoAusencia().getNombre().toLowerCase().contains(txt);
                    }
                    case FILTRO_FECHA_INI -> {
                        LocalDate fecha = dpFiltroSol.getValue();
                        yield fecha == null || (s.getFechaInicio() != null && s.getFechaInicio().equals(fecha));
                    }
                    case FILTRO_FECHA_FIN -> {
                        LocalDate fecha = dpFiltroSol.getValue();
                        yield fecha == null || (s.getFechaFin() != null && s.getFechaFin().equals(fecha));
                    }
                    default -> true;
                })
                .collect(Collectors.toList());

        tablaSolicitudes.getItems().setAll(filtradas);
    }

    @FXML
    private void limpiarFiltroSol() {
        cmbFiltroSol.setValue(null);
        txtFiltroSol.clear();
        dpFiltroSol.setValue(null);
        txtFiltroSol.setVisible(true);
        txtFiltroSol.setManaged(true);
        dpFiltroSol.setVisible(false);
        dpFiltroSol.setManaged(false);
        tablaSolicitudes.getItems().setAll(todasLasSolicitudes);
    }

    @FXML
    private void onFiltroAuChanged() {
        String sel = cmbFiltroAu.getValue();
        if (sel == null) return;
        boolean esFecha = sel.equals(FILTRO_FECHA_INI) || sel.equals(FILTRO_FECHA_FIN);
        txtFiltroAu.setVisible(!esFecha);
        txtFiltroAu.setManaged(!esFecha);
        dpFiltroAu.setVisible(esFecha);
        dpFiltroAu.setManaged(esFecha);
        txtFiltroAu.clear();
        dpFiltroAu.setValue(null);
    }

    @FXML
    private void aplicarFiltroAu() {
        String sel = cmbFiltroAu.getValue();
        if (sel == null) { tablaAusentes.getItems().setAll(todosLosAusentes); return; }

        List<RegistroAusencia> filtrados = todosLosAusentes.stream()
                .filter(r -> switch (sel) {
                    case FILTRO_NOMBRE -> {
                        String txt = txtFiltroAu.getText().trim().toLowerCase();
                        yield txt.isEmpty() || (r.getEmpleadoSolicitud().getNombre() + " " + r.getEmpleadoSolicitud().getApellidos()).toLowerCase().contains(txt);
                    }
                    case FILTRO_TIPO -> {
                        String txt = txtFiltroAu.getText().trim().toLowerCase();
                        yield txt.isEmpty() || r.getTipoSolicitud().getNombre().toLowerCase().contains(txt);
                    }
                    case FILTRO_FECHA_INI -> {
                        LocalDate fecha = dpFiltroAu.getValue();
                        yield fecha == null || (r.getSolicitud().getFechaInicio() != null && r.getSolicitud().getFechaInicio().equals(fecha));
                    }
                    case FILTRO_FECHA_FIN -> {
                        LocalDate fecha = dpFiltroAu.getValue();
                        yield fecha == null || (r.getSolicitud().getFechaFin() != null && r.getSolicitud().getFechaFin().equals(fecha));
                    }
                    default -> true;
                })
                .collect(Collectors.toList());

        tablaAusentes.getItems().setAll(filtrados);
    }

    @FXML
    private void limpiarFiltroAu() {
        cmbFiltroAu.setValue(null);
        txtFiltroAu.clear();
        dpFiltroAu.setValue(null);
        txtFiltroAu.setVisible(true);
        txtFiltroAu.setManaged(true);
        dpFiltroAu.setVisible(false);
        dpFiltroAu.setManaged(false);
        tablaAusentes.getItems().setAll(todosLosAusentes);
    }

    public void cargarSolicitudes() {
        todasLasSolicitudes = solicitudService.obtenerPendientes();
        tablaSolicitudes.getItems().setAll(todasLasSolicitudes);
    }

    public void cargarAusentes() {
        todosLosAusentes = registroAusenciaService.obtenerAusentesProximos7Dias();
        tablaAusentes.getItems().setAll(todosLosAusentes);
    }

    @FXML
    public void cargarTablas() {
        cargarSolicitudes();
        cargarAusentes();
    }

    @FXML
    private void verDetallesSolicitud() {
        SolicitudAusencia seleccionada = tablaSolicitudes.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            AlertHelper.warning("Selecciona una solicitud de la tabla para ver sus detalles.");
            return;
        }
        GestorEscenas.cargarPopUpGestAusencias(seleccionada);
        cargarSolicitudes();
    }

    @FXML
    private void verDetallesAusente() {
        RegistroAusencia seleccionado = tablaAusentes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertHelper.warning("Selecciona un empleado ausente de la tabla para ver sus detalles.");
            return;
        }
        GestorEscenas.cargarDetalleAusenciaSimple(seleccionado.getSolicitud());
        cargarAusentes();
    }
}