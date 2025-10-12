package com.example.TypeAheadSearch;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class TypeAheadSearchApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().load();
		dotenv.entries().forEach(entry->{
			System.setProperty(entry.getKey(), entry.getValue());
		});
		SpringApplication.run(TypeAheadSearchApplication.class, args);
	}

}
