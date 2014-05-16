package cn.aiseminar.aisentry.transceiver;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class PeerManager {
	private Context mContext = null;
	private Transceiver mOwner = null;
	private String mEntryName = null;
	
	public PeerManager(Context context, Transceiver owner)
	{
		mOwner = owner;
		mContext = context;
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
