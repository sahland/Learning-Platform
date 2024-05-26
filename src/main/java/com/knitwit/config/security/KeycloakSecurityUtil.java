package com.knitwit.config.security;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class KeycloakSecurityUtil {

    Keycloak keycloak;

    @Value("${server-url}")
    private String serverUrl;

    @Value("${realm}")
    private String realm;

    @Value("${client-id}")
    private String clientId;

    @Value("${grant-type}")
    private String grantType;

    @Value("${name}")
    private String username;

    @Value("${password}")
    private String password;

    public Keycloak getKeycloakInstance() {
        if(keycloak == null) {
            keycloak = KeycloakBuilder
                    .builder().serverUrl(serverUrl).realm(realm)
                    .clientId(clientId).grantType(grantType)
                    .username(username).password(password).build();
        }
        return keycloak;
    }
    public void updateUser(UserRepresentation userRep) {
        Keycloak keycloak = getKeycloakInstance();
        keycloak.realm(realm).users().get(userRep.getId()).update(userRep);
    }

    public UserRepresentation getUserByUsername(String username) {
        Keycloak keycloak = getKeycloakInstance();
        return keycloak.realm(realm).users().search(username).get(0);
    }

    public void updateUserPassword(String username, CredentialRepresentation credential) {
        UserRepresentation userRep = getUserByUsername(username);
        userRep.setCredentials(Arrays.asList(credential));
        updateUser(userRep);
    }

    public void updateUserEmail(String username, String newEmail) {
        UserRepresentation userRep = getUserByUsername(username);
        userRep.setEmail(newEmail);
        updateUser(userRep);
    }
}