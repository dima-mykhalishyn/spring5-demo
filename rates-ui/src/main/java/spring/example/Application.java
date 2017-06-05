package spring.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * UI Runner
 * @author dmihalishin@gmail.com
 */
@SpringBootApplication(scanBasePackages = "spring.example")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
