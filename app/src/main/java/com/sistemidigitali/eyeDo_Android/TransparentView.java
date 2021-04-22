package com.sistemidigitali.eyeDo_Android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TransparentView extends View {
    private Paint textPaint;
    private Paint paint;
    private Rect rect;
    private String plateType="";
    private Long now = System.currentTimeMillis();
    private Long time = 0L;
    private int firstNet = 0;
    private int secondNet = 0;
    private Handler mHandler;
    private float[] coordinates = {0,0,0,0};


    private Path arrowLeft;
    private Path arrowRight;
    private int arrowColor = Color.RED;
    private Path arrowMode = null;


    public void setImgDim(int imgDim) {
        this.imgDim = imgDim;
    }

    private int imgDim;

    private void init(){

        //TODO useful in the future for object detection
        //rect = new Rect(-100,-100,-100,-100); //outside the paintable region

        paint = new Paint();

        paint.setStrokeWidth(15);
        paint.setStyle(Paint.Style.STROKE);


        arrowRight = new Path();
        arrowRight.moveTo(100, 0);
        arrowRight.lineTo(0, -50);
        arrowRight.lineTo(0, +50);
        arrowRight.lineTo(100,0);
        arrowRight.moveTo(0,0);
        arrowRight.lineTo(-150,0);
        //arrowRight.offset(Constants.printedImageWidth/2, Constants.printedImageWidth/2);
        arrowRight.close();

        arrowLeft = new Path();
        arrowLeft.moveTo(-100, 0);
        arrowLeft.lineTo(0, -50);
        arrowLeft.lineTo(0, 50);
        arrowLeft.lineTo(-100, 0);
        arrowLeft.moveTo(0,0);
        arrowLeft.lineTo(150,0);
        //arrowLeft.offset(Constants.printedImageWidth/2, Constants.printedImageWidth/2);
        arrowLeft.close();

        invalidate();

        //delete rectangle if nothing is detected for more than 800ms
        /*
        Timer t = new Timer( );
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                //Se sono passati più di 800 ms dall'ultima volta
                if((System.currentTimeMillis() - now)>400){

                    clear();
                }
            }
        }, 0,300);*/
    }

    public void clear(){
        /*if(activity!=null)
        activity.runOnUiThread(() -> {

        });*/

        coordinates = new float[]{0,0,0,0};
        arrowMode=null;
        //setRect(new Rect(-10,-10,-10,-10));
        postInvalidate(); //invalidate();
    }

    /*
    public void setRect(Rect rect) {
        //rettangolo fatto un po' più largo della bounding box cosi' da non coprire la targa sul preview
        this.rect = new Rect(rect.left-10, rect.top -10, rect.right +10, rect.bottom +10);
    }*/

    public void draw(boolean updateTime, float[] coordinates){
        this.coordinates=coordinates;
        /*
        if(updateTime) {
            now = System.currentTimeMillis();
        }*/


        float upperX = coordinates[2] * Constants.printedImageWidth;
        float centerX = Constants.printedImageWidth/2;
        float deltaX = Constants.printedImageWidth * Constants.maxDistFromCenterPercentage;

        //1) PRIORITY: if the upper X point is out of the feasible distance
        //ROTATE LEFT
        if( upperX < (centerX-deltaX)){
            paint.setColor(Color.RED);
            arrowColor = Color.RED;
            arrowMode = arrowLeft;
        }
        //ROTATE RIGHT
        else if (upperX > (centerX+deltaX)){
            paint.setColor(Color.RED);
            arrowColor = Color.RED;
            arrowMode = arrowRight;
        }
        //2) if condition 1) is correct, then look at condition 2
        else{
            double lineAngle = Utils.GetAngleOfLineBetweenTwoPoints(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
            //if the angle is > 120° or < 60°
            if(lineAngle > (90 + Constants.deltaAngleMax) ){
                paint.setColor(Color.RED);
                arrowColor = Color.RED;
                arrowMode = arrowLeft;
            }
            else if ( lineAngle < (90 - Constants.deltaAngleMax)){
                paint.setColor(Color.RED);
                arrowColor = Color.RED;
                arrowMode = arrowRight;
            }
            else if(lineAngle > (90 + Constants.deltaAngleWarning) ){
                paint.setColor(Color.rgb(255,153,0)); //orange
                arrowColor = Color.rgb(255,153,0);
                arrowMode = arrowLeft;
            }
            else if(lineAngle < (90 - Constants.deltaAngleWarning)){
                paint.setColor(Color.rgb(255,153,0)); //orange
                arrowColor = Color.rgb(255,153,0);
                arrowMode = arrowRight;
            }
            else{
                arrowMode=null;
                paint.setColor(Color.GREEN);
            }
            Log.d("angle", "ANGLE: " + lineAngle);
        }

        //invalidate();
        postInvalidate();//will call on Draw()
    }

    public TransparentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TransparentView(Context context) {
        super(context);
        init();
    }




    boolean done = false;
    @Override
    public void onDraw(Canvas canvas) {
        paint.setStrokeWidth(15);
        canvas.drawLine(coordinates[0]*(float)Constants.printedImageWidth,coordinates[1]*(float)Constants.printedImageHeight,coordinates[2]*(float)Constants.printedImageWidth,coordinates[3]*(float)(float)Constants.printedImageHeight, paint);
        if(arrowMode!=null){
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(30);
            paint.setColor(arrowColor);
            //arrowMode.offset(Constants.printedImageWidth/2, Constants.printedImageWidth/2);

            if(!done) {
                arrowRight.offset(Constants.printedImageWidth, Constants.printedImageHeight-200);
                arrowLeft.offset(Constants.printedImageWidth, Constants.printedImageHeight-200);
                done = true;
            }
            Path rr = arrowMode;

            canvas.drawPath(rr, paint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.v("TransparentView", "TransparentView onDetachedFromWindow");
        /*
        textPaint.reset();
        textPaint = null;
        paint.reset();
        paint = null;

        rect = null;
        */
        super.onDetachedFromWindow();
    }


}
