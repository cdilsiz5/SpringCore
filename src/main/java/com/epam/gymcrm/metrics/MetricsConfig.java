package com.epam.gymcrm.metrics;


import com.epam.gymcrm.repository.UserRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {
    public MetricsConfig(MeterRegistry registry, UserRepository userRepository) {
        Gauge.builder("gymcrm.users.active.count", userRepository::countActiveUsers)
                .description("Aktif kullanıcı sayısı")
                .register(registry);
    }
}
