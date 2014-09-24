public class ProcPing extends Thread {
    public byte[] mailbox;
    private ProcPing peer;
    private PingPing parent;
    private int numMsg;
    private byte[] data;

    public ProcPing(String name, byte[] data, PingPing parent, int numMsg) {
        this.setName(name);
        this.data = data;
        this.parent = parent;
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
            send(data);
            if(i!=numMsg) {
                recv();
            }
        }
        parent.wakeup();
    }
}

