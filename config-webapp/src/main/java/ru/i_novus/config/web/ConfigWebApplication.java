package ru.i_novus.config.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Locale;

@SpringBootApplication
public class ConfigWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigWebApplication.class, args);
    }

    @Bean
    public MessageSourceAccessor messageSourceAccessor(MessageSource messageSource) {
        return new MessageSourceAccessor(messageSource, Locale.getDefault());
    }
}
