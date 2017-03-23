package de.flo_aumeier.popularmoviesstage2.model.error;

/**
 * Created by Flo on 23.03.2017.
 */

public enum ErrorMessages {
    NO_NETWORK_CONNECTION("Sorry, no internet connection :(");
    private String message;

    ErrorMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
