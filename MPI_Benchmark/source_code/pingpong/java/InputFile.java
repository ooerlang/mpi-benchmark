
import java.io.*;

public class InputFile {
    private FileReader fileStream = null;
    private BufferedReader in = null;

    public boolean open(String filename) {
        try {
            fileStream = new FileReader(filename);
            in = new BufferedReader(fileStream);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String readAll() {
        try {
            String content = new String();

            while(true) {
                String temp = in.readLine();
                if (temp == null) {
                    break;
                }
                content += temp + "\n";
            }
            return content;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return null;
        }
    }

    public String readLine() {
        try {
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close() {
        try {
            in.close();
            fileStream.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
