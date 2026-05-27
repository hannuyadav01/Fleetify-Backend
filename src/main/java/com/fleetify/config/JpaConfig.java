package com.fleetify.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// Enables @CreatedDate and @LastModifiedDate on BaseEntity to auto-populate timestamps.
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
