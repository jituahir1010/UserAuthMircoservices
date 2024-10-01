package org.example.for_doc1.Controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.for_doc1.dto.SendEmailDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class Controller {

    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;

    public Controller(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }


    @GetMapping("/ch")
    public String ch() throws JsonProcessingException {

        SendEmailDto sendEmailDto = new SendEmailDto();
        sendEmailDto.setTo("jituahir998@gmail.com");
        sendEmailDto.setSubject("Hello World checking");
        sendEmailDto.setBody("Hello World checking its Body bro happy, it is comming form the producer service");
//        sendEmailDto.setFrom("ahirjitu345@gmail.com");



        kafkaTemplate.send("quick",objectMapper.writeValueAsString(sendEmailDto)).whenComplete(

                (result,ex) -> {
                    if (ex == null) {
                        System.out.println("Sent message=[" + "message" +
                                "] with offset=[" + result.getRecordMetadata().offset() + "]");
                    } else {
                        System.out.println("Unable to send message=[" +
                                "message" + "] due to : " + ex.getMessage());
                    }
                }
        );
//        kafkaTemplate.send("hs1", "hello bro i am part of data1");

        return "Hello to the dockr  World";
    }

    @GetMapping("/ch1")
    public String ch1() {
        return "Hello to the dockr  World , ye change h bhi iss m ch1 h in the end";
    }

    @GetMapping("/ch2")
    public String ch2() {
        return "han bhi ye third wala h ye ch2";
    }

    @GetMapping("/ch3/{name}")
    public String ch3(@PathVariable String name) {
        if(name.isEmpty() ){
            return "han bhi ye third wala h ye ch3";
        }
        else{
            return "han bhih ye ch3" + name + "ye ch3 ka end";
        }

    }
    @PostMapping("/ch4")
    public SendEmailDto ch4(@RequestBody SendEmailDto sendEmailDto) {
//        return "han bhi ye third wala h ye ch4";
        return sendEmailDto;
    }
}
