package br.com.ndrewcoding.springboot2.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.ndrewcoding.springboot2.service.NdrewCodingUserDetailsService;
import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private final NdrewCodingUserDetailsService ndrewCodingUserDetailsService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests().antMatchers("/animes/admin/**").hasRole("ADMIN")
				.antMatchers("/animes/**").hasRole("USER").antMatchers("/actuator/**").permitAll().anyRequest()
				.authenticated().and().formLogin().and().httpBasic();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		System.out.println(passwordEncoder.encode("ndrewcoding"));
		auth.inMemoryAuthentication().withUser("andrew2").password(passwordEncoder.encode("ndrewcoding"))
				.roles("USER", "ADMIN").and().withUser("bunro2").password(passwordEncoder.encode("ndrewcoding"))
				.roles("USER");
		auth.userDetailsService(ndrewCodingUserDetailsService).passwordEncoder(passwordEncoder);
	}
}
