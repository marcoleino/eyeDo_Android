package com.sistemidigitali.eyeDo_Android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.RadioButton;

public class SettingsPage extends AppCompatActivity {

    private RadioButton V2oi8;
    private RadioButton mixed;
    //private RadioButton ptnof32;
    private RadioButton oi8;
    //private RadioButton of32;
    private RadioButton noNone, yesNone;

    //TODO we left only the LytNetV1 - Int 8 and the LytNetV2 - Int 8, commenting the others --> lighter app

    private CompoundButton.OnCheckedChangeListener checkedChangeListener = (buttonView, isChecked) -> {
        if (isChecked) {
            if (buttonView.getId() == R.id.V2oi8) {
                Constants.CHOSEN_MODEL = Constants.LNV2_oi8;
                Constants.MixedNets=false;
            //} else if (buttonView.getId() == R.id.ptnof32) {
                //Constants.CHOSEN_MODEL = Constants.normOptF32;
            } else if (buttonView.getId() == R.id.oi8) {
                Constants.CHOSEN_MODEL = Constants.LNV1_oi8;
                Constants.MixedNets=false;
                //} else if (buttonView.getId() == R.id.of32) {
                //Constants.CHOSEN_MODEL = Constants.of32;
            }
            else if (buttonView.getId() == R.id.mixed){
                Constants.MixedNets=true;
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener checkedChangeListener2 = (buttonView, isChecked) -> {
        if (isChecked) {
            if (buttonView.getId() == R.id.noNone) {
                Constants.coordinatesWithNoneClass=false;
            } else if (buttonView.getId() == R.id.yesNone) {
                Constants.coordinatesWithNoneClass=true;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingspage);

        //Model choice
        //nof32 = findViewById(R.id.nof32);
        //ptnof32 = findViewById(R.id.ptnof32);
        oi8 = findViewById(R.id.oi8);
        //of32 = findViewById(R.id.of32);
        V2oi8 = findViewById(R.id.V2oi8);
        mixed = findViewById(R.id.mixed);

        if(Constants.MixedNets)
            mixed.setChecked(true);
        else{
            if (Constants.CHOSEN_MODEL.equals(Constants.LNV2_oi8))
                V2oi8.setChecked(true);
                //else if (Constants.CHOSEN_MODEL.equals(Constants.normOptF32))
                //ptnof32.setChecked(true);
            else if (Constants.CHOSEN_MODEL.equals(Constants.LNV1_oi8))
                oi8.setChecked(true);
            //else if (Constants.CHOSEN_MODEL.equals(Constants.of32))
            //of32.setChecked(true);
        }
        mixed.setOnCheckedChangeListener(checkedChangeListener);
        V2oi8.setOnCheckedChangeListener(checkedChangeListener);
        //ptnof32.setOnCheckedChangeListener(checkedChangeListener);
        oi8.setOnCheckedChangeListener(checkedChangeListener);
        //of32.setOnCheckedChangeListener(checkedChangeListener);


        noNone = findViewById(R.id.noNone);
        yesNone = findViewById(R.id.yesNone);

        if(Constants.coordinatesWithNoneClass){
            yesNone.setChecked(true);
        }
        else noNone.setChecked(true);

        noNone.setOnCheckedChangeListener(checkedChangeListener2);
        yesNone.setOnCheckedChangeListener(checkedChangeListener2);


    }

}
