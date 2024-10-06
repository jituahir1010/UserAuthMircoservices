package org.example.for_doc1.Exceptions;



public class UserAlreadyExixt extends RuntimeException {

    public String UserNotSaved() {
        return "This email is already registred Please enter new email";
    }
}
