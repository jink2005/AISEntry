package cn.aiseminar.aisentry.transceiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
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
	private PeerManager mPeerManager = null;
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
		mPeerManager = new PeerManager(mContext, this);
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		public void cancel()
		{
			mbCanceled = true;
		}
	}
}
