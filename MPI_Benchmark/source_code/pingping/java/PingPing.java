public class PingPing extends Thread {

    private final int NUM_TOTAL_PROC = 2;
    private int numFinishedProcess = 0;

    private long timeSpawn;
    private long timeExec;

    public long timeStart;
    public long timeEnd;

    private int dataSize;
    private int numRept;
    private String outLocation;

    public PingPing(int dataSize, int numMsg, String outLocation) {
        this.dataSize = dataSize;
        this.numRept = numMsg;
        this.outLocation = outLocation;
    }

    public void run() {
        byte[] data = generateData(dataSize);

        timeStart = timeMicroSeg();
        ProcPing p1 = new ProcPing("1", data, this, numRept);
        ProcPing p2 = new ProcPing("2", data, this, numRept);
        timeEnd = timeMicroSeg();

        timeSpawn = timeEnd - timeStart;

        timeStart = timeMicroSeg();
        p1.setPeer(p2);
        p1.start();
        p2.setPeer(p1);
        p2.start();

        sleepUntilFinish();

        timeEnd = timeMicroSeg();

        timeExec = timeEnd - timeStart;

        Store.writeResultPeer(outLocation, dataSize, numRept, timeExec, timeSpawn);
    }

    private synchronized void sleepUntilFinish() {
        while(numFinishedProcess!= NUM_TOTAL_PROC) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void wakeup() {
        numFinishedProcess++;
        notifyAll();
    }

    private byte[] generateData(int dataSize) {
        byte[] data = new byte[dataSize];
        return data;
    }

    private long timeMicroSeg() {
        return System.nanoTime()/1000;
    }
}
