package shared.model;

import javafx.application.Platform;
import shared.Controller;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

    private int[] ports = {4444, 4445, 4446};
    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;

    public Server() {
        for (int port : ports) {
            try {
                serverSocket = new ServerSocket(port);
                System.out.println("Servidor creado con éxito en el puerto " + port);
            } catch (IOException ex) {
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

            InputStream readFile = clientSocket.getInputStream();

            //PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            System.out.println("Cliente: " + in.readLine());
            System.out.println("Solicitud aceptada.");
            //out.println("Conexión aceptada.");

            OutputStream writeFile = null;
            try {
                writeFile = new FileOutputStream(Utils.getPath() + "r_" + in.readLine());
            } catch (FileNotFoundException e) {
                System.out.println("Archivo no encontrado.");
            }

            byte[] bytes = new byte[1024 * 6];

            int count;
            while ((count = readFile.read(bytes)) >= 0) {
                writeFile.write(bytes, 0, count);
            }

            System.out.println("Archivo recibido: " + writeFile);

            readFile.close();
            writeFile.close();

            //out.close();
            in.close();
            clientSocket.close();
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
