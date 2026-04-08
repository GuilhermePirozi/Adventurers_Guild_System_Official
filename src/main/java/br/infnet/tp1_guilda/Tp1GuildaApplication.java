package br.infnet.tp1_guilda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class Tp1GuildaApplication {

    public static void main(String[] args) {
        SpringApplication.run(Tp1GuildaApplication.class, args);
    }

}
