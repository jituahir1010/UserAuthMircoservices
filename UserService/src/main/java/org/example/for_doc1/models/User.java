package org.example.for_doc1.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;
import java.time.ZonedDateTime;

//import java.sql.Date;
import java.util.Date;
import java.util.List;

@Entity(name = "users")
@Getter
@Setter
public class User extends BaseModel{
    private String name;
//    @Column(unique = true)
    private String email;
    private String hashedPassword;

    @ManyToMany
    private List<Role> roles;
    private boolean isEmailVerified = false;
    private int Otp;
    private ZonedDateTime otpDate;
}
