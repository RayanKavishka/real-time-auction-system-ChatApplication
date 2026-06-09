package lk.ijse.realtimeauctionsystem;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    @FXML
    private Text highestBid;

    @FXML
    private Text itemName;

    @FXML
    private TextField msgField;

    @FXML
    private TextArea textArea;

    @FXML
    private Text userName;

    @FXML
    private Button disconnectBtn;

    private DataOutputStream output;
    private String clientName;

    private Socket remoteSocket;

    @FXML
    public void initialize() {
        new Thread(() -> {
            try {
                remoteSocket = new Socket("localhost", 6000);
                output = new DataOutputStream(remoteSocket.getOutputStream());
                DataInputStream input = new DataInputStream(remoteSocket.getInputStream());

                clientName = input.readUTF();
                userName.setText(clientName);
                itemName.setText(input.readUTF());
                highestBid.setText(input.readUTF());

                disconnectBtn.setOnAction(e -> {
                    Stage stage = (Stage) disconnectBtn.getScene().getWindow();
                    stage.close();

                    System.out.println(clientName + " is Disconnected");

                });

                while(true) {
                    String response = input.readUTF();
                    Platform.runLater(() -> {
                        try {
                            String[] msgArray = response.split("LKR ");
                            String highestBidValue = msgArray[1];
                            highestBid.setText(highestBidValue);
                            textArea.appendText(response);

                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    });
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }).start();
    }

    @FXML
    void handleSendMsg() {
        try {
            String bid = msgField.getText();
            output.writeUTF(bid);
            output.flush();

            msgField.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleClientDisconnect()  {
        try {
            remoteSocket.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
