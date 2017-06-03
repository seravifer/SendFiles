package shared.model;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    private Socket socket = null;

    public Client(String ip, int port) {
        try {
            socket = new Socket(ip, port);
        } catch (UnknownHostException e) {
            System.err.println("Servidor no encontrado..");
        } catch (IOException e) {
            System.err.println("Error en la conexión del servidor.");
        }

    }
    public void send(String path) throws IOException {
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Error en la conexión con el servidor.");
        }

        System.out.println("Solicitando conexión.");
        out.println("Solicitando conexión.");

        File file = new File(path);
        //long length = file.length();

        out.println(file.getName());
        System.out.println("Enviando archivo...");

        InputStream localFilw = null;
        try {
           localFilw = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println("El archivo no esta disponible.");
        }
        OutputStream sendFile = socket.getOutputStream();

        byte[] bytes = new byte[1024 * 6];
        int count;
        while ((count = localFilw.read(bytes)) >= 0) {
            sendFile.write(bytes, 0, count);
        }

        System.out.println("Archivo enviado con éxito.");

        sendFile.close();
        localFilw.close();

        out.close();
        in.close();
        //socket.close();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
