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
	
	public PeerManager(Context context, Transceiver owner)
	{
		mOwner = owner;
		mContext = context;
	}
}
