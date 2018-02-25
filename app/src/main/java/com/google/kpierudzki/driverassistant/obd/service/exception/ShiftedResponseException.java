package com.google.kpierudzki.driverassistant.obd.service.exception;

/**
 * Created by Kamil on 15.09.2017.
 */

public class ShiftedResponseException extends RuntimeException {

    public ShiftedResponseException() {
        super("Niewłaściwe odpowiedzi z urządzenia. Odłącz je na chwilę od zasilania.");
    }
}
