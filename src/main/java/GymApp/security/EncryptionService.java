package GymApp.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EncryptionService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public EncryptionService(BCryptPasswordEncoder bCryptPasswordEncoder){
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public String encryptString(String input){
        return bCryptPasswordEncoder.encode(input);
    }
    public boolean matchStrings(String rawString, String encodedString) {
        return bCryptPasswordEncoder.matches(rawString, encodedString);
    }
}
