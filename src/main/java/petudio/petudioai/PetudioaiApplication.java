package petudio.petudioai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PetudioaiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetudioaiApplication.class, args);
	}

}
