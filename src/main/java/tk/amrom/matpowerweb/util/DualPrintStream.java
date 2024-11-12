package tk.amrom.matpowerweb.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class DualPrintStream extends PrintStream {
    private final PrintStream originalSystemOut;
    private final PrintStream originalSystemErr;
    private final ByteArrayOutputStream outputStream;

    public DualPrintStream(PrintStream originalSystemOut, PrintStream originalSystemErr, ByteArrayOutputStream outputStream) {
        super(outputStream);
        this.originalSystemOut = originalSystemOut;
        this.originalSystemErr = originalSystemErr;
        this.outputStream = outputStream;
    }

    @Override
    public void write(int b) {
        originalSystemOut.write(b);  // 同时输出到标准控制台
        originalSystemErr.write(b);  // 同时输出到错误流
        super.write(b);              // 捕获输出到内存
    }

    @Override
    public void flush() {
        super.flush();                // 确保捕获流被刷新
        originalSystemOut.flush();    // 确保原始的控制台输出被刷新
        originalSystemErr.flush();    // 确保错误流被刷新
    }

    public String getCapturedOutput() {
        return outputStream.toString();
    }

    public void clearCapturedOutput() {
        outputStream.reset();  // 清空捕获的输出
    }
}
