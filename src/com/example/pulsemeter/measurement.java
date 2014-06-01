package com.example.pulsemeter;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class measurement extends ActionBarActivity implements OnClickListener{

	GraphView graphView;
	TextView text;
	int result; // pulses per minute
	GraphViewSeries dataToDraw;
	ArrayList<Integer> measurements;
	View start;
	OutputStream send;
	InputStream receive;
	int x;
	MyApplication app;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.measure);
		x = 0;
		result = 0;
		start = (Button) findViewById(R.id.measureStart);
		start.setOnClickListener(this);
		text = (TextView)findViewById(R.id.diagnosis);
		text.setText("Click START to start measurement.");
		app = (MyApplication)this.getApplication();

		measurements = new ArrayList<Integer>();

		graphView = new LineGraphView(
			    this // context
			    , "Pulses per second" // heading
			);
		graphView.setVerticalLabels(new String[]{"pulse", "rest"});
		graphView.setHorizontalLabels(new String[]{"0","10", "20","30","40","50","60"});

			LinearLayout layout = (LinearLayout) findViewById(R.id.measureLayout);

			layout.addView(graphView);

	}

	public void drawGraph(int val)
	{

		// append data 
		dataToDraw = new GraphViewSeries(new GraphViewData[] {
			});
		graphView.addSeries(dataToDraw); // data
		dataToDraw.appendData(new GraphViewData(x, val){}, true, 60);
		x++;
	}

	public void calculateResult()
	{
		String ex = "Your pulse is: ";
		result = 0;
		int prev = 0;

		for(int val : measurements){
			if(prev > val){
				result++;
				System.out.println(val);
			}
		}

		ex += result + " pulses/min";
		text = (TextView)findViewById(R.id.diagnosis);
		if(result >= 60 && result <= 100){
			ex += "\nThis is normal pulse rate.";
			text.setText(ex);
		}
		else if(result < 60){
			ex += "\nThis is low pulse rate.";
			text.setText(ex);
		}
		else{
			ex += "\nThis is high pulse rate.";
		}


	}

	public void estabilishConnection()
	{
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		ArrayList<String> mArrayAdapter = new ArrayList<String>();
		BluetoothDevice arduino;
		text = (TextView)findViewById(R.id.diagnosis);


		if(pairedDevices.size() > 0){
			for(BluetoothDevice device : pairedDevices){
				System.out.println(device.getName());
		        mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
		        if(device.getName().equals("BOLUTEK")) // connect with device with this name, else: use getAddress() to obtain MAC address
		        {
		        	text.setText("znalazlem");
		        	arduino = device;
						try {
							getDataFromArduino(arduino);
						//	final Handler handler = new Handler();
						//	ConnectThread connection = new ConnectThread(arduino);

						} catch (IOException e) {
							text.setText("Error occured");
							e.printStackTrace();
						}
		        	break;
		        }
		        else
		    		text.setText("The needed device couldn't be found. The measurements won't be made.");
			}
		}
        else
    		text.setText("The bluetooth didn't detect any devices.");
	}

	public void getDataFromArduino(BluetoothDevice arduino) throws IOException
	{
		BluetoothSocket arduinoSocket;

    	UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //standard SerialPortService ID from android documentation
        arduinoSocket = arduino.createRfcommSocketToServiceRecord(uuid);
        arduinoSocket.connect();
        send = arduinoSocket.getOutputStream();
        receive = arduinoSocket.getInputStream();
        
        String ms = "start";     // send START signal
        send.write(ms.getBytes());
        
        final Handler handler = new Handler();
       Thread workerThread = new Thread(new Runnable()
        {
            public void run()
            {
            	byte[] buffer = new byte[1024];  // buffer store for the stream
            	
            	long start = System.currentTimeMillis();
            	long end = start + 20*1000;
    	        
    	        while (System.currentTimeMillis() < end) {
    	        	
    	            try { 
//    	                System.out.println("JESTEM");

    	                final int bytes = receive.read();  ////////??????????????????????????????
    	                System.out.println(bytes-48);
//    	                System.out.println("JESTEMMMMMMMM"); 
    	                handler.post(new Runnable()
    	                 
    	                {
     	                	public void run() 
    	                	{ 
    	                		measurements.add(bytes-48);
//    	                		drawGraph(bytes);
    	                	//	System.out.println(bytes);
    	                	}
    	                }
    	                );
    	            } catch (IOException e) {
 //   	                System.out.println("SPADAM");
    	                break;
    	            }  
    	        } 
               
            }
        });
        workerThread.start();
        
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.measureStart){
			estabilishConnection();

		calculateResult(); // after minute of measurement? 
		}

	}



}

