package com.sistemidigitali.eyeDo_Android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import static com.sistemidigitali.eyeDo_Android.Utils.assetFilePath;


public class MainActivity extends AppCompatActivity implements CameraEvent {


    public int counterForMixed = 0;
    Classifier classifier;
    Classifier classifier2;//for mixed
    Bitmap rotatedBitmap;
    ArrayList<Long> elabTimes;
    ArrayList<Long> preElabTimes;
    private MyCamera camera;
    private TextureView textureView;
    private boolean waitFor = false;
    private TextView textView;
    private SoundThread sound;
    private String[] lastStates;
    private float[][] lastCoords;
    public int globalTrafficLightState = 4;
    public int globalTrafficLightStateConsecutiveCounter = 0;
    private ImageView iv;    //Drawing on screen the TL state
    private TransparentView transparentView;

    @Override
    protected void onResume() {
        super.onResume();
        begin();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.need_requestCAMERAandWRITEPermissions(this);

        iv = findViewById(R.id.tlView);
        textView = findViewById(R.id.textView);
        textView.setVisibility(View.GONE);
        textureView = findViewById(R.id.texture);
        // Create second surface with another holder (holderTransparent)
        transparentView = findViewById(R.id.TransparentView);

        findViewById(R.id.settings).setVisibility(View.VISIBLE);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setVisibility(View.GONE);
                iv.setVisibility(View.VISIBLE);
            }
        });

        //after onCreate(), onResume() is always called
        //begin();
        elabTimes = new ArrayList<>();
        preElabTimes = new ArrayList<>();
        lastStates = new String[Constants.consecutiveElaborations];
        lastCoords = new float[Constants.consecutiveElaborations][4];

    }

    private void begin() {
        //camera is not accessed, but it's necessary to instantiate it in order to activate the view
        camera = new Camera2(this, this, textureView);
        if(!Constants.MixedNets) {
            classifier = new Classifier(assetFilePath(this, Constants.CHOSEN_MODEL), this);
            classifier2 = null; //free memory if turning off the mixed computation
        }
        else{
            Log.d("mixed","Mixed started");
            classifier = new Classifier(assetFilePath(this, Constants.LNV1_oi8), this);
            classifier2 = new Classifier(assetFilePath(this, Constants.LNV2_oi8), this);
        }
        sound = new SoundThread();
        sound.start();

        //Make tipsToast if it's the first time in the app :)
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 10000);
                try {
                    File f = new File(getApplicationContext().getFilesDir() + "/notFirstTime");
                    if (!f.exists()) {
                        f.createNewFile();
                        Context context = getApplicationContext();

                        CharSequence text = "If you don't see the camera, please restart the app!";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        text = "You can click on the TrafficLight to see the stats (and vice-versa)! :)";
                        toast = Toast.makeText(context, text, duration);
                        toast.show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(r, 5500);
    }

    public void buttonHandler(View view) {
        switch (view.getId()) {
            case R.id.settings:
                onPauseCapture();
                Intent i = new Intent(this, SettingsPage.class);
                startActivityForResult(i, 100);
                break;
        }
    }

    @Override
    protected void onStop() {
        //paying attention to bitmap recycling
        sound.setKeepGoing(false);
        super.onStop();
    }

    @Override
    protected void onPause() {
        //paying attention to bitmap recycling
        sound.setKeepGoing(false);
        super.onPause();
    }

    void onPauseCapture() {
        /*
        possibly useful
        */
    }

    @Override
    public void internalElaboration(Bitmap data, String imgFormat) {
        rotatedBitmap = Utils.rotate(data, 270);
        Result res;
        if(Constants.MixedNets){
            if(counterForMixed==0){
                res = classifier.predict(rotatedBitmap);
                Log.d("mixed","Mixed 1");

                counterForMixed = counterForMixed +1;
            }
            else {
                Log.d("mixed","Mixed 2");

                counterForMixed = counterForMixed -1 ;
                res = classifier2.predict(rotatedBitmap);
            }
        }
        else{
            res = classifier.predict(rotatedBitmap);
        }
        //Bitmap drawing = Utils.crearPunto(coordinates[0]*(float)bitmap.getWidth(),coordinates[1]*(float)bitmap.getHeight(),coordinates[2]*(float)bitmap.getWidth(),coordinates[3]*(float)bitmap.getHeight(), Color.RED, bitmap);
        rotatedBitmap.recycle();
        data.recycle();
        data = null;
        rotatedBitmap = null;

        showResults(res.getOutClass());

        //Function that keeps track of how many consecutive elaboration the tf state has been different from None
        if(globalTrafficLightState!=4)
            globalTrafficLightStateConsecutiveCounter++;
        else globalTrafficLightStateConsecutiveCounter = 0;

        drawResults(res.getCoordinates());
        endElab();
    }

    /**
     * Function that draws the predicted coordinates when the class is not None and calculating the moving average from the last 4 found coordinates
     * @param coordinates
     */
    private void drawResults(float[] coordinates) {
        //Add state to lastStates and shift
        for (int i = 1; i < lastCoords.length; i++) {
            lastCoords[i] = lastCoords[i - 1];
        }
        lastCoords[0] = coordinates;
        //Check if it's the right moment to draw the Line or to Cancel it (when a None state is found within the last 4 traffic light states)

        //If the class None has not been predicted for more than 4 consecutive times...
        if(Constants.coordinatesWithNoneClass || (globalTrafficLightStateConsecutiveCounter>=Constants.consecutiveElaborations)){

            float[] avgCoords = new float[4];
            float avg;
            for(int i = 0; i < 4; i++){
                avg = 0;
                for (int j = 0; j < lastCoords.length; j++){
                    avg += lastCoords[j][i];
                }
                avgCoords[i] = avg/lastCoords.length;
            }

            transparentView.draw(true, avgCoords);
        }
        //Else it is the moment to clear the line view
        else{
            transparentView.clear();
        }
    }




    /**
     * Function that shows all the results and changes the state of the traffic light after 4 consecutive equal classes
     * @param predicted class
     */
    private void showResults(String predicted) {
        //Add state to lastStates and shift
        for (int i = 1; i < lastStates.length; i++) {
            lastStates[i] = lastStates[i - 1];
        }
        lastStates[0] = predicted;
        //Check if it's the right moment to change State (after 4 equal results)
        boolean allEquals = true;
        for (int i = 1; i < lastStates.length; i++) {
            if (lastStates[0] == null || lastStates[i] == null || !lastStates[i].equals(lastStates[0])) {
                allEquals = false;
                break;
            }
        }
        int i = 0;
        if (allEquals) {
            for (i = 0; i < Constants.Classes.length; i++) {
                if (Constants.Classes[i].equals(lastStates[0])) {
                    //Change Traffic Light Image
                    if (i == 0)
                        runOnUiThread(() -> iv.setImageResource(R.drawable.tl_red));
                    else if (i == 1)
                        runOnUiThread(() -> iv.setImageResource(R.drawable.tl_green));
                    else if (i == 4)
                        runOnUiThread(() -> iv.setImageResource(R.drawable.tl_none));
                    else
                        runOnUiThread(() -> iv.setImageResource(R.drawable.tl_yellow));

                    //Set new Sound state
                    sound.setState(i);
                    globalTrafficLightState = i ;
                    break;
                }
            }
        }

        //Play sounds
        preElabTimes.add((Constants.endPreElab - Constants.startPreElab));
        elabTimes.add((Constants.endElab - Constants.startElab));


        runOnUiThread(() -> textView.setText(Constants.CHOSEN_MODEL + "\n" + "Predicted class: " + predicted + "\n" + "Pre-elaboration avg time: \n" + Utils.calculateAverage(preElabTimes) + "ms\n" +
                "Elaboration avg time: \n" + Utils.calculateAverage(elabTimes) + "ms\n "));
    }




    @Override
    public void startElab() {
        waitFor = true;
    }

    @Override
    public void endElab() {
        waitFor = false;
    }

    @Override
    public boolean isInElaboration() {
        return waitFor;
    }


}
