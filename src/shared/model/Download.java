package shared.model;

import javafx.concurrent.Task;

import java.io.*;
import java.net.Socket;

public class Download extends Task<String> {

    private Socket clientSocket;

    public Download(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    protected String call() throws Exception {

        DataInputStream in = new DataInputStream(clientSocket.getInputStream());
        InputStream readFile = clientSocket.getInputStream();
        OutputStream writeFile = null;

        String nameFile = in.readUTF();
        long length = in.readLong();

        try {
            writeFile = new FileOutputStream(Utils.getPath() + "r_" + nameFile);
        } catch (FileNotFoundException e) {
            System.err.println("A ocurrido un problema con el archivo.");
        }

        updateValue(nameFile);
        updateProgress(0, length);

        int count;
        long size = 0;

        byte[] bytes = new byte[1024];

        while ((count = readFile.read(bytes)) > 0) {
            writeFile.write(bytes, 0, count);
            updateProgress(size += count, length);
        }

        readFile.close();
        writeFile.close();

        in.close();
        clientSocket.close();

        return nameFile;
    }
}
