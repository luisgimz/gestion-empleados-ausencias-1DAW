package com.example.proyecto.controller;

import com.example.proyecto.model.Empleado;
import com.example.proyecto.model.SolicitudAusencia;
import com.example.proyecto.model.Usuario;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class GestorEscenas {

    private static void aplicarCSS(Scene scene) {
        scene.getStylesheets().add(
                GestorEscenas.class.getResource(
                        "/com/example/proyecto/css/styles.css").toExternalForm());
    }

    public static Stage cargarEditarEmpleado(Empleado empleado, Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    GestorEscenas.class.getResource("/com/example/proyecto/editar-empleado.fxml"));
            VBox root = loader.load();

            EditarEmpleadoController ctrl = loader.getController();
            ctrl.initData(empleado, usuario);

            Stage stage = new Stage();
            stage.setTitle("Editar empleado");
            Scene scene = new Scene(root);
            aplicarCSS(scene);
            stage.getIcons().add(new Image(
                    GestorEscenas.class.getResourceAsStream("/com/example/proyecto/images/app-icon.png")));
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            return stage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Stage cargarPopUpGestAusencias(SolicitudAusencia solicitud) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    GestorEscenas.class.getResource("/com/example/proyecto/popup-gest-ausencias.fxml"));
            VBox root = loader.load();

            PopUpGestAusenciasController ctrl = loader.getController();
            ctrl.initData(solicitud);

            Stage stage = new Stage();
            stage.setTitle("Solicitud Ausencia");
            Scene scene = new Scene(root);
            aplicarCSS(scene);
            stage.getIcons().add(new Image(
                    GestorEscenas.class.getResourceAsStream("/com/example/proyecto/images/app-icon.png")));
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            return stage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void cargarConfirmSolAusencias(String mensajePrincipal, String mensajeSecundario) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    GestorEscenas.class.getResource("/com/example/proyecto/confirm-sol-ausencias.fxml"));
            VBox root = loader.load();

            ConfirmSolAusenciasController ctrl = loader.getController();
            ctrl.setMensajes(mensajePrincipal, mensajeSecundario);

            Stage stage = new Stage();
            stage.setTitle("Confirmar solicitud");
            Scene scene = new Scene(root);
            aplicarCSS(scene);
            stage.getIcons().add(new Image(
                    GestorEscenas.class.getResourceAsStream("/com/example/proyecto/images/app-icon.png")));
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void cargarDetalleAusenciaSimple(SolicitudAusencia solicitud) {
        String nombre = solicitud.getEmpleado().getNombre() + " " +
                solicitud.getEmpleado().getApellidos();
        cargarDetalleAusenciaSimpleConNombre(solicitud, nombre);
    }

    public static void cargarDetalleAusenciaSimpleConNombre(SolicitudAusencia solicitud,
                                                            String nombreEmpleado) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    GestorEscenas.class.getResource("/com/example/proyecto/detalle-ausencia-simple.fxml"));
            VBox root = loader.load();

            DetalleAusenciaSimpleController ctrl = loader.getController();
            ctrl.initData(solicitud, nombreEmpleado);

            Stage stage = new Stage();
            stage.setTitle("Detalle Ausencia");
            Scene scene = new Scene(root);
            aplicarCSS(scene);
            stage.getIcons().add(new Image(
                    GestorEscenas.class.getResourceAsStream("/com/example/proyecto/images/app-icon.png")));
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Stage cargarDetalleAusenciaPendiente(SolicitudAusencia solicitud,
                                                       String nombreEmpleado) {
        try {
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
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Stage cargarAnyadirEmpleado() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    GestorEscenas.class.getResource("/com/example/proyecto/anyadir-empleado.fxml"));
            VBox root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Añadir empleado");
            Scene scene = new Scene(root);
            stage.getIcons().add(new Image(
                    GestorEscenas.class.getResourceAsStream("/com/example/proyecto/images/app-icon.png")));
            aplicarCSS(scene);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            return stage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void cargarPopupEliminarEmpleado() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    GestorEscenas.class.getResource("/com/example/proyecto/popup-eliminar-empleado.fxml"));
            VBox root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Eliminar empleado");
            Scene scene = new Scene(root);
            aplicarCSS(scene);
            stage.getIcons().add(new Image(
                    GestorEscenas.class.getResourceAsStream("/com/example/proyecto/images/app-icon.png")));
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void cargarCambiarPassword(Usuario usuario, String nombreEmpleado) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    GestorEscenas.class.getResource("/com/example/proyecto/cambiar-password.fxml"));
            VBox root = loader.load();

            CambiarPasswordController ctrl = loader.getController();
            ctrl.initData(usuario);

            Stage stage = new Stage();
            stage.setTitle("Cambiar contraseña - " + nombreEmpleado);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    GestorEscenas.class.getResource("/com/example/proyecto/css/styles.css").toExternalForm());
            stage.getIcons().add(new Image(
                    GestorEscenas.class.getResourceAsStream("/com/example/proyecto/images/app-icon.png")));
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.error("Error al abrir el formulario de contraseña.");
        }
    }
}