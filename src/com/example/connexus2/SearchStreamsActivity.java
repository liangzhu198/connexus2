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
import java.util.Locale;

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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SearchStreamsActivity extends Activity {

	public final static String EXTRA_MESSAGE = "com.example.connexus2.MESSAGE";
	private ArrayList<Stream> streamArray = null;
	private String message = null;
    private GridView gridview = null;		
    private Context mContext = this;
    private int indexList[];
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_streams);
		Intent intent = getIntent();
		if (intent.getFlags() == 1){
			message = intent.getStringExtra(ViewStreamsActivity.EXTRA_MESSAGE);
			Log.e("message", message);
		} else if (intent.getFlags() == 2){
			message = intent.getStringExtra(SearchStreamsActivity.EXTRA_MESSAGE);
			Log.e("message", message);
		} 
		String url = "http://apt-connexus.appspot.com/AllStreamsServletAPI";
		new DownloadTask().execute(url);		
	}
	
	public void UpdateList(Bitmap[] bitmapArray){
		gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new StreamAdapter(mContext, streamArray, bitmapArray, indexList));
	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	    		Intent intent = new Intent (mContext, ViewAStreamActivity.class);
	    		String streamId = String.valueOf(streamArray.get(indexList[position]).id);
	    		intent.putExtra(EXTRA_MESSAGE, streamId);
	    		intent.setFlags(2);
	    		startActivity (intent);
	        }
	    });
	}

	public class DownloadTask extends AsyncTask<String, Integer, Void> {
        public Bitmap[] bitmapArray;
		// if use array here, will timeout, AsyncTask does not allow long response time
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
			//get search results as an indexList
			int count1 = 0; 
			for (Stream s: streamArray){
				if (s.name.toUpperCase(Locale.US).indexOf(message.toUpperCase(Locale.US))>-1 || message.toUpperCase(Locale.US).indexOf(s.name.toUpperCase(Locale.US))>-1){
					count1 = count1 + 1;
				}
			}
			indexList = new int[count1];
			count1 = 0;
			int count2 = 0;
			for (Stream s: streamArray){
				if (s.name.toUpperCase(Locale.US).indexOf(message.toUpperCase(Locale.US))>-1 || message.toUpperCase(Locale.US).indexOf(s.name.toUpperCase(Locale.US))>-1){
					indexList[count2] = count1;
					count2 = count2 +1;
				}
				count1 = count1 + 1;
			}
			String st[] = new String[streamArray.size()];
			for (int i=0; i< streamArray.size();i++){
				st[i] = streamArray.get(i).coverImageUrl;
			}
    		int count = streamArray.size();
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
        streamArray = gson.fromJson(json, new TypeToken<List<Stream>>(){}.getType());
		// for debug
        for (Stream s : streamArray){
			Log.e("results", s.toString());
        }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_streams, menu);
		return true;
	}
	
	public void ViewStreams (View view) {
		Intent intent = new Intent (this, ViewStreamsActivity.class);
		startActivity (intent);
	}
	
	public void SearchStreams (View view) {
		Intent intent = new Intent (this, SearchStreamsActivity.class);
		EditText editText = (EditText) findViewById (R.id.find_streams);
		String message = editText.getText().toString();
		intent.putExtra(EXTRA_MESSAGE, message);
		intent.setFlags(2);
		startActivity (intent);
	}


}
