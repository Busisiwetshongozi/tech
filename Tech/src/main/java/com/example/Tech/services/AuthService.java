package com.example.Tech.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public FirebaseToken verifyToken(String idToken){
        try {
            // Your token verification logic
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            return decodedToken;
        } catch (FirebaseAuthException e) {
            // Log the error for debugging
            System.out.println("Token verification failed: " + e.getMessage());
            return null;
        }
    }
    public String getUidFromToken(String idToken) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(idToken).getUid();
    }


}

