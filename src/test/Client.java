import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    private static final int PORT = 4444;
    private static final String IP = "127.0.0.1";

    public static void main(String[] args) throws IOException {

        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            socket = new Socket(IP, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Servidor no encontrado..");
        } catch (IOException e) {
            System.err.println("Error en la conexión del servidor.");
        }

        System.out.println("Solicitando conexión.");
        out.println("Solicitando conexión.");
/*
        String fromServer;
        String fromUser;
    try {

        while ((fromServer = in.readLine()) != null) {
            System.out.println("Server: " + fromServer);

            if (fromServer.equals("Bye."))
                break;

            fromUser = stdIn.readLine();
            if (fromUser != null) {
                out.println(fromUser);
            }
        }
    } catch (SocketException e) {
        System.err.println("Conexión perdida.");
    }*/


        File file = new File("C:\\Users\\Sergi\\Google Drive\\Java\\shared\\files\\1.txt");
        System.out.println(file.getAbsoluteFile());
        long length = file.length();

        //out.println(length);
        System.out.println("Enviando archivo...");

        InputStream localFilw = new FileInputStream(file);
        OutputStream sendFile = socket.getOutputStream();

        byte[] bytes = new byte[1024];
        int count;
        while ((count = localFilw.read(bytes)) >= 0) {
            sendFile.write(bytes, 0, count);
        }

        System.out.println("Archivo enviado con éxito.");

        localFilw.close();
        sendFile.close();

        out.close();
        in.close();

        socket.close();
    }
}
