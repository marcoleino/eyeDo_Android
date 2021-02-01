package com.sistemidigitali.eyeDo_Android;

import android.os.Environment;

import java.io.File;

public class Constants {

    //Classes
    public static String[] Classes = new String[]{
            "red", "green", "cGreen", "cBlank", "none"
    };

    //Pytorch Shapes
    public static int inputWidth = 768;
    public static int inputHeight = 576;
    public static int inputChannels = 3;
    public static int batchSize = 1;
    public static float ratio = 4f/3f;

    //Variables
    public static final int CODE_CAMERA_PERMISSION = 111;
    public static final int CODE_WRITE_EXTERNAL_STORAGE = 112;

    //Models
    public static final String normOptF32 = "normalized_nonopt_float32.pt";
    public static final String nof32 = "not_optimized_float32.pt";
    public static final String oi8 = "optimized_int8.pt";
    public static final String of32 = "optimized_float32.pt";
    public static String CHOSEN_MODEL = of32;

    //Testing
    public static long startPreElab;
    public static long endPreElab;
    public static long startElab;
    public static long endElab;



}

