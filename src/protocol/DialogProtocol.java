package protocol;

import java.net.DatagramPacket;
import java.util.regex.Pattern;

public class DialogProtocol {

    protected CommandEnum command;
    private String rawData;
    private String content;

    public DialogProtocol() {}

    public DialogProtocol(String data) {
        this.rawData = data;
        this.command = getCommand(data);
        this.content = getContent(data);
    }

    public DialogProtocol(DatagramPacket p) {
        this((new String(p.getData())).trim());
    }

    public void setCommand(CommandEnum command) {
        this.command = command;
    }
    public void setCommand(String command) {
        this.command = CommandEnum.getEnum(command);
    }
    public void setContent(String content) {
        this.content = content;
    }

    public boolean isConnection() { return command == CommandEnum.CONNECTREQUEST; }
    public boolean isAskingConnection() { return command == CommandEnum.CONNECTREQUEST; }
    public boolean isAbortingConnection() { return command == CommandEnum.ABORTINGCONNECTION; }
    public boolean isChangingPort() { return command == CommandEnum.CHANGINGPORT; }
    public boolean isMessage() { return command == CommandEnum.MESSAGE; }
    public boolean isPing() { return command == CommandEnum.PING; }
    public boolean isPong() { return command == CommandEnum.PONG; }
    public boolean isAck() { return command == CommandEnum.ACK; }
    public boolean isBroadcast() { return command == CommandEnum.BROADCAST; }

    public String getRawData() { return rawData; }
    public String getContent() { return content; }

    @Override
    public String toString() {
        return "DialogProtocol{\ncommand='" + command + "'\ncontent='" + content + "'\n}";
    }

    // =========================================

    public static String requestConnection(Object asker) {
        return "DialogProtocol{\ncommand='ConnectRequest'\ncontent='hello'\n}";
    }
    public static String acknowledgeRequest() {
        return "DialogProtocol{\ncommand='ACK'\ncontent='Bien reçu'\n}";
    }
    public static String ping() {
        return "DialogProtocol{\ncommand='Ping'\ncontent=''\n}";
    }
    public static String pong() {
        return "DialogProtocol{\ncommand='Pong'\ncontent=''\n}";
    }

    public static CommandEnum getCommand(String message) {
        if(message.startsWith("DialogProtocol{\n")) {
            String tab[] = message.split("\n");
            for(int i = 1; i < tab.length; i++) {
                if(tab[i].startsWith("command=")) {
                    return CommandEnum.getEnum(tab[i].replace("'", "").replace("command=",""));
                }
            }
        }
        return CommandEnum.UNKNOWN;
    }

    public static String getContent(String message) {
        if(message.startsWith("DialogProtocol{\n")) {
            String tab[] = message.split("content=");
            if(tab.length != 2) {
                return "";
            } else {
                return tab[1].substring(1,tab[1].length()-1);
            }
        }
        return "";
    }
}
