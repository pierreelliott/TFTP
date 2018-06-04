package protocol;

public enum MessageTypeEnum {

    REQUEST("Request"),
    RESPONSE("Response"),
    BLANK("");

    private final String value;
    private MessageTypeEnum(String value) {
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
    public static MessageTypeEnum getEnum(String s){
        switch (s) {
            case "Request" : return MessageTypeEnum.REQUEST;
        }
        return MessageTypeEnum.BLANK;
    }
}
