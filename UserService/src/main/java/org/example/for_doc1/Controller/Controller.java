package org.example.for_doc1.Controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.example.for_doc1.dtos.*;
import org.example.for_doc1.models.Token;
import org.example.for_doc1.models.User;
import org.example.for_doc1.repositories.TokenRepository;
import org.example.for_doc1.repositories.UserRepo;
import org.example.for_doc1.services.Cookiess;
import org.example.for_doc1.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.io.IOException;
import java.util.*;

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

//    @GetMapping("/validateToken")
//    public User validateToken(String token){
//        Optional<Token> optionalToken = tokenRepository.findByValueAndDeletedEqualsAndExpiryAtGreaterThan(token, false, new Date());
//
//        if(optionalToken.isEmpty()){
//            //token is invalid
//            return null;
//        }
//
//        return optionalToken.get().getUser();
//    }

    @GetMapping("/AllUsers")
    public  ResponseEntity<List<UserResponsedto>> getAllUsers(@RequestParam String email){
        try{
            List<User>  users = userService.Allusrs(email);
            List<UserResponsedto> userResponseDtos = new ArrayList<>();
            UserResponsedto userResponseDto = new UserResponsedto();
            for(User user : users){
                userResponseDto.setEmail(user.getEmail());
                userResponseDto.setUsername(user.getName());
                userResponseDto.setIsverified(user.isEmailVerified());
                userResponseDtos.add(userResponseDto);
            }
            return ResponseEntity.ok(userResponseDtos);
        } catch (RuntimeException e) {
            String msg = "it's Error";
            return  new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/UserByemail")
    public ResponseEntity<User> getUserById(@PathVariable String email){
        Optional<User> optionalUser = userRepo.findByEmail(email);
        if(optionalUser.isEmpty()){
            return ResponseEntity.badRequest().body(null);
        }
        else{
            return ResponseEntity.ok(optionalUser.get());
        }

    }

    @GetMapping("/RequestForAdminAccess")
    public ResponseEntity<String> getRequestForManagerAccess(@RequestParam String Youremail) throws JsonProcessingException {

        Optional<User> savedUser = userRepo.findByEmail(Youremail);
        if(savedUser.isEmpty()){
            String msg = "Your are not user here kindly sign up first";
            return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        }

        String to = savedUser.get().getEmail();
        String name = savedUser.get().getName();
        String bodystring = "Dear " + name + ", \n we have sent your request to the manager for your Admin role Access, Thanks";
        SendEmailDto sendEmailDto = new SendEmailDto();
        sendEmailDto.setTo(to);
        sendEmailDto.setSubject("Admin Role Access");
        sendEmailDto.setBody(bodystring);

        SendEmailDto sendEmailDto2 = new SendEmailDto();
        sendEmailDto2.setTo("jituahir998@gmail.com");
        sendEmailDto2.setSubject("Admin Role Access");
        String bodystring2 = "Hello" + to + "Requested for the admin role thanks";
        sendEmailDto2.setBody(bodystring2);

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

        kafkaTemplate.send("quick", objectMapper.writeValueAsString(sendEmailDto2)).whenComplete(

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


        return ResponseEntity.ok("We have sent you an email regading your Request,  Check Your email");
    }

    @GetMapping("/OtherProjectsIWorkingUpon")
    public ResponseEntity<String> getOtherProjects(){
        return ResponseEntity.ok("1.Book My Window Sheet, -> This app will help users to book window sheets for a particular Airline \n for a particular Dept. and Destination in particular time period. \n PRIORITY -> High Availabity AND Make Sure not More then one user book Same sheet By Handling Cuncurrency  \n2. Captioner -> This extension will suggest caption for social Media Post's After observing postDetails.  ");
    }


    @GetMapping("/VarifyUser")
    public String  varifyUser(){
        return "verifying";
    }




}
