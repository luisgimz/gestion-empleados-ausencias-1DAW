package com.example.proyecto.controller;

import com.example.proyecto.service.UsuarioService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import com.example.proyecto.model.Usuario;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private Button btnOjo;
    @FXML private Label lblError;
    @FXML private VBox loginContainer;

    private boolean mostrandoPassword = false;
    private UsuarioService usuarioService;
    private MenuController menuController;

    public LoginController() {
        try {
            usuarioService = new UsuarioService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        txtPassword.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!txtPasswordVisible.getText().equals(newVal))
                txtPasswordVisible.setText(newVal);
        });
        txtPasswordVisible.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!txtPassword.getText().equals(newVal))
                txtPassword.setText(newVal);
        });

        try {
            Image logo = new Image(
                    getClass().getResourceAsStream("/com/example/proyecto/images/app-icon.png"));
            ImageView iv = new ImageView(logo);
            iv.setFitWidth(80);
            iv.setFitHeight(80);
            iv.setPreserveRatio(true);
            loginContainer.getChildren().add(0, iv);
        } catch (Exception e) {
            // no icon, no crash
        }
    }

    @FXML
    private void togglePassword() {
        mostrandoPassword = !mostrandoPassword;
        txtPassword.setVisible(!mostrandoPassword);
        txtPassword.setManaged(!mostrandoPassword);
        txtPasswordVisible.setVisible(mostrandoPassword);
        txtPasswordVisible.setManaged(mostrandoPassword);
        btnOjo.setText(mostrandoPassword ? "🙈" : "👁");
    }

    public void setMenuController(MenuController mc) {
        this.menuController = mc;
    }

    @FXML
    private void onLogin() {
        String user = txtUsuario.getText();
        String pass = txtPassword.getText();

        Usuario u = usuarioService.obtenerPorUsername(user);
        if (u == null || !u.getPasswordHash().equals(pass)) {
            lblError.setText("Credenciales incorrectas");
            return;
        }
        if (!u.isActivo()) {
            lblError.setText("El usuario no está activo");
            return;
        }

        SesionManager.login(u);
        menuController.init(u);
    }
}