package com.example.pulsemeter;


import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class history extends ActionBarActivity{
	
	MyApplication app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		app = (MyApplication)this.getApplication();

		

	}


}
