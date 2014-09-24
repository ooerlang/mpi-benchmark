public class PingPingMain {

    public static void main(String[] args) {

        int sizeMsg = Integer.parseInt(args[0]);
        int numMsg = Integer.parseInt(args[1]);

        String destination = Store.OUT_PATH + "pingping.txt";

        PingPing p = new PingPing(sizeMsg, numMsg, destination);
        p.start();
    }
}
