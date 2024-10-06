package org.example.for_doc1.Controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.example.for_doc1.dtos.LoginRequestdto;
import org.example.for_doc1.dtos.OtpVerifyRequestDto;
import org.example.for_doc1.dtos.SendEmailDto;
import org.example.for_doc1.dtos.UserSignupRequestdto;
import org.example.for_doc1.models.Token;
import org.example.for_doc1.models.User;
import org.example.for_doc1.repositories.TokenRepository;
import org.example.for_doc1.repositories.UserRepo;
import org.example.for_doc1.services.Cookiess;
import org.example.for_doc1.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class Controller {

    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;
    private UserSignupRequestdto userSignupRequestdto;
    private UserService userService;
    private UserRepo userRepo;
    private Cookiess cookiess;
    private TokenRepository tokenRepository;


    public Controller(KafkaTemplate<String, String> kafkaTemplate,
                      ObjectMapper objectMapper,
                      UserService userService,
                      UserRepo userRepo,
                      Cookiess cookiess,
                      TokenRepository tokenRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.userRepo = userRepo;
        this.cookiess = cookiess;
        this.tokenRepository = tokenRepository;
    }


    @PostMapping("/otp")
    public String signOtp(@RequestBody OtpVerifyRequestDto otpVerifyRequestDto) throws IOException {
        String emailToken = otpVerifyRequestDto.getEmailToken();
        String email = new String(Base64.getDecoder().decode(emailToken));
        Integer otp = otpVerifyRequestDto.getOtp();
        System.out.println(otp);
        if(email.isEmpty()){
            return "Please enter the Token";
        }
        if(otp == null){
            return "Please enter the OTP";
        }

        String tokn = userService.OtpVarify( email, otp);
        return  "your Token :->  "+ tokn;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserSignupRequestdto userSignupRequestdto, HttpServletResponse response) throws IOException {

        String name = userSignupRequestdto.getName();
        String email = userSignupRequestdto.getEmail();
        String password = userSignupRequestdto.getPassword();
        Optional<User> usr = userRepo.findByEmail(email);
        if (usr.isPresent()) {
            String msg = "Please enter new email this email is already used and varified";
//            System.out.println(msg);
            return ResponseEntity.badRequest().body(msg);
        } else {
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                return ResponseEntity.badRequest().body("Please fill all the details");
            }


            Optional<User> savedUser = Optional.ofNullable(userService.signup(name, email, password));

            String to = savedUser.get().getEmail();
            Integer Otp = savedUser.get().getOtp();
            String names = savedUser.get().getName();
            String emailtoken = Base64.getEncoder().encodeToString(to.getBytes());
            String bodystring = "Dear " + names + ", \nYour data security is top priority to us,\n So we hav two values to verify you \nYour OTP " + Otp  +" \n Clik http://localhost:8085/swagger-ui/index.html#/controller/signOtp \n Steps :- \n 1. Click on Try it Out Button on Right \n 2. Now Enter both details" + "\nEmailToken -> " + emailtoken;

            SendEmailDto sendEmailDto = new SendEmailDto();
            sendEmailDto.setTo(to);
            sendEmailDto.setSubject("Otp Verification email");
            sendEmailDto.setBody(bodystring);

            kafkaTemplate.send("quick", objectMapper.writeValueAsString(sendEmailDto)).whenComplete(

                    (result, ex) -> {
                        if (ex == null) {
                            System.out.println("Sent message=[" + "message" +
                                    "] with offset=[" + result.getRecordMetadata().offset() + "]");
                        } else {
                            System.out.println("Unable to send message=[" +
                                    "message" + "] due to : " + ex.getMessage());
                        }
                    }
            );


            return ResponseEntity.ok("We have sent you a Verification email! Please verify yourself.");
        }



//        return null;
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestdto loginRequestdto){
        String email = loginRequestdto.getEmail();
        String password = loginRequestdto.getPassword();
        String token = userService.login(email,password);
        System.out.println(token + "After user find by email");
        return  ResponseEntity.ok("token");
    }

    @GetMapping("/logout")
    public void logout(String token){

        Optional<Token> optionalToken = tokenRepository.findByValueAndDeletedEquals(token, false);

        if(optionalToken.isEmpty()){
            //return token doesn't exist exception
            return;
        }
        Token tkn = optionalToken.get();
        tkn.setDeleted(true); //soft deleted

        tokenRepository.save(tkn);
        return;
    }

    @GetMapping("/validateToken")
    public User validateToken(String token){
        Optional<Token> optionalToken = tokenRepository.findByValueAndDeletedEqualsAndExpiryAtGreaterThan(token, false, new Date());

        if(optionalToken.isEmpty()){
            //token is invalid
            return null;
        }

        return optionalToken.get().getUser();
    }


}
