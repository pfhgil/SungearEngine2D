package Core2D.Utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionsUtils
{
    public static String toString(Exception e)
    {
        String output = "";
        try(StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw)) {
            e.printStackTrace(pw);

            output = sw.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return output;
    }
}
