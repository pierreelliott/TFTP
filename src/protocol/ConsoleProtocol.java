package protocol;

public class ConsoleProtocol {
    private String rawData;
    private String command;
    private String ip;
    private String localfile;
    private String distantfile;

    public ConsoleProtocol(String text) {
        String[] tab = text.split(" ");
        rawData = text;
        command = tab[0];
        ip = tab[1];
        localfile = tab[2];
        if(tab.length == 4) distantfile = tab[3];
    }

//    public static DialogProtocol getProtocoleMessage(String text) {
//        String[] tab = text.split(" ");
//        String command = tab[0];
//        DialogProtocol protoc = new DialogProtocol();
//        boolean recognized = true;
//        switch (command) {
//            case "ping":
//            case "message":
//            case "broadcast":
//                break;
//            default:
//                command = "message";
//                recognized = false;
//        }
//        protoc.setCommand(command);
//        if(!recognized || tab.length > 1) {
//            String content = recognized ? text.substring(command.length()+1) : text;
//            protoc.setContent(content);
//        }
//        return protoc;
//    }
}
