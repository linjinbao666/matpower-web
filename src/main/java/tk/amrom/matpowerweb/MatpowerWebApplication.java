package tk.amrom.matpowerweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan("tk.amrom.matpowerweb")
public class MatpowerWebApplication {

	public static void main(String[] args) {

		SpringApplication.run(MatpowerWebApplication.class, args);
	}

}
