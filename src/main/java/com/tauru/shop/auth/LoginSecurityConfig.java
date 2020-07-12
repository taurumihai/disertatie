package com.tauru.shop.auth;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class LoginSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationMgr) throws Exception {
        authenticationMgr
                .inMemoryAuthentication()
                .withUser("Taurete").password("{noop}Taurete95!#").authorities("ROLE_USER")
                .and()
                .withUser("Admin123!@#").password("{noop}TauruMihai95").authorities("ROLE_USER","ROLE_ADMIN");
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/welcome").access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/addressForm").access("hasRole('ROLE_USER')")
                .antMatchers("/addressForm").access("hasRole('ROLE_USER')")
                .antMatchers("/completeOrder").access("hasRole('ROLE_USER')")
                .antMatchers("/confirmOrder").access("hasRole('ROLE_USER')")
                .antMatchers("/digitals").access("hasRole('ROLE_USER')")
                .antMatchers("/electronics").access("hasRole('ROLE_USER')")
                .antMatchers("/productDetails").access("hasRole('ROLE_USER')")
                .antMatchers("/protection").access("hasRole('ROLE_USER')")
                .antMatchers("/register").access("hasRole('ROLE_USER')")
                .antMatchers("/shoppingCart").access("hasRole('ROLE_USER')")
                .antMatchers("/shoppingCart").access("hasRole('ROLE_USER')")
                .antMatchers("/addProducts").access("hasRole('ROLE_ADMIN')")
                .antMatchers("/adminView").access("hasRole('ROLE_ADMIN')")
                .antMatchers("/stockUpdate").access("hasRole('ROLE_ADMIN')")
                .antMatchers("/viewOrders").access("hasRole('ROLE_ADMIN')")
                .antMatchers("/viewStock").access("hasRole('ROLE_ADMIN')")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .defaultSuccessUrl("/welcome", true)
                .permitAll()
                .and()
                .logout()
                .permitAll()
                .and()
                .httpBasic();
    }
}
