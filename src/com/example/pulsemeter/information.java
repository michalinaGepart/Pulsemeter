package com.example.pulsemeter;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class information extends ActionBarActivity implements OnClickListener{
	
	View info1, info2, info3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.information);

		info1 = (Button) findViewById(R.id.info1);
		info2 = (Button) findViewById(R.id.info2);
		info3 = (Button) findViewById(R.id.info3);
		info1.setOnClickListener(this);
		info2.setOnClickListener(this);
		info3.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.info1)
		{
			Intent i = new Intent(this, info1.class);
			startActivity(i);
		
		}
		else if(v.getId()==R.id.info2)
		{
			Intent i = new Intent(this, info2.class);
			startActivity(i);
		}
		else if(v.getId()==R.id.info3)
		{
			Intent i = new Intent(this, info3.class);
			startActivity(i);
		}
	}


}
