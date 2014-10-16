import java.util.concurrent.Semaphore;

public class PingPing extends Thread{
    private Semaphore semaphore;

    private int qtdProcTotal;

    private long timeSpawn;
    private long timeExec;

    public long timeStart;
    public long timeEnd;

    private int tamDados;
    private int qtdRept;
    private int pairsN;
    private String outLocation;

    public PingPing(int tamDados, int qtdMsg, int pairsN, String outLocation) {
        this.tamDados = tamDados;
        this.qtdRept = qtdMsg;
        this.pairsN = pairsN;
        this.outLocation = outLocation;
        this.semaphore = new Semaphore(-(2 * pairsN) + 1);
    }

    public void run() {
        byte[] dado = generateData(tamDados);
        ProcPing procs[] = new ProcPing[2 * pairsN];
        timeStart = timeMicroSeg();

        timeStart = timeMicroSeg();
        for (int i = 0; i < procs.length; i+= 2) {
            procs[i] = new ProcPing("" + i, dado, this, qtdRept);
            procs[i + 1] = new ProcPing("" + (i + 1), dado, this, qtdRept);
        }
        timeEnd = timeMicroSeg();
        timeSpawn = timeEnd - timeStart;

        timeStart = timeMicroSeg();
        for (int i = 0; i < procs.length; i+= 2) {
            procs[i].setPeer(procs[i+1]);
            procs[i+1].setPeer(procs[i]);

            procs[i].start();
            procs[i+1].start();
        }
        dormirAteTerminar();
        timeEnd = timeMicroSeg();

        timeExec = timeEnd - timeStart;
        Salvar.writeResultPeer(outLocation, tamDados, qtdRept, timeExec, timeSpawn);
    }

    private void dormirAteTerminar() {
        try {
            this.semaphore.acquire();
        } catch (InterruptedException ie)
        {
        }
    }

    public synchronized void acordar() {
        this.semaphore.release();
    }

    private byte[] generateData(int tamDados) {
        byte[] dado = new byte[tamDados];
        return dado;
    }

    private long timeMicroSeg() {
        return System.nanoTime()/1000;
    }
}
