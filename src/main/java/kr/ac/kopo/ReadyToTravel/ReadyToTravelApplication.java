package kr.ac.kopo.ReadyToTravel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ReadyToTravelApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReadyToTravelApplication.class, args);
    }

}

