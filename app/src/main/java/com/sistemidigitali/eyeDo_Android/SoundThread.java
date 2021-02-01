package com.sistemidigitali.eyeDo_Android;

import android.app.Activity;
import android.media.AudioManager;
import android.media.ToneGenerator;

public class SoundThread extends Thread {

    //0 RED; 1 GREEN; 2 cGREEN; 3cBLANK (Yellow); 4 NONE;
    private int stateL;
    private ToneGenerator noneTone = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
    private boolean keepGoing = true;

    //Inizializza a None
    public SoundThread(Activity activity){
        stateL = 4;
    }

    public void run() {
        while(keepGoing){
            if(stateL==4){
                noneTone.startTone(ToneGenerator.TONE_CDMA_NETWORK_BUSY_ONE_SHOT,80);
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(stateL==3 || stateL==2){
                noneTone.startTone(ToneGenerator.TONE_DTMF_3,80);
                try {
                    sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(stateL==1){
                try {
                    //double sound for green
                    noneTone.startTone(ToneGenerator.TONE_CDMA_NETWORK_BUSY,50);
                    sleep(150);
                    noneTone.startTone(ToneGenerator.TONE_CDMA_NETWORK_BUSY,50);
                    sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(stateL==0){
                try {
                    noneTone.startTone(ToneGenerator.TONE_CDMA_DIAL_TONE_LITE,220);
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void setKeepGoing(boolean keepGoing) {
        this.keepGoing = keepGoing;
    }

    public int getStateL() {
        return stateL;
    }

    public void setState(int state) {
        this.stateL = state;
    }
}