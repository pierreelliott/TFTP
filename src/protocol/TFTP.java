package protocol;

import java.io.ByteArrayOutputStream;
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
    public void setNumBloc(short numBloc) {
        this.numBloc = numBloc;
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
    public void setErrorCode(short errorCode) {
        this.errorCode = errorCode;
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
        outputStream.write( this.numBloc );
        outputStream.write( this.data );

        return outputStream.toByteArray( );
    }

    private byte[] ackMessage() throws Exception {
        byte[] opcode = {0, 4};
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write( opcode );
        outputStream.write( this.numBloc );

        return outputStream.toByteArray( );
    }

    private byte[] errorMessage() throws Exception {
        byte[] opcode = {0, 5};
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write( opcode );
        outputStream.write( this.errorCode );
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

    public static byte[] createWritePaquet() throws Exception {
        // TODO
        return new byte[0];
    }

    public static byte[] createDataPaquet() throws Exception {
        // TODO
        // Vérifier que le tableau de byte passé en paramètre n'est pas supérieur à 512 octets
        // Vérifier également que le numéro de bloc est supérieur à 0 (gaffe, c'est un 'short')
        return new byte[0];
    }

    public static byte[] createAckPaquet() throws Exception {
        // TODO
        return new byte[0];
    }

    public static byte[] createErrorPaquet() throws Exception {
        // TODO
        return new byte[0];
    }

    public static TFTP readBytes(byte[] bytes) {
        TFTP protoc = new TFTP();
        protoc.setOpcode((int)bytes[1]);
        switch (protoc.getOpcode()) { // FIXME (à finir)
            case READ:
                break;
            case WRITE:
                break;
            case DATA:
                break;
            case ACK:
                break;
            case ERROR: default:
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
