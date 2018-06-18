import core.Utils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ServeurManager extends Utils implements Runnable {

    private List<Serveur> clients = new ArrayList<>();

    public ServeurManager(InetAddress adr, int port) throws Exception {
        super(port, adr);
    }

    public void establishConnection(DatagramPacket p) {
        try {
            DatagramSocket sock = new DatagramSocket();
            Serveur l = new Serveur(p.getAddress(), p.getPort(), sock, this);
            clients.add(l);
            (new Thread(l)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void init() {
        System.out.println("Serveur ready");
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
        String msg = (new String(p.getData())).trim();
        System.out.println("(Serveur) Message reçu : '" + msg + "'");
        System.out.println("IP : " + p.getAddress().getHostAddress());
//        DialogProtocol message = new DialogProtocol(p);
//        if(message.isAskingConnection()) {
//            System.out.println("Client connecté");
//            establishConnection(p);
//        }
    }

    public void broadcast(String message, Serveur sender) {
        for(Serveur c : clients) {
            if(c != sender) {
                c.sendBroadcast(message);
            }
        }
    }

    // ===================== STATIC ========================

    private static int SERV_PORT = 4000;
    private static String SERV_IP = "127.0.0.2";
    private static InetAddress SERV_INET = null;

    public static void main(String[] args) {
        try {
            InetAddress adr = InetAddress.getByName(SERV_IP);
            SERV_INET = adr;
            ServeurManager serv = new ServeurManager(adr, SERV_PORT);
            (new Thread(serv)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
