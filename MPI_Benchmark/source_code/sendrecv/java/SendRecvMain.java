import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SendRecvMain {
    public static void main(String[] args) {
        int sizeMsg = Integer.parseInt(args[0]);
        int num_proc = Integer.parseInt(args[1]);
        int num_rep = Integer.parseInt(args[2]);

        ExecutorService executor = Executors.newFixedThreadPool(num_proc);
        String destination = Store.OUT_PATH + "sendrecv.txt";

        SendRecv ring = new SendRecv (destination, num_proc, num_proc, num_rep, sizeMsg, executor);
        executor.execute(ring);
    }
}
