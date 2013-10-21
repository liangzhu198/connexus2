package com.example.connexus2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.os.AsyncTask;
import android.util.Log;

public class DownloadFeedTask extends AsyncTask<String, Integer, Void> {
	
	@Override 
	protected void onProgressUpdate (Integer... values){
	}
	
	@Override
	protected Void doInBackground(String... params){
		String url = params[0];
		
		//getting JSON string from URL
		JSONArray json = getJSONFromUrl(url);
		
		//parsing JSON data
		parseJson (json);
		return null;
	}
	
	public JSONArray getJSONFromUrl(String url){
		InputStream is = null;
		JSONArray jobA = null;
		String json = null;
		
		//Making HTTP request
		try {
			//defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient ();
			HttpPost httpPost = new HttpPost(url);
			
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
			
			BufferedReader reader = new BufferedReader (new InputStreamReader(is, "iso-8859-1"), 8);
			StringBuilder sb =new StringBuilder ();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "n");
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
		
		try {
			jobA = new JSONArray(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}
		
		//return JSON String
		return jobA;
	}
	
	public void parseJson (JSONArray json) {

	}
}
