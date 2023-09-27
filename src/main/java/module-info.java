module com.example.skubbepuslespil {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.skubbepuslespil to javafx.fxml;
    exports com.example.skubbepuslespil;
}