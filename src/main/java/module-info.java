module com.legendsayantan.xcrypt {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;
    requires java.desktop;


    opens com.legendsayantan.xcrypt to javafx.fxml;
    exports com.legendsayantan.xcrypt;
}