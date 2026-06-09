package lk.ijse.realtimeauctionsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientInitializer extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage defaultStage) throws Exception {
        for (int i = 1; i < 3; i++) {
            Stage stage = new Stage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/lk/ijse/realtimeauctionsystem/client.fxml"))));
            stage.show();
        }
    }
}
