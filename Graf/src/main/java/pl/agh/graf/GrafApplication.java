package pl.agh.graf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Klasa EwidencjaApplication to główny punkt wejścia.
 * Ta klasa zawiera metodę main, która inicjuje wykonanie aplikacji Spring Boot.
 */
@SpringBootApplication
public class GrafApplication {
	public static void main(String[] args) {
		SpringApplication.run(GrafApplication.class, args);
	}
}
