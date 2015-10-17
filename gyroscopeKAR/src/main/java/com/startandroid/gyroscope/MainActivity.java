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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
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
    TextView gyroXValueText;
    TextView gyroYValueText;
    TextView gyroZValueText;
    TextView accXValueText;
    TextView accYValueText;
    TextView accZValueText;
    TextView srvStatus;

    Button btnStart;
    Button btnStop;

    private Calendar calendar = null;
    private static Logger logger;
    private static boolean isSrvRunning;
    private static int portN; // can get it from GUI-form
    private static ConnectionListener connectionListener = null;
    private static QueuesHolder queuesHolder = null;


    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // preparation for server running
        isSrvRunning = false;
        logger = new Logger();
        portN = 12346;
        queuesHolder = new QueuesHolder(logger);
        connectionListener = new ConnectionListener(logger, portN, queuesHolder);
        calendar = Calendar.getInstance();

       btnStart = (Button) findViewById(R.id.buttonStart);
        OnClickListener oclBtnStart = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartServer();
            }
        };
        btnStart.setOnClickListener(oclBtnStart);

       btnStop = (Button) findViewById(R.id.buttonStop);
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
        gyroXValueText = (TextView) findViewById(R.id.value_gyro_x);
        gyroYValueText = (TextView) findViewById(R.id.value_gyro_y);
        gyroZValueText = (TextView) findViewById(R.id.value_gyro_z);
        accXValueText = (TextView) findViewById(R.id.value_acc_x);
        accYValueText = (TextView) findViewById(R.id.value_acc_y);
        accZValueText = (TextView) findViewById(R.id.value_acc_z);
        srvStatus = (TextView) findViewById(R.id.ServerStatus_value);
    }


    // Start listening for connections from client
    public void StartServer()
    {
        try{
            if( !isSrvRunning && connectionListener != null && queuesHolder != null ) {
                Thread newThread = new Thread(connectionListener);
                newThread.setName("ConnectionListenerThrd");
                newThread.start();
                isSrvRunning = true;
                srvStatus.setText("Started");
            }
        } catch ( Exception e ) {
            logger.WriteLine(e.getMessage(), getClass().getName(), "StartSrv" );
        }
    }

    // Stop all current connections between Server and Client
    // Stop listening for any connection
    public void StopServer() {
        try{
            if( isSrvRunning && connectionListener != null ) {
                connectionListener.StopListenning();;
                isSrvRunning = false;
                srvStatus.setText("Stopped");
            }
        } catch ( Exception e ) {
            logger.WriteLine(e.getMessage(), getClass().getName(), "StopSrv" );
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
                accXValueText.setText(String.format("%1.3f", event.values[SensorManager.DATA_X]));
                accYValueText.setText(String.format("%1.3f", event.values[SensorManager.DATA_Y]));
                accZValueText.setText(String.format("%1.3f", event.values[SensorManager.DATA_Z]));

                try {
                    if( queuesHolder != null && calendar != null ) {
                        Date now = calendar.getTime();
                        Timestamp timeStamp = new Timestamp( now.getTime());
                        TData data = new TData("ACCELEROMETER", timeStamp.toString(), event.values[SensorManager.DATA_X], event.values[SensorManager.DATA_Y], event.values[SensorManager.DATA_Z]);
                        queuesHolder.PushDataToQueues(data);
                    }
                } catch ( Exception e ) {
                    logger.WriteLine(e.getMessage(), getClass().getName(), "PushData");
                }
            }
            break;
            case Sensor.TYPE_GYROSCOPE: {
                //собственно выводим все полученые параметры в текствьюшки наши
                gyroXValueText.setText(String.format("%1.3f", event.values[SensorManager.DATA_X]));
                gyroYValueText.setText(String.format("%1.3f", event.values[SensorManager.DATA_Y]));
                gyroZValueText.setText(String.format("%1.3f", event.values[SensorManager.DATA_Z]));
                try {
                    if( queuesHolder != null && calendar != null ) {
                        Date now = calendar.getTime();
                        Timestamp timeStamp = new Timestamp(now.getTime());
                        TData data = new TData("GYROSCOPE", timeStamp.toString(), event.values[SensorManager.DATA_X], event.values[SensorManager.DATA_Y], event.values[SensorManager.DATA_Z]);
                        queuesHolder.PushDataToQueues(data);
                    }
                } catch ( Exception e ) {
                    logger.WriteLine(e.getMessage(), getClass().getName(), "PushData" );
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
