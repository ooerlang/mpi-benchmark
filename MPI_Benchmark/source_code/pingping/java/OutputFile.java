import java.io.*;

public class OutputFile {
    private FileWriter fileStream = null;
    private BufferedWriter out = null;

    public boolean open(String filename) {
        try {
            fileStream = new FileWriter(filename);
            out = new BufferedWriter(fileStream);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void write(String content) {
        try {
            out.write(content);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());

        }
    }

    public void close() {
        try {
            out.close();
            fileStream.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

}
