import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final int PORT = 4444;

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor creado con éxito.");
        } catch (IOException e) {
            System.err.println("A ocurrido un problema con el puerto.");
        }

        Socket clientSocket = null;
        try {
            System.out.println("Esperando cliente...");
            clientSocket = serverSocket.accept();
            System.out.println("Cliente aceptado con exito.");
        } catch (IOException e) {
            System.err.println("A ocurrido un problema al aceptar el cliente.");
        }

        OutputStream writeFile = null;
        try {
            writeFile = new FileOutputStream("C:\\Users\\Sergi\\Google Drive\\Java\\shared\\files\\2.txt");
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado.");
        }

        InputStream readFile = clientSocket.getInputStream();

       // PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        System.out.println("Cliente: " + in.readLine());
        System.out.println("Solicitud aceptada.");
        //out.println("Conexión aceptada.");

        /*String inputLine;
        while ((inputLine = in.readLine()) != null) {
            System.out.println("Entra en el bucle.");
            out.println(inputLine);
            if (inputLine.equals("Bye."))
                break;
        }*/

        //int length = Integer.parseInt(in.readLine());
        byte[] bytes = new byte[1024];

        int count;
        while ((count = readFile.read(bytes)) >= 0) {
            writeFile.write(bytes, 0, count);
        }

        System.out.println("Archivo recibido.");

        readFile.close();
        writeFile.close();

        //out.close();
        in.close();

        clientSocket.close();
        serverSocket.close();
    }
}
