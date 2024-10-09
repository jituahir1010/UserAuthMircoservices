package org.example.for_doc1.Exceptions;

public class NotAdminException extends  RuntimeException{
    public NotAdminException(String message){
        super(message);
    }
}
