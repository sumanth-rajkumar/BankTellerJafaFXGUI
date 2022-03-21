module com.example.project3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens edu.rutgers.sumanth.cs213 to javafx.fxml;
    exports edu.rutgers.sumanth.cs213;
}