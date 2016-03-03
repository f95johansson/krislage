package com.uniaden.hackathon.outragous_kiwi.outragous_kiwi;


import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;


import android.util.Base64;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity{

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_TAKE_PHOTO = 1;

    private Messenger mMessenger;
    private boolean isBound;
    private Messenger mService = null;
    private ImageView iv;
    private Uri outPutfileUri;
    private String picture;
    private ImageView mImageView;
    private String mCurrentPhotoPath;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private ImageView pictureFrame;


    private String ipAddress;
    private String portAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mMessenger = new Messenger(new IncomingHandler());
        isBound = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        pictureFrame = (ImageView)mViewPager.findViewById(R.id.pictureFrame);
        //pictureFrame.setImageDrawable(getDrawable(R.drawable.frontpage_app));

        ipAddress = Codes.DEFAULT_IP;
        portAddress = Codes.DEFAULT_PORT_STRING;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            Codes.CAMERA_REQUEST);

                }

                dispatchTakePictureIntent();
            }
        });
    }



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);

        String imageDataString = "";
        try {
            // Reading a Image file from file system
            FileInputStream imageInFile = new FileInputStream(f);
            byte imageData[] = new byte[(int) f.length()];
            imageInFile.read(imageData);

            //System.out.println(imageInFile);
            // Converting Image byte array into Base64 String
            imageDataString = encodeImage(imageData);
            // Converting a Base64 String into Image byte array
            //byte[] imageByteArray = decodeImage(imageDataString);
            //System.out.println(imageByteArray);
        } catch (FileNotFoundException e) {
            System.out.println("Image not found" + e);
        } catch (IOException ioe) {
            System.out.println("Exception while reading the Image " + ioe);
        }



        EventInformation event = new EventInformation();
        event.setText("#Groupie");
        event.setPhoto(imageDataString);
        Bundle b = new Bundle();
        b.putParcelable(Codes.EVENT_DATA, event);

        LinearLayout inputContent = (LinearLayout)findViewById(R.id.inputfield);
        inputContent.setBackgroundColor(getColor(R.color.colorBackground));

        sendMsg(Codes.SEND_DATA_TO_SERVER, b);

    }

    /**
     * Encodes the byte array into base64 string
     *
     * @param imageByteArray - byte array
     * @return String a {@link java.lang.String}
     */
    public static String encodeImage(byte[] imageByteArray) {
        return Base64.encodeToString(imageByteArray, Base64.DEFAULT);
        //return Base64.encodeBase64URLSafeString(imageByteArray);
    }

    /**
     * Decodes the base64 string into byte array
     *
     * @param imageDataString - a {@link java.lang.String}
     * @return byte array
     */
    public static byte[] decodeImage(String imageDataString) {
        return Base64.decode(imageDataString, Base64.DEFAULT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        galleryAddPic();
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
        //int id = item.getItemId();
        /*
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        */

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        System.out.println("pressed menu stuff");
        switch (item.getItemId()) {
            case R.id.action_settings:
                openNetworkInputPopup();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private int section;

        public PlaceholderFragment() {
        }

        public PlaceholderFragment(int section) {
            this.section = section;
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = null;
            fragment = new PlaceholderFragment(sectionNumber);
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);




            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = null;
            if(section == 1){
                rootView = inflater.inflate(R.layout.fragment_page_1, container, false);
            } else if (section == 2){
                rootView = inflater.inflate(R.layout.fragment_page_2, container, false);
                ListView list = (ListView)rootView.findViewById(R.id.listView);
                list.setAdapter(new LiveFeedAdapter(getContext()));

            } else if (section == 3){
                rootView = inflater.inflate(R.layout.fragment_page_3, container, false);
            }

            return rootView;
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }


    private void openNetworkInputPopup(){

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this.getApplicationContext());
        View promptsView = li.inflate(R.layout.network_input_popup, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText ip_address = (EditText) promptsView
                .findViewById(R.id.ipText);
        ip_address.setText(ipAddress);

        final EditText port = (EditText) promptsView
                .findViewById(R.id.portText);
        port.setText(this.portAddress);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                Bundle b;
                                // get user input and set it to result
                                // edit text
                                String ip = ip_address.getText().toString();
                                if(!ip.equals("")){
                                    b = new Bundle();
                                    b.putString(Codes.IP_ADDRESS, ip);

                                    sendMsg(Codes.SET_IP_ADDRESS, b);
                                    ipAddress = ip;
                                }

                                String stringPort = port.getText().toString();
                                if(!stringPort.equals("")){
                                    int port = Integer.parseInt(stringPort);
                                    b = new Bundle();
                                    b.putInt(Codes.PORT, port);

                                    sendMsg(Codes.SET_PORT, b);
                                    portAddress = stringPort;
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
