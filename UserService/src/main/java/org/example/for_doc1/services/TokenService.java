package org.example.for_doc1.services;


import org.apache.commons.lang3.RandomStringUtils;
import org.example.for_doc1.models.Token;
import org.example.for_doc1.models.User;
import org.example.for_doc1.repositories.TokenRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class TokenService {

    private TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }




    public Token generateToken(User user) {
        ZonedDateTime currentTime = ZonedDateTime.now();
        ZonedDateTime  ExpiryTime = ZonedDateTime.now().plusDays(10);

        Token token = new Token();
        token.setExpiryAt(ExpiryTime);
        token.setUser(user);
//        token.setValue();
        token.setValue(RandomStringUtils.randomAlphanumeric(128));
        Token savedToken = tokenRepository.save(token);
        return savedToken;
    }

}
