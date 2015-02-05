package com.coretronic.bdt;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
/**
 * Created by darren on 2014/12/18.
 */
public class StepListenerForWidget implements SensorEventListener{
    private String TAG = StepListenerForWidget.class.getSimpleName();
    private SensorManager sensorManager;
    private Context context;
    private long lastTime;
    public static int steps;
    private int tempCount;

    //Constructor
    public StepListenerForWidget(Context context) {
        this.context = context;
    }

    //start ACCELEROMETER sensor
    public void start() {
        Log.i(TAG, "Sensor Listener started");
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //第一個參數就是監聽器,第二個是觸發事件的sensor,第三個是延遲的時間
        //Sensor.manager.SENSOR_DELAY_FASTEST ：0ms
        //Sensor.manager.SENSOR_DELAY_GAME ：   20ms
        //Sensor.manager.SENSOR_DELAY_UI ：     60ms
        //Sensor.manager.SENSOR_DELAY_NORMAL ： 200ms
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    //stop
    public void stop() {
        Log.i(TAG, "[StepListener] stopped");
        sensorManager.unregisterListener(this);
        steps = 0;
    }

    public void dailyInit() {
        Log.i(TAG, "[StepListener] init");
        steps = 0;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ignored
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            Log.i(TAG, "---1---");
            return;
        }
        Log.i(TAG, "---2---");
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float accelationSquareRoot =  + ((x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH)) - 1.0f;
        Log.i(TAG, "accelationSquareRoot:" + accelationSquareRoot);
        long actualTime = System.currentTimeMillis();
        if (actualTime - lastTime > 300) {
            Log.i(TAG, "Time:" + lastTime +"," +actualTime);

            if(accelationSquareRoot < -0.60f){

                steps++;
                tempCount++;
//                if(tempCount==5){
//                    steps = 0;
//                }
                lastTime = actualTime;
            }
        }

    }

    public  int getSteps(){
        Log.i(TAG, "steps: " +steps);
        return steps;
    }

}
