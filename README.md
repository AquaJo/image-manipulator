## image-manipulator
Project was created with blueJ (IDE). Open the project in BlueJ for a better structure (instructions below). \
Its an image-manipulator GUI, working with JavaFX and was developed for a school project. You can use different filter-matrices and functions on images and gifs. If you want, you can directly import them through URLs. \
There is also the ability to include a ftp-server for a public  galery. For that, go into config.java and add your ftp-information. Also turn  boolean ftpServerON to true. \
Language support: English and German
### How to run: 
Go to ./src/ and open package.bluej with bluej (IDE). After opening, compile, right click "BildbearbeitungGUI"-class and left click "Run JavaFX-Application".
### Example: 
original             |  relief filter
:-------------------------:|:-------------------------:
![](https://user-images.githubusercontent.com/84229101/166147318-000da312-31a6-476a-a29c-cf51d37037e7.gif)  |  ![](https://user-images.githubusercontent.com/84229101/166147373-53676ce3-e498-416e-89b0-1bfa7d695d3a.gif)

<p align="center"><sup><sup>source: https://cybercashworldwide.com/royalty-free-gif-images-for-your-website || https://cybercashworldwide.com/wp-content/uploads/2017/05/Gif-Image20.gif
</sup></sup></p>

### Supported actions:

<div align="center">
  
***geometric operations***       |  ***1-pixel operations***       | ***convolution matrices***
:-------------------------:|:-------------------------:|:-------------------------:
vertical mirroring         |greyshades-average         |ridgedetection 1 (3x3)
horizontal mirroring       |greyshades-max             |ridgedetection 2 (3x3)
turn 90 degree             |greyshades-min             |hard edges (3x3)
turn 180 degree            |greyshades-natural         |laplace (3x3)
-|invert                     |sharpening-filter (3x3)
-|change RGB                 |boxblur (3x3 // divisor 9)
-|percents                   |gaussianblur 1 (3x3 // divisor 16)
-|leave pixels               |gaussianblur 2 (5x5 // divisor 256)
-|main function              |unsharp masking (5x5 // divisor -256)
-|-|relief-filter (3x3)
-|-|median-filter (yxy)
-|-|pixelation-filter (yxy)
-|-|main function
</div>

### GUI
<p align="left"> 
  <img src="https://user-images.githubusercontent.com/84229101/166149424-f4cd1b82-9de7-4ed1-a2c6-e94c58275c3c.PNG" width="58%"/>
</p>

### Credits: 
- Big thanks to @DhyanB (https://github.com/DhyanB) for the gif-decoder \
(workaround for error : "java.lang.ArrayIndexOutOfBoundsException: Index 4096 out of bounds for length 4096")
- Thanks to gluonHq for JavaFX 'Scenebuilder'
- Thanks https://memorynotfound.com/ for the instruction of creating a gif-sequence-writer
- Thanks to Thomas Schaller for the imp-package
