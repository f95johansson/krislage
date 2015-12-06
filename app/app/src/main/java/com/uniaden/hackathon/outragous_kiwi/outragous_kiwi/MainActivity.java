package com.uniaden.hackathon.outragous_kiwi.outragous_kiwi;


import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.ExifInterface;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;


import android.util.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_TAKE_PHOTO = 1;

    private final Messenger mMessenger = new Messenger(new IncomingHandler());
    private boolean isBound;
    private Messenger mService = null;
    private ImageView iv;
    private Uri outPutfileUri;
    private String picture;
    private ImageView mImageView;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isBound = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mImageView = (ImageView)findViewById(R.id.pictureContent);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            Codes.CAMERA_REQUEST);

                }

                dispatchTakePictureIntent();

                /*
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(Environment.getExternalStorageDirectory(),
                        "MyPhoto.jpg");
                outPutfileUri = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
                startActivityForResult(intent, 1);


                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }*/


                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
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
        //Uri contentUri = Uri.fromFile(f);
        //mediaScanIntent.setData(contentUri);
        //this.sendBroadcast(mediaScanIntent);

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
        event.setText("This is a funny discription");
        event.setPhoto(imageDataString);
        Bundle b = new Bundle();
        b.putParcelable(Codes.EVENT_DATA, event);
        sendMsg(Codes.SEND_DATA_TO_SERVER, b);

        /*
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(f.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String lat = ExifInterface.TAG_GPS_LATITUDE;
        String lat_data = exif.getAttribute(lat);

        System.out.println(lat_data);
        */
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

        /*
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            //iv.setImageBitmap(imageBitmap);
            mImageView.setImageBitmap(imageBitmap);

            EventInformation event = new EventInformation();
            //event.setPhoto(imageBitmap.);
            event.setText("This is a funny discription");
            Bundle b = new Bundle();
            b.putParcelable(Codes.EVENT_DATA, event);
            sendMsg(Codes.SEND_DATA_TO_SERVER, b);
        }

*/
       // System.out.println(iv.toString());
        /*if(resultCode != RESULT_CANCELED){
            if (requestCode == RESULT_OK) {
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                iv.setImageBitmap(bp);
                System.out.println("Testsssssss");
            }
            System.out.println("HEHE");
        }
        System.out.println("Yepppp");*/

        /*
        System.out.println("PICTURE! --->");
        try {
            URL url = new URL(outPutfileUri.getPath());
            InputStream stream = url.openStream();
            BufferedInputStream buffer = new BufferedInputStream(stream);
            byte[] bytes = new byte[1024];
            StringBuilder picBuild = new StringBuilder();
            while(buffer.read(bytes) != -1){
                picBuild.append(bytes);
            }

            picture = picBuild.toString();

            System.out.println(picture);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        */


    }

    @Override
    public void onStart(){
        System.out.println("onStart!");
        doBindService();
        super.onStart();
    }

    @Override
    public void onStop(){
        //doUnbindService();
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
            Bundle b = new Bundle();

            switch (msg.what){
                case Codes.REG_CLIENT_SUCCESS:
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
