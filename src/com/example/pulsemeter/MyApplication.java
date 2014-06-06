package com.example.pulsemeter;

import android.app.Application;

public class MyApplication extends Application{

	MeasurementData db; 
	

	@Override
	public void onCreate() {
		super.onCreate();

			db = new MeasurementData(this);
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	}
}
