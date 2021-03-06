import imp.*;
import java.awt.Color;
import java.util.Random;

/**
 * Algorithmen zur Änderung der Pixelpositionen eines Pictures
 * z.B. drehen, spiegeln usw.
 *
 * @author Thomas Schaller
 * @version 1.1 (28.11.2019)
 */
public class GeometrischeBildoperationen 
{
    /** spiegeleHorizontal spiegelt das Bild, so dass rechts und links getauscht werden
     * @param originalbild Ein Bild (Picture), das gespiegelt werden soll
     * @return Eine gespiegelte Kopie des Bildes
     */

    public  Picture spiegelHorizontal(Picture originalbild) {
        int breite = originalbild.getWidth();
        int hoehe  = originalbild.getHeight();

        Color[][] pixel = originalbild.getPixelArray();
        Color[][] pixelNeu = new Color[breite][hoehe];

        for(int x=0; x < breite; x++) {
            for(int y=0;y < hoehe; y++) {
                pixelNeu[x][y] = pixel[(breite-1)-x][y];
            }
        }

        Picture neuesBild = new Picture();
        neuesBild.setPixelArray(pixelNeu); 
        return neuesBild;
    }

    public  Picture spiegelVertikal(Picture originalbild) {
        int breite = originalbild.getWidth();
        int hoehe  = originalbild.getHeight();

        Color[][] pixel = originalbild.getPixelArray();
        Color[][] pixelNeu = new Color[breite][hoehe];

        for(int y=0; y <hoehe; y++) {
            for(int x=0;x < breite; x++) {
                pixelNeu[x][y] = pixel[x][(hoehe-1)-y];
            }
        }

        Picture neuesBild = new Picture();
        neuesBild.setPixelArray(pixelNeu); 
        return neuesBild;
    }

    public  Picture drehe90Grad(Picture originalbild) {
        int breite = originalbild.getWidth();
        int hoehe  = originalbild.getHeight();

        Color[][] pixel = originalbild.getPixelArray();
        Color[][] pixelNeu = new Color[hoehe][breite];

        for(int x=0; x <breite; x++) {
            for(int y=0; y < hoehe; y++) {
                pixelNeu[hoehe-y-1][x] = pixel[x][y];
            }
        }

        Picture neuesBild = new Picture();
        neuesBild.setPixelArray(pixelNeu); 
        return neuesBild;
    }

    public  Picture drehe180Grad(Picture originalbild) {
        Picture neuesBild = new Picture();
        for (int i = 0; i < 2; ++i) {
            int breite = originalbild.getWidth();
            int hoehe  = originalbild.getHeight();

            Color[][] pixel = originalbild.getPixelArray();
            Color[][] pixelNeu = new Color[hoehe][breite];

            for(int x=0; x <breite; x++) {
                for(int y=0; y < hoehe; y++) {
                    pixelNeu[hoehe-y-1][x] = pixel[x][y];
                }
            }

            neuesBild.setPixelArray(pixelNeu);
            originalbild = neuesBild;
        }
        return neuesBild;
    }

}
