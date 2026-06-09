package lk.ijse.realtimeauctionsystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final ArrayList<DataOutputStream> clients = new ArrayList<>();
    private static int clientCount = 0;
    private static Map<String, Socket> connectedClients = new HashMap<>();

    private static double currentBid;
    private static String winner;

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(6000);
                System.out.println("[Auction server started - port 6000]");

                System.out.print("Item name: Rolex Watch");
                String itemName = "Rolex Watch";

                double startPrice = 8000;
                currentBid = startPrice;
                System.out.println("Starting price: LKR " + startPrice);

                while(true) {
                    Socket localSocket = serverSocket.accept();
                    DataInputStream input = new DataInputStream(localSocket.getInputStream());
                    DataOutputStream output = new DataOutputStream(localSocket.getOutputStream());

                    clientCount++;
                    clients.add(output);

                    String clientName = "Client-" + clientCount;
                    output.writeUTF(clientName);
                    output.flush();
                    System.out.println(clientName + " - Connected");

                    output.writeUTF(itemName);
                    output.flush();

                    output.writeUTF(String.valueOf(currentBid));
                    output.flush();

                    connectedClients.put(clientName, localSocket);

                    new Thread(() -> {
                        try {
                            while(true) {
                                try {
                                    String bid = input.readUTF();
                                    System.out.println();

                                    if (Double.parseDouble(bid) > currentBid) {
                                        currentBid = Double.parseDouble(bid);
                                        System.out.println("BID ACCEPTED - " + clientName
                                                + " : LKR " + bid + " (new highest)");

                                        Socket relevantClient = connectedClients.get(clientName);
                                        DataOutputStream dataOutputStream = new DataOutputStream(relevantClient.getOutputStream());
                                        dataOutputStream.writeUTF("Current highest bid is your : LKR " + bid + "\n");
                                        dataOutputStream.flush();

                                        winner = clientName;

                                    } else {
                                        currentBid = startPrice;
                                        System.out.println("BID REJECTED - " + clientName
                                                + " : LKR " + bid + " (too low)");

                                        Socket relevantClient = connectedClients.get(clientName);
                                        DataOutputStream dataOutputStream = new DataOutputStream(relevantClient.getOutputStream());
                                        dataOutputStream.writeUTF("REJECTED: Your bid is too low : LKR " + bid + "\n");
                                        dataOutputStream.flush();

                                        winner = "";
                                    }

                                    if (!winner.equals("")) {
                                        for (DataOutputStream client : clients) {
                                            client.writeUTF("[Auction Closed] WINNER: " + clientName + " - LKR " + currentBid);
                                            client.flush();
                                        }
                                    }

                                } catch (NumberFormatException ne) {
                                    System.out.println("Oops! Invalid bid.");
                                }
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }).start();
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }).start();
    }
}