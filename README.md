# eyeDo: an Android Application for the Visually Impaired that use LytNet Neural Network to recognize Pedestrian Traffic Light

## Introduction
In this project we train a neural network called LytNet, then we convert the model from Pytorch to TorchScript and develop an Android App that use it to recognize pedestrian traffic light. The following image rappresent the approach followed and the related path options.

![](path.png)

To train and test the CNN we have used Pedestrian-Traffic-Lights (PTL) that is a high-quality image dataset of street intersections, created for the detection of pedestrian traffic lights and zebra crossings. Images have variation in weather, position and orientation in relation to the traffic light and zebra crossing, and size and type of intersection. To download the dataset and for more information visit ImVisible project (link above).

Link to owr repo with <b>Training</b> and <b>Conversion Pytorch to TorchScript</b>: https://github.com/marcoleino/eyeDoPy

## Application

We have developed a simple app to use the LytNet network to recognize pedestrian traffic light. The app should be used only in landscape mode. The app is composed of a preview of the camera, a settings menu (to choose the type of net to use) and an image of a traffic light that will light based on the color recognized (black if none) that can be clicked to see informations about the net and performance.

The app change state of the color recognized only if after 3 consecutive net prediction are the same, to increase accuracy at the cost of a slight drop in performance. for each state the app (a thread) emits a different sound:

 1. None: low tone with slow frequency
 2. Red: low tone with slightly higher frequency (1 per sec)
 3. Green: double mid tone with slightly higher frequency
 4. Yellow: one high tone with high frequency (>1 per sec)
 
 ### API and Camera

The app uses API of Camera2 class for Android. The app select a camera with a resolution higher than 768x576 and choose a preferable ratio of 4:3, if 4:3 can't be found it choose the higher resolution and add black pixel to the image to fit for the correct ratio. Images are also rotate to avoid stretching, because they are received as portrait but must be landscape. For every case the image will be resized during PreProcess to 768x576.

### Elaboration

Elaboration of images by the CNN are carried out for every new frame received only if the previous elaboration is finished (this to avoid calling the function for every single frame). They are performed by the class Classifier, called by MainActivity.

To see how we properly pre-process an image before sending it to the network in TorchScript look at the PreProcess function inside of Classifier in the Android App, where we make our own transpose algorithm.

To use the application you can download the code and open it inside Android Studio.
