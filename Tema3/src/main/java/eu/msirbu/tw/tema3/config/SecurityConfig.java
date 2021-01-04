package eu.msirbu.tw.tema3.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) {
        String[] unsecuredResources = { "/images/**", "/css/**", "/fonts/**"};
        web.ignoring().antMatchers(unsecuredResources);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/", "/login").permitAll()
                .anyRequest().authenticated().and().oauth2Login()
                .loginPage("/login").defaultSuccessUrl("/manage");
    }
}
