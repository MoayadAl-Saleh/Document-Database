package com.Moayad.slavenode.configuration;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SlaveConfiguration
{
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate ()
    {
        return new RestTemplate ();
    }

}
