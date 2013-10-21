//debug this and find out why the image is not saved locally. 

package com.example.connexus2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.gson.Gson;

public class CameraActivity extends Activity {

	public static final int MEDIA_TYPE_IMAGE = 1;
	private Camera mCamera;
	private CameraPreview mPreview;
    public final static String TAG = "MyCamera";
	public Image image = null;
	public static int uploadflag = 0;
	public static String streamId = null;
	public static String streamName = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		streamId = intent.getStringExtra(UploadActivity.EXTRA_MESSAGE);
		Log.e("test", "streamId=" +streamId);
		if (checkCameraHardware (this)){
			setContentView(R.layout.activity_camera);

	        // Create an instance of Camera
	        mCamera = getCameraInstance();

	        // Create our Preview view and set it as the content of our activity.
	        mPreview = new CameraPreview(this, mCamera);
	        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
	        preview.addView(mPreview);
	        
			// Add a listener to the Capture button
			Button captureButton = (Button) findViewById(R.id.button_capture);
			captureButton.setOnClickListener(new View.OnClickListener() {
			        @Override
			        public void onClick(View v) {
			            // get an image from the camera
			        	uploadflag = 0;
			            Log.e("test", "flag set to zero");
			            mCamera.takePicture(null, null, mPicture);
			        }
			    }
			);
		} else{
			setContentView(R.layout.activity_upload);
		}
	}

	/** Check if this device has a camera */
	private boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}

    // picture object that is called in mCamera.takePicture
	private PictureCallback mPicture = new PictureCallback() {

	    @Override
	    public void onPictureTaken(byte[] data, Camera camera) {

	        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
	        if (pictureFile == null){
	            Log.d(TAG, "Error creating media file, check storage permissions");
	            return;
	        }

	        try {
	            FileOutputStream fos = new FileOutputStream(pictureFile);
	            Log.e("test", "begin to write in data");
	            fos.write(data);
	            Log.e("test", "finish writting");
	            fos.close();
	            Log.e("test", "close writing");
	            image = new Image(data);
	            Log.e("test", "close writing");
	        	uploadflag = 1;
	            Log.e("test", "uploadflag is set to 1");
	        } catch (FileNotFoundException e) {
	            Log.d(TAG, "File not found: " + e.getMessage());
	        } catch (IOException e) {
	            Log.d(TAG, "Error accessing file: " + e.getMessage());
	        }
	    }
	};
	
	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is moujnted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCamera");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
        Log.e("test", "check directory");
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCamera", "failed to create directory");
	            return null;
	        }
	    }
        Log.e("test", "check directory success");
	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    }  else {
	        return null;
	    }
        Log.d("test", "mediaFile is returned");
	    return mediaFile;
	}
	
	// I assume this will be taken automatically
    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();              // release the camera immediately on pause event
    }
	
    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
    
    //the following functions are for upload, Actally, this can be considered to create a new class to handle those tasks
    
	//trigger upload by button
	public void upLoadHaddler (View view){
		Log.e("message", "check if uploadflag ready, uploadflag = " + uploadflag); 
		if (uploadflag == 1){
    		new DownloadTask().execute();
		}
	}
	
	// use another thread for upload task
	public class DownloadTask extends AsyncTask<String, Integer, Void> {
		// if use array here, will timeout, AsyncTask does not allow long response time
		@Override 
		protected void onProgressUpdate (Integer... values){
		}
		
		@Override
		protected void onPostExecute(Void result) {
		}
		
		@Override
		protected Void doInBackground(String... params){
    		Log.e("message", "upload started"); 
			upLoadImage (image);
    		Log.e("message", "upload finished"); 
			return null;
		}
	}
	
	public static void upLoadImage (Image image){
		Log.e("message", "to json"); 
		String apiUrl = "http://apt-connexus.appspot.com/UploadServletAPI?streamId=" + streamId + "&streamName=" + streamName;
		Gson gson = new Gson();
		String tstJson = gson.toJson(image);
		Log.e("message", "to json finished"); 
		MakeHttpPost(apiUrl, tstJson);
	}
    
	//Http Request
	public static void MakeHttpPost (String apiUrl, String tstJson){
		try {
			HttpClient c = new DefaultHttpClient();
			HttpPost p = new HttpPost(apiUrl);
			p.setEntity(new StringEntity(tstJson));
			Log.e("message", "to be post"); 
			HttpResponse r = c.execute(p);
			Log.e("message", "post finished"); 
		} catch(IOException e){
			System.out.println(e);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera, menu);
		return true;
	}
	public void ViewStreams (View view) {
		Intent intent = new Intent (this, ViewStreamsActivity.class);
		startActivity (intent);
	}

}
