package org.example.for_doc1.dtos;
import io.swagger.v3.oas.annotations.media.Schema;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpVerifyRequestDto {


    private  String emailToken;

    @Schema(description = "It can be null", nullable = true)
    private  Integer otp;




}
