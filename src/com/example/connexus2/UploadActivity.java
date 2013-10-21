package com.example.connexus2;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

public class UploadActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.connexus2.MESSAGE";
	final static int PICK_CONTACT_REQUEST = 1; // the request code
	public static String imagePath;
	public Image image = null;
	public static String streamId = null;
	//streamName will not be used by API so will not be set here
	public static String streamName = null;
	//check if image is ready to upload
	public static int uploadflag = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);
		Intent intent = getIntent();
		streamId = intent.getStringExtra(ViewAStreamActivity.EXTRA_MESSAGE);
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.upload, menu);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("test", "result got");
		uploadflag = 0;
		//Check which request we're responding to 
		if (requestCode == PICK_CONTACT_REQUEST){
			//Make sure the request was successful
			if (resultCode == RESULT_OK){
				//The user picked a contact.
				// The Intent's data Uri identifies which image was selected.
				Uri uri = data.getData();
	            Log.i("uri", uri.toString());
	            if(null != uri && !"".equals(uri)){
	            	//uri cannot be null for upload

	            	try {
	            		//get chosen data as byte []
	            		String[] filePathColumn = {MediaStore.Images.Media.DATA};
	            		Cursor cursor = getContentResolver().query(uri,filePathColumn, null, null, null);
	            		cursor.moveToFirst(); //the cursor got points to where before the first item (position = -1), so move to first item, can also use "cursor.moveToNext()"
	            		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	            		imagePath = cursor.getString(columnIndex); //get the path of the chosen photo
	            		Log.e("imagepath", imagePath); //print out imagePath
	            		FileInputStream inStream = new FileInputStream(imagePath);
	            		byte [] buffer = new byte[inStream.available()]; //get the maximum length of byte by available
	            		inStream.read(buffer); //read the stream and save into byte[]
	            		cursor.close();
	            		inStream.close();
	            		// create image and upload it
	            		image = new Image(buffer);
	            		uploadflag = 1;
	            		Log.e("message", "image is ready for upload"); 
	            		} catch (Exception e) {
	    				Toast.makeText(getApplicationContext(), "Out of memery", Toast.LENGTH_LONG).show();
	    				e.printStackTrace();
	    			}
	            }
			}
		}  else {
			Toast.makeText(getApplicationContext(), "Haven't chosen a photo", Toast.LENGTH_LONG).show();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	//upload image function
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
	
	public void takePhoto(View view) {
	    // Do something in response to button
		Intent intent = new Intent (this, CameraActivity.class);
		intent.putExtra(EXTRA_MESSAGE, streamId);
		startActivity (intent);
	}
	//trigger choose photo from local
	public void upLoad (View view) {
		Intent intent = new Intent(); 
		intent.setType("image/*");  
		intent.setAction(Intent.ACTION_GET_CONTENT); 
		startActivityForResult(intent, PICK_CONTACT_REQUEST);
	}
	
	//trigger upload by button
	public void upLoadHaddler (View view){
		Log.e("message", "check if uploadflag ready, uploadflag = " + uploadflag); 
		if (uploadflag == 1){
    		new DownloadTask().execute();
		}
	}
}
