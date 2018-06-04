import core.Utils;
import protocol.ConsoleProtocol;
import protocol.DialogProtocol;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Scanner;

public class Client extends Utils implements Runnable {

    private String SERV_IP = STATIC_SERV_IP;
    private int SERV_PORT = STATIC_SERV_PORT;

    public Client() throws Exception {
        super(new DatagramSocket());
    }

    public void init() {
        // Démarre la gestion de la console côté client
        // L'utilisateur peut utiliser celle-ci pour envoyer des messages à travers le réseau
        (new Thread(() -> {
            input();
        })).start();
    }

    public void process() {
        int length = 10000;
        byte[] buf = new byte[length];
        DatagramPacket p = new DatagramPacket(buf, length);
        try {
            getSocket().receive(p);
            traiterReponse(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void traiterReponse(DatagramPacket p) {
        DialogProtocol message = new DialogProtocol(p);
        if(message.isChangingPort()) {
            System.out.println("Connecté au serveur");
            SERV_PORT = p.getPort();
        } else if(message.isAck()) {
            System.out.println("Message reçu par le serveur");
        } else if(message.isPong()) {
            System.out.println("Pong !");
        } else if(message.isBroadcast()) {
            System.out.println("Broadcasted : " + message.getContent());
        }
    }

    public String input() {
        while(true) {
            Scanner scan = new Scanner(System.in);
            String text = scan.nextLine();
            if(!text.equalsIgnoreCase("")) {
                if(this.envoyer(
                        (ConsoleProtocol.getProtocoleMessage(text)).toString(),
                        SERV_IP, SERV_PORT))
                {
                    System.out.println("Message envoyé");
                } else {
                    System.out.println("Erreur envoi");
                }
            }
        }
    }

    /* ======================================= */

    private static int STATIC_SERV_PORT = 4000;
    private static String STATIC_SERV_IP = "127.0.0.2";

    public static void main(String[] args) {
        try {
            Client client = new Client();
            boolean env = client.envoyer(DialogProtocol.requestConnection(client),
                    STATIC_SERV_IP, STATIC_SERV_PORT);
            if(env)
                System.out.println("Connexion demandée");
            else
                System.out.println("Erreur connexion");

            (new Thread(client)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
