package protocol;

import java.io.UnsupportedEncodingException;

public class TFTP {

    /**
     * Pour info (Opcode) :
     * 1 - Read request
     * 2 - Write request
     * 3 - DATA
     * 4 - Ack
     * 5 - ERROR
     */

    private TFTPOpcode opcode; // Sur 2 byte
    private String fileTitle;
    private String mode = "octet";

    private int numBloc; // Sur 2 byte
    private byte[] data; // Max 512 byte

    private int errorCode;
    private String errorMsg;

    public TFTP() {}

    public TFTPOpcode getOpcode() { return opcode; }
    public void setOpcode(TFTPOpcode opcode) {
        this.opcode = opcode;
    }
    public void setOpcode(int opcode) {
        this.opcode = TFTPOpcode.getEnum(opcode);
    }
    public String getFileTitle() {
        return fileTitle;
    }
    public void setFileTitle(String title) { this.fileTitle = title; }
    public String getMode() {
        return mode;
    }
    public void setMode(String m) { this.mode = m; }
    public int getNumBloc() {
        return numBloc;
    }
    public void setNumBloc(int numBloc) {
        this.numBloc = numBloc;
    }
    public byte[] getData() {
        return data;
    }
    public void setData(byte[] data) {
        this.data = data;
    }
    public int getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
    public String getErrorMsg() {
        return errorMsg;
    }
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public static TFTP createReadPaquet(String title) {
        /*String msg = "";
        msg += 2; // Opcode sur 2 bytes
        msg += "titre_fichier"; // Une chaîne de caractères
        msg += null; // 1 byte null (entièrement à 0)
        msg += "octet"; // Le mode (netascii|octet|mail)
        msg += null; // 1 byte null (entièrement à 0)*/
        TFTP msg = new TFTP();
        msg.setOpcode(1);
        msg.setFileTitle(title);
//        msg.setMode("octet");
        return msg;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        char c = 'a';
        byte b = (byte) c;
        int i = c;
        System.out.println(c);
        System.out.println(b);
        System.out.println(i);

        System.out.println("=================");

        String text = "ab2";
        char[] tab = text.toCharArray();
        byte[] tab2 = new byte[10];
        for(int j = 0; j < tab.length ; j++) {
            tab2[j] = (byte)tab[j];
        }
        tab2[tab.length] = -1;
        for(int j = 0; j < tab2.length ; j++) {
            System.out.println(tab2[j]);
        }

    }

}
