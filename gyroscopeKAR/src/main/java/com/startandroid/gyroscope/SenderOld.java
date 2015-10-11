package com.startandroid.gyroscope;

import android.os.AsyncTask;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.*;

/**
 * Created by Acer-PC on 03.10.2015.
 */
public class SenderOld extends AsyncTask<String, Void, String> {
    private String x;
    private String y;
    private String z;
    public SenderOld(String _x, String _y, String _z) {
        x = _x;
        y = _y;
        z = _z;
    }

    public void Update(String _x, String _y, String _z){
        x = _x;
        y = _y;
        z = _z;

    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        // Отправка по сети не должна быть в main thread/UI
        // http://stackoverflow.com/questions/23840331/how-to-resolve-android-os-networkonmainthreadexception

        //INSERT YOUR FUNCTION CALL HERE
        /*
        String serverIpAddress = "192.168.0.106";
        int serverPort = 12346;
        try{
            Socket s = new Socket(serverIpAddress, serverPort);
            String text = x + ", " + y + ", " + z;
            s.getOutputStream().write(text.getBytes());
            s.close();
        }catch(Exception e) {
            e.printStackTrace();
        }
        */

        int port = 12346;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            int i = 0;
            while(true) {
                Socket fromClient = serverSocket.accept();
                ++i;
                //System.out.println("Connection " + i);
                String text =  x + ", " + y + ", " + z;
                fromClient.getOutputStream().write(text.getBytes());
                fromClient.close();
                //System.out.println(data);
                fromClient.close();
                //break;// один раз отсылаем, если нашли клиента и выходим из цикла
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return "Executed!";

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

    }
}
