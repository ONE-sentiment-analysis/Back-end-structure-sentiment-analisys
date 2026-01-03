package br.com.one.sentiment_analysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EnableFeignClients
@SpringBootApplication
public class SentimentAnalisysApplication {
	private static final Logger log = LoggerFactory.getLogger(SentimentAnalisysApplication.class);
	static void main(String[] args) {
		SpringApplication.run(SentimentAnalisysApplication.class, args);
		log.info("Application started successfully.");
		System.out.println("Server running on http://localhost:8080");	
		System.out.println("Health available at http://localhost:8080/actuator/health"); 
		System.out.println("Swagger UI available at http://localhost:8080/swagger-ui/index.html");
	}

}
