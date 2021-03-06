package com.mannu;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
//@PropertySource("classpath:default.properties")
public class SpringContext {

//	@Value("${title}")
//	String title;
	
	@Bean(name="login")
	public Login createLogin() {
		return new Login();
	}
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer setUp() {
        return new PropertySourcesPlaceholderConfigurer();
    }
	
}
