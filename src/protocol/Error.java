package protocol;

public class Error extends Exception {

    private int numErreur;

    public Error(String message, int code) {
        super(message);
        this.numErreur = code;
    }

    public int getCode() { return this.numErreur; }
}
