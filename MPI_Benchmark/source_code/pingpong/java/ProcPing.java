public class ProcPing extends Thread {
    public boolean waiting = false;
    public byte[] mailbox;
    private ProcPong peer;
    private PingPong parent;
    private int numMsg;
    private byte[] data;

    public ProcPing(String name, byte[] data, PingPong parent, int numMsg) {
        this.setName(name);
        this.data = data;
        this.parent = parent;
        this.numMsg = numMsg;
    }

    public void setPeer(ProcPong peer) {
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
            send(data);
            recv();
        }
        parent.wakeup();
    }
}
