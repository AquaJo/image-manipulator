import imp.*;
import java.awt.Color;
import java.util.Random;

import java.util.Arrays;   
public class filter
{
    public  Picture convolutionMatrix(Picture originalPicture, int[] pattern, int colorAddend, double matrixDivisor, int times, boolean median, boolean pixelating) {
        Picture newPicture = new Picture();
        int patternSqrt_y0_p = 0;
        for(int t=0; t < times; t++) {
            if (Math.sqrt(pattern.length) % 1 != 0 || (Math.sqrt(pattern.length)) % 2 != 1) {
                System.out.println("Error: square of 'length of 'pattern'' is a decimal OR square of 'length of 'pattern'' is even");
                return null;
            }
            int patternSqrt = (int) Math.sqrt(pattern.length);
            int offset;
            if (pixelating) {
                offset = patternSqrt;//((patternSqrt-1)/2) + 1 +((patternSqrt-1)/2) + 1;
            } else {
                offset = 1;
            }

            int width = originalPicture.getWidth();
            int height  = originalPicture.getHeight();
            Color[][] pixel = originalPicture.getPixelArray();
            Color[][] pixelNew = new Color[width][height];

            int patternSqrt_xy0 = (patternSqrt-(patternSqrt-1)/2)-1; // allererster Pixel x, y der für die Matrix-Filter-Größe in Betracht kommt
            int patternSqrt_x0_p = 0; // Deklarierung und erste Initialisierung des Start-x-Pixels für jeden Filterblock eines Pixels (in dem Fall natürlich für den ersten betrachteten Pixel)
            for(int x = patternSqrt_xy0; x + (patternSqrt-1)/2 < width; x+=offset) { // Prozedere für jeden Pixel für den der Filter anwenndbar ist
                patternSqrt_y0_p = 0;
                for(int y = patternSqrt_xy0; y+(patternSqrt-1)/2 < height; y+=offset) {
                    int sumR = 0;
                    int sumG = 0;
                    int sumB = 0;
                    int counter = 0;
                    if (median) {
                        double [] pixelFilterR = new double[pattern.length];
                        double [] pixelFilterG = new double[pattern.length];
                        double [] pixelFilterB= new double[pattern.length];
                        for (int x1 = 0; x1 < patternSqrt; x1++) { // einzelne Pixel innerhalb der Reichweite des Filters anschauen, um damit den Farbwert des neuen zu bestimmen
                            for (int y1 = 0; y1 < patternSqrt; y1++) {
                                pixelFilterR[counter] = pixel[patternSqrt_x0_p+x1][patternSqrt_y0_p+y1].getRed()*pattern[counter]; // normalerweise immer 1
                                pixelFilterG[counter] = pixel[patternSqrt_x0_p+x1][patternSqrt_y0_p+y1].getGreen()*pattern[counter];
                                pixelFilterB[counter] = pixel[patternSqrt_x0_p+x1][patternSqrt_y0_p+y1].getBlue()*pattern[counter];
                                counter += 1;
                            }
                        }
                        Arrays.sort(pixelFilterR);
                        Arrays.sort(pixelFilterG);
                        Arrays.sort(pixelFilterB);
                        sumR = (int) pixelFilterR[(pattern.length-1)/2 + 1];
                        sumG = (int) pixelFilterG[(pattern.length-1)/2 + 1];
                        sumB = (int) pixelFilterB[(pattern.length-1)/2 + 1];
                    } else {
                        for (int x1 = 0; x1 < patternSqrt; x1++) { // einzelne Pixel innerhalb der Reichweite des Filters anschauen, um damit den Farbwert des neuen zu bestimmen
                            for (int y1 = 0; y1 < patternSqrt; y1++) {
                                sumR += pixel[patternSqrt_x0_p+x1][patternSqrt_y0_p+y1].getRed()*pattern[counter];
                                sumG += pixel[patternSqrt_x0_p+x1][patternSqrt_y0_p+y1].getGreen()*pattern[counter];
                                sumB += pixel[patternSqrt_x0_p+x1][patternSqrt_y0_p+y1].getBlue()*pattern[counter];
                                counter += 1;
                            }
                        }

                        sumR = (int) (sumR/(matrixDivisor) + colorAddend);
                        sumG = (int) (sumG/(matrixDivisor) + colorAddend);
                        sumB = (int) (sumB/(matrixDivisor) + colorAddend);
                        if (sumR < 0) {
                            sumR = 0;
                        } else if (sumR > 255) {
                            sumR = 255;
                        }
                        if (sumG < 0) {
                            sumG = 0;
                        } else if (sumG > 255) {
                            sumG = 255;
                        }
                        if (sumB < 0) {
                            sumB = 0;
                        } else if (sumB > 255) {
                            sumB = 255;
                        }
                    }
                    pixelNew[x][y] = new Color(sumR, sumG, sumB);

                    if (pixelating) {
                        for (int x1 = 0; x1 < patternSqrt; x1++) { // jeden Pixel im Filter auf den Ergebniswert setzen, wenn pixelating == true
                            for (int y1 = 0; y1 < patternSqrt; y1++) {
                                pixelNew[patternSqrt_x0_p+x1][patternSqrt_y0_p+y1] = new Color(sumR, sumG, sumB);
                            }
                        }
                    }
                    patternSqrt_y0_p += offset; // ""
                }
                patternSqrt_x0_p += offset; // "Start-x-Pixel für jeden "Filterblock" um 1 weiterschieben; keine ganz neue Berechnung nötig

            }

            if (!pixelating) {
                for (int m = 0; m < 2; m++) {// alle Ränder bestimmen mit nahem bestimmtem Farbwert
                    int fTP=patternSqrt_xy0; // "firstTopPixel_YPOS" --> bezieht sich auf die bereits gesetzte Pixel nach der Filteranwendung
                    int fBP=height-1 - patternSqrt_xy0;
                    int fLP=patternSqrt_xy0; //"firstLeftPixel_XPOS"
                    int fRP=width-1 - patternSqrt_xy0;
                    for (int n = 0; n < 2; n++) { // System von 1 und 0 benutzt, ähnlich zu if Abfragen: wenn m = 0 --> 0+x<- ; m = 1 --> 1*(y-x)+x --> y<-
                        for (int x = 0; x < m*(width-fLP)+fLP; x++) {
                            for (int y=0; y < m*(fTP-height)+height; y++) {
                                if (y < fTP && m== 0) { // Unterscheidungen: pixel über bzw. unter dem höchsten bestimmten filterbasierten Pixel müssen durch den naheliegensten bestimmten Pixel bestimmt werden
                                    pixelNew[n*(fRP+1)+x][y] = new Color(pixelNew[n*(fRP-fLP)+fLP][fTP].getRed(),pixelNew[n*(fRP-fLP)+fLP][fTP].getGreen(),pixelNew[n*(fRP-fLP)+fLP][fTP].getBlue());
                                } else if (m*(x-y)+y <= m*(fRP-fBP)+fBP) {
                                    if (m== 0) {
                                        pixelNew[n*(fRP+1)+x][y] = new Color(pixelNew[n*(fRP-fLP)+fLP][y].getRed(),pixelNew[n*(fRP-fLP)+fLP][y].getGreen(),pixelNew[n*(fRP-fLP)+fLP][y].getBlue());
                                    } else {
                                        pixelNew[x][n*(fBP+1)+y] = new Color(pixelNew[x][n*(fBP-fTP)+fTP].getRed(),pixelNew[x][n*(fBP-fTP)+fTP].getGreen(),pixelNew[x][n*(fBP-fTP)+fTP].getBlue());
                                    }
                                } else if (m== 0){
                                    pixelNew[n*(fRP+1)+x][y] = new Color(pixelNew[n*(fRP-fLP)+fLP][fBP].getRed(),pixelNew[n*(fRP-fLP)+fLP][fBP].getGreen(),pixelNew[n*(fRP-fLP)+fLP][fBP].getBlue());
                                }
                            }
                        }
                    }
                }
            } else { // Pixelblöcke am Rand erzeugen
                int fBP= patternSqrt_y0_p;
                //System.out.println("fBP = "+fBP);
                int fRP = patternSqrt_x0_p;
                int counter = 0;
                for (int n = 0; n < 2; ++n) {
                    if (fBP + n*(-fBP  + fRP) != height + n*(-height  + width)) {
                        for (int x = 0; x < width/offset + n* ((-width/offset) + height/offset); ++x) { // x nicht immer für x bedeutend --> 0, 1 System für beide Ränder angewandt -- wahrscheinlich unübersichtlicher und ineffizienter als 2-Versionen davon zu schreiben, jedoch kürzer im Code
                            int sumR = 0, sumG = 0, sumB = 0;
                            for (int x1 = x*offset + n*((-(x*offset)) + fRP) ; x1 < x*offset+offset + n* ((-(x*offset+offset)) + width); ++x1) {
                                for (int y1 = fBP + n* ((-fBP) + x*offset); y1 < height + n*((-height) + x*offset+offset); ++y1) {
                                    sumR += pixel[x1][y1].getRed();
                                    sumG += pixel[x1][y1].getGreen();
                                    sumB += pixel[x1][y1].getBlue();
                                    counter += 1;
                                }
                            }
                            sumR/=counter;
                            sumG/=counter;
                            sumB/=counter;
                            counter = 0;
                            /*
                            for (int x1 = x*offset; x1 < x*offset+offset; ++x1) {
                            for (int y1 = fBP; y1 < height; ++y1) {
                            pixelNew[x1][y1] = new Color(sumR,sumG,sumB);
                            }
                            }*/
                            for (int x1 = x*offset + n*((-(x*offset)) + fRP) ; x1 < x*offset+offset + n* ((-(x*offset+offset)) + width); ++x1) {
                                for (int y1 = fBP + n* ((-fBP) + x*offset); y1 < height + n*((-height) + x*offset+offset); ++y1) {
                                    pixelNew[x1][y1] = new Color(sumR,sumG,sumB);
                                }
                            }
                        }
                    }
                }
            }

            /*for (int x = 0; x < width; x++) { // Überprüfungsmethode ob wirklich die ganze Liste von pixelNew gefüllt ist
            for (int y = 0; y < height; y++) {
            if (pixelNew[x][y]== null) {
            System.out.println("null found at x= "+x+"; y= "+y);
            }
            }
            }*/

            newPicture.setPixelArray(pixelNew);
            originalPicture = newPicture;
        }
        //return (einPixel.graustufenNatuerlich(einPixel.invertieren(newPicture))); // wenn man wollte könnte man zusätlich das entstandene Bild invertieren etc. über klassenübergreifende Methoden
        return(newPicture);
    }
    public Picture pixelation(Picture originalbild, int times, int sqSize) {
        int[] s = new int[sqSize*sqSize];
        for (int n = 0; n < s.length; n++) {
            s[n] = 1;
        }
        return(convolutionMatrix(originalbild, s, 0, s.length, times, false, true));
    }
    
    public Picture matrixIRidgeDetection1I3x3(Picture originalbild, int times, int colorAddend) { // erster Filter aus Schule
        return (convolutionMatrix(originalbild, new int[]{0,-1,0,-1,4,-1,0,-1,0}, colorAddend, 1, times, false, false));
    }

    public Picture matrixIRidgeDetection2I3x3(Picture originalbild, int times, int colorAddend) {
        return (convolutionMatrix(originalbild, new int[]{-1,-1,-1,-1,8,-1,-1,-1,-1}, colorAddend, 1, times, false, false));
    }

    public Picture matrixISharpenI3x3(Picture originalbild, int times) {
        return (convolutionMatrix(originalbild, new int[]{0,-1,0,-1,5,-1,0,-1,0}, 0, 1, times, false, false));
    }

    public Picture matrixIBoxBlurI3x3(Picture originalbild, int times) {
        return (convolutionMatrix(originalbild, new int[]{1,1,1,1,1,1,1,1,1}, 0, 9, times, false, false));
    }

    public Picture matrixIGaussianBlurI3x3(Picture originalbild, int times) {
        return (convolutionMatrix(originalbild, new int[]{1,2,1,2,4,2,1,2,1}, 0, 16, times, false, false));
    }

    public Picture matrixIGaussianBlurI5x5(Picture originalbild, int times) {
        return (convolutionMatrix(originalbild, new int[]{1,4,6,4,1,4,16,24,16,4,6,24,36,24,6,4,16,24,16,4,1,4,6,4,1}, 0, 256, times, false, true));
    }

    public Picture matrixIUnsharpMaskingI5x5(Picture originalbild, int times) {
        return (convolutionMatrix(originalbild, new int[]{1,4,6,4,1,4,16,24,16,4,6,24,-476,24,6,4,16,24,16,4,1,4,6,4,1}, 0, -256, times, false, false));
    }

    public Picture matrixIReliefFilterI3x3(Picture originalbild, int times) { // weitere aus der Schule (vorherige, bereits aus der Schule bekannte Filter: *Sharpen, *BoxBlur
        return(convolutionMatrix(originalbild, new int[] {-2,-1,0,-1,1,1,0,1,2}, 0, 1, times, false, false));
    }

    public Picture matrixIHarteKantenI3x3(Picture originalbild, int times) {
        return(convolutionMatrix(originalbild, new int[] {0,-1,0,0,1,0,0,0,0}, 0, 1, times, false, false));
    }

    public Picture matrixILaplaceI3x3(Picture originalbild, int times) {
        return(convolutionMatrix(originalbild, new int[] {0,1,0,1,-4,1,0,1,0}, 0, 1, times, false, false));
    }

    public Picture medianFilter(Picture originalbild, int SqSize, int times) {
        int[] s = new int[SqSize*SqSize];
        for (int n = 0; n < s.length; n++) {
            s[n] = 1;
        }
        return(convolutionMatrix(originalbild, s, 0, 1, times, true, false));
    }
}
