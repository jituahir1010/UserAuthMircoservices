package org.example.consumer.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.consumer.Service.SendingEmail;
import org.example.consumer.dto.SendEmailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import  javax.mail.*;

@Service
public class consumer {



//    private static final Logger LOGGER = LoggerFactory.getLogger(consumer.class);

    private  KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;
    private SendingEmail sendingEmail;
    public consumer(KafkaTemplate<String, String> kafkaTemplate
            ,ObjectMapper objectMapper,
                    SendingEmail sendingEmail) {
                  this.kafkaTemplate = kafkaTemplate;
                  this.objectMapper = objectMapper;
                  this.sendingEmail = sendingEmail;
    }

//    Logger log = LoggerFactory.getLogger(consumer.class);

//    LOGGER.info(String.format("Event message received -> %s", eventMessage));
//    System.out.println(log)

//    @KafkaListener(topics = "quick", groupId = "testi")
    @KafkaListener(topics = "quick", groupId = "testi")
    public String listen(String message) throws JsonProcessingException {
//        SendEmailDto sendEmailDto = new SendEmailDto();
//        Logger log = LoggerFactory.getLogger(message);
        System.out.println("event listned");
        SendEmailDto  sendEmailDto = objectMapper.readValue(message,SendEmailDto.class);
        String to = sendEmailDto.getTo();
        String subject = sendEmailDto.getSubject();
        String body = sendEmailDto.getBody();
//        String from = sendEmailDto.getFrom();

        System.out.println("sendign email start here");
        String res =  sendingEmail.sendit( to, subject,  body);
        System.out.println("sendign email end here");

        return res;



    }



}
