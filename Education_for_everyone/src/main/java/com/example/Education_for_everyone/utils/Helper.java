package com.example.Education_for_everyone.utils;

import org.keycloak.KeycloakPrincipal;
import org.springframework.security.core.Authentication;


public class Helper {


    //metoda asta ne intoarce username-ul care e encodat in token
    public static String getKeycloakUser(Authentication authentication) {
        return ((KeycloakPrincipal) authentication.getPrincipal()).getKeycloakSecurityContext().getToken().getPreferredUsername();
    }

}

