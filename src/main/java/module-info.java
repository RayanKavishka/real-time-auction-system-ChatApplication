module lk.ijse.realtimeauctionsystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens lk.ijse.realtimeauctionsystem to javafx.fxml;
    exports lk.ijse.realtimeauctionsystem;
}