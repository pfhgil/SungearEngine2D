package Core2D.Utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionsUtils
{
    public static String toString(Exception e)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        return sw.toString();
    }
}
