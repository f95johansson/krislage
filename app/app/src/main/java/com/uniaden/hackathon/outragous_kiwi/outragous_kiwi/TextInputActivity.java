package com.uniaden.hackathon.outragous_kiwi.outragous_kiwi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class TextInputActivity extends AppCompatActivity {

    private Messenger mMessenger;
    private boolean isBound;
    private Messenger mService = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        mMessenger = new Messenger(new IncomingHandler());
        isBound = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textinput_data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                sendMsg(Codes.SEND_DATA_TO_SERVER, savedInstanceState);
            }
        });
    }


    @Override
    public void onStart(){
        System.out.println("onStart!");
        doBindService();
        super.onStart();
    }

    @Override
    public void onStop(){
        doUnbindService();
        super.onStop();
    }




    class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg){
            Bundle b = new Bundle();

            switch (msg.what){
                case Codes.REG_CLIENT_SUCCESS:
                    break;
                case Codes.UNABLE_TO_SEND_DATA:
                    /*
                    Snackbar.make(viewGroup, "Unable to send data to server", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                    break;
            }
        }
    }

    public void sendMsg(int code, Bundle bundle){

        if(mService != null){
            Message msg = Message.obtain(null, code);

            switch (code){
                case Codes.REG_CLIENT:
                    msg.replyTo = mMessenger;
                    break;
                case Codes.UNREG_CLIENT:
                    msg.replyTo = mMessenger;
                    break;
            }

            if(bundle != null){
                msg.setData(bundle);
            }

            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }


    }

    public void doBindService(){
        if(!isBound){
            Thread t = new Thread(){
                public void run() {
                    getApplicationContext().bindService(new Intent(getApplicationContext(), ServerComService.class), mConnection, Context.BIND_AUTO_CREATE);
                }
            };
            t.start();
            isBound = true;
        }
    }

    public void doUnbindService(){
        if(isBound){
            getApplicationContext().unbindService(mConnection);
            isBound = false;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            sendMsg(Codes.REG_CLIENT, null);
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
            mService = null;
            isBound = false;
        }
    };

}
