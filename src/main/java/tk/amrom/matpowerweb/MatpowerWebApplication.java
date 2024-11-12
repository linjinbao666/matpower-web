package tk.amrom.matpowerweb;

import java.io.ByteArrayOutputStream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import tk.amrom.matpowerweb.util.DualPrintStream;


@SpringBootApplication
@ComponentScan("tk.amrom.matpowerweb")
public class MatpowerWebApplication implements CommandLineRunner {

	private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final DualPrintStream dualPrintStream = new DualPrintStream(System.out, System.err, outputStream);


	public static void main(String[] args) {

		SpringApplication.run(MatpowerWebApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.setOut(dualPrintStream);
        System.setErr(dualPrintStream);

	}

	public String getCapturedConsoleOutput() {
        return dualPrintStream.getCapturedOutput();
    }

	public void clearCapturedConsoleOutput() {
        dualPrintStream.clearCapturedOutput();
    }
}
