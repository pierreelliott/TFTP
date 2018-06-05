package protocol;

public enum TFTPOpcode {

    READ("Read"),
    WRITE("Write"),
    DATA("Data"),
    ACK("Ack"),
    ERROR("Error");

    private final String value;
    private TFTPOpcode(String value) {
        this.value = value;
    }
    public int toInt() {
        switch (value) {
            case "Read": return 1;
            case "Write": return 2;
            case "Data": return 3;
            case "Ack": return 4;
            case "Error": default: return 5;
        }
    }

    public static TFTPOpcode getEnum(byte[] code){
        if(code[0] == 0 ) {
            switch(code[1]) {
                case 1: return READ;
                case 2: return WRITE;
                case 3: return DATA;
                case 4: return ACK;
                case 5:
                default: return ERROR;
            }
        } else {
            return ERROR;
        }
    }

    public static TFTPOpcode getEnum(int code) {
        byte[] b = {0, (byte)code};
        return getEnum(b);
    }
}
