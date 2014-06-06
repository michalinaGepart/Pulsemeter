package com.example.pulsemeter;
import static com.example.pulsemeter.Constants.TABLE_NAME;
import static com.example.pulsemeter.Constants.timeOfMeasurement;
import static com.example.pulsemeter.Constants.resultOfMeasurement;
import static com.example.pulsemeter.Constants._ID;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MeasurementData extends SQLiteOpenHelper{

	private static final String DATABASE_NAME = "measurementHistory.db";
	private static int DB_VERSION = 1;
	
	public MeasurementData(Context context) {
		
		super(context, DATABASE_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE "+ TABLE_NAME + " ( "  + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+ timeOfMeasurement + " VARCHAR(30) , " + resultOfMeasurement + " INT NOT NULL);");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
		
	}
	

	
}
