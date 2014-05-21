package cn.aiseminar.aisentry;

import cn.aiseminar.aisentry.aimouth.AIMouth;
import cn.aiseminar.aisentry.transceiver.Transceiver;
import cn.aiseminar.aisentry.transceiver.Transceiver.DataTransferThread;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class AISEntryActivity extends Activity {

    private AIMouth mMouth = null;
    private Transceiver mTransceiver = null;
    private Handler mMsgHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_aisentry);
        mMsgHandler = new AISHandler();
    }

    @Override
	protected void onStart() {
		super.onStart();
		
        AISystemLoader systemLoader = new AISystemLoader(this);
        systemLoader.checkEnvironment();
        
		if (null == mMouth)
		{
			mMouth = new AIMouth(this);
			mMouth.setMsgHandler(mMsgHandler);
		}
		
		if (null == mTransceiver)
		{
			mTransceiver = new Transceiver(this);
			mTransceiver.setMsgHandler(mMsgHandler);
			mTransceiver.startBroadcasting();
			mTransceiver.connectToEntry("172.25.6.138", 39153);
		}
	}
    
    @SuppressLint("HandlerLeak")
	class AISHandler extends Handler
    {
		@Override
		public void handleMessage(Message msg) {
			if (AISMessageCode.MOUTH_MSG_BASE + AIMouth.TTS_State.TTS_READY.ordinal() == msg.what)
			{
				if (null != mMouth)
				{
					mMouth.speak(AISEntryActivity.this.getString(R.string.st_welcome));
				}
				return;
			}
			
			if (AISMessageCode.TRANSCEIVER_MSG_BASE + DataTransferThread.DATATYPE_PLAINTEXT == msg.what)
			{
				if (null != mMouth && null != msg.obj)
				{
					String message = (String) msg.obj;
					mMouth.speak(message);
				}
				return;
			}
			
			super.handleMessage(msg);
		}
    }
}
