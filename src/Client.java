import core.Utils;
import protocol.ConsoleProtocol;
import protocol.DialogProtocol;
import protocol.TFTP;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class Client extends Utils implements Runnable {

    private String SERV_IP = STATIC_SERV_IP;
    private int SERV_PORT = STATIC_SERV_PORT;

    private boolean hasMore = true;
    private short lastBloc = -1;
    private byte[] lastData;
    private FileInputStream fileIn;
    private FileOutputStream fileOut;
    private String filePath = "img7.jpg";
//    private String filePath = "index.html";

    public Client() throws Exception {
        super(new DatagramSocket());
    }

    public void init() {
        // Démarre la gestion de la console côté client
        // L'utilisateur peut utiliser celle-ci pour envoyer des messages à travers le réseau
//        (new Thread(() -> {
//            input();
//        })).start();
        try {
            byte[] msg = TFTP.createReadPaquet(filePath);
            envoyer(msg, SERV_IP, SERV_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void process() {
        int length = 10000;
        byte[] buf = new byte[length];
        DatagramPacket p = new DatagramPacket(buf, length);
        try {
            while(hasMore) {
                getSocket().receive(p);
                // TODO Il faudrait ajouter une tempo
                if(p.getPort() != SERV_PORT) {
                    SERV_PORT = p.getPort();
                }
                traiterReponse(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        setFinished();
    }

    public void write(String file, byte[] data, int length) {
        try{
            if(fileOut == null) {
                fileOut = new FileOutputStream (file);
            }
            fileOut.write(data,0, length);
            if(length != 512) {
                fileOut.close();
            }
        } catch(IOException ex) {
            System.out.println(ex);
        }
    }

    public byte[] read() {
        byte[] buffer= new byte[512];
        int b;
        try{
            if(fileIn == null) {
                fileIn = new FileInputStream("d:\\temp\\test.txt");
            }

            for (int i=0; i < buffer.length; i++){
                b = fileIn.read(); // Lire un caractère
                if (b == -1) // Si c'est la fin de lecture (ie, y a plus rien), on arrête
                    break;
                buffer[i]=(byte)b;
            }

            // Trouver une condition pour savoir qu'on peut fermer
            fileIn.close();
        } catch(IOException ex) {
            System.out.println(ex);
        }
        return buffer;
    }

    public void traiterReponse(DatagramPacket p) {
        byte[] data = p.getData();
        int length = p.getLength();
        try {
            TFTP response = TFTP.readBytes(data);
            byte[] tab;
            switch (response.getOpcode()) {
                case READ:
                    break;
                case WRITE:
                    break;
                case DATA:
                    // TODO Il faut écrire le résultat dans le fichier
                    // Si le lastBloc est identique à response.getNumBloc() => on ne réécrit pas dans le fichier
                    if(lastBloc != response.getNumBloc()) {
                        if(lastBloc == -1) lastBloc = 0;
                        tab = response.getData();
                        write(filePath, tab, length - 4);
                    }
                    hasMore = (length == 516);
                    lastBloc = response.getNumBloc();
                    if(lastBloc < 0) {
                        System.out.println("Aïe");
                    }

                    tab = TFTP.createAckPaquet(response.getNumBloc());
                    envoyer(tab, SERV_IP, SERV_PORT);
                    break;
                case ACK:
                    if(lastBloc != response.getNumBloc()) {
                        System.out.println("Message reçu");
                        // Lire le fichier
                        // Lui envoyer la suite
                    } else {
                        System.out.println("Message perdu\nRenvoi du fichier");
                        envoyer(lastData, SERV_IP, SERV_PORT);
                    }
                    break;
                case ERROR:
                    System.out.println("Erreur (code "+ response.getErrorCode() +") : " + response.getErrorMsg());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private static int STATIC_SERV_PORT = 69;
    private static String STATIC_SERV_IP = "127.0.0.1";

    public static void main(String[] args) {
        try {
            Client client = new Client();

            (new Thread(client)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
