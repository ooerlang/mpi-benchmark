public class PingPongPrincipal {

	public static void main(String[] args) {
		int tamMsg = Integer.parseInt(args[0]);
		int qtdMsg = Integer.parseInt(args[1]);
		int pairsN = Integer.parseInt(args[2]);
        String localSaida = (args.length >= 4) ? args[3] : null;

		PingPong p = new PingPong(tamMsg, qtdMsg, pairsN, localSaida);
		p.start();
	}

}