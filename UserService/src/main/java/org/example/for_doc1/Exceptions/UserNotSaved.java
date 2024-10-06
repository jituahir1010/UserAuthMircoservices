package org.example.for_doc1.Exceptions;

public class UserNotSaved extends RuntimeException {

    public String UserNotSaved() {
        return "Either you are alrady Saved or we are not able to save your";
    }
}
