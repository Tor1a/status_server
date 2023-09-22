package com.example.status_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class StatusServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(StatusServerApplication.class, args);
	}

}
