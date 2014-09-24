public class ProcPong extends Thread {
    public boolean waiting = false;
    public byte[] mailbox;
    private ProcPing peer;
    private int numMsg;
    private byte[] data;

    public ProcPong(String name, byte[] data, int numMsg) {
        this.setName(name);
        this.data = data;
        this.numMsg = numMsg;
    }

    public void setPeer(ProcPing peer) {
        this.peer = peer;
    }

    private synchronized void send(byte[] msg) {
        peer.mailbox = msg.clone();
    }

    private void recv() {
        while (true) {
            synchronized (this) {
                if (mailbox != null) {
                    mailbox = null;
                    break;
                }
            }
        }
    }

    public void run() {
        for (int i = 1; i <= numMsg; i++) {
            recv();
            send(data);
        }
    }
}
