package com.startandroid.gyroscope;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;
import android.view.View.OnClickListener;


public class MainActivity extends Activity implements SensorEventListener {

    /** Объект типа сенсор менеджер */
    SensorManager mSensorManager;
    /** Создали объект типа сенсор для получения данных угла наклона телефона */
    Sensor mAccelerometerSensor;
    Sensor mMagneticFieldSensor;
    Sensor mGyroscopeSensor;
    /** Наши текствью в которые будем все выводить */
    TextView mForceValueText;
    TextView mXValueText;
    TextView mYValueText;
    TextView mZValueText;
    TextView simpleText;
    Button btnStart;
    Button btnStop;
    boolean isServerStarted;
    ServerMain serverMain;
    TextView gyroXValueText;
    TextView gyroYValueText;
    TextView gyroZValueText;


    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isServerStarted = false;

       btnStart = (Button) findViewById(R.id.button);
        OnClickListener oclBtnStart = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartServerAndSendData();

            }
        };
        btnStart.setOnClickListener(oclBtnStart);
       btnStop = (Button) findViewById(R.id.button2);
        OnClickListener oclBtnStop = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StopServer();

            }
        };
        btnStop.setOnClickListener(oclBtnStop);

        // присвоили менеджеру работу с серсором
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // создали список сенсоров для записи и сортировки
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        // делаем проверку если больше нуля значит все хорошо и начинаем обрабатывать работу датчика
        if (sensors.size() > 0) {
            // форич для зацикливания работы, что бы не единожды выполнялось, а постоянно
            for (Sensor sensor : sensors) {
                // берем данные с акселерометра
                switch (sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        // если пусто значит возвращаем значения сенсора
                        if (mAccelerometerSensor == null)
                            mAccelerometerSensor = sensor;
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        // если пусто значит возвращаем значения сенсора
                        if (mGyroscopeSensor == null)
                            mGyroscopeSensor = sensor;
                        break;
                    default:
                        break;
                }
            }
        }
        // привязываем наши объекты к нашей разметке
        mXValueText = (TextView) findViewById(R.id.value_x);
        mYValueText = (TextView) findViewById(R.id.value_y);
        mZValueText = (TextView) findViewById(R.id.value_z);
        gyroXValueText = (TextView) findViewById(R.id.valueGyroX);
        gyroYValueText = (TextView) findViewById(R.id.valueGyroY);
        gyroZValueText = (TextView) findViewById(R.id.valueGyroZ);
        simpleText = (TextView) findViewById(R.id.textView);
    }


    public void StartServerAndSendData()
    {
        if( !isServerStarted ) {
            simpleText.setText("Started!");
            isServerStarted = true;
            serverMain = new ServerMain();
            Thread listenThread = new Thread(serverMain);
            listenThread.setName("ServerMain thread");
            listenThread.start();

            // Отправка по сети не должна быть в main thread/UI
            // http://stackoverflow.com/questions/23840331/how-to-resolve-android-os-networkonmainthreadexception
            //Sender sender = new Sender(mXValueText.getText().toString(), mYValueText.getText().toString(), mZValueText.getText().toString());
            //sender.execute("");
        } else {
            simpleText.setText("Already started!");
        }
    }

    public void StopServer() {
        if( isServerStarted ) {
            simpleText.setText("Stopped!");
            isServerStarted = false;
            serverMain.Close();
        } else {
            simpleText.setText("Already stopped!");
        }
    }

    @Override
    protected void onPause() {

        //говорим что данные будем получать из этого окласса
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();
        //регистрируем сенсоры в объекты сенсора
        mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagneticFieldSensor, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mGyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        //создали массив в которые будем записывать наши данные полученые с датчиков
        float[] values = event.values;
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER: {
                //собственно выводим все полученые параметры в текствьюшки наши
                mXValueText.setText(String.format("%1.3f", event.values[SensorManager.DATA_X]));
                mYValueText.setText(String.format("%1.3f", event.values[SensorManager.DATA_Y]));
                mZValueText.setText(String.format("%1.3f", event.values[SensorManager.DATA_Z]));
                //Logger.GetLogger().WriteLine("put element in queue " + event.values[SensorManager.DATA_X] + ", " + event.values[SensorManager.DATA_Y] + ", " + event.values[SensorManager.DATA_Z]);

                if( !GyroQueue.GetGyroQueue().Offer("ACCELEROMETER", event.values[SensorManager.DATA_X], event.values[SensorManager.DATA_Y], event.values[SensorManager.DATA_Z]) ) {
                    //Logger.GetLogger().WriteLine("error in Offer");
                }
            }
            break;
            case Sensor.TYPE_GYROSCOPE: {
                //собственно выводим все полученые параметры в текствьюшки наши
                gyroXValueText.setText(String.format("%1.3f", event.values[SensorManager.DATA_X]));
                gyroYValueText.setText(String.format("%1.3f", event.values[SensorManager.DATA_Y]));
                gyroZValueText.setText(String.format("%1.3f", event.values[SensorManager.DATA_Z]));
                //Logger.GetLogger().WriteLine("put element in queue " + event.values[SensorManager.DATA_X] + ", " + event.values[SensorManager.DATA_Y] + ", " + event.values[SensorManager.DATA_Z]);

                if( !GyroQueue.GetGyroQueue().Offer("GYROSCOPE", event.values[SensorManager.DATA_X], event.values[SensorManager.DATA_Y], event.values[SensorManager.DATA_Z]) ) {
                    //Logger.GetLogger().WriteLine("error in Offer");
                }
            }
            break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
