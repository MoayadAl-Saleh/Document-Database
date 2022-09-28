package com.Moayad.slavenode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import java.util.Collections;

@SpringBootApplication
@EnableEurekaClient
public class SlaveNodeApplication
{

    public static void main (String[] args)
    {
        SpringApplication app = new SpringApplication (SlaveNodeApplication.class);
        app.setDefaultProperties (Collections.singletonMap ("server.port", "8082"));
        app.run (args);
//		SpringApplication.run(SlaveNodeApplication.class, args);
    }

}
