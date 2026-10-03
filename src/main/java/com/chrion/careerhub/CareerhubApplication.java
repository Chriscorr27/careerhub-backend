package com.chrion.careerhub;

import com.chrion.careerhub.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
@EnableScheduling
public class CareerhubApplication {

	public static void main(String[] args) {

		SpringApplication.run(CareerhubApplication.class, args);
	}

}
