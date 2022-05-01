import imp.*;
import javafx.scene.Parent;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Dimension;

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

//import javafx.beans.property.BooleanProperty;
//import javafx.beans.property.SimpleBooleanProperty;
//import javax.swing.event.ChangeListener;

import java.util.List;
import java.util.stream.*;
import java.util.Arrays;

import java.io.IOException;
import java.net.URL;
import java.awt.image.*;
import javax.imageio.*;

import javafx.embed.swing.SwingFXUtils;

public class GaleryController {
    String[] fileTypes;
    String[] fileNames;
    @FXML
    private VBox mainG;

    @FXML
    private ImageView viewer1;

    @FXML
    private ImageView viewer2;

    @FXML
    private ImageView viewer3;

    @FXML
    private ImageView viewer4;

    @FXML
    private ImageView viewer5;

    @FXML
    private ImageView viewer6;

    @FXML
    private Button openGif1;

    @FXML
    private Button openGif2;

    @FXML
    private Button openGif3;

    @FXML
    private Button openGif4;

    @FXML
    private Button openGif5;

    @FXML
    private Button openGif6;

    private FileChooser fileChooser;
    public static int importGalery = 0;
    config cfg = new config();
    ftpServer ftp = new ftpServer();
    int site;
    long pause = cfg.ftpExercisingPauses;
    String ftpDir = cfg.ftpProjectHomeDirectory;
    final String downloadPath = "./images/temp/cloudDownloads/";
    static JFrame gifFrame = new JFrame();
    @FXML
    private PictureViewer viewer;
    public void initialize() {
        if (cfg.ftpServerON) {
            site = 1;
            startDownloadingDisplaying(site); //"+" macht in dem Fall nichts, da fileNames == null
        }
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

    public int startDownloadingDisplaying (int site) { // site 
        hideAllOpenGifs();
        String folder = ftpDir+"/images/";
        fileNames = ftp.listFilesFromDirectory(folder, true, false, Math.abs((site-1)*6),Math.abs(((site-1)*6)+5)); // String Directory, boolean filter, boolean all, int first, int last
        pause (pause); // safety-Pause aufgrund von vorhergegangenem Verlust von FTP-Zugang über normale Adresse aus unerklärlichen Gründen
        String[] fileTypes = getFileTypes(fileNames);
        if (fileNames.length > 0) {
            String [] ftpPaths = new String[fileNames.length];
            String [] localPaths = new String[fileNames.length];
            new File(downloadPath).mkdirs();
            for (int i = 0; i < fileNames.length; ++i) { //downloaden der Images
                ftpPaths[i] = folder+fileNames[i];
                localPaths[i] = downloadPath+"picture"+i+"."+fileTypes[i];
                //ftp.downloadFile(folder+fileNames[i],"./images/temp/picture"+i+"."+fileTypes[i]); // login auf ftp erfolgt gleich mehrmals /// damalige Methode
            }
            ftp.downloadFiles(ftpPaths,localPaths);
            for (int j = 0; j < fileNames.length; ++j) { // setzen der Images
                BufferedImage img = null;
                try {
                    img = ImageIO.read(new File(downloadPath+"picture"+j+"."+fileTypes[j]));
                } catch (IOException e) {
                }
                WritableImage imgRes = SwingFXUtils.toFXImage(img, null);
                if (j == 0) {
                    viewer1.setImage(imgRes);
                    if (fileTypes[j].equals("gif")) {
                        openGif1.setVisible(true);
                    }
                } else if (j == 1) {
                    viewer2.setImage(imgRes);
                    if (fileTypes[j].equals("gif")) {
                        openGif2.setVisible(true);
                    }
                } else if (j == 2) {
                    viewer3.setImage(imgRes);
                    if (fileTypes[j].equals("gif")) {
                        openGif3.setVisible(true);
                    }
                } else if (j == 3) {
                    viewer4.setImage(imgRes);
                    if (fileTypes[j].equals("gif")) {
                        openGif4.setVisible(true);
                    }
                } else if (j == 4) {
                    viewer5.setImage(imgRes);
                    if (fileTypes[j].equals("gif")) {
                        openGif5.setVisible(true);
                    }
                } else {
                    viewer6.setImage(imgRes);
                    if (fileTypes[j].equals("gif")) {
                        openGif6.setVisible(true);
                    }
                }
            }
            return fileNames.length; // nicht mehr nötig --> fileNames ist nun für die ganze Klasse verfügbar
        } else {
            return 0;
        }
    }

    public void hideAllOpenGifs() {
        openGif1.setVisible(false);
        openGif2.setVisible(false);
        openGif3.setVisible(false);
        openGif4.setVisible(false);
        openGif5.setVisible(false);
        openGif6.setVisible(false);
    }

    public String[] getFileTypes(String[] fileNames) {
        fileTypes = new String[fileNames.length];
        for (int i = 0; i < fileNames.length; ++i) {
            String fileName = fileNames[i];
            fileTypes[i] = fileName.substring(fileName.lastIndexOf('.')+1);
        }
        return fileTypes;
    }

    @FXML
    void bBack(ActionEvent event) {
        site -= 1;
        int elementsNum = startDownloadingDisplaying(site);
        if (elementsNum > 0) { // Seitenänderung kann vorgenommen werden Liste an anzugeinenden Elementen > 0
            if (elementsNum < 2) {
                viewer2.setImage(null);
            }
            if (elementsNum < 3) {
                viewer3.setImage(null);
            }
            if (elementsNum < 4) {
                viewer4.setImage(null);
            }
            if (elementsNum < 5) {
                viewer5.setImage(null);
            }
            if (elementsNum < 6) {
                viewer6.setImage(null);
            }
        } else {
            site += 1; // Revidierung der Seitenänderung, Listenlänge an anzuzeigenden Elementen = 0
        }
    }

    @FXML
    void bDownload1(ActionEvent event) {
        download(0);
    }

    void download(int itemIndex) {
        if (fileNames.length > 0) {
            int lastViewer = 6;
            if (viewer1.getImage() == null) {
                lastViewer = 0;
            } if (viewer2.getImage() == null) {
                lastViewer = 1;
            } else if (viewer3.getImage() == null) {
                lastViewer = 2;
            } else if (viewer4.getImage() == null) {
                lastViewer = 3;
            } else if (viewer5.getImage() == null) {
                lastViewer = 4;
            } else if (viewer6.getImage() == null) {
                lastViewer = 5;
            }
            if (itemIndex  < lastViewer) {
                fileChooser = new FileChooser();
                fileChooser.setTitle("Save Image");

                String item = fileNames[itemIndex];
                String type = fileTypes[itemIndex];
                String fileNameWithoutExtension = item.replaceFirst("[.][^.]+$", "");
                fileChooser.setInitialFileName(fileNameWithoutExtension);

                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(type,"*."+type)); 
                BufferedImage image = null;
                try {
                    image = ImageIO.read(new File(downloadPath+"picture"+itemIndex+"."+type));
                } catch (IOException e) {
                }
                File file = fileChooser.showSaveDialog(null);
                if (file != null && image != null) {
                    try {
                        File SFile = new File(file.getAbsolutePath());
                        ImageIO.write(image,type,SFile);
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
                fileChooser.getExtensionFilters().removeAll();
            }
        }
    }

    @FXML
    void bDownload2(ActionEvent event) {
        download(1);
    }

    @FXML
    void bDownload3(ActionEvent event) {
        download(2);
    }

    @FXML
    void bDownload4(ActionEvent event) {
        download(3);
    }

    @FXML
    void bDownload5(ActionEvent event) {
        download(4);
    }

    @FXML
    void bDownload6(ActionEvent event) {
        download(5);
    }

    @FXML
    void bNext(ActionEvent event) {
        site += 1;
        int elementsNum = startDownloadingDisplaying(site);
        if (elementsNum > 0) { // Seitenänderung kann vorgenommen werden Liste an anzugeinenden Elementen > 0
            if (elementsNum < 2) {
                viewer2.setImage(null);
            }
            if (elementsNum < 3) {
                viewer3.setImage(null);
            }
            if (elementsNum < 4) {
                viewer4.setImage(null);
            }
            if (elementsNum < 5) {
                viewer5.setImage(null);
            }
            if (elementsNum < 6) {
                viewer6.setImage(null);
            }
        } else {
            site -= 1; // Revidierung der Seitenänderung, Listenlänge an anzuzeigenden Elementen = 0
        }
    }

    @FXML
    void bOpenGif1(ActionEvent event) {
        if (fileNames.length > 0 && openGif1.isVisible() == true) {
            setGifFrame(1);
        }
    }

    @FXML
    void bOpenGif2(ActionEvent event) {
        if (fileNames.length > 0 && openGif2.isVisible() == true) {
            setGifFrame(2);
        }
    }

    @FXML
    void bOpenGif3(ActionEvent event) {
        if (fileNames.length > 0 && openGif3.isVisible() == true) {
            setGifFrame(3);
        }
    }

    @FXML
    void bOpenGif4(ActionEvent event) {
        if (fileNames.length > 0 && openGif4.isVisible() == true) {
            setGifFrame(4);
        }
    }

    public void setGifFrame(int num) {
        gifFrame.getContentPane().removeAll();
        gifFrame.repaint();
        //BackgroundPanel bgPanel = new BackgroundPanel(img, BackgroundPanel.SCALED, 0.0f, 0.0f);
        Icon icon = new ImageIcon(downloadPath+"picture"+(num-1)+".gif");
        JLabel label = new JLabel();
        label.setIcon(icon);

        gifFrame.add(label);
        gifFrame.pack();

        gifFrame.setVisible(true);
    }

    @FXML
    void bOpenGif5(ActionEvent event) {
        if (fileNames.length > 0 && openGif5.isVisible() == true) {
            setGifFrame(5);
        }
    }

    @FXML
    void bOpenGif6(ActionEvent event) {
        if (fileNames.length > 0 && openGif6.isVisible() == true) {
            setGifFrame(6);
        }
    }

    @FXML
    public void exitApplication(ActionEvent event) {
        gifFrame.dispose();
    }
    /*
    @FXML
    void bImport1(ActionEvent event) {
    importImg(0);
    }

    void importImg(int itemIndex) {
    if (fileNames.length > 0) {
    int lastViewer = 6;
    if (viewer1.getImage() == null) {
    lastViewer = 0;
    } if (viewer2.getImage() == null) {
    lastViewer = 1;
    } else if (viewer3.getImage() == null) {
    lastViewer = 2;
    } else if (viewer4.getImage() == null) {
    lastViewer = 3;
    } else if (viewer5.getImage() == null) {
    lastViewer = 4;
    } else if (viewer6.getImage() == null) {
    lastViewer = 5;
    }
    if (itemIndex  < lastViewer) {
    //String type = fileTypes[itemIndex]; // 
    //String name = fileNames[itemIndex];
    //String localPath = "./images/temp/picture"+itemIndex+"."+type;
    //String ftpPath = cfg.ftpProjectHomeDirectory+"/"+name;
    Picture pic = new Picture("D:/OneDrive/Privat/Dokumente/Meine Programme/bluej/farboperationen3.2.22 2Pmedian4-copyBU-copy2/images/temp/picture4.jpg");
    //Controller c = new Controller();
    //c.importP(pic);
    }
    }
    }

    @FXML
    void bImport2(ActionEvent event) {
    importImg(1);
    }

    @FXML
    void bImport3(ActionEvent event) {
    importImg(2);
    }

    @FXML
    void bImport4(ActionEvent event) {
    importImg(3);
    }

    @FXML
    void bImport5(ActionEvent event) {
    importImg(4);
    }

    @FXML
    void bImport6(ActionEvent event) {
    importImg(5);
    }*/
}
