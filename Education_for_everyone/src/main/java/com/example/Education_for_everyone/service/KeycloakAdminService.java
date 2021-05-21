package com.example.Education_for_everyone.service;

import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import java.util.Collections;

@Service
public class KeycloakAdminService {

    @Value("${keycloak.realm}")
    private String keycloakRealm; /* aici este vorba de realm-ul pe care vreau sa il manageruiesc, in cazul nostru e education_for_everyone.
    Ne folosim de @Value("${keycloak.realm}") ca sa luam numele realmului (education_for_everyone) din application.yml */

    private final Keycloak keycloak; //obiectul asta nu e un Bean, ceva ce poate fi injectat prin @Autowired asa ca trebuie sa creez eu un Bean de tipul Keycloak
    private RealmResource realmResource; /*pentru operatiile pe care vrem sa le facem de ex de salvare. Pe asta nu il pot initializa
    in constructor ptc am nevoie de obiectul de tip Keycloak ca sa pot sa il initializez pe realmResource asa ca il initializez intr-o
    metoda pe care o notez cu @PostConstruct */

    @Autowired
    public KeycloakAdminService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    @PostConstruct // dupa ce spring creeaza serviciul de tip Keycloak imi apeleaza singur si metoda initRealmResource
    public void initRealmResource() {
        this.realmResource = this.keycloak.realm(keycloakRealm);
    }


    //pentru ca sa alvam un user
    public void registerUser(String username, String password, String role) {

        //astea sunt campurile pe care le-am fi completat in mod normal din interfata
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setUsername(username);

        //astea sunt campurile pe care le-am fi completat in mod normal din interfata
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);
        credentialRepresentation.setTemporary(false);
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));

        Response response = realmResource.users().create(userRepresentation); //Facem salvarea de user folosind realmResource.users().create. Metoda create intoarce un obiect de tip Response
        String keycloakUserId = CreatedResponseUtil.getCreatedId(response); //ca sa pot lua user-ul si sa ii fac update cu rolul am nevoie de id ul lui din keycloak
        UserResource userResource = realmResource.users().get(keycloakUserId); //ne luam user-ul proaspat salvat din realmResource dupa id. Metoda ni-l returneaza sub forma UserResource

        RoleRepresentation roleRepresentation = realmResource.roles().get(role).toRepresentation(); /*pe roleRepresentation o sa
        il initializez cu rolul pe care il iau direct din realm pe baza numelui rolului. Numele e ROLE_PROFESSOR de exemplu. Nu am nevoie de tot
        obiectul de tip role pe care keycloak il tine salvat , doar numele lui */

        userResource.roles().realmLevel().add(Collections.singletonList(roleRepresentation));
    }
}
