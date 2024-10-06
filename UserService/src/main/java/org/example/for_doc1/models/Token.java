package org.example.for_doc1.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
public class Token extends BaseModel{
    private String value;

    @ManyToOne
    private User user;

    private ZonedDateTime expiryAt;
}