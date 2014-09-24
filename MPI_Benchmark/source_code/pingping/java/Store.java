public class Store {
    public static String OUT_PATH = "../../docs/java/out_java_";

    public static void writeResultAlltoall(String outLocation, int dataSize, int numRept, int numProcs, long timeMin, long timeMax, long timeAvg, long timeSpawn) {
        String header = String.format("%-10s\t %-15s\t %-15s\t %-17s\t %-17s\t %-17s\t %-17s\t%n",
                                      "#bytes", "#repetitions", "#processes", "t_min[usec]", "t_max[usec]", "t_avg[usec]", "spawn_time[usec]");
        String result = String.format("%-9d\t %-14d\t %-14d\t %-16d\t %-16d\t %-16d\t %-16d\t%n",
                                      dataSize, numRept, numProcs, timeMin, timeMax, timeAvg,timeSpawn);

        writeResult(outLocation, header, result);
    }

    public static void writeResultMulti(String outLocation, int dataSize, int numProcs, int numRept, long timeExec, long timeSpawn) {
        String header = String.format("%-13s %-13s %-12s %-16s %-12s %n", "bytes", "processos", "repeticoes", "tempo exec[usec]", "tempo de spawn[usec]");
        String result = String.format("%-13d\t %-12d\t %-12d\t %-15d\t %-20d\t %n", dataSize, numProcs, numRept, timeExec, timeSpawn);

        writeResult(outLocation, header, result);
    }

    public static void writeResultPeer(String outLocation, int dataSize, int numRept, long timeExec, long timeSpawn) {
        String header = String.format("%-13s\t %-12s\t %-15s\t %-12s\t %n", "bytes", "repeticoes", "t[usec]", "tempo de spawn[usec]");
        String result = String.format("%-13d\t %-12d\t %-14d\t %-20d\t %n",dataSize, numRept,timeExec,timeSpawn);

        writeResult(outLocation, header, result);
    }

    private static void writeResult(String outLocation, String header, String result) {
        InputFile file = new InputFile();
        if (!file.open(outLocation)) {
            OutputFile fileOut = new OutputFile();
            fileOut.open(outLocation);
            fileOut.write(header);
            fileOut.close();
        }

        append(outLocation, result);
    }

    private static boolean append(String filename, String content) {
        InputFile fileIn = new InputFile();
        if(fileIn.open(filename)) {
            String strIn = fileIn.readAll();
            fileIn.close();

            String strOut = strIn + content;

            OutputFile fileOut = new OutputFile();
            fileOut.open(filename);
            fileOut.write(strOut);
            fileOut.close();
            return true;
        } else {
            return false;
        }
    }
}
