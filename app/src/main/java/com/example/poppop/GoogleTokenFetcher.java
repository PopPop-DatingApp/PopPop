package com.example.poppop;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class GoogleTokenFetcher {

    public static String fetchAccessToken(String serverAuthCode) throws IOException, GeneralSecurityException {
        String clientId = "750488609187-dta4s9ud1jle0vtumvadopmd04giqd9j.apps.googleusercontent.com";
        String clientSecret = "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";
        String redirectUri = "https://poppop-datingapp.firebaseapp.com/__/auth/handler";

        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                "https://accounts.google.com/o/oauth2/v2/auth",
                clientId,
                clientSecret,
                serverAuthCode,
                redirectUri)
                .execute();

        return tokenResponse.getAccessToken();
    }
}

