package com.uniaden.hackathon.outragous_kiwi.outragous_kiwi;

import android.os.StrictMode;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Isak on 05/12/15.
 */
public class NetworkCom {

    private static final String IP_ADDRESS  = "130.239.221.107";
    private static final int PORT           = 1337;
    private static Gson gson;

    private Socket socket;
    private DataOutputStream outToServer;
    private BufferedReader inFromServer;

    public NetworkCom() {
        gson = new Gson();
    }

    private void connect(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            outToServer = new DataOutputStream(socket.getOutputStream());
            inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnect(){
        if(socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
        }
    }

    public void sendMessage(EventInformation info){
        connect();
        try {
            outToServer.writeBytes(gson.toJson(info));
        } catch (IOException e) {
            e.printStackTrace();
        }

        disconnect();
    }
}
