import java.io.IOException;
import java.io.File;
import org.apache.commons.io.FileUtils;
public class deleteTemp
{
    public static void delete() {
        try
        {
            FileUtils.deleteDirectory(new File("./images/temp/"));
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        new File("./images/temp/").mkdirs();
    }
}
