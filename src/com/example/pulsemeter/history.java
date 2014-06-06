package com.example.pulsemeter;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import static com.example.pulsemeter.Constants.TABLE_NAME;
import static com.example.pulsemeter.Constants.timeOfMeasurement;
import static com.example.pulsemeter.Constants.resultOfMeasurement;

public class history extends ActionBarActivity{
	
	MyApplication app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		app = (MyApplication)this.getApplication();
		
		try{
			Cursor cursor = takeEvents();
			showEvents(cursor);
		} finally{
			app.db.close();
		}

		

	}
	
	private void showEvents(Cursor cursor)
	{
		
	}
	
	private Cursor takeEvents()
	{
		SQLiteDatabase database = app.db.getReadableDatabase();
		String query = "SELECT " + resultOfMeasurement + ", " +  timeOfMeasurement +" FROM " + TABLE_NAME + " ORDER BY " + timeOfMeasurement + ";";
		Cursor cursor = database.rawQuery(query, null);
		return cursor;
	}


}
