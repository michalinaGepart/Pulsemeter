package com.example.pulsemeter;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends ActionBarActivity implements OnClickListener{

	private final static int REQUEST_ENABLE_BT = 1;
	int resultCode; 
	View measurePulse, measurementsHistory, measurementsInformation; 
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.activity_main);
		
		
		measurePulse = (Button) findViewById(R.id.measure);
		measurePulse.setOnClickListener(this);
		
		measurementsHistory = (Button) findViewById(R.id.history);
		measurementsHistory.setOnClickListener(this);
		
		measurementsInformation = (Button) findViewById(R.id.info);
		measurementsInformation.setOnClickListener(this);
	} 

	
	@Override 
	public void onClick(View v) {     // commented out just for purposes of testing  
		if(v.getId() == R.id.measure){ 
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) // THE DEVICE DOESNT SUPPORT BLUETOOTH
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.dialog_message).setTitle(R.string.dialog_title);
			AlertDialog dialog = builder.create();
			dialog.show();
			  
		}
		else 
		{
			
			if (!mBluetoothAdapter.isEnabled()) // BLUETOOTH IS DISABLED
			{
			    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			    onActivityResult(REQUEST_ENABLE_BT, resultCode, enableBtIntent);
			}
			if(mBluetoothAdapter.isEnabled() || resultCode==RESULT_OK)
			{
				Intent i = new Intent(this, measurement.class);
				startActivity(i);
			}
		}

		
		}
		else if(v.getId() == R.id.history){
			Intent i = new Intent(this, history.class);
			startActivity(i);
			
		}
		else if(v.getId() == R.id.info){
			Intent i = new Intent(this, information.class);
			startActivity(i);
			
		}
	}

}
