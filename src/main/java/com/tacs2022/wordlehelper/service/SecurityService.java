package com.tacs2022.wordlehelper.service;

import com.tacs2022.wordlehelper.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

@Service
public class SecurityService {
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    public byte[] getSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    public byte[] hash(String password, byte[] salt) {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return factory.generateSecret(spec).getEncoded();
        }catch(NoSuchAlgorithmException e){
            System.out.println("NoSuchAlgorithmException for hash method");
            return null;
        } catch(InvalidKeySpecException e){
            System.out.println("InvalidKeySpecException for hash method");
            return null;
        }
    }

    public boolean validatePassword(String password, User user) {
        String actualPass = new String(user.getHashedPass());
        String givenPass = new String(this.hash(password, user.getSalt()));
        return actualPass.equals(givenPass);
    }

}
