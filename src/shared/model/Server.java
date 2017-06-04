package shared.model;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

    public ObservableList<Task<String>> listFiles = FXCollections.observableArrayList();
    private int[] ports = {4444, 4445, 4446};
    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;

    public Server() {
        for (int port : ports) {
            try {
                serverSocket = new ServerSocket(port);
                System.out.println("Servidor creado con éxito en el puerto " + port);
            } catch (IOException e) {
                continue;
            }
            break;
        }
    }

    private void receive() throws IOException {
        while (true) {
            try {
                System.out.println("Esperando cliente...");
                clientSocket = serverSocket.accept();
                System.out.println("Cliente aceptado con éxito.");
            } catch (IOException e) {
                System.err.println("A ocurrido un problema al aceptar el cliente.");
            }

            Download download = new Download(clientSocket);
            Platform.runLater(() -> listFiles.add(0, download));
            new Thread(download).start();

        }
    }

    @Override
    public void run() {
        try {
            receive();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
