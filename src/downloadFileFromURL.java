import imp.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.io.InputStream;

public class downloadFileFromURL {
    public static boolean download(String urlLink, File fileLoc) {
        
        try {
            byte[] buffer = new byte[1024];
            int readbyte = 0; //Stores the number of bytes written in each iteration.    if no byte is left -1 gets returned

            URL url = new URL(urlLink);
            //double filesize = (double)http.getContentLengthLong();
            try {
                InputStream openConnection = url.openStream();
                HttpURLConnection http = (HttpURLConnection)url.openConnection();
                BufferedInputStream input = new BufferedInputStream(http.getInputStream());
                FileOutputStream ouputfile = new FileOutputStream(fileLoc);
                BufferedOutputStream bufferOut = new BufferedOutputStream(ouputfile, 1024);
                while((readbyte = input.read(buffer, 0, 1024)) >= 0) {
                    //Writing the content onto the file.
                    bufferOut.write(buffer,0,readbyte);
                }
                //System.out.println("Your download is now complete.");
                bufferOut.close();
                input.close();
                return true;
            } catch (Exception ex) {
                return false;
            }

        }
        catch(IOException e){
            e.printStackTrace();
            return false;
        }

    }
}

