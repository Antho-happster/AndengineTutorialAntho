package fr.happster.formationAndEngine.helpers;

import android.app.Activity;
import android.content.Context;
 
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
 
public class AccelerometerHelper extends Activity implements SensorEventListener {
 
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
 
    public static float TILT;
 
    public AccelerometerHelper(Activity oActivity) {
 
        mSensorManager = (SensorManager) oActivity.getSystemService(Context.SENSOR_SERVICE);
 
        //verification de la présence d'un accéléromètre
        if (mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) 
        {
            mAccelerometer = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
 
    }
 
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
 
    }
 
    @Override
    //values[0]: Acceleration minus Gx on the x-axis
    public void onSensorChanged(SensorEvent event) {
        TILT = event.values[0];
    }
 
}