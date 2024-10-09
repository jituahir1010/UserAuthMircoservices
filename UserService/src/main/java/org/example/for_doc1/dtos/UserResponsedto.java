package org.example.for_doc1.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponsedto {
    private String username;
    private String email;
    private Boolean isverified;
}
