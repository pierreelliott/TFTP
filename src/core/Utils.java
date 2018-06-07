package core;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public abstract class Utils extends CommInterface implements Runnable {

    private boolean finished = false;

    public Utils(DatagramSocket o) throws Exception {
        super(o);
    }

    public Utils(int port, InetAddress adr) throws Exception {
        super(port, adr);
    }

    @Override
    /**
     * Fonction réalisant les traitements des clients et serveurs
     * Client et Serveur héritent de cette classe et n'ont plus à redéfinir
     * cette méthode "run", il leur suffit de redéfinir les méthodes
     * "init", "preprocess", "process", "postprocess"
     */
    public void run() {
        try {
            init();
            while(!finished) {
                preprocess();
                process();
                postprocess();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setFinished() { finished = true; }

    /* ================== STATIC ======================== */

    /*
        Fonctions réalisées dans le premier exercice du TP
     */

    public static List<Integer> scan(int dep, int fin) {
        List<Integer> list = new ArrayList<>();
        DatagramSocket ds = null;
        for(int i = dep; i <= fin; i++) {
            try {
                ds = new DatagramSocket(i);
            } catch (Exception ex) {
                list.add(i);
            } finally {
                if(ds != null) {
                    ds.close();
                }
                ds = null;
            }
        }
        return list;
    }

    public static boolean isOccuped(int port) {
        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket(port);
        } catch (Exception ex) {
            return false;
        } finally {
            if(ds != null) {
                ds.close();
            }
        }
        return true;
    }
}
