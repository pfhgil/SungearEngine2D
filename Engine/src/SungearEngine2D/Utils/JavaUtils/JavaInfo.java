package SungearEngine2D.Utils.JavaUtils;

/**
 * Helper struct to hold information about one installed java version
 ****************************************************************************/
public class JavaInfo
{
    public String path;        //! Full path to java.exe executable file
    public String version;     //! Version string. "Unkown" if the java process returned non-standard version string
    public boolean is64bits;    //! true for 64-bit javas, false for 32

    /**
     * Calls 'javaPath -version' and parses the results
     * @param javaPath: path to a java.exe executable
     ****************************************************************************/
    public JavaInfo(String javaPath) {
        String versionInfo = RuntimeStreamer.execute( new String[] { javaPath, "-version" } );
        String[] tokens = versionInfo.split("\"");

        if (tokens.length < 2) this.version = "Unkown";
        else this.version = tokens[1];
        this.is64bits = versionInfo.toUpperCase().contains("64-BIT");
        this.path = javaPath;
    }

    /**
     * @return Human-readable contents of this JavaInfo instance
     ****************************************************************************/
    public String toString() {
        return this.path + ":\n  Version: " + this.version + "\n  Bitness: " + (this.is64bits ? "64-bits" : "32-bits");
    }
}