package com.uniaden.hackathon.outragous_kiwi.outragous_kiwi;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.util.ArrayList;

/**
 * Created by Isak on 05/12/15.
 */
public class ServerComService extends Service {

    private static Messenger mMessenger;
    private static ArrayList<Messenger> mClients = new ArrayList<Messenger>();
    private SendUIMsg msgSender;


    public ServerComService() {
        mMessenger = new Messenger(new ParseUIMsg());
        msgSender = new SendUIMsg();
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
                    mClients.add(msg.replyTo);
                    System.out.println("REGISTERING CLIENT");
                    msgSender.sendMsg(Codes.REG_CLIENT_SUCCESS);
                    break;
                case Codes.UNREG_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case Codes.SEND_DATA_TO_SERVER:

                    break;
            }

        }
    }

    private class SendUIMsg {

        public void sendMsg(int code){
            Message msg = Message.obtain(null, code);
            sendToClients(msg);
        }

        public void sendMsg(int code, Bundle bundle){
            Message msg = Message.obtain(null, code);
            msg.setData(bundle);
            sendToClients(msg);
        }

        private void sendToClients(Message msg){
            synchronized (mClients){
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
}
