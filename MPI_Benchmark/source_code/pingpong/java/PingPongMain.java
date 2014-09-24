public class PingPongMain {

    public static void main(String[] args) {
        int sizeMsg = Integer.parseInt(args[0]);
        int numMsg = Integer.parseInt(args[1]);

        String destination = Store.OUT_PATH + "pingpong.txt";

        PingPong p = new PingPong(sizeMsg, numMsg, destination);
        p.start();
    }

}
