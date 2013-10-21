package com.example.connexus2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;

public class MainActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.connexus2.MESSAGE";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;

	}

	public void LoginMessage (View view) {
	    // Do something in response to button
		Intent intent = new Intent (this, LoginActivity.class);
		EditText editText1 = (EditText) findViewById (R.id.gmail_id);
		String message1 = editText1.getText().toString();
		EditText editText2 = (EditText) findViewById (R.id.gmail_pwd);
		String message2 = editText2.getText().toString();
		LoginMsg loginMsg = new LoginMsg (message1, message2); 
		Gson gson = new Gson();
		String json =gson.toJson(loginMsg);
		intent.putExtra(EXTRA_MESSAGE, json);
		startActivity (intent);
	}
	
	public void ViewStreams (View view) {
		Intent intent = new Intent (this, ViewStreamsActivity.class);
		startActivity (intent);
	}

}
