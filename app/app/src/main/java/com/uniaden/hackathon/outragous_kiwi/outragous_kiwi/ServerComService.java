package com.uniaden.hackathon.outragous_kiwi.outragous_kiwi;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;

import java.util.ArrayList;

/**
 * Created by Isak on 05/12/15.
 */
public class ServerComService extends Service {

    private static Messenger mMessenger;
    private static ArrayList<Messenger> mClients = new ArrayList<Messenger>();
    private SendUIMsg msgSender;
    private NetworkCom network;


    public ServerComService() {
        mMessenger = new Messenger(new ParseUIMsg());
        msgSender = new SendUIMsg();
        network = new NetworkCom(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("BINDING ON SERVICE!");
        return mMessenger.getBinder();
    }

    private class ParseUIMsg extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Codes.REG_CLIENT:
                    synchronized (mClients){
                        mClients.add(msg.replyTo);
                    }
                    //msgSender.sendMsg(Codes.REG_CLIENT_SUCCESS);
                    break;
                case Codes.UNREG_CLIENT:
                    synchronized (mClients){
                        mClients.remove(msg.replyTo);
                    }
                    break;
                case Codes.SEND_DATA_TO_SERVER:
                    EventInformation info = msg.getData().getParcelable(Codes.EVENT_DATA);
                    network.sendMessage(info);
                    break;
                case Codes.SET_IP_ADDRESS:
                    String ip = msg.getData().getString(Codes.IP_ADDRESS);
                    System.out.println(ip);
                    network.setIpAddress(ip);
                    break;
                case Codes.SET_PORT:
                    int port = msg.getData().getInt(Codes.PORT);
                    System.out.println(port + "");
                    network.setPORT(port);
                    break;
            }

        }
    }

    public void sendMsg(int code){
        msgSender.sendMsg(code);
    }

    public void sendMsg(int code, Bundle bundle){
        msgSender.sendMsg(code, bundle);
    }

    private class SendUIMsg {

        public void sendMsg(int code){
            synchronized (mClients) {
                Message msg = Message.obtain(null, code);
                sendToClients(msg);
            }
        }

        public void sendMsg(int code, Bundle bundle){
            synchronized (mClients) {
                Message msg = Message.obtain(null, code);
                msg.setData(bundle);
                sendToClients(msg);
            }
        }

        private void sendToClients(Message msg){

            for (Messenger msgC: mClients) {
                try {
                    msgC.send(msg);
                } catch (RemoteException e) {
                    mClients.remove(msgC);
                }
            }

        }

    }
}
