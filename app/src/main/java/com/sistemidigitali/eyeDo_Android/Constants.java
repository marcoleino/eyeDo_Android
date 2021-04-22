package com.sistemidigitali.eyeDo_Android;

public class Constants {

    //Pytorch Shapes
    public static final int inputWidth = 768;
    public static final int inputHeight = 576;
    public static final int inputChannels = 3;
    public static final int batchSize = 1;
    public static final float ratio = 4f / 3f;
    //Variables
    public static final int CODE_CAMERA_PERMISSION = 111;
    public static final int consecutiveElaborations = 4;
    //Models
    public static final String normOptF32 = "normalized_opt_float32.pt";
    public static final String LNV2_oi8 = "LYTNetV2_int8.pt";
    public static final String LNV1_oi8 = "LYTNetV1_int8.pt";
    //public static final String oi8 = "ObjDetResnet34.pt";
    //public static final String of32 = "optimized_float32.pt";
    //Classes
    public static String[] Classes = new String[]{
            "red", "green", "cGreen", "cBlank", "none"
    };
    public static String CHOSEN_MODEL = LNV1_oi8;

    //Testing
    public static long startPreElab;
    public static long endPreElab;
    public static long startElab;
    public static long endElab;
    public static int printedImageWidth;
    public static int printedImageHeight;

    public static boolean coordinatesWithNoneClass = false;
    public static int deltaAngleWarning = 20;
    public static int deltaAngleMax = 35; //30 degree limit
    public static float maxDistFromCenterPercentage = 0.25f;
    public static boolean MixedNets = true;
}

