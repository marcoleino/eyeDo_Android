package com.sistemidigitali.eyeDo_Android;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Utils {




    public static Bitmap convertBitmapFromRGBtoBGR(Bitmap bitmap){
        int[] rgbPixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(rgbPixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int t;
        Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        for (int h = 0; h < bitmap.getHeight(); h++) {
            for (int w = 0; w < bitmap.getWidth(); w++) {
                t = bitmap.getPixel(w, h);
                int redValue = Color.red(t);
                int blueValue = Color.blue(t);
                int greenValue = Color.green(t);

                bitmap2.setPixel(w, h, Color.rgb(blueValue, redValue, greenValue));
            }
        }
        return bitmap2;
    }


    public static float getMaxValue(float[] numbers){
        float maxValue = numbers[0];
        for(int i=1;i < numbers.length;i++){
            if(numbers[i] > maxValue){
                maxValue = numbers[i];
            }
        }
        return maxValue;
    }
    public static float getMinValue(float[] numbers){
        float minValue = numbers[0];
        for(int i=1;i<numbers.length;i++){
            if(numbers[i] < minValue){
                minValue = numbers[i];
            }
        }
        return minValue;
    }
    public static int argMax(float[] inputs){
        int maxIndex = -1;
        float maxvalue = 0.0f;
        for (int i = 0; i < inputs.length; i++){
            if(inputs[i] > maxvalue) {
                maxIndex = i;
                maxvalue = inputs[i];
            }
        }
        return maxIndex;
    }
    public static String assetFilePath(Context context, String assetName) {
        File file = new File(context.getFilesDir(), assetName);
        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        } catch (IOException e) {
            Log.e("test", "Error process asset " + assetName + " to file path");
        }
        return null;
    }


    //Permissions
    public static boolean need_requestCAMERAandWRITEPermissions(Activity activity){
            int permission2 = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
            if (permission2 != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.CODE_CAMERA_PERMISSION);
                return true;
            }
        return false;
    }
    public static boolean need_requestWRITE_EXTERNAL_STORAGE(Activity activity){
            int permission2 = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int permission1 = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permission1 != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.CODE_WRITE_EXTERNAL_STORAGE);
                return true;
            }
        return false;
    }


    /**
     * Funzione che produce in uscita un Bitmap che viene ridimensionato a qualsiasi nuova altezza e larghezza, mantenendo le proporzioni
     * Se le nuove altezza e larghezza risultino essere non proporzionali rispetto al Bitmap in entrata, allora si mantiene comunque la proporzione
     * riempendo di nero gli spazi in eccesso.
     * Attenzione: testata solo per far entrare in QUADRATI qualsiasi immagine in ingresso. Mantiene le proporzioni e 'fa entrare' in quadrati qualsiasi immagine
     * TIME < 1ms
     * @param in    Bitmap in ingresso
     * @param newH  Nuova altezza
     * @param newW  Nuova larghezza
     * @return  Nuovo Bitmap ridimensionato
     */
    public static Bitmap resize (Bitmap in, int newH, int newW) {

        Bitmap out = Bitmap.createBitmap(newH, newW, Bitmap.Config.ARGB_4444);
        int inW = in.getWidth();
        int inH = in.getHeight();
        int H = -1;
        int W = -1;

        Bitmap resized = null;

        /*
        1) RIDIMENSIONIAMO L'IMMAGINE ALLA DIMENSIONE MASSIMA VOLUTA MANTENENDO L'ASPECT RATIO
        */
        if (inH < inW) {
            float ratio = (float) inW / newW;
            H = (int) (inH / ratio);
            resized = Bitmap.createScaledBitmap(in, newW, H, true);

            //se filter=false , l'immagine non viene filtrata--> maggiori performance, peggiore qualità
        }
        if (inH > inW) {
            float ratio = (float) inH / newH;
            W = (int) (inW / ratio);
            resized = Bitmap.createScaledBitmap(in, W, newH, true);

            //se filter=false , l'immagine non viene filtrata--> maggiori performance, peggiore qualità
        }
        if (inH == inW) {
            resized = Bitmap.createScaledBitmap(in, newW, newH, true);

        }

        /*
        2) Creiamo il contenitore finale della nostra immagine e riempiamo manualmente i pixel
         */
        out = Bitmap.createBitmap(newW, newH, resized.getConfig());
        Canvas canvas = new Canvas(out);
        canvas.drawBitmap(resized, new Matrix(), null);

        return out;
    }


    public static String pathCombine(String... paths) {

        File finalPath = new File(paths[0]);

        for (int i = 1; i < paths.length; i++) {
            finalPath = new File(finalPath, paths[i]);
        }

        return finalPath.getPath();
    }



    public static String getDateNow(Boolean millisec){
        String form = "yyyyMMdd_HHmmss_SSS";
        if(!millisec)
        {
            form = "yyyyMMdd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(form);
        return sdf.format(new Date());
    }





    /**Write bitmaps for the last 2 pictures, normal use
     *
     * @param bmp bmp from SCAT usually
     * @param path path from SCAT usually
     * @param fileName fileName from SCAT usually
     * @param extension .jpeg
     */

    public static boolean writeBitmapOnFile(Bitmap bmp, String path, String fileName, String extension){
        if(bmp == null || path == null || fileName ==null || extension == null) return false;
        File folder = new File(path);
        if (!folder.exists()) {
            boolean create = folder.mkdirs();
            if(!create) {
                Log.e("error", "non sono riuscito a creare la cartella");
                return false;
            }
        }

        fileName = Utils.pathCombine(path,fileName+extension);

        try (FileOutputStream out = new FileOutputStream(fileName)) {
            if(extension.equalsIgnoreCase(".jpg")) {
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            }else{
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                // PNG is a lossless format, the compression factor (100) is ignored
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static  Long calculateAverage(List<Long> nums) {
        Long sum = 0l;
        if(!nums.isEmpty()) {
            for (Long mark : nums) {
                sum += mark;
            }
            return sum / nums.size();
        }
        return sum;
    }


    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        float scale = (float)4/(float)3;
        int centerX = bitmap.getWidth()/2;
        int centerY = bitmap.getHeight()/2;
        matrix.postScale(scale,1/scale,centerX,centerY);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

}
