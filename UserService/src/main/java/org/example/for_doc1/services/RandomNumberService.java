package org.example.for_doc1.services;

import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class RandomNumberService {

    public int generateRandomSixDigitNumber() {
        return ThreadLocalRandom.current().nextInt(1000, 10000); // Generates a number between 100000 and 999999
    }
}
