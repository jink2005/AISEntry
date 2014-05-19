package cn.aiseminar.aisentry.transceiver;

import android.content.Context;

public class Transceiver {
	private Context mContext = null;
	private PeerManager mPeerManager = null;
	
	public Transceiver(Context context)
	{
		mContext = context;
		mPeerManager = new PeerManager(mContext, this);
	}
}
