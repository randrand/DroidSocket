/* 
 * Android practice project 2: socket programming and client/server communication
 * 
 * When the Poke button is pressed, the Android phone creates a TCP socket to the server
 * and sends the word "Poke".  It then waits for a response message from the server, 
 * which the Android client displays on its screen. The response message tells the client 
 * how many pokes has been received so far by the server.
 * 
 * Author: JJ
 */

package com.example.droidsocket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {
	private Button pokeServer;
	private boolean connected = false;
	private TextView status; // Display server response (#of pokes)
	private String txtOutput = "";
	private Timer timer = new Timer();
	
	// HARD CODED server IP address and port; for practice only
	private String serverIpAddress = "192.168.2.104";//"127.0.0.1";
	private int serverPort = 4444;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Grab references to button and textView, and create lister for button
		pokeServer = (Button) findViewById(R.id.pokeButton);
		pokeServer.setOnClickListener(connectListener);
		status = (TextView) findViewById(R.id.serverStatusTextView);
		// Start UI update timer
		startTimer();
	}

	// Update UI timer
	protected void startTimer() {
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				mHandler.obtainMessage(1).sendToTarget();
			}
		}, 0, 300);
	}

	// Update UI textbox
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			status.setText(txtOutput);
		}
	};

	// On button click, start a new thread to set up socket connection,
	// and send/receive messages
	private OnClickListener connectListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!connected) {
				if (!serverIpAddress.equals("")) {
					Thread cThread = new Thread(new ClientThread());
					cThread.start();
				}
			}
		}
	};

	// Poke server, then grab input from server and display in textView
	public class ClientThread implements Runnable {
		public void run() {
			try {
				// Create connection via socket
				InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
				// Log.d("ClientActivity", "C: Connecting...");
				Socket socket = new Socket(serverAddr, serverPort);
				connected = true;
				// Log.d("ClientActivity", "C: Connected!");
				try {
					//Log.d("ClientActivity", "C: Sending command.");
					// New reader to receive msgs from server
					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					// Writer to send msg to server
					PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket
							.getOutputStream())), true);
					// Send message to server
					out.println("Poke");
					//Log.d("ClientActivity", "C: Sent.");
					// Get input from server and store into textView variable
					txtOutput = br.readLine();
					//Log.d("ClientActivity", "C: Received.");

				} catch (Exception e) {
					//Log.e("ClientActivity", "S: Error", e);
					txtOutput = "Send/Receive failed";
				}
				//close connection
				socket.close();
				connected = false;
				//Log.d("ClientActivity", "C: Closed.");
			} catch (Exception e) {
				//Log.e("ClientActivity", "C: Error", e);
				connected = false;
				txtOutput = "Socket connection failed";
			}
		}
	}
}
