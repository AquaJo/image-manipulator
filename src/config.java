/**
configurate program here
 */
public class config
{
    static boolean ftpServerON = false; // falls Probleme aufgrund des ftpServers auftreten // könnte auch verbindungscheck am Anfang durchführen und dann die Var ändern!    
    
    static String ftpServer = "xxx.xxx.xx.xxx";
    static String ftpUsername = "";
    static String ftpPassword = "";
    
    static int ftpPort = 21;
    static String ftpProjectHomeDirectory = "imageHandling";
    static long ftpExercisingPauses = 600; //in ms
    
    static int gifDelay = 51; // in ms
    static String[] allowedFileTypes= {"png", "jpg","gif","gif&ct=g"}; // noch hinzuzufügen
}
