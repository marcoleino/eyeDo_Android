package com.sistemidigitali.eyeDo_Android;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import org.pytorch.Tensor;
import org.pytorch.Module;
import org.pytorch.IValue;
import java.io.IOException;
import java.io.InputStream;


public class Classifier {

    Module model;
    //float[] mean = {120.56737612047593f, 119.16664454573734f, 113.84554638827127f};
    //float[] std = {66.32028460114392f, 65.09469952002551f, 65.67726614496246f};
    //float[] mean = TensorImageUtils.TORCHVISION_NORM_MEAN_RGB;
    //float[] std = TensorImageUtils.TORCHVISION_NORM_STD_RGB;
    float[] mean = {0.00000000000000f, 0.00000000000000f, 0.00000000000000f};
    float[] std =  {1.00000000000000f, 1.00000000000000f, 1.00000000000000f};
    float[][][][] pix = new float[Constants.batchSize][Constants.inputChannels][Constants.inputHeight][Constants.inputWidth];
    float [] pixFlat = new float[Constants.batchSize * Constants.inputChannels * Constants.inputHeight * Constants.inputWidth];
    long [] shape = {Constants.batchSize , Constants.inputChannels , Constants.inputHeight , Constants.inputWidth};

    Context context;

    public Classifier(String modelPath, Context context){
        model = Module.load(modelPath);
        this.context = context;
    }



    private Tensor preElaboration(Bitmap bitmap){
        //if the ratio of the bitmap is close to the Constants.ratio--> apply a normal resize; otherwise apply a custom resize (fill with black pixels to adapt to the Constants.ratio without stretching the image)
        if((((float)bitmap.getWidth()/(float)bitmap.getHeight()) - Constants.ratio) < 0.1 || (((float)bitmap.getWidth()/(float)bitmap.getHeight()) - Constants.ratio) > -0.1)
            bitmap = Bitmap.createScaledBitmap(bitmap,Constants.inputWidth,Constants.inputHeight,false);
        else
            bitmap = Utils.resize(bitmap, Constants.inputHeight, Constants.inputWidth);
        int t;
        //Manually creating a flat rgb array referring to a (1,3,576,768) shape
        //1. transposing the dimensions: (768,576,3) --> (1,3,576,768)
        for (int h = 0; h < bitmap.getHeight(); h++){
            for(int w = 0; w < bitmap.getWidth(); w++){
                t = bitmap.getPixel(w,h);
                pix[0][0][h][w] = Color.red(t);
                pix[0][1][h][w] = Color.green(t);
                pix[0][2][h][w] = Color.blue(t);
            }
        }
        //2. flattening the array
        int d = 0;
        for (int c = 0; c < 3; c++){
            for (int h = 0; h < bitmap.getHeight(); h++){
                for(int w = 0; w < bitmap.getWidth(); w++){
                    pixFlat[d]=pix[0][c][h][w];
                    d++;
                }
            }
        }
        return Tensor.fromBlob( pixFlat, shape );
    }


    public static double[] multiply(double[][] a, double[] x) {
        int m = a.length;
        int n = a[0].length;
        if (x.length != n) throw new RuntimeException("Illegal matrix dimensions.");
        double[] y = new double[m];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                y[i] += a[i][j] * x[j];
        return y;
    }

    public String predict(Bitmap bitmap){
        //predictTest();
        Constants.startPreElab = System.currentTimeMillis();
        Tensor tensor = preElaboration(bitmap);
        Constants.endPreElab = System.currentTimeMillis();


        IValue inputs = IValue.from(tensor);
        Constants.startElab = System.currentTimeMillis();
        IValue iv = model.forward(inputs);
        Constants.endElab = System.currentTimeMillis();

        IValue[]  outs = iv.toTuple();

        Tensor out1 = outs[0].toTensor();
        float[] scores = out1.getDataAsFloatArray();

        Tensor out2 = outs[1].toTensor();
        float[] scores2 = out2.getDataAsFloatArray();

        int classIndex = Utils.argMax(scores);
        return Constants.Classes[classIndex];
    }

    public String predictTest() {

        Bitmap bitmap;
        Resources res = context.getResources(); //if you are in an activity
        AssetManager am = res.getAssets();
        String fileList[] = new String[0];
        try {
            fileList = am.list("test");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (fileList != null)
        {
            for ( int i = 0;i<fileList.length;i++)
            {
                try {
                    InputStream is = am.open("test/"+fileList[i]);
                    bitmap = BitmapFactory.decodeStream(is);
                    is.close();

                    Constants.startPreElab = System.currentTimeMillis();
                    Tensor hopeTensor = preElaboration(bitmap);
                    Constants.endPreElab = System.currentTimeMillis();

                    //Lunch inference
                    IValue inputs = IValue.from(hopeTensor);
                    Constants.startElab = System.currentTimeMillis();
                    IValue iv = model.forward(inputs);
                    Constants.endElab = System.currentTimeMillis();

                    //Process outputs: mode and coordinates
                    IValue[]  outs = iv.toTuple();
                    Tensor out1 = outs[0].toTensor();
                    float[] scores = out1.getDataAsFloatArray();
                    Tensor out2 = outs[1].toTensor();
                    float[] scores2 = out2.getDataAsFloatArray();
                    int classIndex = Utils.argMax(scores);

                    //rect = new Rect(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);

                    /*The last fully connected layer has two outputs of dimension 5 and
                    4, representing the number of class predictions and the predicted
                    coordinates respectively*/

                    System.out.println( "Prediction for: " + fileList[i] + " --> "+ Constants.Classes[classIndex] + " SCORES_: " + scores);
                    bitmap.recycle();
                    bitmap = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("",fileList[i]);
            }
        }
        return "Test finished!";
    }
}

