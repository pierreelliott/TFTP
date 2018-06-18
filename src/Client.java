import core.Utils;
import protocol.Error;
import protocol.TFTP;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Scanner;

public class Client extends Utils implements Runnable {

    private String SERV_IP;// = STATIC_SERV_IP;
    private int SERV_PORT = STATIC_SERV_PORT;

    private boolean hasMore = true;
    private short lastBloc = -1;
    private byte[] lastData;
    private FileInputStream fileIn;
    private FileOutputStream fileOut;
    private String distantPath = "img7.jpg";
    private String localPath = "image.jpg";

    public Client(String localPath, String distantPath, String address) throws Exception {
        super(new DatagramSocket());
        this.localPath = localPath;
        this.distantPath = distantPath;
        this.SERV_IP = address;
    }

    public void init() throws Error {
        try {
            byte[] msg = TFTP.createReadPaquet(distantPath);
            envoyer(msg, SERV_IP, SERV_PORT);
        } catch (Exception e) {
            throw new Error("Erreur création paquet", -3);
        }
    }

    public int processing() {
        try {
            init();
            int length = 10000;
            byte[] buf = new byte[length];
            DatagramPacket p = new DatagramPacket(buf, length);
            getSocket().setSoTimeout(10000);
            while(hasMore) {
                getSocket().receive(p);
                if(p.getPort() != SERV_PORT) {
                    SERV_PORT = p.getPort();
                }
                traiterReponse(p);
            }
        } catch (Exception e) {
            if(e instanceof Error) {
                return ((Error) e).getCode();
            } else {
                return -1; // Erreur inconnue côté client
            }
        }
        return 0; // Tout s'est bien passé
    }

    public void write(String file, byte[] data, int length) throws Error {
        try{
            if(fileOut == null) {
                fileOut = new FileOutputStream (file);
            }
            fileOut.write(data,0, length);
            if(length != 512) {
                fileOut.close();
            }
        } catch(IOException ex) {
            throw new Error("File access Problem", -2);
        }
    }

    public byte[] read(String file) {
        byte[] buffer= new byte[512];
        int b;
        try{
            if(fileIn == null) {
                fileIn = new FileInputStream(file);
            }

            for (int i=0; i < buffer.length; i++){
                b = fileIn.read(); // Lire un caractère
                if (b == -1) // Si c'est la fin de lecture (ie, y a plus rien), on arrête
                    break;
                buffer[i]=(byte)b;
            }

            // TODO Trouver une condition pour savoir qu'on peut fermer
            fileIn.close();
        } catch(IOException ex) {
            System.out.println(ex);
        }
        return buffer;
    }

    public void traiterReponse(DatagramPacket p) throws Exception {
        byte[] data = p.getData();
        int length = p.getLength();
        TFTP response = TFTP.readBytes(data);
        byte[] tab;
        switch (response.getOpcode()) { // Si le paquet envoyé par le serveur est un ...
            // Seules les réponses à des paquets DATA et ERROR sont implémentées
            case READ:
                break;
            case WRITE:
                break;
            case DATA:
                // Si le lastBloc est identique à response.getNumBloc() => on ne réécrit pas dans le fichier
                if(lastBloc != response.getNumBloc()) {
                    tab = response.getData();
                    write(localPath, tab, length - 4);
                }
                hasMore = (length == 516);
                lastBloc = response.getNumBloc();
                if(lastBloc < 0) {
                    throw new Error("Unknown transfer ID.", -5);
                }

                tab = TFTP.createAckPaquet(response.getNumBloc());
                envoyer(tab, SERV_IP, SERV_PORT);
                break;
            case ACK: // Pas fini
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
//                System.out.println("Erreur (code "+ response.getErrorCode() +") : " + response.getErrorMsg());
                throw new Error(response.getErrorMsg(), response.getErrorCode());
        }
    }

    /* ======================================= */

    public static void input() {
        int res;
        while(true) {
            System.out.println("=========================================================");
            System.out.println("Entrer commande sous la forme : commande localFile distantFile ip_serveur");
            System.out.println("Avec commande = (get|receive)");
            Scanner scan = new Scanner(System.in);
            String text = scan.nextLine();
            if(!text.equalsIgnoreCase("")) {
                String[] tab = text.split(" ");
                if(tab[0].matches("(get|receive)")) {
                    res = receiveFile(tab[1], tab[2], tab[3]);
                    if( res == 0) {
                        System.out.println("La récupération du fichier s'est déroulée sans encombres (Code " + res + ")");
                    } else {
                        System.out.println(TFTP.getMessageForCode(res));
                    }
                } else {
                    System.out.println("Commande non reconnue");
                }
            } else {
                break;
            }
        }
    }

    private static int STATIC_SERV_PORT = 69;
    private static String STATIC_SERV_IP = "127.0.0.1";

    public static int receiveFile(String localPath, String distantPath, String address) {
        Client client = null;
        try {
            client = new Client(localPath, distantPath, address);
            return client.processing();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void main(String[] args) {
        // Exemple d'utilisation de la méthode ReceiveFile

//        String ad = "134.214.117.162";
//        int ret = receiveFile("image.jpg","img7.jpg", ad);
//        System.out.println(ret);
//        if( ret == 0) {
//            System.out.println("La récupération du fichier s'est déroulée sans encombres.");
//        } else {
//            System.out.println(TFTP.getMessageForCode(ret));
//        }

        (new Thread(() -> {
            input();
        })).start();
    }
}
