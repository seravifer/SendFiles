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
        OutputStream sendFile = socket.getOutputStream();
        DataOutputStream out = null;
        InputStream localFile = null;

        try {
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Error en la conexión con el servidor.");
        }

        File file = new File(path);
        long length = file.length();

        out.writeUTF(file.getName());
        out.writeLong(length);

        try {
            localFile = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.err.println("El archivo no esta disponible.");
        }

        byte[] bytes = new byte[1024];

        int count;
        while ((count = localFile.read(bytes)) > 0) {
            sendFile.write(bytes, 0, count);
            sendFile.flush();
        }

        sendFile.close();
        localFile.close();

        out.close();
    }
}
