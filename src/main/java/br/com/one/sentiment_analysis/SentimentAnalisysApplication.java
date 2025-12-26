package br.com.one.sentiment_analysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SentimentAnalisysApplication {

	public static void main(String[] args) {
		SpringApplication.run(SentimentAnalisysApplication.class, args);
		System.out.println("Server running on http://localhost:8080");
	}

}
