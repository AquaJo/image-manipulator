import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class language
{
    public static String getLanguage() throws IOException  {
        BufferedReader br = new BufferedReader(new FileReader("./language.txt"));
        String line = null;
        try {
            StringBuilder sb = new StringBuilder();
            line = br.readLine(); // da sowieso bis jz. nur 1 Zeile benötigt
        } finally {
            br.close();
        }
        if (line.contains("German")) {
            return "German";
        } else {
            return "English";
        }
    }
}
