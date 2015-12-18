package org.imc.demo.auth.jwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableAutoConfiguration
@EnableWebMvc
@ComponentScan(basePackages = {"org.imc.demo.auth.jwt"})
public class JwtAuthDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(JwtAuthDemoApplication.class, args);
    }

}
