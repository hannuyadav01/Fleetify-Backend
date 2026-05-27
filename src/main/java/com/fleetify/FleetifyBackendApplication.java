package com.fleetify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing //enables automatic auditing in Spring Data JPA.
public class FleetifyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FleetifyBackendApplication.class, args);
	}

}
//hello