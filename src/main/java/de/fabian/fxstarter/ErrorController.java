package de.fabian.fxstarter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class ErrorController implements Initializable {
    private static Exception exception;
    @FXML
    private Label errorLbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorLbl.setText(exception.getMessage());
    }

    public static Exception getException() {
        return exception;
    }

    public static void setException(Exception exception) {
        ErrorController.exception = exception;
    }
}
