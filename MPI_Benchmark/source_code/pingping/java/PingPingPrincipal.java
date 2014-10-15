public class PingPingPrincipal {

	public static void main(String[] args) { 

		int tamMsg = Integer.parseInt(args[0]);
		int qtdMsg = Integer.parseInt(args[1]);
    int pairsN = Integer.parseInt(args[2]);

		String localSaida = Salvar.OUT_PATH + "pingping.txt";

		PingPing p = new PingPing(tamMsg, qtdMsg, pairsN, localSaida);
		p.start();
	}
}
