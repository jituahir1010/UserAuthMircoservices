package org.example.consumer.Controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.kafka.clients.producer.internals.Sender;
import org.example.consumer.Service.SendingEmail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.Subject;

@RestController
@RequestMapping("/hello")
public class Controller {

    SendingEmail sendingEmail = new SendingEmail();

    public  Controller(SendingEmail sendingEmail){
        this.sendingEmail = sendingEmail;
    }

    @GetMapping("/first")
    public String setCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie("foo", "hello_mr_dj");
        cookie.setSecure(false);
        cookie.setHttpOnly(false);
        cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
        response.addCookie(cookie);
        return "cookie is added!";
    }


    @GetMapping("/sec")
    public String second() {
        String to = "jituahir998@gmail.com";
        String Subject = "Hello it try emial";
        String body = "Hello it try emial bro an  this is the body of it";
        String res = sendingEmail.sendit(to, Subject, body);
        return  res;
//        return "hello ji it is second(/hello/second)";
    }

    @GetMapping("/third")
    public String third() {
        return "it is my third(/hello/third)";
    }

    @GetMapping("/fourth")
    public String fourth() {
        return "it is fourth (/hello/fourth)";
    }
//
//    @GetMapping("/fifth")
//    public  String fifth() {
//        return "Hello ji ye fifth (/hello/fifth)";
//    }


    @GetMapping("/sixth")
    public String sixth() {
        return "yes it is sixth ";
    }

}
