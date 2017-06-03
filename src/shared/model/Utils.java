package shared.model;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class Utils {

    public static String myIP() throws IOException {
        URL connection = new URL("http://checkip.amazonaws.com/");
        URLConnection con = connection.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

        return reader.readLine();
    }

    public static String getPath() {
        Properties properties = new Properties();
        InputStream in = null;

        try {
            in = new FileInputStream("config.properties");
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return properties.getProperty("defaultPatch");
    }

    public void setPath(String path) {
        Properties properties = new Properties();
        OutputStream out = null;

        try {
            out = new FileOutputStream("config.properties");
            properties.setProperty("defaultPatch", path);

            properties.store(out, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
