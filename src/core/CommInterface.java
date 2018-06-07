package core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;

public abstract class CommInterface {

    private DatagramSocket socket;

    public CommInterface(DatagramSocket sock) {
        socket = sock;
    }

    public CommInterface(int port, InetAddress adr) throws Exception {
        socket = new DatagramSocket(port, adr);
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public DatagramPacket receive(DatagramPacket p) {
        try {
            socket.receive(p);
            return p;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean envoyer(byte[] data, InetAddress ip, int port) {
        DatagramPacket dp = new DatagramPacket(data, data.length, ip, port);
        try {
            socket.send(dp);
        } catch (IOException e) {
            System.err.println("Erreur envoi :\n" + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean envoyer(String data, InetAddress ip, int port) {
        try {
            return envoyer(data.getBytes("UTF-8"), ip, port);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean envoyer(byte[] data, String ip, int port) {
        InetAddress ad = null;
        try {
            ad = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            System.err.println("Erreur résolution adresse IP");
            return false;
        }
        return envoyer(data, ad, port);
    }

    public boolean envoyer(String data, String ip, int port) {
        InetAddress ad = null;
        try {
            ad = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            System.err.println("Erreur résolution adresse IP");
            return false;
        }
        return envoyer(data, ad, port);
    }

    public void init() {}

    public void preprocess() {}

    public void postprocess() {}

    public void process() {}
}
