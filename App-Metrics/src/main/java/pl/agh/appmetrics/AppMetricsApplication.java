package pl.agh.appmetrics;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAutoConfiguration
@EnableDiscoveryClient
@EnableScheduling
@SpringBootApplication
@EnableAdminServer
public class AppMetricsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppMetricsApplication.class, args);
    }

}
