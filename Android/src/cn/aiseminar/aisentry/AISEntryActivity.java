package cn.aiseminar.aisentry;

import cn.aiseminar.aisentry.aimouth.AIMouth;
import cn.aiseminar.aisentry.transceiver.Transceiver;
import cn.aiseminar.aisentry.transceiver.Transceiver.DataTransferThread;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class AISEntryActivity extends Activity {

    private AIMouth mMouth = null;
    private Transceiver mTransceiver = null;
    private Handler mMsgHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_aisentry);
        mMsgHandler = new AISHandler();
        
        View fullView = findViewById(R.id.entryImage);
        fullView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				connectToEntry();
			}
		});
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
		}
	}
    
    private void connectToEntry()
    {
    	final EditText inputServer = new EditText(this);
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Connect to PC Entry").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer);
    	builder.setNegativeButton(android.R.string.cancel, null);
    	builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String serverInfo = inputServer.getText().toString();
				int seperPos = serverInfo.indexOf(":");
				String ipAddress = serverInfo.substring(0, seperPos);
				String ipPort = serverInfo.substring(seperPos + 1);
				
				mTransceiver.connectToEntry(ipAddress, Integer.parseInt(ipPort));
			}
		});
    	builder.show();
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
