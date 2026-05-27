package com.fleetify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@org.springframework.scheduling.annotation.EnableScheduling
@SpringBootApplication
public class FleetifyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FleetifyBackendApplication.class, args);
	}

}
//hello