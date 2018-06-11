package protocol;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;

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

    private short numBloc; // Plus simple à écrire en byte
    private byte[] data; // Max 512 byte

    private short errorCode; // Plus simple à écrire en byte
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
    public short getNumBloc() {
        return numBloc;
    }
    public byte[] getNumBlocBytes() {
        ByteBuffer dbuf = ByteBuffer.allocate(2);
        dbuf.putShort(this.numBloc);
        return dbuf.array();
    }
    public void setNumBloc(short numBloc) {
        this.numBloc = numBloc;
    }
    public void setNumBloc(byte[] tab) {
        if(tab.length != 2) return;
        ByteBuffer wrapped = ByteBuffer.wrap(tab);
        this.numBloc = wrapped.getShort();
    }
    public byte[] getData() {
        return data;
    }
    public void setData(byte[] data) {
        this.data = data;
    }
    public short getErrorCode() {
        return errorCode;
    }
    public byte[] getErrorCodeBytes() {
        ByteBuffer dbuf = ByteBuffer.allocate(2);
        dbuf.putShort(this.errorCode);
        return dbuf.array();
    }
    public void setErrorCode(short errorCode) {
        this.errorCode = errorCode;
    }
    public void setErrorCode(byte[] tab) {
        if(tab.length != 2) return;
        ByteBuffer wrapped = ByteBuffer.wrap(tab);
        this.errorCode = wrapped.getShort();
    }
    public String getErrorMsg() {
        return errorMsg;
    }
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public byte[] toByte() throws Exception {
        switch (opcode) {
            case READ: return readMessage();
            case WRITE: return writeMessage();
            case DATA: return dataMessage();
            case ACK: return ackMessage();
            case ERROR: default: return errorMessage();
        }
    }

    private byte[] readMessage() throws Exception {
        byte[] opcode = {0, 1};
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write( opcode );
        outputStream.write( this.fileTitle.getBytes("UTF-8") );
        // Ne pas oublier le charsetName dans le getBytes ! (Utiliser UTF-8 pour plus de compatibilité)
        outputStream.write( (byte)0 );
        outputStream.write( this.mode.getBytes("UTF-8") );
        outputStream.write( (byte)0 );

        return outputStream.toByteArray( );
    }

    private byte[] writeMessage() throws Exception {
        byte[] opcode = {0, 2};
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write( opcode );
        outputStream.write( this.fileTitle.getBytes("UTF-8") );
        // Ne pas oublier le charsetName dans le getBytes ! (Utiliser UTF-8 pour plus de compatibilité)
        outputStream.write( (byte)0 );
        outputStream.write( this.mode.getBytes("UTF-8") );
        outputStream.write( (byte)0 );

        return outputStream.toByteArray( );
    }

    private byte[] dataMessage() throws Exception {
        byte[] opcode = {0, 3};
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write( opcode );
        outputStream.write( this.getNumBlocBytes() );
        outputStream.write( this.data );

        return outputStream.toByteArray( );
    }

    private byte[] ackMessage() throws Exception {
        byte[] opcode = {0, 4};
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write( opcode );
        outputStream.write( this.getNumBlocBytes() );

        return outputStream.toByteArray( );
    }

    private byte[] errorMessage() throws Exception {
        byte[] opcode = {0, 5};
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write( opcode );
        outputStream.write( this.getErrorCodeBytes() );
        outputStream.write( this.errorMsg.getBytes("UTF-8") );
        outputStream.write( (byte)0 );

        return outputStream.toByteArray( );
    }

    // ======================= STATIC ============================

    public static byte[] createReadPaquet(String title) throws Exception {
        TFTP msg = new TFTP();
        msg.setOpcode(1);
        msg.setFileTitle(title);
//        msg.setMode("octet");
        return msg.toByte();
    }

    public static byte[] createWritePaquet(String title) throws Exception {
        TFTP msg = new TFTP();
        msg.setOpcode(2);
        msg.setFileTitle(title);
//        msg.setMode("octet");
        return msg.toByte();
    }

    public static byte[] createDataPaquet(byte[] data, short numBloc) throws Exception {
        if (data.length > 512)
            System.out.println("Erreur lors de la création du datapacket : " + "taille des données = " + data.length + " > 512 o");
        else if (numBloc <= 0)
            System.out.println("Erreur lors de la création du datapacket : " + "numéro de bloc = " + numBloc + " < 1");
        else {
            TFTP msg = new TFTP();
            msg.setOpcode(3);
            msg.setData(data);
            msg.setNumBloc(numBloc);
            return msg.toByte();
        }
        return null;
    }

    public static byte[] createAckPaquet(short numBloc) throws Exception {
        if (numBloc <= 0)
            System.out.println("Erreur lors de la création du ackpackect : " + "numéro de bloc = " + numBloc + " < 1");
        else {
            TFTP msg = new TFTP();
            msg.setOpcode(4);
            msg.setNumBloc(numBloc);
            return msg.toByte();
        }
        return null;
    }

    public static String getMessageForCode(int errorCode) {
        String errorMsg = "";
        switch (errorCode) {
            case -2: errorMsg = "Problème d'accès au fichier local"; break;
            case -1 : errorMsg = "Erreur inconnue côté client"; break;
                // Erreurs TFTP "normales" en dessous
            case 0 :  errorMsg = "Not defined, see error message (if any)."; break;
            case 1 :  errorMsg = "File not found."; break;
            case 2 :  errorMsg = "Access violation."; break;
            case 3 :  errorMsg = "Disk full or allocation exceeded."; break;
            case 4 :  errorMsg = "Illegal TFTP operation."; break;
            case 5 :  errorMsg = "Unknown transfer ID."; break;
            case 6 :  errorMsg = "File already exists."; break;
            case 7 :  errorMsg = "No such user."; break;
            default :
                errorMsg = "Code d'erreur inconnu = " + errorCode;
                return null;
        }
        return errorMsg;
    }

    public static byte[] createErrorPaquet(short errorCode) throws Exception {
        String errorMsg = getMessageForCode(errorCode);
        TFTP msg = new TFTP();
        msg.setOpcode(4);
        msg.setErrorCode(errorCode);
        msg.setErrorMsg(errorMsg);
        return msg.toByte();
    }

    public static TFTP readBytes(byte[] bytes) throws Exception {
//        System.out.println("Bytes :");
//        for(int i = 0; i < bytes.length; i++) {
//            System.out.println(bytes[i]);
//        }
//        System.out.println("=================================");

        TFTP protoc = new TFTP();
        protoc.setOpcode((int)bytes[1]);
        switch (protoc.getOpcode()) { // FIXME (à finir)
            case READ:
                break;
            case WRITE:
                break;
            case DATA:
                byte[] nbloc1 = { bytes[2], bytes[3] };
                protoc.setNumBloc(nbloc1); // La conversion est fausse
                protoc.setData(Arrays.copyOfRange(bytes, 4, 516));
                break;
            case ACK:
                byte[] nbloc2 = { bytes[2], bytes[3] };
                protoc.setNumBloc(nbloc2);
                break;
            case ERROR: default:
                byte[] nbloc3 = { bytes[2], bytes[3] };
                protoc.setErrorCode(nbloc3);
                protoc.setErrorMsg(new String(Arrays.copyOfRange(bytes, 4, bytes.length-1), "UTF-8"));
                break;
        }
        return protoc;
    }

    public static void main(String[] args) throws Exception {
        char c = 'a';
        byte b = (byte) c;
        short i = 4000;
        System.out.println(c);
        System.out.println(b);
        System.out.println(i);

        System.out.println("=================");

        String text = "ab2";
        byte[] tab = text.getBytes("UTF-8");
        for(int j = 0; j < tab.length ; j++) {
            System.out.println(tab[j]);
        }
    }

}
