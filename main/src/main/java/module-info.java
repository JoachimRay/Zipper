module zipper {
    requires javafx.controls;
    requires javafx.fxml;

    opens zipper to javafx.fxml;
    exports zipper;
}
