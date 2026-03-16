package org.example.hominganimal;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("org.example.hominganimal.infrastructure.persistence.mapper")
public class HomingAnimalApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomingAnimalApplication.class, args);
    }

}
