import java.util.concurrent.Semaphore;

public class PingPong extends Thread{
    private Semaphore semaphore;

    private long timeSpawn;
    private long timeExec;

    public long timeStart;
    public long timeEnd;

    private int tamDados;
    private int qtdRept;
    private int pairsN;
    private String outLocation;

    public PingPong(int tamDados, int qtdMsg, int pairsN, String outLocation) {
        this.tamDados = tamDados;
        this.qtdRept = qtdMsg;
        this.pairsN = pairsN;
        this.outLocation = outLocation;
        this.semaphore = new Semaphore(-pairsN + 1);
    }

    public void run() {
        byte[] dado = generateData(tamDados);
        ProcPing[] procsPing = new ProcPing[pairsN];
        ProcPong[] procsPong = new ProcPong[pairsN];

        timeStart = timeMicroSeg();
        for (int i = 0; i < pairsN; i++) {
            procsPing[i] = new ProcPing("" + (2*i), dado, this, qtdRept);
            procsPong[i] = new ProcPong("" + (2*i+1), dado, qtdRept);
        }
        timeEnd = timeMicroSeg();
        timeSpawn = timeEnd - timeStart;

        timeStart = timeMicroSeg();
        for (int i = 0; i < pairsN; i++) {
            procsPing[i].setPeer(procsPong[i]);
            procsPong[i].setPeer(procsPing[i]);

            procsPing[i].start();
            procsPong[i].start();
        }
        dormirAteTerminar();
        timeEnd = timeMicroSeg();
        timeExec = timeEnd - timeStart;

        Salvar.writeResultPeer(outLocation, tamDados, qtdRept, timeExec, timeSpawn);
    }

    private void dormirAteTerminar() {
        try {
            semaphore.acquire();
        }
        catch (InterruptedException ie) {}
    }

    public void acordar() {
        semaphore.release();
    }

    private byte[] generateData(int tamDados) {
        byte[] dado = new byte[tamDados];
        return dado;
    }

    private long timeMicroSeg() {
        return System.nanoTime()/1000;
    }
}
