package protocol;

public enum CommandEnum {

    CONNECTREQUEST("ConnectRequest"),
    ABORTINGCONNECTION("AbortingConnection"),
    CHANGINGPORT("ChangingPort"),
    BROADCAST("Broadcast"),
    MESSAGE("Message"),
    ACK("Ack"),
    PING("Ping"),
    PONG("Pong"),
    UNKNOWN("Unknown");

    private final String value;
    private CommandEnum(String value) {
        this.value = value;
    }

    /**
     * @return String name
     */
    public String getString() {
        return this.value;
    }

    /**
     * Get Enum from name
     * @param s ID
     * @return Enum
     */
    public static CommandEnum getEnum(String s){
        switch (s) {
            case "connectrequest":
            case "ConnectRequest" : return CommandEnum.CONNECTREQUEST;
            case "message": case "MESSAGE":
            case "Message" : return CommandEnum.MESSAGE;
            case "ack": case "ACK":
            case "Ack" : return CommandEnum.ACK;
            case "ping": case "PING":
            case "Ping" : return CommandEnum.PING;
            case "pong": case "PONG":
            case "Pong" : return CommandEnum.PONG;
            case "ChangingPort": case "CHANGINGPORT":
            case "changingport": return CommandEnum.CHANGINGPORT;
            case "Broadcast": case "BROADCAST":
            case "broadcast": return CommandEnum.BROADCAST;
        }
        return CommandEnum.UNKNOWN;
    }
}
