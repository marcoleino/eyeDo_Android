# eyeDo: an Android Application for the Visually Impaired that use LytNet Neural Network to recognize Pedestrian Traffic Light

## Introduction
In this project we train a neural network called LytNet, then we convert the model from Pytorch to TorchScript and develop an Android App that use it to recognize pedestrian traffic light. The following image rappresent the approach followed and the related path options.

![](path.png)

To train and test the CNN we have used Pedestrian-Traffic-Lights (PTL) that is a high-quality image dataset of street intersections, created for the detection of pedestrian traffic lights and zebra crossings. Images have variation in weather, position and orientation in relation to the traffic light and zebra crossing, and size and type of intersection. To download the dataset and for more information visit ImVisible project (link above).

## Application


To see how we properly pre-process an image before sending it to the network in TorchScript look at the PreProcess function inside of Classifier in the Android App, where we make our own transpose algorithm.

To use the application you can download the code and open it inside Android Studio.
