/*
 * Vacations @ Contoso
 * (C) 2021 Matei Sîrbu.
 */
package eu.msirbu.tw.tema3.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Allows customization of security options.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Specifies the resources available without credentials.
     * @param web WebSecurity object.
     */
    @Override
    public void configure(WebSecurity web) {
        String[] unsecuredResources = { "/images/**", "/css/**", "/fonts/**"};
        web.ignoring().antMatchers(unsecuredResources);
    }

    /**
     * Specifies the login & logout procedures and redirect paths.
     * @param http The HttpSecurity object.
     * @throws Exception The exception that might be thrown by authorizeRequests().
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/").permitAll()
                .anyRequest().authenticated().and().oauth2Login()
                .loginPage("/").defaultSuccessUrl("/dashboard")
                .and().logout()
                .logoutSuccessUrl("/").deleteCookies("JSESSIONID")
                .and().csrf().disable();
    }
}
