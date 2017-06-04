package sendFiles.model;

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
        InputStream downloadFile = clientSocket.getInputStream();
        OutputStream saveFile = null;

        String nameFile = in.readUTF();
        long sizeFile = in.readLong();

        try {
            saveFile = new FileOutputStream(Utils.getPath() + "r_" + nameFile);
        } catch (FileNotFoundException e) {
            System.err.println("A ocurrido un problema con el archivo.");
        }

        updateValue(nameFile);
        updateProgress(0, sizeFile);

        int count;
        long size = 0;

        byte[] bytes = new byte[1024];

        while ((count = downloadFile.read(bytes)) > 0) {
            saveFile.write(bytes, 0, count);
            saveFile.flush();
            updateProgress(size += count, sizeFile);
        }

        downloadFile.close();
        saveFile.close();

        in.close();
        clientSocket.close();

        return nameFile;
    }
}
