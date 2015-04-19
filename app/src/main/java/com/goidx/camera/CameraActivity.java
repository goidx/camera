package com.goidx.camera;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CameraActivity extends ActionBarActivity {

    private final static String TAG = "cameraActivity";
    public final int REQUEST_IMAGE_CAPTURE = 100;
    public Button mTakePhoto;
    public String mCurrentPhotoPath;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mTakePhoto = (Button) findViewById(R.id.takephoto);

        mTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchtakePictureIntent();

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera, menu);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            DisplayImageTaken();
            galleryAddPic();


        }
    }


    private void DisplayImageTaken(){

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(mCurrentPhotoPath)), "image/*");
        startActivity(intent);

    }

    /**
     * Launch the Intent to bring up Camera.
     */
    public void dispatchtakePictureIntent(){

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takePictureIntent.resolveActivity(getPackageManager()) != null){

            Uri photoFileUri = null;

            try{
                photoFileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                Log.v(TAG,photoFileUri.toString());

            }catch (IOException ex) {


            }


            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFileUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private  Uri getOutputMediaFileUri(int type) throws IOException {
        return Uri.fromFile(createImageFile(type));

    }

    /**
     * Create File on System to Store Image
     * @param type
     * @return
     * @throws IOException
     */
    private File createImageFile(int type) throws IOException {


        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"GCamera");

        if(!mediaStorageDir.exists()) {
            if(!mediaStorageDir.mkdirs()){
                Log.v(TAG, "Fail to create storage directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;


        if(type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath()+File.separator+ "IMG_"+timeStamp+".jpg");

        }else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath()+File.separator+"VID_"+timeStamp+".mp4");

        }else {
            return null;

        }

        mCurrentPhotoPath = mediaFile.getPath();


        return mediaFile;

    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }



}
