package tk.amrom.matpowerweb.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ConsoleCaptureStream extends PrintStream {
    private final ByteArrayOutputStream outputStream;

    public ConsoleCaptureStream(ByteArrayOutputStream outputStream) {
        super(outputStream);
        this.outputStream = outputStream;
    }

    public String getCapturedOutput() {
        return outputStream.toString();
    }
}
