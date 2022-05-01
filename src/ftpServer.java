import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.awt.image.*;
import java.util.List;
/**
 * erstellt Verbindung zum ftp-server & ermöglicht globalen Austausch
 */
public class ftpServer
{
    config cfg = new config();
    String ftpServer = cfg.ftpServer;
    String username = cfg.ftpUsername;
    String password = cfg.ftpPassword;
    String homeDirectory = cfg.ftpProjectHomeDirectory;
    int port = cfg.ftpPort;
    long pause = cfg.ftpExercisingPauses;
    FTPClient ftpClient = new FTPClient();
    List<String> allowedFileTypes = Arrays.asList(cfg.allowedFileTypes);
    public void initializeHomeDirectory() { // erstellt, falls nicht vorhanden das HomeDirectory im ftpServer für das Projekt // erstellt alle benötigten Folder
        try {
            ftpClient.connect(ftpServer, port);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            boolean success = createDirectories(homeDirectory);
            if (success) {
                System.out.println("Home-directory has been successfully created || already existed");
            } else {
                System.out.println("creating home-directoy failed");
            }
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean createDirectories (String dirPath) throws IOException {
        String[] pathElements = dirPath.split("/");
        if (pathElements != null && pathElements.length > 0) {
            for (String singleDir : pathElements) {
                boolean existed = ftpClient.changeWorkingDirectory(singleDir);
                if (!existed) {
                    boolean created = ftpClient.makeDirectory(singleDir);
                    if (created) {
                        ftpClient.changeWorkingDirectory(singleDir);
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void downloadFile(String fileLoc, String SaveLoc) { // 1. Parameter serverseitig, 2. Parameter lokal
        try {
            // APPROACH #1: using retrieveFile(String, OutputStream)
            ftpClient.connect(ftpServer, port);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            String remoteFile = fileLoc;
            File downloadFile = new File(SaveLoc);
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
            boolean success = ftpClient.retrieveFile(remoteFile, outputStream);
            outputStream.close();

            if (success) {
                System.out.println("File '"+ remoteFile+"' has been downloaded successfully.");
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void downloadFiles(String[] fileLoc, String[] SaveLoc) {
        try
        {
            ftpClient.connect(ftpServer, port);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            for (int n = 0; n < fileLoc.length; ++n) {
                String remoteFile = fileLoc[n];
                File downloadFile = new File(SaveLoc[n]);
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
                boolean success = ftpClient.retrieveFile(remoteFile, outputStream);
                outputStream.close();

                if (success) {
                    System.out.println("File '"+ remoteFile+"' has been downloaded successfully.");
                }
                pause(pause);
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        } // kein logout Testweise
    }

    public void pause (long TimeSec) {
        if (TimeSec != 0) {
            System.out.println("Pause for "+TimeSec+" ms");
            try
            {
                Thread.sleep(TimeSec);
            }
            catch (InterruptedException ie)
            {
                ie.printStackTrace();
            }
        }
    }

    public void uploadFile(String SaveLoc, String fileLoc) { // ""
        try {
            // APPROACH #1: uploads file only using an InputStream
            ftpClient.connect(ftpServer, port);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            File localFile = new File(fileLoc);
            String remoteFile = SaveLoc;
            InputStream inputStream = new FileInputStream(localFile);

            boolean done = ftpClient.storeFile(remoteFile, inputStream);
            inputStream.close();
            if (done) {
                System.out.println("Following file has been successfully uploaded: '"+localFile+"'");
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public String[] listFilesFromDirectory(String Directory, boolean filter, boolean all, int first, int last) { // gilt nur für Bilder
        try {
            // APPROACH #1: Lists Files from specific Directory
            ftpClient.connect(ftpServer, port);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            FTPFile[] files = ftpClient.listFiles(Directory);
            System.out.println("--------------------------------");
            System.out.println("Directory-Data at: '"+Directory+"'");
            System.out.println("As Detailed List:");
            printFileDetails(files);

            List <String> filesString = new ArrayList<>(Arrays.asList()); // String-list erstellen
            System.out.println("-------------------");
            System.out.println("As String list (Mode: only pictures: "+filter+", full directory: "+all+"; else (without first directories): first: "+first+", last: "+last+")");
            if (files.length != 0) {
                int counter = 0;
                if (all) {
                    for (int i = 0; i < files.length; ++i) {
                        String txt = files[i].getName();
                        if (filter) {
                            if (allowedFileTypes.contains((txt.substring(txt.lastIndexOf('.')+1)))) {
                                filesString.add(txt);
                                System.out.println(filesString.get(counter));
                                counter +=1;
                            }
                        } else {
                            filesString.add(txt);
                            System.out.println(txt);
                        }
                    }
                } else {
                    int offset;
                    for (offset = 0; offset < 1; ++offset) { //filtert unnötige Directories raus, --> die folder die ganz oben gelistet sind mit charAt(0) == '['
                        if ((files[offset].getName().charAt(0) == '.')) {
                            offset += 1;
                        } else {
                            break;
                        }
                    }
                    System.out.println("Offset-times : "+offset);
                    if (last+offset > files.length-1) {
                        last = files.length-1;
                    } else {
                        last += offset;
                    }
                    System.out.println("real first index : "+ (first+offset) + "   ;   real last index : "+(last));
                    for (int i = first + offset; i < last+1; ++i) {
                        String txt = files[i].getName();
                        if (filter) {
                            if (allowedFileTypes.contains((txt.substring(txt.lastIndexOf('.')+1)))) { // // sollte sowieso immer so sein mit dem Offset / stellt sicher das die gedownloadeten Dateien vom Typ png oder jpg sind
                                filesString.add(txt);
                                System.out.println(filesString.get(filesString.size()-1)); // ginge auch txt
                            }
                        } else {
                            filesString.add(txt);
                            System.out.println(txt);
                        }
                    }
                }
            }
            System.out.println("--------------------------------");
            //filesString.add(String.valueOf(first)); // background-Vorgang --> gebraucht um nächstes last / first Item bestimmen zu können // theoretisch ungenau, da falls jmd während man in der Galerie ist etwas hochlädt kann es Verschiebungen geben, deswegen steht es auch in den configs zur Option diese Einstellung auszuschalten, --> nur sinnvoll, wenn nur Bilddateien im Ordner anzutreffen sind 
            //filesString.add(String.valueOf(last));
            String [] filesStringA = new String[filesString.size()];
            for (int j = 0; j < filesString.size(); ++j) {
                filesStringA[j]=filesString.get(j);
            }
            return(filesStringA);
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void printFileDetails(FTPFile[] files) {
        DateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        /*for (FTPFile file : files) { // no size limit
        String details = file.getName();
        if (file.isDirectory()) {
        details = "[" + details + "]";
        }
        details += "\t\t" + file.getSize();
        details += "\t\t" + dateFormater.format(file.getTimestamp().getTime());

        System.out.println(details);
        }*/
        int times; // size limit added
        System.out.println("Directory size is "+files.length);
        if (files.length > 100) {
            System.out.println("Max directory length reached: official directory size is over 100 files.");
            times = 100;
        } else {
            times = files.length;
        }
        for (int i = 0; i < times; ++i) {
            String details = files[i].getName();
            if (files[i].isDirectory()) {
                details = "[" + details + "]";
            }
            details += "\t\t" + files[i].getSize();
            details += "\t\t" + dateFormater.format(files[i].getTimestamp().getTime());        
            System.out.println(details);
        }
    }

    public void createDirectory(String Directory) { // keine Methode um gleich mehrere Folder nacheinander zu erstellen, geht nur wenn der Parentfolder schon existiert
        FTPClient ftpClient = new FTPClient();
        try
        {
            ftpClient.connect(ftpServer, port);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            boolean created = ftpClient.makeDirectory(Directory);
            if (created) {
                System.out.println("Directory '"+Directory+"' has been successfully created");
            }
            ftpClient.logout();
            ftpClient.disconnect();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public void deleteFile(String Directory, String fileName) { // ginge auch mithilfe von nur 1. Parameter
        try
        {
            ftpClient.connect(ftpServer, port);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            boolean deleted = ftpClient.deleteFile(fileName);
            if (deleted) {
                System.out.println("The file was deleted successfully.");
            } else {
                System.out.println("Could not delete the file.");
            }
            ftpClient.logout();
            ftpClient.disconnect();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public void serverTest() {
        FTPClient ftpClient = new FTPClient();
        ftpClient.enterLocalPassiveMode();
        try {
            ftpClient.connect(ftpServer, port);
            showServerReply(ftpClient);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("Operation failed. Server reply code: " + replyCode);
                return;
            }
            boolean success = ftpClient.login(username, password);
            showServerReply(ftpClient);
            if (!success) {
                System.out.println("Could not login to the server");
                return;
            } else {
                System.out.println("LOGGED IN SERVER");
            }
        } catch (IOException ex) {
            System.out.println("Oops! Something wrong happened");
            ex.printStackTrace();
        }
    }

    private static void showServerReply(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                System.out.println("SERVER: " + aReply);
            }
        }
    }

    public void connectToServer() {
        try
        {
            ftpClient.connect(ftpServer, port);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
}
