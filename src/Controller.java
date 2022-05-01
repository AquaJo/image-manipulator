import imp.*;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Icon;
import javax.swing.JFrame;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;

import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.*;
import java.io.File;
import javafx.scene.image.*;

import javafx.scene.text.*;

import javafx.application.*;
import javafx.scene.layout.Region;
import javafx.stage.*;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javax.swing.event.ChangeListener;

import java.util.List;
import java.util.stream.*;
import java.util.Arrays;

import java.io.IOException;
import java.net.URL;
import java.awt.image.*;
import javax.imageio.*;

import java.util.ArrayList;
import java.util.List;

import javax.imageio.stream.ImageInputStream;

import java.awt.Dimension;
import java.awt.Toolkit;

import javafx.animation.AnimationTimer;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.metadata.*;
import org.apache.commons.io.FileUtils;
import static java.nio.file.StandardCopyOption.*;
public class Controller {
    @FXML
    private HBox consoleID;

    @FXML
    private TextField console;

    @FXML
    private HBox hauptbereich;
    @FXML
    private VBox main;
    @FXML
    private VBox bottomNodes;
    @FXML
    private Label lDateiname;
    @FXML
    private Text tConsole;

    @FXML
    private PictureViewer viewer;

    @FXML
    private Menu galery;

    @FXML
    private Button playGif;

    Boolean galeryFirstFire;
    private FileChooser fileChooser;

    private GeometrischeBildoperationen algo1;
    private einPixel algo2;
    private filter algo3;

    String consoleRequest;
    String tconsl;
    String fileName;
    boolean takeGif;

    config cfg = new config();
    String ftpDir = cfg.ftpProjectHomeDirectory;
    ftpServer ftp = new ftpServer();
    List<String> allowedFileTypes = Arrays.asList(cfg.allowedFileTypes);

    List<String> oldPaths = new ArrayList<>();
    List<String> gifFolderNames = new ArrayList<>();
    public void initialize() {
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("images"));

        algo1 = new GeometrischeBildoperationen();
        algo2 = new einPixel();
        algo3 = new filter();
        hideConsole();
    }

    void hideConsole () {
        consoleID.managedProperty().bind(consoleID.visibleProperty());
        consoleID.setVisible(false);
    }

    void showConsole() {
        consoleID.managedProperty().bind(consoleID.visibleProperty());
        consoleID.setVisible(true);
    }

    @FXML
    void bBack(ActionEvent event) {
        back();
    }

    void back () {
        viewer.back();

        int lastItemIndexG = gifFolderNames.size()-1;
        String filePath = lDateiname.getText();
        if (filePath.substring(filePath.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
            try
            {
                FileUtils.deleteDirectory(new File("./images/temp/local/gifImages/"+gifFolderNames.get(lastItemIndexG)));
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
            gifFolderNames.remove(lastItemIndexG);
            //System.out.println(gifFolderNames);
        }
        int lastItemIndexP = oldPaths.size()-1;
        if (lastItemIndexP != -1) {
            lDateiname.setText(oldPaths.get(lastItemIndexP));
            oldPaths.remove(lastItemIndexP);
        }
        if (lDateiname.getText().substring(lDateiname.getText().lastIndexOf(".")+1).toLowerCase().equals("gif")) {
            playGif.setVisible(true);
            Picture pic = new Picture("./images/temp/local/gifImages/"+gifFolderNames.get(gifFolderNames.size()-1)+"/frame_0.png");
            setImage(pic, false);
        } else {
            playGif.setVisible(false);
        }
        //System.out.println(oldPaths);
    }

    @FXML
    void mLanguageChange(ActionEvent event) throws IOException { 
        try {
            FileWriter writer;
            if (language.getLanguage().equals("German")) {
                writer = new FileWriter("./language.txt", false);
                writer.write("English");
            } else {
                writer = new FileWriter("./language.txt", false);
                writer.write("German");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }  
    }

    public void setImage(Picture pic, boolean saveOld) { // schnelle Lösung um die Pfade ebenfalls zu speichern , dafür import von Arrays, --> macht es bequemer Items zu adden, deleten
        takeGif = false;

        /*String filePath = lDateiname.getText(); // nicht sinnvoll und sogar fehlerführend , --> lDateiname wird normalerweise erst danach geändert, da oldPaths egänrz werden muss
        if (filePath.substring(filePath.lastIndexOf('.')+1).equals("gif")) {
        playGif.setVisible(true);
        //oldPaths.add(lDateiname.getText());
        } else {
        playGif.setVisible(false);
        }*/

        if (saveOld) {
            //System.out.println(lDateiname.getText());
            oldPaths.add(lDateiname.getText());
        } else {
        }
        viewer.setImage(pic, saveOld);
    }
    static int leaveRate;
    static int allowRate;
    int gifSize=0;
    @FXML
    void bConsole(ActionEvent event) throws IOException {
        if (isNumeric(console.getText())){
            if (consoleRequest == "RidgeDetection1") {
                int colorAddent = Integer.parseInt(console.getText()); // könnte man auch als einheitliche Var am Anfang für alle initialisieren
                ///
                String path = lDateiname.getText();
                if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
                    // filter on GIF-Images : CASE 2
                    String gifFolder = filterOnGifImages(null); // --> erstellt folder, --> keine Filteranwendung, wenn param == null
                    Picture pic;
                    for (int i = 0; i < gifSize; ++i) {
                        pic = new Picture(gifFolder+"frame_"+i+".png");

                        setImage(algo3.matrixIRidgeDetection1I3x3(pic, 1, colorAddent),false);

                        File outputfile = new File(gifFolder+"frame_"+i+".png");
                        try
                        {
                            ImageIO.write(algo2.convertPicture(viewer.getImage()),"png",outputfile); // leider nötig, da der Imageviewer über Picture images funktioniert, --> und jeder Filter etc. auf diese eingestellt ist, --> Bilder müssen ganz von vorne ausgelesen und geschrieben werden
                        }
                        catch (IOException ioe)
                        {
                            ioe.printStackTrace();
                        }
                    }  
                    pic = new Picture(gifFolder+"frame_0.png");
                    setImage(pic, true);
                } else {
                    Picture img = algo3.matrixIRidgeDetection1I3x3(viewer.getImage(), 1, colorAddent);
                    if (img!= null) {
                        setImage(img, true);
                    }
                }
                ///
            } else if (consoleRequest == "RidgeDetection2") {
                int colorAddent = Integer.parseInt(console.getText());
                ///
                String path = lDateiname.getText();
                if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
                    // filter on GIF-Images : CASE 2
                    String gifFolder = filterOnGifImages(null); // --> erstellt folder, --> keine Filteranwendung, wenn param == null
                    Picture pic;
                    for (int i = 0; i < gifSize; ++i) {
                        pic = new Picture(gifFolder+"frame_"+i+".png");

                        setImage(algo3.matrixIRidgeDetection2I3x3(pic, 1, colorAddent),true);

                        File outputfile = new File(gifFolder+"frame_"+i+".png");
                        try
                        {
                            ImageIO.write(algo2.convertPicture(viewer.getImage()),"png",outputfile); // leider nötig, da der Imageviewer über Picture images funktioniert, --> und jeder Filter etc. auf diese eingestellt ist, --> Bilder müssen ganz von vorne ausgelesen und geschrieben werden
                        }
                        catch (IOException ioe)
                        {
                            ioe.printStackTrace();
                        }
                    }  
                    pic = new Picture(gifFolder+"frame_0.png");
                    setImage(pic, true);
                } else {
                    Picture img = algo3.matrixIRidgeDetection2I3x3(viewer.getImage(), 1, colorAddent);
                    if (img!= null) {
                        setImage(img, true);
                    }
                }
                ///
            } else if (consoleRequest == "Median") {
                int SqrSize = Integer.parseInt(console.getText());
                ///
                String path = lDateiname.getText();
                if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
                    // filter on GIF-Images : CASE 2
                    String gifFolder = filterOnGifImages(null); // --> erstellt folder, --> keine Filteranwendung, wenn param == null
                    Picture pic;
                    for (int i = 0; i < gifSize; ++i) {
                        pic = new Picture(gifFolder+"frame_"+i+".png");

                        setImage(algo3.medianFilter(pic, SqrSize , 1), false);

                        File outputfile = new File(gifFolder+"frame_"+i+".png");
                        try
                        {
                            ImageIO.write(algo2.convertPicture(viewer.getImage()),"png",outputfile); // leider nötig, da der Imageviewer über Picture images funktioniert, --> und jeder Filter etc. auf diese eingestellt ist, --> Bilder müssen ganz von vorne ausgelesen und geschrieben werden
                        }
                        catch (IOException ioe)
                        {
                            ioe.printStackTrace();
                        }
                    }  
                    pic = new Picture(gifFolder+"frame_0.png");
                    setImage(pic, true);
                } else {
                    Picture img = algo3.medianFilter(viewer.getImage(), SqrSize , 1);
                    if (img!= null) {
                        setImage(img, true);
                    }
                }
                ///

            } else if (consoleRequest == "PixelationMatrix") {
                int sqrSize = Integer.parseInt(console.getText());
                ///
                String path = lDateiname.getText();
                if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
                    // filter on GIF-Images : CASE 2
                    String gifFolder = filterOnGifImages(null); // --> erstellt folder, --> keine Filteranwendung, wenn param == null
                    Picture pic;
                    for (int i = 0; i < gifSize; ++i) {
                        pic = new Picture(gifFolder+"frame_"+i+".png");

                        setImage(algo3.pixelation(pic, 1,sqrSize), false);

                        File outputfile = new File(gifFolder+"frame_"+i+".png");
                        try
                        {
                            ImageIO.write(algo2.convertPicture(viewer.getImage()),"png",outputfile); // leider nötig, da der Imageviewer über Picture images funktioniert, --> und jeder Filter etc. auf diese eingestellt ist, --> Bilder müssen ganz von vorne ausgelesen und geschrieben werden
                        }
                        catch (IOException ioe)
                        {
                            ioe.printStackTrace();
                        }
                    }  
                    pic = new Picture(gifFolder+"frame_0.png");
                    setImage(pic, true);
                } else {
                    Picture img = algo3.pixelation(viewer.getImage(), 1,sqrSize);
                    if (img != null) {
                        setImage(img, true);
                    }
                }
                ///
            }
        } else if (consoleRequest == "Percents") {
            try {
                List<String> splitStr = Arrays.asList(console.getText().split(","));
                float RP = Float.parseFloat(splitStr.get(0));
                float GP = Float.parseFloat(splitStr.get(1));
                float BP = Float.parseFloat(splitStr.get(2));

                ///
                String path = lDateiname.getText();
                if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
                    // filter on GIF-Images : CASE 2
                    String gifFolder = filterOnGifImages(null); // --> erstellt folder, --> keine Filteranwendung, wenn param == null
                    Picture pic;
                    for (int i = 0; i < gifSize; ++i) {
                        pic = new Picture(gifFolder+"frame_"+i+".png");

                        setImage(algo2.Prozente(pic,RP,GP,BP, "nach"), false);

                        File outputfile = new File(gifFolder+"frame_"+i+".png");
                        try
                        {
                            ImageIO.write(algo2.convertPicture(viewer.getImage()),"png",outputfile); // leider nötig, da der Imageviewer über Picture images funktioniert, --> und jeder Filter etc. auf diese eingestellt ist, --> Bilder müssen ganz von vorne ausgelesen und geschrieben werden
                        }
                        catch (IOException ioe)
                        {
                            ioe.printStackTrace();
                        }
                    }  
                    pic = new Picture(gifFolder+"frame_0.png");
                    setImage(pic, true);
                } else {
                    Picture img = algo2.Prozente(viewer.getImage(),RP,GP,BP, "nach");
                    if (img!= null) {
                        setImage(img, true);
                    }
                }
                ///

            } catch(NumberFormatException e) {
            }
        } else if (consoleRequest == "ChangeRGB") {

            List<String> splitStr = Arrays.asList(console.getText().split(","));
            String RC = splitStr.get(0);
            String GC = splitStr.get(1);
            String BC = splitStr.get(2);
            String[] newRGB = checkRedGreenBlue(RC,GC,BC);
            RC = newRGB[0];
            GC = newRGB[1];
            BC = newRGB[2];

            ///
            String path = lDateiname.getText();
            if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
                // filter on GIF-Images : CASE 2
                String gifFolder = filterOnGifImages(null); // --> erstellt folder, --> keine Filteranwendung, wenn param == null
                Picture pic;
                for (int i = 0; i < gifSize; ++i) {
                    pic = new Picture(gifFolder+"frame_"+i+".png");

                    setImage(algo2.vertauschen(pic, RC, GC, BC), false);

                    File outputfile = new File(gifFolder+"frame_"+i+".png");
                    try
                    {
                        ImageIO.write(algo2.convertPicture(viewer.getImage()),"png",outputfile); // leider nötig, da der Imageviewer über Picture images funktioniert, --> und jeder Filter etc. auf diese eingestellt ist, --> Bilder müssen ganz von vorne ausgelesen und geschrieben werden
                    }
                    catch (IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
                }  
                pic = new Picture(gifFolder+"frame_0.png");
                setImage(pic, true);
            } else {
                Picture img = algo2.vertauschen(viewer.getImage(), RC, GC, BC);
                if (img!= null) {
                    setImage(img, true);
                }
            }
            ///

            /*txt= consolee.getText();  // Umsetzung mit String nicht möglich, da console.getText() zwar angeblich einen String-Type returnt jedoch dieser sich nicht mit Strings richtig vergleichen lässt, --> returnt immer false

            if (!(String.valueOf(txt.charAt(txt.length()-1)).matches("[a-z?]"))) {
            StringBuffer sb = new StringBuffer(txt);
            sb.deleteCharAt(txt.length()-1); //letzter char muss entfernt werden, da String "abc" sonst nicht als solches anerkannt werden würde
            txt = sb.toString();
            }

            List<String> splitStr = Arrays.asList(txt.split(","));
            RC = splitStr.get(0);
            GC = splitStr.get(1);
            BC = splitStr.get(2);
            setImage(algo2.vertauschen(viewer.getImage(), RC, GC, BC), true);
            /*if (RC=="Red"||RC=="Green"||RC=="Blue"&&GC=="Red"||GC=="Green"||GC=="Blue"&&BC=="Red"||BC=="Green"||BC=="Blue") {
            setImage(algo2.vertauschen(viewer.getImage(), RC, GC, BC), true);

            String a = console.getText();
            tConsole.setText(console.getText().toString());
            System.out.print(tConsole.getText().length());
            }*/
        } else if (consoleRequest== "MainMatrices") {
            List<String> splitStr;
            try {
                splitStr = Arrays.asList(console.getText().split(","));
                int addent = Integer.parseInt(splitStr.get(1));
                System.out.print("colorAddent = "+addent);

                double divisor = Double.parseDouble(splitStr.get(2));
                System.out.print("   divisor = "+divisor);

                boolean pixelating;
                if (StringEqualsWeirdString("true", splitStr.get(3))) {
                    pixelating = true; 
                } else {
                    pixelating = false;
                }

                splitStr = Arrays.asList(splitStr.get(0).split("/"));
                int[] pattern = new int[splitStr.size()];
                for (int i = 0; i < splitStr.size(); ++i) {
                    System.out.print("item "+i+" of pattern contains "+ splitStr.get(i));
                    pattern[i] = Integer.parseInt(splitStr.get(i));
                }
                ///
                String path = lDateiname.getText();
                if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
                    // filter on GIF-Images : CASE 2
                    String gifFolder = filterOnGifImages(null); // --> erstellt folder, --> keine Filteranwendung, wenn param == null
                    Picture pic;
                    for (int i = 0; i < gifSize; ++i) {
                        pic = new Picture(gifFolder+"frame_"+i+".png");

                        setImage(algo3.convolutionMatrix(pic, pattern, addent, divisor, 1, false, pixelating), false);

                        File outputfile = new File(gifFolder+"frame_"+i+".png");
                        try
                        {
                            ImageIO.write(algo2.convertPicture(viewer.getImage()),"png",outputfile); // leider nötig, da der Imageviewer über Picture images funktioniert, --> und jeder Filter etc. auf diese eingestellt ist, --> Bilder müssen ganz von vorne ausgelesen und geschrieben werden
                        }
                        catch (IOException ioe)
                        {
                            ioe.printStackTrace();
                        }
                    }  
                    pic = new Picture(gifFolder+"frame_0.png");
                    setImage(pic, true);
                } else {
                    Picture img = algo3.convolutionMatrix(viewer.getImage(), pattern, addent, divisor, 1, false, pixelating);
                    if (img!= null) {
                        setImage(img, true);
                    }
                }
                ///
            } catch(NumberFormatException e) {
            }
        } else if (consoleRequest== "Main1Pixel") {
            List<String> splitStr;
            splitStr = Arrays.asList(console.getText().split(","));
            String mode = splitStr.get(0);
            if (StringEqualsWeirdString("durchschnitt", mode)) { //Modus bestimmen
                mode = "durchschnitt";
            } else if(StringEqualsWeirdString("max", mode)) {
                mode = "max";
            } else if(StringEqualsWeirdString("min", mode)) {
                mode = "min";
            } else if(StringEqualsWeirdString("natuerlich", mode)) {
                mode = "natuerlich";
            } else {
                mode = "invertiert";
            }

            String RC = splitStr.get(1); // RGB-Verwechslung bestimmten (3 Parameter)
            String GC = splitStr.get(2);
            String BC = splitStr.get(3);
            String[] newRGB = checkRedGreenBlue(RC,GC,BC);
            RC = newRGB[0];
            GC = newRGB[1];
            BC = newRGB[2];

            float RP; // Prozente bestimmen (3 Parameter)
            float GP;
            float BP;
            try {
                RP = Float.parseFloat(splitStr.get(4));
                GP = Float.parseFloat(splitStr.get(5));
                BP = Float.parseFloat(splitStr.get(6));
                setImage(algo2.Prozente(viewer.getImage(),RP,GP,BP, "nach"), true);
            } catch(NumberFormatException e) {
                RP = 100;
                GP = 100;
                BP = 100;
            }

            String befAft = splitStr.get(7); // // Prozente vor/nach bestimmen
            if(StringEqualsWeirdString("vor", befAft)) {
                befAft = "vor";
            } else {
                befAft = "nach";
            }

            try { //Pixelauslassrate auslesen
                leaveRate = Integer.parseInt(splitStr.get(8));
            } catch(NumberFormatException e) {
                leaveRate = 0;
            }

            try { //Pixelauslassrate auslesen
                allowRate = Integer.parseInt(splitStr.get(9));
            } catch(NumberFormatException e) {
                allowRate = 0;
            }
            //System.out.print("mode = "+mode+"   RC = "+RC+"   GC = "+GC+ "   BC = "+BC+"   RP = "+RP+"   GP = "+GP+"   BP = "+BP+"   vorNach = "+vorNach+"   Pixelauslassrate = "+pixelauslassrate);
            int[] allowLeaveRateList = new int[2];
            if (allowRate == 0 && leaveRate == 0) {
                allowLeaveRateList = new int[]{0};
            } else {
                allowLeaveRateList = new int[]{leaveRate,allowRate};
            }

            ///
            String path = lDateiname.getText();
            if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
                // filter on GIF-Images : CASE 2
                String gifFolder = filterOnGifImages(null); // --> erstellt folder, --> keine Filteranwendung, wenn param == null
                Picture pic;
                for (int i = 0; i < gifSize; ++i) {
                    pic = new Picture(gifFolder+"frame_"+i+".png");

                    setImage(algo2.farboperationen(pic, mode, RC, GC, BC, RP, GP, BP, befAft, leaveRate, allowRate, allowLeaveRateList), false);

                    File outputfile = new File(gifFolder+"frame_"+i+".png");
                    try
                    {
                        ImageIO.write(algo2.convertPicture(viewer.getImage()),"png",outputfile); // leider nötig, da der Imageviewer über Picture images funktioniert, --> und jeder Filter etc. auf diese eingestellt ist, --> Bilder müssen ganz von vorne ausgelesen und geschrieben werden
                    }
                    catch (IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
                }  
                pic = new Picture(gifFolder+"frame_0.png");
                setImage(pic, true);
            } else {
                Picture img = algo2.farboperationen(viewer.getImage(), mode, RC, GC, BC, RP, GP, BP, befAft, leaveRate, allowRate, allowLeaveRateList);
                if (img!= null) {
                    setImage(img, true);
                }
            }
            ///
        } else if (consoleRequest == "URLName") {
            //System.out.println(console.getText());
            if (weirdStringContains("/", console.getText())) {
                String userDir = System.getProperty("user.dir");
                URL url;
                try {
                    if (console.getText().contains("giphy.com")) {
                        url = giphyException(console.getText());
                    } else {
                        url = new URL(console.getText());
                    }
                    fileName = url.getFile().substring(url.getFile().lastIndexOf('/')+1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                List<String> list = Arrays.asList(cfg.allowedFileTypes);
                String FileType = fileName.substring(fileName.lastIndexOf('.')+1);
                if (stringContainsItemFromList(fileName, cfg.allowedFileTypes, ".") && list.contains(FileType)  && weirdStringContains(":", fileName) == false) {
                    File fileLoc;
                    String finalName="";
                    if (FileType.toLowerCase().equals("gif")) { // Bestimmung fileLoc für gif-Files --> mögl. final-Name-Bestiimung schon hier nötig
                        finalName = url.toString().replace("/",""); // besser "folderName"
                        finalName = finalName.replace(":","");
                        finalName = finalName.replace("*","");
                        finalName = finalName.replace("?","");
                        finalName = finalName.replace("<","");
                        finalName = finalName.replace(">","");
                        finalName = finalName.replace("|","");
                        while (gifFolderNames.contains(finalName)) {
                            finalName += "copy";
                        }
                        fileLoc = new File("./images/temp/local/gifImages/"+finalName+"/"+fileName); 
                        new File("./images/temp/local/gifImages/"+finalName+"/").mkdirs();
                    } else {
                        fileLoc = new File("./images/temp/"+fileName); 
                    }

                    if (downloadFileFromURL.download(url.toString(),fileLoc)) {
                        /////
                        if (FileType.toLowerCase().equals("gif"))  {
                            gifFolderNames.add(finalName);
                            gifSize = extractGif(fileLoc.toString(),"./images/temp/local/gifImages/"+gifFolderNames.get(gifFolderNames.size()-1)+"/");
                            setImage(new Picture("./images/temp/local/gifImages/"+gifFolderNames.get(gifFolderNames.size()-1)+"/"+"frame_0.png"),true); //
                            playGif.setVisible(true);
                        } else {
                            setImage(new Picture("./images/temp/"+fileName), true);
                        }
                        ///
                        //FileUtils.forceDelete(fileLoc);
                        fileLoc.delete();
                        lDateiname.setText(url.toString());
                    } else {
                        // vllt noch Folder von gifFile wieder löschen -- nicht umbedingt nötig, sowieso nur temp. bis zum nächstem Start
                    }
                }
            } else {
                String userDir = System.getProperty("user.dir");
                File file = new File(userDir+"/images/examples/"+console.getText()); // Überprüfung ob das Bild in irgendeinem folder aus images vorhanden ist (im voraus definierte Ordner außer temp)
                if (file.exists()) { //erster Folder in dem eine solche Datei aufzufinden ist wird gewählt
                    Picture newP = new Picture(file.getAbsolutePath());
                    setImage(newP, true);
                    fileName = console.getText();
                } else {
                    file = new File(userDir+"/images/saves/"+console.getText());
                    if (file.exists()) {
                        Picture newP = new Picture(file.getAbsolutePath());
                        setImage(newP, true);
                        fileName = console.getText();
                    }
                }
            }
            String name = lDateiname.getText();
            if ((name.substring(name.lastIndexOf(".")+1)).toLowerCase().equals("gif")) {
                playGif.setVisible(true);
            } else {
                playGif.setVisible(false);
            }
        } else if (consoleRequest == "SavePG") {
            if (viewer.getImage() != null) {
                String userDir = System.getProperty("user.dir");
                if (fileName!=null && !String.valueOf(console.getText().charAt(0)).equals(".")) {
                    String actualFileType = fileName.substring(fileName.lastIndexOf('.')+1);
                    String fileType = console.getText().substring(console.getText().lastIndexOf('.')+1); // mit substring könnte man scheinbar auch eine funktioniernde getText()-Methode integrieren
                    String fileLoc;
                    boolean TypeAlreadyNamed = true;
                    File file;
                    if (actualFileType.equals("gif")) {
                        String sFile;
                        String gifFolderName = gifFolderNames.get(gifFolderNames.size()-1);
                        if (takeGif) { // vom temp Folder nehmen --> gif wurde sich schon angeschaut + wurde nicht "überschrieben" von anderem Gif // nochmal benutzt bei Saves
                            if (GifNameCounter == 1) {
                                sFile = "./images/temp/local/tempGifs/res1.gif";
                            } else {
                                sFile = "./images/temp/local/tempGifs/res2.gif"; 
                            }
                        } else { // gif erzeugen
                            sFile = "./images/temp/local/gifImages/"+gifFolderName+"/res.gif";
                            createGif("./images/temp/local/gifImages/"+gifFolderName+"/", sFile);
                        }
                        file = new File(sFile);
                        fileLoc = sFile;

                        if (actualFileType.equals(fileType)) { // Angabe mit und ohne File-Endung möglich
                            TypeAlreadyNamed = true;
                        }  else {
                            TypeAlreadyNamed = false;
                        }
                    } else {
                        if (actualFileType.equals(fileType)) { // Angabe mit und ohne File-Endung möglich
                            fileLoc = userDir+"/images/temp/"+console.getText();
                            TypeAlreadyNamed = true;
                        }  else {
                            fileLoc = userDir+"/images/temp/"+console.getText()+"."+actualFileType;
                            TypeAlreadyNamed = false;
                        }
                        file = new File(fileLoc);
                        try
                        {
                            ImageIO.write(algo2.convertPicture(viewer.getImage()),actualFileType,file);
                        }
                        catch (IOException ioe)
                        {
                            ioe.printStackTrace();
                        }
                    }
                    String folder = cfg.ftpProjectHomeDirectory+"/images/";
                    if (TypeAlreadyNamed) {
                        ftp.uploadFile(folder+console.getText(), fileLoc);
                    } else {
                        ftp.uploadFile(folder+console.getText()+"."+actualFileType, fileLoc);
                    }

                    if (!takeGif) {
                        file.delete();
                    }
                }
            }
        } else if (consoleRequest == "LeaveRateVertColor") {
            List<String> splitStr;
            splitStr = Arrays.asList(console.getText().split(","));
            if (splitStr.size() > 2) {
                int R = Integer.valueOf(splitStr.get(0));
                int G = Integer.valueOf(splitStr.get(1));
                int B = Integer.valueOf(splitStr.get(2));
                if (allowRate > 0) {
                    if (R <= 255 && R >= 0 && G <= 255 && G >= 0 && B <= 255 && B >= 0) { // auch in Main-Methode
                        int[] RGBs = {R, G, B};

                        ///
                        String path = lDateiname.getText();
                        if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
                            // filter on GIF-Images : CASE 2
                            String gifFolder = filterOnGifImages(null); // --> erstellt folder, --> keine Filteranwendung, wenn param == null
                            Picture pic;
                            for (int i = 0; i < gifSize; ++i) {
                                pic = new Picture(gifFolder+"frame_"+i+".png");

                                setImage(algo2.farboperationen(pic, null, "Red", "Green", "Blue", 100, 100, 100, "vor", leaveRate, allowRate, RGBs), false);

                                File outputfile = new File(gifFolder+"frame_"+i+".png");
                                try
                                {
                                    ImageIO.write(algo2.convertPicture(viewer.getImage()),"png",outputfile); // leider nötig, da der Imageviewer über Picture images funktioniert, --> und jeder Filter etc. auf diese eingestellt ist, --> Bilder müssen ganz von vorne ausgelesen und geschrieben werden
                                }
                                catch (IOException ioe)
                                {
                                    ioe.printStackTrace();
                                }
                            }  
                            pic = new Picture(gifFolder+"frame_0.png");
                            setImage(pic, true);
                        } else {
                            Picture img = algo2.farboperationen(viewer.getImage(), null, "Red", "Green", "Blue", 100, 100, 100, "vor", leaveRate, allowRate, RGBs);
                            if (img!= null) {
                                setImage(img, true);
                            }
                        }
                        ///
                    }
                }
            }
        } else if (consoleRequest == "LeaveRateVert") {
            List<String> splitStr;
            splitStr = Arrays.asList(console.getText().split(","));
            leaveRate = Integer.valueOf(splitStr.get(0));
            allowRate = Integer.valueOf(splitStr.get(1));
        }
        console.setText("");
        hideConsole();
        if (consoleRequest == "LeaveRateVert" ) {
            consoleRequest = "LeaveRateVertColor";
            showConsole();
            if (language.getLanguage().equals("German")) {
                tConsole.setText("Farbe der Auslasspixel benötigt (R,G,B)");
            } else {
                tConsole.setText("Outletcolor needed (R,G,B)");
            }
        }
    }

    private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
        int nNodes = rootNode.getLength();
        for (int i = 0; i < nNodes; i++) {
            if (rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName)== 0) {
                return((IIOMetadataNode) rootNode.item(i));
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        rootNode.appendChild(node);
        return(node);
    }

    int gifDelayTime (String path) throws IOException { // in ms
        ImageReader reader = ImageIO.getImageReadersBySuffix("gif").next();
        reader.setInput(ImageIO.createImageInputStream(new FileInputStream(path)));
        IIOMetadata imageMetaData =  reader.getImageMetadata(0);
        String metaFormatName = imageMetaData.getNativeMetadataFormatName();

        IIOMetadataNode root = (IIOMetadataNode)imageMetaData.getAsTree(metaFormatName);

        IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");
        return Integer.parseInt(graphicsControlExtensionNode.getAttribute("delayTime")) * 10;
    }

    public int extractGif(String gifLoc, String resFolder) throws IOException {
        // write gifDelay to metadata.txt file
        FileWriter writer;
        writer = new FileWriter(resFolder+"Metadata.txt", false);
        writer.write(String.valueOf(gifDelayTime(gifLoc)));
        writer.close();
        //

        InputStream in = new FileInputStream(gifLoc);
        final GifDecoder.GifImage gif = GifDecoder.read(in);

        final int width = gif.getWidth();
        final int height = gif.getHeight();
        final int background = gif.getBackgroundColor();
        final int frameCount = gif.getFrameCount();
        for (int i = 0; i < frameCount; i++) {
            final BufferedImage img = gif.getFrame(i);
            final int delay = gif.getDelay(i);
            ImageIO.write(img, "png", new File(resFolder + "frame_" + i + ".png"));
        }
        in.close();
        gifSize = frameCount;
        return frameCount;
    }

    public URL giphyException(String link) throws IOException {
        String key="";
        if (link.contains("media")) {
            List<String> splitStr;
            splitStr = Arrays.asList(link.split("/"));
            //System.out.println("splitStr : "+splitStr);
            for (int i = 0; i < splitStr.size(); ++i) {
                if (splitStr.get(i).equals("media")) {
                    key = splitStr.get(i+1);
                    //System.out.println("Key: "+key);
                    key = '.'+key;
                    key = key.substring(key.lastIndexOf('.')+1);
                    //System.out.println("Key: "+key);
                    break;
                }
            }
        } else {
            if (link.contains("-")) {
                key = link.substring(link.lastIndexOf('-')+1);
            } else {
                key = link.substring(link.lastIndexOf("/")+1);
            }
        }
        link = "https://i.giphy.com/media/"+key+"/giphy.gif";
        return new URL(link);
    }

    public static boolean stringContainsItemFromList(String inputStr, String[] items, String joinItemBL)
    {
        inputStr = inputStr.toLowerCase();
        for(int i =0; i < items.length; i++)
        {
            if(inputStr.contains(joinItemBL+items[i].toLowerCase()))
            {
                return true;
            }
        }
        return false;
    }

    private int IntIgnMNHBI(int number) {
        if (number == 0) {
            return 2;
        } else {
            return number;
        }
    }

    public static String[] checkRedGreenBlue(String RC,String GC,String BC) { // da zweimal benötigt
        String[] list = new String[3];
        if (StringEqualsWeirdString("Red",RC)) { //Equal-Abfragen nötig, da die getWeirdString() Funktion nicht funktioniert, --> da die Umwandlung von diesen Chars auf neue zusammengesetze Chars nicht funktioniert, sodass man damit arbeiten kann
            list[0] = "Red";                        // "==" Abfragen mit getText() scheinen auch im weiteren Verlauf nicht zu stimmen; andere Mögl. wäre gewesen mit str.contains() zu arbeiten, welches scheinbar dennoch funktioniert
        } else if (StringEqualsWeirdString("Green",RC)) {
            list[0] = "Green";        
        } else {
            list[0] = "Blue";
        }
        if (StringEqualsWeirdString("Red",GC)) {
            list[1] = "Red";
        } else if (StringEqualsWeirdString("Green",GC)) {
            list[1] = "Green";
        } else {
            list[1] = "Blue";
        }
        if (StringEqualsWeirdString("Red",BC)) {
            list[2] = "Red";
        } else if (StringEqualsWeirdString("Green",BC)) {
            list[2] = "Green";
        } else {
            list[2] = "Blue";
        }
        return list;
    }

    public static boolean isNumeric(String str) { 
        try {  
            Integer.parseInt(str);  
            return true;
        } catch(NumberFormatException e){  
            return false;  
        }  
    }

    @FXML
    void mCloseConsole(ActionEvent event) {
        hideConsole();
        console.setText("");
    }

    @FXML
    void mLeaveVert(ActionEvent event) throws IOException{ // leave pixels wäre besserer Name im Nachhinein
        consoleRequest = "LeaveRateVert";
        showConsole();
        if (language.getLanguage().equals("German")) {
            tConsole.setText("'Auslassrate, Zulassrate' benötigt");
        } else {
            tConsole.setText("'Outletrate, allowrate' needed");
        }
    }

    @FXML
    void mChangeRGB(ActionEvent event) throws IOException {
        consoleRequest = "ChangeRGB";
        showConsole();
        if (language.getLanguage().equals("German")) {
            tConsole.setText("Schema: vertausche 'Red' mit 'Green'/'Blue',...,... (ohne Leerzeichen)");
        } else {
            tConsole.setText("scheme: change 'Red' with 'Green'/'Blue',...,... (without spaces)");
        }
    }

    @FXML
    void mDelete(ActionEvent event) throws IOException {
        Picture img = new Picture();
        fileName = null;
        setImage(img, true);
        playGif.setVisible(false);
        if (language.getLanguage().equals("German")) {
            lDateiname.setText("Bildname");
        } else {
            lDateiname.setText("Picture-Name");
        }
    }

    @FXML
    void mGalerie(ActionEvent event) { // kleines workaround um event zu triggern wenn nur Menu-Feld gedrückt wird // keine Funktion mehr
        /*galery.showingProperty().addListener(
        (observableValue, oldValue, newValue) -> {
        if (newValue) {
        // the first menuItem is triggered
        galery.getItems().get(0).fire();
        }
        }
        );*/
    }

    @FXML
    void mOpenGalery (ActionEvent event) {
        Stage stage = new Stage();
        stage.setWidth(750);
        stage.setHeight(550);
        GaleryGUI g = new GaleryGUI();
        g.start(stage);
        /*if (galeryFirstFire) { //alte workaround-Methode
        galeryFirstFire = false;
        } else {
        Stage stage = new Stage();
        stage.setWidth(750);
        stage.setHeight(570);
        GaleryGUI g = new GaleryGUI();
        g.start(stage);
        }*/
    }

    @FXML
    void mPixelationMatrix(ActionEvent event) throws IOException {
        consoleRequest = "PixelationMatrix";
        showConsole();
        if (language.getLanguage().equals("German")) {
            tConsole.setText("Weite des quadratischen Filters benötigt (muss ungerade sein)");
        } else {
            tConsole.setText("Width of the square filter needed (needs to be odd)"); 
        }
    }

    @FXML
    void mGaussianBlur1(ActionEvent event) {
        String path = lDateiname.getText();
        if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
            filterOnGifImages("gaussianBlur1");    
        } else {
            setImage(algo3.matrixIGaussianBlurI3x3(viewer.getImage(), 1),true);
        }
    }

    @FXML
    void mGaussianBlur2(ActionEvent event) {
        String path = lDateiname.getText();
        if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
            filterOnGifImages("gaussianBlur2");    
        } else {
            setImage(algo3.matrixIGaussianBlurI5x5(viewer.getImage(), 1),true);
        }
    }

    @FXML
    void mGrayscaleAverage(ActionEvent event) {
        String path = lDateiname.getText();
        if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
            filterOnGifImages("greyscaleAverage");    
        } else {
            setImage(algo2.graustufenDurchschnitt(viewer.getImage()), true);
        }
    }

    @FXML
    void mGrayscaleMax(ActionEvent event) {
        String path = lDateiname.getText();
        if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
            filterOnGifImages("greyscaleMax");    
        } else {
            setImage(algo2.graustufenMax(viewer.getImage()), true);
        }
    }

    @FXML
    void mGrayscaleMin(ActionEvent event) {
        String path = lDateiname.getText();
        if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
            filterOnGifImages("greyscaleMin");    
        } else {
            setImage(algo2.graustufenMin(viewer.getImage()), true);
        }
    }

    @FXML
    void mGrayscaleNatural(ActionEvent event) {
        String path = lDateiname.getText();
        if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
            filterOnGifImages("greyscaleNatural");    
        } else {
            setImage(algo2.graustufenNatuerlich(viewer.getImage()), true);
        }
    }

    @FXML
    void mHarteKanten(ActionEvent event) {
        String path = lDateiname.getText();
        if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
            filterOnGifImages("hardEdges");    
        } else {
            setImage(algo3.matrixIHarteKantenI3x3(viewer.getImage(), 1), true);
        }
    }

    @FXML
    void mInvert(ActionEvent event) throws IOException{
        String path = lDateiname.getText();
        if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
            filterOnGifImages("invert");    
        } else {
            setImage(algo2.invertieren(viewer.getImage()), true);
        }
    }
    int GifNameCounter = 2;
    @FXML
    void bPlayGif(ActionEvent event) {
        if (playGif.isVisible() == true) {
            String gifFolderLoc = "./images/temp/local/tempGifs/";
            new File(gifFolderLoc).mkdirs();
            File file;
            if (GifNameCounter == 1) { // workaround --> beim Überschreiben des selben Files wurde teilweise das neue nicht im JFrame übernommen
                file = new File(gifFolderLoc+"res1.gif");
                createGif("./images/temp/local/gifImages/"+gifFolderNames.get(gifFolderNames.size()-1)+"/", gifFolderLoc+"res2.gif");
                setGifFrame(gifFolderLoc+"res2.gif");
                GifNameCounter = 2;
                file.delete();
            } else {
                file = new File(gifFolderLoc+"res2.gif");
                createGif("./images/temp/local/gifImages/"+gifFolderNames.get(gifFolderNames.size()-1)+"/", gifFolderLoc+"res1.gif");
                setGifFrame(gifFolderLoc+"res1.gif");
                GifNameCounter = 1;
                file.delete();
            }
            takeGif = true;
        }
    }
    static JFrame gifFrame = new JFrame();
    public void setGifFrame(String gifLoc) {
        //String fileName = lDateiname.getText().substring(lDateiname.getText().lastIndexOf('/')+1);
        //gifFrame.dispose();
        //gifFrame = new JFrame();
        for (int i= 0; i < 8; i++) {        
            gifFrame.getContentPane().removeAll();
            gifFrame.repaint();

            //BackgroundPanel bgPanel = new BackgroundPanel(img, BackgroundPanel.SCALED, 0.0f, 0.0f);
            Icon icon = new ImageIcon(gifLoc);
            JLabel label = new JLabel();
            label.setIcon(icon);
            gifFrame.add(label);
            gifFrame.pack();
        }

        gifFrame.setVisible(true);
    }

    public String filterOnGifImages(String mode) { // Filter auf jedes Image vom Gif-Image anwenden // gleichzeitig res Folder returner für gif file --> bei komplexeren Methoden
        // Commands um Bilder umzuwandeln nach geg. mode --> bssl unschön, aber schwer anders umzusetzen, vllt. mit reflection --> nur mit bei einfachereren Methoden einsetzbar
        String gifFolder1 = "./images/temp/local/gifImages/"+gifFolderNames.get(gifFolderNames.size()-1); //!
        /*Random rand = new Random();
        int adding = rand.nextInt(999999999);
        while (new File(gifFolder1+String.valueOf(adding)).exists()) { // Sicherstellung der Individualität
        adding = rand.nextInt(999999999);
        }*/
        String gifFolder = gifFolder1;
        //gifFolder += String.valueOf(adding);
        gifFolder += "copy";
        gifFolderNames.add(gifFolder.substring(gifFolder.lastIndexOf('/')+1));
        gifFolder += "/";

        new File(gifFolder).mkdirs();
        File src = new File(gifFolder1);
        File dest = new File(gifFolder);
        try
        {
            FileUtils.copyDirectory(src, dest);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }

        Picture pic;
        if (mode != null) {
            for (int i = 0; i < gifSize; ++i) {
                pic = new Picture(gifFolder+"frame_"+i+".png");

                if (mode == "mirrorVertical") { // geom. (one pixel)
                    setImage(algo1.spiegelVertikal(pic),false);
                } else if (mode == "mirrorHorizontal") {
                    setImage(algo1.spiegelHorizontal(pic),false);
                } else if (mode == "turn90") {
                    setImage(algo1.drehe90Grad(pic),false);
                } else if (mode == "turn180") {
                    setImage(algo1.drehe180Grad(pic),false);
                } else if (mode == "greyscaleAverage") { // one pixel
                    setImage(algo2.graustufenDurchschnitt(pic),false);
                } else if (mode == "greyscaleMin") {
                    setImage(algo2.graustufenMin(pic),false);
                } else if (mode == "greyscaleMax") {
                    setImage(algo2.graustufenMax(pic),false);
                } else if (mode == "greyscaleNatural") {
                    setImage(algo2.graustufenNatuerlich(pic),false);
                } else if (mode == "invert") {
                    setImage(algo2.invertieren(pic),false);
                } else if (mode == "sharpen") { // matrices
                    setImage(algo3.matrixISharpenI3x3(pic, 1),false);
                } else if (mode == "boxblur") {
                    setImage(algo3.matrixIBoxBlurI3x3(pic, 1), false);
                } else if (mode == "gaussianBlur1") {
                    setImage(algo3.matrixIGaussianBlurI3x3(pic, 1), false);
                } else if (mode == "gaussianBlur2") {
                    setImage(algo3.matrixIGaussianBlurI5x5(pic, 1), false);
                } else if (mode == "unsharpMasking") {
                    setImage(algo3.matrixIUnsharpMaskingI5x5(pic, 1), false);
                } else if (mode == "reliefFilter") {
                    setImage(algo3.matrixIReliefFilterI3x3(pic, 1), false);
                } else if (mode == "hardEdges") {
                    setImage(algo3.matrixIHarteKantenI3x3(pic, 1), false);
                } else if (mode == "laplace") {
                    setImage(algo3.matrixILaplaceI3x3(pic, 1), false);
                }

                File outputfile = new File(gifFolder+"frame_"+i+".png");
                try
                {
                    ImageIO.write(algo2.convertPicture(viewer.getImage()),"png",outputfile); // leider nötig, da der Imageviewer über Picture images funktioniert, --> und jeder Filter etc. auf diese eingestellt ist, --> Bilder müssen ganz von vorne ausgelesen und geschrieben werden
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
            pic = new Picture(gifFolder+"frame_0.png");
            setImage(pic, true);
        }
        return gifFolder;
    }

    void createGif(String folder, String resPath) {
        //String fileName = lDateiname.getText().substring(lDateiname.getText().lastIndexOf('/')+1);
        try {
            BufferedImage first = ImageIO.read(new File(folder+"frame_0.png"));
            ImageOutputStream output = new FileImageOutputStream(new File(resPath));//new File("./images/temp/local/gifImages/"+gifFolder+fileName));

            // get gif-Delay from metadata.txt file
            BufferedReader br = new BufferedReader(new FileReader(folder+"metadata.txt"));
            String line = null;
            try {
                StringBuilder sb = new StringBuilder();
                line = br.readLine(); // da sowieso bis jz. nur 1 Zeile benötigt
            } finally {
                br.close();
            }
            int delayTime = Integer.parseInt(line);
            //System.out.println(delayTime);
            //

            GifSequenceWriter writer = new GifSequenceWriter(output, first.getType(), delayTime, true);
            writer.writeToSequence(first);
            //System.out.println(gifSize);
            File[] images = new File[gifSize];
            for (int i = 0; i < gifSize; ++i) {
                images[i] = new File(folder+"frame_"+i+".png");

            }

            for (File image : images) {
                BufferedImage next = ImageIO.read(image);
                writer.writeToSequence(next);
            }

            writer.close();
            output.close();
        } catch (IOException ioe) {

        }
    }

    @FXML
    void mLaplace(ActionEvent event) {
        String path = lDateiname.getText();
        if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
            filterOnGifImages("laplace");    
        } else {
            setImage(algo3.matrixILaplaceI3x3(viewer.getImage(), 1), true);
        }
    }

    @FXML
    void mMainFunction1Pix(ActionEvent event) throws IOException {
        //char[] charList = {'0','0','0','0','0','0','0','0','0','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
        consoleRequest = "Main1Pixel";
        showConsole();
        if (language.getLanguage().equals("German")) {
            tConsole.setText("Schema: Modus ('durchschnitt'/'max'/'min'/'natuerlich'(Graustufen)// 'invertiert'),vertauschR('Green','Blue'),vertauschG,vertauschB,prozentR,prozentG,prozentB((ohne%)),Prozente 'vor'/'nach' Prozess,Pixelauslassrate,Pixelzulassrate");
        } else {
            tConsole.setText("Scheme: Mode ('average'='durchschnitt'/'max'='max'/'min'='min'/'natural'='natürlich' (greyscales) // 'inverted'='invertiert'),changeR('Green','Blue'),changeG,changeB,percentR,percentG,percentB((without%)), percents 'before'='vor' or after='nach' main process, Pixel-outletrate, Pixel-allowRate"); 
        }
    }

    public static boolean StringEqualsWeirdString(String compare, String strange) {
        if (compare.length() != strange.length()) {
            return false;
        } else {
            for (int i = 0; i < compare.length(); ++i) {
                if (compare.charAt(i)!= strange.charAt(i)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean weirdStringContains(String compare, String strange) { // str.contains ginge scheinbar auch direkt mit getText()
        boolean res = false;
        String cuttedStr ="";
        for (int i = 0; i <= strange.length()-compare.length(); ++i) {
            if (compare.charAt(0)==strange.charAt(i)) {
                //System.out.println("identical at: "+(i+1)); 
                for (int j = 0; j < compare.length(); ++j) {
                    cuttedStr = cuttedStr + strange.charAt(i+j);
                }
                //System.out.println("result of identical: "+cuttedStr);
                if (StringEqualsWeirdString(compare, cuttedStr)) {
                    return true;
                }
            }
            cuttedStr="";
        }
        return false;
    }

    public String getWeirdString(String weirdString) { // funktioniert nicht wird im weiteren Verlauf als null aufgefasst
        char[] charList = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','0','1','2','3','4','5','6','7','8','9'};
        String stringRes = "";
        for (int j = 0; j < weirdString.length(); ++j) {
            for (int i = 0; i < charList.length; ++i) {
                if (weirdString.charAt(j) == charList[i]) {
                    stringRes += charList[i]; 
                }
            }
        }
        return stringRes;
    }

    @FXML
    void consoleTextChanged(InputMethodEvent event) {
        tconsl = console.getText().toString();
        System.out.print("Changed to: "+tconsl);
    }

    @FXML
    void mMainFuntionMatrixes(ActionEvent event) throws IOException {
        consoleRequest = "MainMatrices";
        showConsole();
        if (language.getLanguage().equals("German")) {
            tConsole.setText("Schema: Filter 'x/y/z/...',Farbsummand,Teiler,Pixelisation (true -> wahr, false --> falsch) (Filteralgorithmus läuft erst von oben -> unten)");
        } else {
            tConsole.setText("Scheme: Matrix-Pattern 'x/y/z/...', coloraddent, divisor, pixelating (true, false) (Algorithm goes first from up to down)");
        }
    }

    @FXML
    void mMedianMatrix(ActionEvent event) throws IOException {
        consoleRequest = "Median";
        showConsole();
        if (language.getLanguage().equals("German")) {
            tConsole.setText("Breite des quadratischen Filters benötigt");
        } else {
            tConsole.setText("Width of the square filter needed");
        }
    }

    @FXML
    void mMirrorHorizontal(ActionEvent event) {
        String path = lDateiname.getText();
        if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
            filterOnGifImages("mirrorHorizontal");    
        } else {
            setImage(algo1.spiegelHorizontal(viewer.getImage()), true);
        }

    }

    @FXML
    void mMirrorVertikal(ActionEvent event) {
        String path = lDateiname.getText();
        if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
            filterOnGifImages("mirrorVertical");    
        } else {
            setImage(algo1.spiegelVertikal(viewer.getImage()), true);  
        }
    }

    @FXML
    void mOpen(ActionEvent event) throws IOException{
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            fileName = file.getName();
            Picture neuesBild = new Picture(file.getAbsolutePath());
            String name = file.getAbsolutePath();
            if (allowedFileTypes.contains((name.substring(name.lastIndexOf(".")+1)).toLowerCase())) {
                if ((name.substring(name.lastIndexOf(".")+1)).toLowerCase().equals("gif")) {
                    playGif.setVisible(true);
                    String finalName = name.replace("/",""); // besser "folderName"
                    finalName = finalName.replace(":","");
                    finalName = finalName.replace("*","");
                    finalName = finalName.replace("?","");
                    finalName = finalName.replace("<","");
                    finalName = finalName.replace(">","");
                    finalName = finalName.replace("|","");
                    finalName = finalName.replace("\\", "Ä");
                    String fileName = finalName.substring(finalName.lastIndexOf("Ä")+1);
                    while (gifFolderNames.contains(finalName)) {
                        finalName += "c";
                    }
                    File fileLoc = new File("./images/temp/local/gifImages/"+finalName+"/"+fileName); // destination-path for gif
                    gifFolderNames.add(finalName);
                    new File("./images/temp/local/gifImages/"+finalName+"/").mkdirs(); // create Folder for GifImages
                    File srcFile = new File(file.getAbsolutePath()); // source Path as file
                    FileUtils.copyFile(srcFile, fileLoc);
                    gifSize = extractGif(fileLoc.toString(),"./images/temp/local/gifImages/"+finalName+"/");
                    setImage(new Picture("./images/temp/local/gifImages/"+finalName+"/"+"frame_0.png"),true);
                    fileLoc.delete();
                } else {
                    playGif.setVisible(false);
                    setImage(neuesBild, true);
                }
                lDateiname.setText(file.getAbsolutePath());
            }
        }
    }

    @FXML
    void mOpenURLName(ActionEvent event) throws IOException {
        consoleRequest = "URLName";
        showConsole();
        if (language.getLanguage().equals("German")) {
            tConsole.setText("URL oder Filename benötigt");
        } else {
            tConsole.setText("URL or filename needed");
        }
    }

    @FXML
    void mPercents(ActionEvent event) throws IOException {
        consoleRequest = "Percents";
        showConsole();
        if (language.getLanguage().equals("German")) {
            tConsole.setText("Schema: Prozent R, Prozent G, Prozent B, Prozente entw. 'vor' / 'nach' Prozessen anwenden (Prozte ohne % angeben)");
        } else {
            tConsole.setText("Scheme: percent R, percent G, percent B, percents 'before'='vor' or 'after'='nach' main process (percents without %)");
        }
    }

    @FXML
    void mQuit(ActionEvent event) {
        deleteTemp.delete();
        Platform.exit();
    }

    @FXML
    void mReliefMatrix(ActionEvent event) {
        String path = lDateiname.getText();
        if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
            filterOnGifImages("reliefFilter");    
        } else {
            setImage(algo3.matrixIReliefFilterI3x3(viewer.getImage(), 1), true);
        }
    }

    @FXML
    void mRidgeDetection1(ActionEvent event) throws IOException {
        consoleRequest = "RidgeDetection1";
        showConsole();
        if (language.getLanguage().equals("German")) {
            tConsole.setText("Farbsummand benötigt");
        } else {
            tConsole.setText("Coloraddent needed");
        }
    }

    @FXML
    void mRidgeDetection2(ActionEvent event) throws IOException {
        consoleRequest = "RidgeDetection2";
        showConsole();
        if (language.getLanguage().equals("German")) {
            tConsole.setText("Farbsummand benötigt");
        } else {
            tConsole.setText("Coloraddent needed");
        }
    }

    @FXML
    void mSave(ActionEvent event) throws IOException{
        String filePath = lDateiname.getText();
        //System.out.println(filePath);
        List<String> splitString = Arrays.asList(fileName.split("\\."));
        //System.out.println(splitString.get(splitString.size()-2));
        String fileNameC = splitString.get(splitString.size()-2);
        while (new File("./images/saves/"+fileNameC+"."+getFileType(fileName)).exists()) {
            fileNameC += "c";
        }
        fileNameC += "."+getFileType(fileName);
        if (filePath.substring(filePath.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
            sGifSave("./images/saves/"+fileNameC);
        } else {
            File file = new File("./images/saves/"+fileNameC);
            ImageIO.write(algo2.convertPicture(viewer.getImage()),getFileType(lDateiname.getText()),file);
        }
    }

    @FXML
    void mSavePG(ActionEvent event) throws IOException {
        if (cfg.ftpServerON) {
            consoleRequest = "SavePG";
            showConsole();
            if (language.getLanguage().equals("German")) {
                tConsole.setText("Filename benötigt");
            } else {
                tConsole.setText("Filename needed");
            }
        }
    }

    @FXML
    void mSaveAs(ActionEvent event) {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");

        String fileNameWithoutExtension = fileName.replaceFirst("[.][^.]+$", "");
        fileChooser.setInitialFileName(fileNameWithoutExtension);

        String fT = getFileType(fileName);
        //System.out.println("fileName :" + fileName+"          Filetype: "+ fT);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(fT,"*."+fT));
        if (fT.toLowerCase().equals("gif")) {
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                sGifSave(file.getAbsolutePath());
            } else {
            }
        } else {
            BufferedImage image = algo2.convertPicture(viewer.getImage()) ;   
            File file = fileChooser.showSaveDialog(null);
            if (file != null && image != null) {
                try {
                    File SFile = new File(file.getAbsolutePath());
                    ImageIO.write(image,getFileType(fileName),SFile);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        fileChooser.getExtensionFilters().removeAll();
    }

    void sGifSave (String saveLoc) {
        String gifFolderName = gifFolderNames.get(gifFolderNames.size()-1);
        String sFile;
        if (takeGif) { // vom temp Folder nehmen --> gif wurde sich schon angeschaut + wurde nicht "überschrieben" von anderem Gif    // doch nochmal in SavePG benutzt!!
            if (GifNameCounter == 1) {
                sFile = "./images/temp/local/tempGifs/res1.gif";
            } else {
                sFile = "./images/temp/local/tempGifs/res2.gif"; 
            }
        } else { // gif erzeugen
            sFile = "./images/temp/local/gifImages/"+gifFolderName+"/res.gif";
            createGif("./images/temp/local/gifImages/"+gifFolderName+"/", sFile);
        }

        File src = new File(sFile);
        try
        {
            //FileUtils.moveFile(src, sFileFC); //
            FileUtils.copyFile(src, new File (saveLoc));
            if (!takeGif) {
                src.delete();
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public String getFileType(String str) {
        String fileType = "jpg";
        String type = str.substring(str.lastIndexOf(".")+1);
        if (allowedFileTypes.contains(type)) {
            fileType = type;
        }
        /*if (weirdStringContains(".jpg", str)) { // Extensions wählen
        fileType = "jpg";
        } else if (weirdStringContains(".png", str)) {
        fileType = "png";
        }*/
        return fileType;
    }

    @FXML
    void mSharpeningMatrix(ActionEvent event) {
        String path = lDateiname.getText();
        if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
            filterOnGifImages("sharpen");    
        } else {
            setImage(algo3.matrixISharpenI3x3(viewer.getImage(), 1), true);
        }
    }

    @FXML
    void mBoxBlur(ActionEvent event) {
        String path = lDateiname.getText();
        if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
            filterOnGifImages("boxblur");    
        } else {
            setImage(algo3.matrixIBoxBlurI3x3(viewer.getImage(), 1), true);
        }
    }

    @FXML
    void mTurn180(ActionEvent event) {
        String path = lDateiname.getText();
        if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
            filterOnGifImages("turn180");    
        } else {
            setImage(algo1.drehe180Grad(viewer.getImage()), true);
        }
    }

    @FXML
    void mTurn90(ActionEvent event) {
        String path = lDateiname.getText();
        if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
            filterOnGifImages("turn90");    
        } else {
            setImage(algo1.drehe90Grad(viewer.getImage()), true);
        }
    }

    @FXML
    void mUndo(ActionEvent event) {
        back();
    }

    @FXML
    void mUnsharpMasking(ActionEvent event) {
        String path = lDateiname.getText();
        if (path.substring(path.lastIndexOf(".")+1).toLowerCase().equals("gif")) {
            filterOnGifImages("unsharpMasking");    
        } else {
            setImage(algo3.matrixIUnsharpMaskingI5x5(viewer.getImage(), 1), true);
        }
    }

}
