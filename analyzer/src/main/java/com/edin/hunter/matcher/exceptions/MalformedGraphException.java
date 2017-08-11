package com.edin.hunter.matcher.exceptions;

import java.lang.reflect.MalformedParametersException;

public class MalformedGraphException extends MalformedParametersException{
    public MalformedGraphException(String message){
        super(message);
    }
}
