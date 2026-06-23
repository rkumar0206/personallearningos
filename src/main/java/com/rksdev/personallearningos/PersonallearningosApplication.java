package com.rksdev.personallearningos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PersonallearningosApplication {

	static void main(String[] args) {
		SpringApplication.run(PersonallearningosApplication.class, args);
	}

}
