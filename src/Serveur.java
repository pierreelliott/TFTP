import core.Utils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Serveur extends Utils implements Runnable {

    private InetAddress ipClient;
    private int portClient;
    private boolean samePortAsParent = false;

    private ServeurManager parentServeur;

    public Serveur(InetAddress ip, int port, DatagramSocket ds, ServeurManager parent) throws Exception {
        super(ds);
        ipClient = ip;
        portClient = port;
        parentServeur = parent;
    }

    public boolean traitement(DatagramPacket p) {
//        DialogProtocol text = new DialogProtocol(p);
////        System.out.println("=============================");
////        System.out.println("Un client a envoyé un message");
//
////        System.out.println(text);
//        if(text.isPing()) {
//            envoyer(DialogProtocol.pong(), ipClient, portClient);
//        } else {
//            envoyer(DialogProtocol.acknowledgeRequest(), ipClient, portClient);
//        }
////        System.out.println("Message traité");
//
//        if(text.isAbortingConnection()) {
//            return false;
//        }

        return true;
    }

    public void sendBroadcast(String message) {
        envoyer(message, ipClient, portClient);
    }

    @Override
    public void init() {
//        DialogProtocol response = new DialogProtocol();
//        response.setCommand(CommandEnum.CHANGINGPORT);
//        response.setContent("");
//        envoyer(response.toString(), ipClient, portClient);
    }

    @Override
    public void process() {
        int length = 10000;
        byte[] buf;
        DatagramPacket p;
        buf = new byte[length];
        p = new DatagramPacket(buf, length);
        p = receive(p);
        if(p == null) {
            System.out.println("Problème réception");
            return;
        }

        traitement(p);
    }
}
