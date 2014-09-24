public class PingPong extends Thread {

    private boolean waiting = true;

    private long timeSpawn;
    private long timeExec;

    public long timeStart;
    public long timeEnd;

    private int sizeData;
    private int numRept;
    private String outLocation;

    public PingPong(int sizeData, int numMsg, String outLocation) {
        this.sizeData = sizeData;
        this.numRept = numMsg;
        this.outLocation = outLocation;
    }

    public void run() {
        byte[] data = generateData(sizeData);

        timeStart = timeMicroSeg();
        ProcPing ping = new ProcPing("1", data, this, numRept);
        ProcPong pong = new ProcPong("2", data, numRept);
        timeEnd = timeMicroSeg();

        timeSpawn = timeEnd - timeStart;

        timeStart = timeMicroSeg();

        ping.setPeer(pong);
        ping.start();
        pong.setPeer(ping);
        pong.start();

        sleepUntilFinish();
        timeEnd = timeMicroSeg();

        timeExec = timeEnd - timeStart;

        Store.writeResultPeer(outLocation, sizeData, numRept, timeExec, timeSpawn);
    }

    private synchronized void sleepUntilFinish() {
        while(waiting) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void wakeup() {
        waiting = false;
        notifyAll();
    }

    private byte[] generateData(int sizeData) {
        byte[] data = new byte[sizeData];
        return data;
    }

    private long timeMicroSeg() {
        return System.nanoTime()/1000;
    }
}
