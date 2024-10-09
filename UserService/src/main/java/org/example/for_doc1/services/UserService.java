package org.example.for_doc1.services;

//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.assertTrue;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.for_doc1.Exceptions.NotAdminException;
import org.example.for_doc1.Exceptions.NotvarifiedException;
import org.example.for_doc1.Exceptions.UserNotFoundException;
import org.example.for_doc1.Exceptions.UserNotSaved;
import org.example.for_doc1.dtos.UserSignupRequestdto;
import org.example.for_doc1.models.Role;
import org.example.for_doc1.models.Token;
import org.example.for_doc1.models.User;
import org.example.for_doc1.repositories.UserRepo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepo userRepo;
    private PasswordEncoder passwordEncoder;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private RandomNumberService randomNumberService;
    private TokenService tokenService;
    private Cookiess cookiess;

    public UserService(UserRepo userRepo,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       RandomNumberService  randomNumberService,
                       TokenService tokenService,
                       Cookiess cookiess) {

        this.userRepo = userRepo;
        this.passwordEncoder = bCryptPasswordEncoder;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.randomNumberService = randomNumberService;
        this.tokenService = tokenService;
        this.cookiess = cookiess;
    }


    public List<User> Allusrs(String email) {
        Optional<User> usr = userRepo.findByEmail(email);

        if(usr.isEmpty()){
            throw new UserNotFoundException("Please make an account First");
        }
        User user = usr.get();
        if(user.isEmailVerified() == true){
            List<Role> roles = user.getRoles();
            if(roles.contains("Admin")){
                List<User> users = userRepo.findAll();
                return  users;
            }
            else{
                String msg = "you are Varified but not an admin, request for admin role  http://localhost:8085/swagger-ui/index.html#/controller/RequestForManagerAccess";
                throw new NotAdminException(msg);
            }

        }
        else {
            String msg = "You are not a varified Yourself Varify yoursel at:  http://localhost:8085/swagger-ui/index.html#/controller/VarifyUser";
            throw new NotvarifiedException(msg);
        }

    }


    public String login(String email, String password) {
        Optional<User> usr = userRepo.findByEmail(email);

        if (usr.isPresent()) {
            User usr1 = usr.get();
            if(!bCryptPasswordEncoder.matches(password, usr1.getHashedPassword())){
                return "Your password did not match with saved password";
            }
            Token tokn = tokenService.generateToken(usr1);
            return tokn.getValue();
        }
        else{
            return  "User Not Found";
        }
    }





//    Here we will verify  the OTP entered by the user
//    @Test
    public  String OtpVarify(String email, Integer otp ) {

        Optional<User> usr = userRepo.findByEmail(email);
        if(usr.isPresent()){
            if(usr.get().isEmailVerified() == true){
                return "You are already Varified";
            }
            if(usr.get().getOtp() == otp){
                User usr1 = usr.get();
                Token tokn = tokenService.generateToken(usr1);

                return  tokn.getValue();
            }
            else{
                return "Please enter Correct otp";
            }
        }
        else{
            return  "Bhai! Please shi EmailToken daalo, It's all about your data's Security ";
        }

    }


    public User signup(String name, String email, String password) {

        User user = new User();
//        String email = userSignupRequestdto.getEmail();
//        String password = userSignupRequestdto.getPassword();
//        String name = userSignupRequestdto.getName();

        user.setEmail(email);
        user.setName(name);
        user.setHashedPassword(bCryptPasswordEncoder.encode(password));
        user.setOtp(randomNumberService.generateRandomSixDigitNumber());
        ZonedDateTime OtpExpiryTime = ZonedDateTime.now().plusMinutes(10);
        user.setOtpDate(OtpExpiryTime);



            Optional<User> usr = Optional.ofNullable(userRepo.save(user));
            if(usr.isPresent()) {

                return usr.get();
            }
            else {
                throw new UserNotSaved();
            }
//            return null;

    }

}



