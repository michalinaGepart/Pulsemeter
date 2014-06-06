package com.example.pulsemeter;



import java.io.IOException;
import static com.example.pulsemeter.Constants.TABLE_NAME;
import static com.example.pulsemeter.Constants.timeOfMeasurement;
import static com.example.pulsemeter.Constants.resultOfMeasurement;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
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
	int finalresult; // pulses per minute
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
		finalresult = 0;
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

	public void drawGraph()
	{

		int howMany = finalresult;
		int step = 60/finalresult;
		int x = 0;
		dataToDraw = new GraphViewSeries(new GraphViewData[] { new GraphViewData(6,8)
			});
		graphView.addSeries(dataToDraw); // data
//		dataToDraw.appendData(new GraphViewData(x, finalresult){}, true, 60);
//		for(int i=0; i < 2; i++)
//		{
//			dataToDraw.appendData(new GraphViewData(x, 1){}, true, 60);
//			x += step;
//			dataToDraw.appendData(new GraphViewData(x, 0){}, true, 60);
//			x += step;
//		}
	}

	public void calculateResult(int result)
	{
		String ex = "Your pulse is: ";
		
		System.out.println("final result = " + finalresult + "result : " + result);

		ex += finalresult + " pulses/min";
		text = (TextView)findViewById(R.id.diagnosis);
		if(finalresult >= 60 && finalresult <= 100){
			ex += "\nThis is normal pulse rate.";
			text.setText(ex);
		}
		else if(finalresult < 60){
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
		        if(device.getName().equals("HC-05")) // connect with device with this name, else: use getAddress() to obtain MAC address
		        {
		        	text.setText("Device found");
		        	arduino = device;
						try {
							System.out.println("ZNALAZLEM BOLUTKA");
							getDataFromArduino(arduino);

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
        System.out.println("wyslalem znaki");
        
        final Handler handler = new Handler();
       Thread workerThread = new Thread(new Runnable()
        {
    	   
            public void run()
            {
         	   System.out.println("zaczynam nowy wˆtek");
                final byte delimiter = 10; //This is the ASCII code for a newline character
                
                boolean stopWorker = false;
                int readBufferPosition = 0;
                byte [] readBuffer = new byte[1024];
    	         
    	        while (!Thread.currentThread().isInterrupted() && !stopWorker) {
    	        	
                        try 
                        {
                            int bytesAvailable = receive.available();                        
                            if(bytesAvailable > 0)
                            {
                                byte[] packetBytes = new byte[bytesAvailable];
                                receive.read(packetBytes);
                                System.out.println(packetBytes);
                                for(int i=0;i<bytesAvailable;i++)
                                {
                                    byte b = packetBytes[i];
                                    if(b == delimiter)
                                    {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;
                                        
                                        handler.post(new Runnable()
                                        {
                                            public void run()
                                            {
                                                System.out.println("wynik to: "  + data);
                                                finalresult = Integer.parseInt(data.trim());
                                                System.out.println("final result: " + finalresult);
                                        		calculateResult(finalresult); 
                                        		drawGraph();
//                                        		try{
//                                        			String time = getCurrentTimeFormat("d-m-y h-m-s");
//                                        			addEvent(finalresult, time);
//                                        			
//                                        		} finally{
//                                        			app.db.close();
//                                        		}
                                            }
                                        });
                                    }
                                    else
                                    {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        } 

    	            catch (IOException e) {
    	            	System.out.println("wywalam wyjatek");
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
	
		}

	}
	
	private String getCurrentTimeFormat(String timeFormat){
		  String time = "";
		  SimpleDateFormat df = new SimpleDateFormat(timeFormat);
		  Calendar c = Calendar.getInstance();
		  time = df.format(c.getTime());
		 
		  return time;
		}
	
	private void addEvent(int result, String time){
		SQLiteDatabase database = app.db.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(timeOfMeasurement, time);
		values.put(resultOfMeasurement, result);
		database.insertOrThrow(TABLE_NAME, null, values);
		
	}




}

