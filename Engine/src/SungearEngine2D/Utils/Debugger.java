package SungearEngine2D.Utils;

import Core2D.Log.Log;
import Core2D.Transform.Transform;
import Core2D.Utils.ExceptionsUtils;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;
import java.util.Properties;

public class Debugger
{
    private static MBeanServerConnection mbs;

    public static void init()
    {
        String host = "localhost";
        int port = 8099;
        JMXServiceURL serviceURL = null;
        try {
            serviceURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi");
        } catch (MalformedURLException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
        JMXConnector conn = null;
        try {
            conn = JMXConnectorFactory.connect(serviceURL, null);
        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        if(conn != null) {
            try {
                mbs = conn.getMBeanServerConnection();
            } catch (IOException e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            }
        } else {
            throw new RuntimeException("JMXConnector did not connect.");
        }
    }

    public static class CPU
    {

    }

    public static class GPU
    {
        public static String getGPUUsagePercentage() {
            //Runtime.getRuntime().gc();
            return "";
        }
    }

    public static class Memory
    {
        public static long getCurrentHeapSizeInMB() { return Runtime.getRuntime().totalMemory() / 1024 / 1024; }

        public static long getMaxHeapSizeInMB() { return Runtime.getRuntime().maxMemory() / 1024 / 1024; }

        public static long getFreeHeapSizeInMB() { return Runtime.getRuntime().freeMemory() / 1024 / 1024; }

        public static long getHeapUsageInMB() { return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024; }
    }
}
