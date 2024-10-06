package org.example.for_doc1.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.RestController;

@Service
public class Cookiess {


    public String setCookie( HttpServletResponse response, String value) {
        Cookie cookie = new Cookie("Token", value);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(10 * 24 * 60 * 60); // expires in 10 days
        response.addCookie(cookie);
        return  "cookies has been set";
    }

}
