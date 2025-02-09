package dbbrowser;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class LoginController {

    @FXML
    private TextField dbUrlField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button connectButton;
    @FXML
    private VBox mainContainer;

    @FXML
    private void handleConnect(ActionEvent event) {
        String url = dbUrlField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        
        if (!url.startsWith("jdbc:mysql://")) {
            url = "jdbc:mysql://" + url; 
        }

        try {
          
            Class.forName("com.mysql.cj.jdbc.Driver");

        
            Connection conn = DriverManager.getConnection(url, username, password);
            if (conn != null) {
                System.out.println("Connection successful!");
                loadMainScreen();
            }
        } catch (SQLException e) {
       
        if ("28000".equals(e.getSQLState())) {
            showAlert("Authentication Failed", "Incorrect username or password. Please try again.");
        } else {
            showAlert("Connection Failed", "SQL Error: " + e.getMessage());
        }
        e.printStackTrace();
        } catch (ClassNotFoundException e) {
            showAlert("Connection Failed", "JDBC Driver Not Found: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Connection Failed", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadMainScreen() {
        try {
            Stage stage = (Stage) connectButton.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dbbrowser/MainView.fxml"));
            Scene scene = new Scene(loader.load());

            stage.setScene(scene);
            stage.setTitle("Database Browser - Main");

            MainController mainController = loader.getController();
            mainController.initialize();
        } catch (Exception e) {
            showAlert("Error", "Failed to load main screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleConnect(new ActionEvent(connectButton, null));
        }
    }
}
