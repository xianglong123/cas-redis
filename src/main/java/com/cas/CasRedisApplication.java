package com.cas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 10)
@SpringBootApplication
public class CasRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(CasRedisApplication.class, args);
    }

}
