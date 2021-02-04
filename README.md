# eyeDo: an Android Application for the Visually Impaired that use LytNet Neural Network to recognize Pedestrian Traffic Light

## Introduction
The strategy that has been followed is described in the following image:

![](path.png)


Link to owr repo with <b>Training</b> and <b>Conversion Pytorch to TorchScript</b>: https://github.com/marcoleino/eyeDoPy

## App

We have developed a simple app that uses the trained LytNet network to recognize the state of pedestrian traffic lights. The app can be used only in landscape mode. It is composed by a preview of the camera, a settings menu (to choose the type of net to use) and an image of a traffic light that will light differently respect to the recognized color (black if none) which can be clicked to see informations about the net and its performance.

The app changes state of the recognized color only if 4 consecutive net predictions are the same, to increase accuracy at the cost of a slight drop in performance. For each state, the app (a thread) emits a different sound:

 1. None: low tone with slow frequency
 2. Red: low tone with slightly higher frequency (1 per sec)
 3. Green: double mid tone with medium frequency
 4. Yellow: one high tone with high frequency (>1 per sec)
 
 ### API and Camera

The app uses API of Camera2 class for Android. The app selects a camera with a resolution higher than 768x576 and chooses a preferable ratio of 4:3: If 4:3 can't be found it chooses the highest resolution and add black pixels to the image to fit it for the correct ratio. In any case, the image will be resized during PreProcess to 768x576.

### Elaboration

The elaboration of images by the CNN is carried out for each new frame received only when the previous elaboration is finished (this is to avoid calling the elaboration function for every single frame). They are performed by the class Classifier, called by the MainActivity.

To see how we properly pre-process an image before sending it to the network in TorchScript, look at the PreProcess function inside of Classifier in the Android App, where we make our own transposing algorithm.

To use the application you can download the code and open it inside Android Studio.
