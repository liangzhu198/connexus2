package com.example.connexus2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ViewAStreamActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.connexus2.MESSAGE";
    private static String streamId = null;
    private ArrayList<ConnexusImage> imageArray = null;
    private Context mContext = this;
    private GridView gridview = null;		
    private int indexList[];
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_astream);
		Intent intent = getIntent();
		if (intent.getFlags() == 1){
			streamId = intent.getStringExtra(ViewStreamsActivity.EXTRA_MESSAGE);
			Log.e("message", String.valueOf(streamId));
		} else if (intent.getFlags() == 2){
			streamId = intent.getStringExtra(SearchStreamsActivity.EXTRA_MESSAGE);
			Log.e("message", String.valueOf(streamId));
		} 		
		String url = "http://apt-connexus.appspot.com/SingleStreamServletAPI?streamId=" + streamId;
		new DownloadTask().execute(url);
	}
	
	public void UpdateList(Bitmap[] bitmapArray){
		gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new ImageAdapter(mContext, imageArray, bitmapArray, indexList));
	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	 Toast.makeText(mContext, "" + imageArray.get(indexList[position]).comments == null ? "No Comments" : imageArray.get(indexList[position]).comments, Toast.LENGTH_SHORT).show();
	        }
	    });
	}
	
	public class DownloadTask extends AsyncTask<String, Integer, Void> {
		// if use array here, will timeout, AsyncTask does not allow long response time
        public Bitmap[] bitmapArray;
		@Override 
		protected void onProgressUpdate (Integer... values){
		}
		
		@Override
		protected void onPostExecute(Void result) {
			UpdateList(bitmapArray);
		}
		
		@Override
		protected Void doInBackground(String... params){
			String url = params[0];
			//getting JSON string from URL
			String json = getJSONFromUrl(url);
			//parsing JSON data
			parseJson (json);
			indexList = new int[imageArray.size()];

			for (int i=0;i<imageArray.size();i++){
				indexList[i]=i;
			}
			String st[] = new String[imageArray.size()];
			for (int i=0; i< imageArray.size();i++){
				st[i] = imageArray.get(i).bkUrl;
			}
    		int count = imageArray.size();
    		bitmapArray = new Bitmap[count];
    		for (int i = 0; i < count; i++){
		    bitmapArray[i] = getBitMap(mContext, st[i]);
    		}
    		Log.e("bitmap", "complete");
			return null;
		}
	}
	
    public synchronized Bitmap getBitMap(Context c, String url){
    	Bitmap bitmap = null;
    	URL myFileUrl = null;
    	
    	try {
    		myFileUrl = new URL(url);
    	} catch (MalformedURLException e) {
    		bitmap =BitmapFactory.decodeResource(this.getResources(), R.drawable.defaultimage); //set default image
    		return bitmap;
    	}
    	try {
    		// Open URL
    		HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
    		conn.setDoInput(true);
    		conn.connect();
            InputStream is = conn.getInputStream();    
            int length = (int) conn.getContentLength();  
            if (length != -1) {  
                byte[] imgData = new byte[length];            
                byte[] temp = new byte[512];  
                int readLen = 0;  
                int destPos = 0;  
                while ((readLen = is.read(temp)) > 0) {  
                    System.arraycopy(temp, 0, imgData, destPos, readLen);  
                    destPos += readLen;  
                }  

                bitmap = BitmapFactory.decodeByteArray(imgData, 0,  
                        imgData.length);
            }
    	} catch (IOException e) {
    		bitmap =BitmapFactory.decodeResource(this.getResources(), R.drawable.defaultimage);  //set default image
    		return bitmap;
    	}
    	return bitmap;
    }
	
	public String getJSONFromUrl(String url){
		InputStream is = null;
		String json = null;
		//Making HTTP request
		try {
			//defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient ();
			HttpGet httpGet = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
			BufferedReader reader = new BufferedReader (new InputStreamReader(is, "iso-8859-1"), 8);
			StringBuilder sb =new StringBuilder ();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			is.close();
			json = sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
		//return JSON String
		return json;
	}
	
	public void parseJson(String json) {

		// parsing JSON object
		Gson gson = new Gson();
        imageArray = gson.fromJson(json, new TypeToken<List<ConnexusImage>>(){}.getType());
		// for debug
        for (ConnexusImage s : imageArray){
			Log.e("results", s.toString());
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_astream, menu);
		return true;
	}
	public void ViewStreams (View view){
		Intent intent = new Intent (this, ViewStreamsActivity.class);
		startActivity (intent);
	}
	
	public void UploadImage (View view){
		Intent intent = new Intent (this, UploadActivity.class);
		intent.putExtra(EXTRA_MESSAGE, streamId);
		startActivity (intent);
	}

}
