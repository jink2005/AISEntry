package cn.aiseminar.aisentry.transceiver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.EnumSet;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

public class Transceiver {
	private Context mContext = null;
	private String mEntryName = null;
	private MulticastSocket mBroadcastSocket = null;
	private BroadcastServer mBroadcastServer = null;
	
	private static final int BROADCAST_PORT = 45000;
	private static final String BROADCAST_IP = "224.0.0.1";
	private static final int BROADCAST_INTERVAL = 4000;
	
	private Timer mBroadcastTimer = null;
	private TimerTask mBroadcastTask = new TimerTask() {
		@Override
		public void run() {
			sendBroadcastDatagram();
		}
	};
	
	public Transceiver(Context context)
	{
		mContext = context;
		mEntryName = Build.MODEL;
		
		try {
			mBroadcastSocket = new MulticastSocket(BROADCAST_PORT);
			InetAddress broadcastAddress = InetAddress.getByName(BROADCAST_IP);
			mBroadcastSocket.joinGroup(broadcastAddress);
			
			mBroadcastServer = new BroadcastServer();
			mBroadcastServer.start();


			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isWifiOnline()
	{
		if (null == mContext)
			return false;
		
		ConnectivityManager connMgr = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return (null != netInfo && netInfo.isConnected());
	}
	/**
	 * start UDP broadcast to notify other entry
	 */
	public void startBroadcasting()
	{
		if (null == mBroadcastTimer)
		{
			mBroadcastTimer = new Timer();
		}
		mBroadcastTimer.schedule(mBroadcastTask, BROADCAST_INTERVAL);
	}
	
	private void sendBroadcastDatagram()
	{
		try {
			mBroadcastSocket.setTimeToLive(4);
			
			InetAddress address = InetAddress.getByName(BROADCAST_IP);
			Log.d(mEntryName, String.valueOf(address.isMulticastAddress()));
			
			String strData = mEntryName + "@" + String.valueOf(BROADCAST_PORT);
			byte[] data = strData.getBytes();			
			DatagramPacket dataPacket = null;
			dataPacket = new DatagramPacket(data, data.length, address, BROADCAST_PORT);
			mBroadcastSocket.send(dataPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void connectToEntry(String entryAddress, int entryPort)
	{
		ConnectThread connThread = new ConnectThread(entryAddress, entryPort);
		connThread.start();
	}
	
	private void startTransferData(Socket connectedSocket)
	{
		DataTransferThread transceiverThread = new DataTransferThread(connectedSocket);
		transceiverThread.start();
		transceiverThread.sendGreetingMessage();
	}
	
	class BroadcastServer extends Thread
	{
		private DatagramPacket mDataPacket = null;
		private boolean mbCanceled = false;
		
		public BroadcastServer()
		{
			byte[] receiveData = new byte[1024];
			mDataPacket = new DatagramPacket(receiveData, receiveData.length);
		}
		@Override
		public void run() {
			while(! mbCanceled)
			{
				try {
					mBroadcastSocket.receive(mDataPacket);
					String data = new String(mDataPacket.getData(), 0, mDataPacket.getLength());
					Log.d(mEntryName, data);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void cancel()
		{
			mbCanceled = true;
		}
	}
	
	class ConnectThread extends Thread
	{
		private Socket mConnectedSocket = null;
		private String mDestAddress = null;
		private int mDestPort = 0;
		
		public ConnectThread(String dstAddress, int dstPort)
		{
			mDestAddress = dstAddress;
			mDestPort = dstPort;
		}
		
		@Override
		public void run() {
			try {
				mConnectedSocket = new Socket(mDestAddress, mDestPort);
				startTransferData(mConnectedSocket);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public class DataTransferThread extends Thread
	{
		public static final int CONNECTION_STATE_WAITINGFORGREETING = 0;
		public static final int CONNECTION_STATE_READINGGREETING = 1;
		public static final int CONNECTION_STATE_READYFORUSE = 2;
		
		public static final int DATATYPE_PLAINTEXT = 0;
		public static final int DATATYPE_PING = 1;
		public static final int DATATYPE_PONG = 2;
		public static final int DATATYPE_GREETING = 3;
		public static final int DATATYPE_UNDEFINED = 4;
		
		private Socket mConnectedSocket = null;
		private int mConnectionState = CONNECTION_STATE_WAITINGFORGREETING;
		private boolean mbGreetingSent = false;
		private InputStream mInputStream = null;
		private OutputStream mOutputStream = null;
		private boolean mbCanceled = false;
		
		public DataTransferThread(Socket connectedSocket)
		{
			mConnectedSocket = connectedSocket;
			try {
				mInputStream = mConnectedSocket.getInputStream();
				mOutputStream = mConnectedSocket.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			byte[] buffer = new byte[1024000];
			while(! mbCanceled)
			{
				try {
					int readByte = mInputStream.read(buffer);
					if (readByte <= 0)
						continue;
					
					String data = new String(buffer, 0, readByte);
					processReadyRead(data);
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
		
		public void processReadyRead(String data)
		{
			int curDataType = peekDataTypeFromProtocolHeader(data);
			if (DATATYPE_UNDEFINED == curDataType)
				return;
			
			int sizePosStart = data.indexOf(" ");
			if (sizePosStart < 0)
				return;
			sizePosStart += 1;
			
			int sizePosEnd = data.indexOf(" ", sizePosStart);
			if (sizePosEnd < sizePosStart)
				return;
			
			int dataLength = 0;
			try
			{
				dataLength = Integer.parseInt(data.substring(sizePosStart, sizePosEnd));
			}
			catch (NumberFormatException e)
			{
				return;
			}
			
			if (dataLength <= 0)
				return;
			
			String pureData = data.substring(sizePosEnd + 1);
			
			if (CONNECTION_STATE_WAITINGFORGREETING == mConnectionState)
			{
				if (DATATYPE_GREETING != curDataType)
					return;
				mConnectionState = CONNECTION_STATE_READINGGREETING;
			}
			
			if (CONNECTION_STATE_READINGGREETING == mConnectionState)
			{
				String username = pureData + '@' + mConnectedSocket.getInetAddress().getHostAddress();
				Log.d(mEntryName, username);
				
				if (! mbGreetingSent)
					sendGreetingMessage();
				mConnectionState = CONNECTION_STATE_READYFORUSE;
				
				return;
			}
			
			if (CONNECTION_STATE_READYFORUSE == mConnectionState)
			{
				switch(curDataType)
				{
				case DATATYPE_PING:
					write("POING 1 p".getBytes());
					break;
				case DATATYPE_PLAINTEXT:
					Log.d(mEntryName, pureData);
					break;
				}				
			}
		}
		
		public int peekDataTypeFromProtocolHeader(String data)
		{
			if (data.startsWith("PING "))
			{
				return DATATYPE_PING;
			}
			if (data.startsWith("PONG "))
			{
				return DATATYPE_PONG;
			}
			if (data.startsWith("MESSAGE "))
			{
				return DATATYPE_PLAINTEXT;
			}
			if (data.startsWith("GREETING "))
			{
				return DATATYPE_GREETING;
			}
						
			return DATATYPE_UNDEFINED;
		}
		
		public void sendGreetingMessage()
		{
			String data = "GREETING " + mEntryName.length() + ' ' + mEntryName;
			write(data.getBytes());
			mbGreetingSent = true;
		}
		
		public void write(final byte[] buffer)
		{
			try {
				mOutputStream.write(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void close()
		{
			try {
				mConnectedSocket.close();
				mbCanceled = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
