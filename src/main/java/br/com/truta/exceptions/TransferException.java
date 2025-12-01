package br.com.truta.exceptions;

public class TransferException extends RuntimeException {

    private String message;
    private String code;

    public TransferException(String message, String code) {
        super(
            "Message: " + message + " Error_code: " + code
        );
        this.message = message;
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

}
