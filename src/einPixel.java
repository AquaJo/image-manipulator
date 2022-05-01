import imp.*;
import java.awt.Color;
import java.util.Random;
import java.awt.image.*;

public class einPixel
{
    public static  Picture farboperationen(Picture originalbild, String mode, String vertauschR, String vertauschG, String vertauschB, float prozentR, float prozentG, float prozentB, String prozenteVorNach, int pixelauslassrate, int zulassrate, int[] pAColorRGBs) {
        int breite = originalbild.getWidth();
        int hoehe  = originalbild.getHeight();

        Color[][] pixel = originalbild.getPixelArray();
        Color[][] pixelNeu = new Color[breite][hoehe];
        int graustufe=38219;
        int R=0;
        int G=0;
        int B=0;
        if (prozentR > 100) { // Eingrenzung der Prozentwerte
            prozentR = 100;
        } else if (prozentR < 0) {
            prozentR = 0;
        }
        if (prozentG > 100) {
            prozentG = 100;
        } else if (prozentG < 0) {
            prozentG = 0;
        }
        if (prozentB > 100) {
            prozentB = 100;
        } else if (prozentB < 0) {
            prozentB = 0;
        }
        int pACounter_x = 0;
        int pACounter_y = 0;
        int offset = 1;
        for(int x=0; x < breite; x+=offset) {
            pACounter_x += 1;
            for(int y=0;y < hoehe; y+=offset) {
                pACounter_y += 1;
                if (vertauschR=="Red"){ //Red, Green, Blue Values initialisieren / Sortierung ob zB. Red wirklich Red-Werte beinhaltet //passiert vor erweiterter Pixelfarbeninitialisierung , könnte man auch noch für nach Bestimmung einreichten, jedoch gibt es viele Optionen für Pixelfarbenveränderungen. So könnte man theoretisch auch wollen,dass diese Verwechslung der Farbkanäle nach/vor einer Bestimmung der Farbänderung der Pixelfarbe durch Prozentzahlen erfolgt. 
                    R=pixel[x][y].getRed();
                } else if (vertauschR=="Green") {
                    R=pixel[x][y].getGreen();
                } else if (vertauschR=="Blue") {
                    R=pixel[x][y].getBlue();
                }
                if (vertauschG=="Red"){
                    G=pixel[x][y].getRed();  
                } else if (vertauschG=="Green") {
                    G=pixel[x][y].getGreen();
                } else if (vertauschG=="Blue") {
                    G=pixel[x][y].getBlue();
                }
                if (vertauschB=="Red"){
                    B=pixel[x][y].getRed();  
                } else if (vertauschB=="Green") {
                    B=pixel[x][y].getGreen();
                } else if (vertauschB=="Blue") {
                    B=pixel[x][y].getBlue();
                }

                if (prozenteVorNach == "vor") { // Prozentwerte anwenden / passiert vor erweiterter Pixelfarbeninitialisierung
                    R = (int) Math.round((prozentR/100)*R);
                    G = (int) Math.round((prozentG/100)*G);
                    B = (int) Math.round((prozentB/100)*B);
                }
                // einzelne Modi durchgehen
                if (mode == "durchschnitt") { 
                    graustufe = (int) Math.round((R+G+B)/3);
                } else if (mode == "min") {
                    if (R <= B && R <= G) {
                        graustufe = R;
                    } else if (G <= B) {
                        graustufe = G;
                    } else {
                        graustufe = B;
                    }
                } else if (mode == "max") {
                    if (R >= B && R >= G) {
                        graustufe = R;
                    } else if (G >= B) {
                        graustufe = G;
                    } else {
                        graustufe = B;
                    }
                } else if (mode == "natuerlich") {
                    graustufe = (int) Math.round((R*(29.9/100) + G*(58.7/100) + B*(11.4/100)));
                }

                if (graustufe != 38219) {
                    R=graustufe;
                    G=graustufe;
                    B=graustufe;
                }

                if (mode == "invertiert") { // auch wenn zb. rot durch blau verwechselt/ersetzt wurde, wird bei der Farbveränderung bei x% für rot die farbe blau folglich verändert nicht automatisch rot
                    R=255-R;
                    G=255-G;
                    B=255-B;
                }

                if (prozenteVorNach == "nach") {
                    R = (int) Math.round((prozentR/100)*R); // Prozentwerte anwenden / passiert nach Pixelfarbeninitialisierung
                    G = (int) Math.round((prozentG/100)*G);
                    B = (int) Math.round((prozentB/100)*B);
                }
                pixelNeu[x][y] = new Color(R, G, B);
                
                if (pACounter_y == zulassrate) {
                    offset = pixelauslassrate + 1;
                    pACounter_y = 0;
                } else {
                    offset = 1;
                }
            }
            if (pACounter_x == zulassrate) {
                    offset = pixelauslassrate + 1;
                    pACounter_x = 0;
                } else {
                    offset = 1;
                }
        }

        if (pAColorRGBs.length > 2) { // bei pixelauslassrate ausgelassenePixel mit bestimmter Farbe aus Liste ersetzen
            int RpA = pAColorRGBs[0];
            int GpA = pAColorRGBs[1];
            int BpA = pAColorRGBs[2];
            if (RpA <= 255 && RpA >= 0 && GpA <= 255 && GpA >= 0 && B <= 255 && BpA >= 0) {
                for (int x=0; x < breite; x++) {
                    for(int y=0; y < hoehe; y++) {
                        if (pixelNeu[x][y] == null ) {
                            pixelNeu[x][y] = new Color(RpA,GpA,BpA);
                        }
                    }
                }
            }
        }

        Picture neuesBild = new Picture();
        neuesBild.setPixelArray(pixelNeu); 
        return neuesBild;
    }

    public BufferedImage convertPicture(Picture pic) {
        Color[][] picC = pic.getPixelArray();
        int width = pic.getWidth();
        int height = pic.getHeight();
        BufferedImage newPic = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newPic.setRGB(i,j,picC[i][j].getRGB());
            }
        }
        return newPic;
    }

    public Picture oeffneBild (Picture originalbild) {
        return(farboperationen(originalbild,"nichts", "Red", "Green", "Blue", 100, 100, 100, null, 0,0, new int[]{0}));
    }

    public static Picture graustufenDurchschnitt(Picture originalbild) {
        return (farboperationen(originalbild,"durchschnitt", "Red", "Green", "Blue", 100, 100, 100, null, 0,0, new int[]{0}));
    }

    public static Picture graustufenMax(Picture originalbild) {
        return (farboperationen(originalbild,"max", "Red", "Green", "Blue", 100, 100, 100, null, 0,0, new int[]{0}));
    }

    public static Picture graustufenMin(Picture originalbild) {
        return (farboperationen(originalbild, "min", "Red", "Green", "Blue", 100, 100, 100, null, 0,0, new int[]{0}));
    }

    public static Picture graustufenNatuerlich(Picture originalbild) {
        return (farboperationen(originalbild, "natuerlich", "Red", "Green", "Blue", 100, 100, 100, null, 0,0, new int[]{0}));
    }

    public static Picture invertieren(Picture originalbild) {
        return (farboperationen(originalbild, "invertiert",  "Red", "Green", "Blue", 100, 100, 100, null, 0,0, new int[]{0}));
    }

    public static Picture vertauschen(Picture originalbild, String vertauschR, String vertauschG, String vertauschB) {
        return (farboperationen(originalbild, null, vertauschR, vertauschG, vertauschB, 100, 100, 100, null, 0,0, new int[]{0})) ;
    }

    public static Picture Prozente(Picture originalbild, float prozentR, float prozentG, float prozentB, String nachOderVorPixelFarbinitialisierung) {
        return (farboperationen(originalbild, null, "Red", "Green", "Blue", prozentR, prozentG, prozentB, nachOderVorPixelFarbinitialisierung, 0,0, new int[]{0}));
    }
}
