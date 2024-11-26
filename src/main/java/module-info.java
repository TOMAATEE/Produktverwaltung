module de.fabian.fxstarter {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.poi.ooxml;
    requires commons.math3;


    opens de.fabian.fxstarter to javafx.fxml;
    exports de.fabian.fxstarter;
    exports de.fabian.fxstarter.assets;
    opens de.fabian.fxstarter.assets to javafx.fxml;
}