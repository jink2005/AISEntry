package cn.aiseminar.aisentry.transceiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class PeerManager {
	private Context mContext = null;
	private Transceiver mOwner = null;
	private String mEntryName = null;
	private MulticastSocket broadcastSocket = null;
	
	private static final int BROADCAST_PORT = 45000;
	
	public PeerManager(Context context, Transceiver owner)
	{
		mOwner = owner;
		mContext = context;
		
		try {
			broadcastSocket = new MulticastSocket();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startBroadcasting()
	{
		DatagramPacket dataPacket = null;
		try {
			broadcastSocket.setTimeToLive(4);
			byte[] data = "192.168.0.105".getBytes();
			InetAddress address = InetAddress.getByName("224.0.0.1");
			System.out.println(address.isMulticastAddress());
			dataPacket = new DatagramPacket(data, data.length, address, BROADCAST_PORT);
			broadcastSocket.send(dataPacket);
			broadcastSocket.close();
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
}
