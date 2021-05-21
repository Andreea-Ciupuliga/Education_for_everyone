package com.example.Education_for_everyone.config;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@KeycloakConfiguration
@Import(KeycloakSpringBootConfigResolver.class)
public class KeycloakConfig extends KeycloakWebSecurityConfigurerAdapter {

    /* De primele 3 metode are nevoie spring in comunicarea cu keycloak. Pe baza lor se face decodarea token-ului
     * pentru a vedea ce rol are userul si tot pe baza lor se face validarea respectivului token. Le putem
     * lua ca atare */

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Bean
    public KeycloakSpringBootConfigResolver KeycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    @Bean
    @Override //acici avem modul prin care se face autentificarea
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new NullAuthenticatedSessionStrategy();
    }


    @Override //prin metoda asta separam ce endpointuri pot fi accesate de anumiti useri cu anumite roluri
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http
                .cors().and().csrf().disable()
                .authorizeRequests()    //sa se autorizeze requesturile
                .antMatchers("/student/register").permitAll() //dam acces la toti ptc nu ai cum sa fii autentificat atunci cand te inregistrezi pt prima data
                .antMatchers("/professor/register").permitAll()
                .anyRequest().authenticated();  //orice endpoint trebuie sa fii autentificat pentru a-l putea apela
    }

}
