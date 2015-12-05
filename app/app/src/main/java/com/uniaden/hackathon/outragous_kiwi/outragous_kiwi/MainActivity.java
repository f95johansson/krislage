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
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private final Messenger mMessenger = new Messenger(new IncomingHandler());
    private boolean isBound;
    private Messenger mService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isBound = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case Codes.REG_CLIENT_SUCCESS:
                    System.out.println("REGISTER SUCCESS!");
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
        System.out.println("binding...");
        if(!isBound){
            getApplicationContext().bindService(new Intent(getApplicationContext(), ServerComService.class), mConnection, Context.BIND_AUTO_CREATE);
            isBound = true;
            System.out.println("binding more....");
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
