package com.example.pulsemeter;

import android.app.Application;

public class MyApplication extends Application{

	MeasurementData db = new MeasurementData(this);
}
