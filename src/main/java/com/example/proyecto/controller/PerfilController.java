package com.example.proyecto.controller;

import com.example.proyecto.model.Empleado;
import com.example.proyecto.model.SolicitudAusencia;
import com.example.proyecto.model.Usuario;
import com.example.proyecto.service.EmpleadoService;
import com.example.proyecto.service.SolicitudAusenciaService;
import com.example.proyecto.service.UsuarioService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class PerfilController {

    @FXML private ImageView imgFoto;
    @FXML private Label lblNombre;
    @FXML private Label lblEmail;
    @FXML private Label lblRol;
    @FXML private VBox panelDerecho;
    @FXML private Button btnMiPerfil;
    @FXML private Button btnSolicitudes;

    private Usuario usuarioActual;
    private Empleado empleadoActual;

    private UsuarioService usuarioService;
    private EmpleadoService empleadoService;
    private SolicitudAusenciaService solicitudService;

    public PerfilController() {
        try {
            usuarioService   = new UsuarioService();
            empleadoService  = new EmpleadoService();
            solicitudService = new SolicitudAusenciaService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initData(Usuario u) {

        try {
            u = usuarioService.obtenerPorId(u.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        usuarioActual = u;

        try {
            empleadoActual = empleadoService.obtenerPorDni(u.getEmpleado().getDni());
        } catch (Exception e) {
            empleadoActual = u.getEmpleado();
        }

        lblNombre.setText(empleadoActual.getNombre() + " " + empleadoActual.getApellidos());
        lblEmail.setText(empleadoActual.getEmailCorporativo());
        lblRol.setText(u.getRol().getNombre());

        cargarFoto();
        mostrarMiPerfil();
    }

    private void cargarFoto() {
        Image img;

        if (empleadoActual.getFoto() != null && empleadoActual.getFoto().length > 0) {
            img = new Image(new ByteArrayInputStream(empleadoActual.getFoto()));
        } else {
            img = new Image(
                    getClass().getResourceAsStream("/com/example/proyecto/images/default-avatar.png"));
        }

        imgFoto.setImage(img);
        imgFoto.setFitWidth(80);
        imgFoto.setFitHeight(80);
        imgFoto.setPreserveRatio(false);

        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(80, 80);
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        imgFoto.setClip(clip);
    }

    @FXML
    private void mostrarMiPerfil() {

        setActiveButton(btnMiPerfil);

        try {
            empleadoActual = empleadoService.obtenerPorDni(empleadoActual.getDni());

            VBox content = new VBox(16);
            content.setStyle("-fx-padding: 20 30;");
            VBox.setVgrow(content, javafx.scene.layout.Priority.ALWAYS);

            VBox card = new VBox(12);
            card.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; " +
                    "-fx-border-radius: 8; -fx-background-radius: 8; " +
                    "-fx-padding: 28 32; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
            VBox.setVgrow(card, javafx.scene.layout.Priority.ALWAYS);

            card.getChildren().addAll(
                    infoRow("Nombre completo", empleadoActual.getNombre() + " " + empleadoActual.getApellidos()),
                    separator(),
                    infoRow("Email corporativo", empleadoActual.getEmailCorporativo()),
                    separator(),
                    infoRow("Email personal", safe(empleadoActual.getEmailPersonal())),
                    separator(),
                    infoRow("Teléfono", safe(empleadoActual.getTelefono())),
                    separator(),
                    infoRow("Dirección", safe(empleadoActual.getDireccion())),
                    separator(),
                    infoRow("Fecha de alta", safe(empleadoActual.getFechaAlta())),
                    separator(),
                    infoRow("Estado laboral", safe(empleadoActual.getEstadoLaboral())),
                    separator(),
                    infoRow("Días vacaciones", String.valueOf(empleadoActual.getDiasVacacionesDisponibles()))
            );

            Button btnMod = new Button("Modificar perfil");
            btnMod.setOnAction(e -> abrirModificarPerfil());

            Button btnPass = new Button("Cambiar contraseña");
            btnPass.setOnAction(e -> abrirCambiarPassword());
            btnPass.setStyle("-fx-background-color: transparent; -fx-border-color: #3498db; " +
                    "-fx-border-radius: 4; -fx-text-fill: #3498db; -fx-padding: 6 16;");

            HBox botones = new HBox(10, btnMod, btnPass);
            botones.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            botones.setStyle("-fx-padding: 12 0 0 0;");

            card.getChildren().addAll(separator(), botones);
            content.getChildren().add(card);
            panelDerecho.getChildren().setAll(content);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void mostrarSolicitudes() {

        setActiveButton(btnSolicitudes);

        try {
            List<SolicitudAusencia> lista =
                    solicitudService.obtenerPorDni(empleadoActual.getDni());

            TableView<SolicitudAusencia> tabla = new TableView<>();

            TableColumn<SolicitudAusencia, String> colTipo = new TableColumn<>("Tipo");
            colTipo.setCellValueFactory(c ->
                    new SimpleStringProperty(c.getValue().getTipoAusencia().getNombre()));

            TableColumn<SolicitudAusencia, String> colInicio = new TableColumn<>("Desde");
            colInicio.setCellValueFactory(c ->
                    new SimpleStringProperty(c.getValue().getFechaInicio().toString()));

            TableColumn<SolicitudAusencia, String> colFin = new TableColumn<>("Hasta");
            colFin.setCellValueFactory(c ->
                    new SimpleStringProperty(c.getValue().getFechaFin().toString()));

            TableColumn<SolicitudAusencia, Integer> colDias = new TableColumn<>("Días");
            colDias.setCellValueFactory(c ->
                    new SimpleIntegerProperty(c.getValue().getDiasSolicitados()).asObject());

            TableColumn<SolicitudAusencia, String> colEstado = new TableColumn<>("Estado");
            colEstado.setCellValueFactory(c ->
                    new SimpleStringProperty(c.getValue().getEstado()));

            tabla.getColumns().addAll(colTipo, colInicio, colFin, colDias, colEstado);
            tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tabla.setItems(FXCollections.observableArrayList(lista));

            Button btnAbrir = new Button("Ver detalle");
            btnAbrir.setOnAction(e -> {
                SolicitudAusencia sel = tabla.getSelectionModel().getSelectedItem();
                if (sel == null) {
                    AlertHelper.warning("Selecciona una solicitud.");
                    return;
                }
                if ("PENDIENTE".equals(sel.getEstado())) {
                    GestorEscenas.cargarDetalleAusenciaPendiente(sel, lblNombre.getText());
                    mostrarSolicitudes();
                } else {
                    GestorEscenas.cargarDetalleAusenciaSimpleConNombre(sel, lblNombre.getText());
                }
            });

            VBox wrapper = new VBox(10, tabla, btnAbrir);
            VBox.setVgrow(tabla, javafx.scene.layout.Priority.ALWAYS);
            VBox.setVgrow(wrapper, javafx.scene.layout.Priority.ALWAYS);
            panelDerecho.getChildren().setAll(wrapper);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirModificarPerfil() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/proyecto/modificar-perfil.fxml"));
            VBox root = loader.load();

            ModificarPerfilController ctrl = loader.getController();
            ctrl.initData(empleadoActual, this);

            Stage stage = new Stage();
            stage.setTitle("Modificar perfil");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/com/example/proyecto/css/styles.css").toExternalForm());
            stage.getIcons().add(new Image(
                    GestorEscenas.class.getResourceAsStream("/com/example/proyecto/images/app-icon.png")));
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void abrirCambiarPassword() {
        GestorEscenas.cargarCambiarPassword(usuarioActual, empleadoActual.getNombre());
    }

    public void refrescarPerfil() {
        try {
            empleadoActual = empleadoService.obtenerPorDni(empleadoActual.getDni());
            cargarFoto();
            mostrarMiPerfil();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setActiveButton(Button active) {
        String normal = "-fx-background-color: transparent; -fx-padding: 10 16;";
        String activeStyle = "-fx-background-color: #dbeafe; -fx-border-color: #3b82f6; -fx-border-width: 0 0 0 3;";

        btnMiPerfil.setStyle(normal);
        btnSolicitudes.setStyle(normal);
        active.setStyle(activeStyle);
    }

    private String safe(Object val) {
        return val != null ? val.toString() : "No disponible";
    }

    private VBox infoRow(String label, String value) {
        Label l = new Label(label);
        l.setStyle("-fx-font-size: 11; -fx-text-fill: #7f8c8d;");
        Label v = new Label(value);
        v.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        return new VBox(2, l, v);
    }

    private Separator separator() {
        return new Separator();
    }
}