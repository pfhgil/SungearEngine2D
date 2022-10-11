package SungearEngine2D.Utils.JavaUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RuntimeStreamer extends Thread {
    InputStream is;
    String lines;

    RuntimeStreamer(InputStream is) {
        this.is = is;
        this.lines = "";
    }
    public String contents() {
        return this.lines;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br  = new BufferedReader(isr);
            String line = null;
            while ( (line = br.readLine()) != null) {
                this.lines += line + "\n";
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Execute a command and wait for it to finish
     * @return The resulting stdout and stderr outputs concatenated
     ****************************************************************************/
    public static String execute(String[] cmdArray) {
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec(cmdArray);
            RuntimeStreamer outputStreamer = new RuntimeStreamer(proc.getInputStream());
            RuntimeStreamer errorStreamer  = new RuntimeStreamer(proc.getErrorStream());
            outputStreamer.start();
            errorStreamer.start();
            proc.waitFor();
            return outputStreamer.contents() + errorStreamer.contents();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }
    public static String execute(String cmd) {
        String[] cmdArray = { cmd };
        return RuntimeStreamer.execute(cmdArray);
    }
}