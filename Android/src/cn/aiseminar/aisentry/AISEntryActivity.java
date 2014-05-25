package cn.aiseminar.aisentry;

import cn.aiseminar.aisentry.aimouth.AIMouth;
import cn.aiseminar.aisentry.reader.BookShelfActivity;
import cn.aiseminar.aisentry.transceiver.Transceiver;
import cn.aiseminar.aisentry.transceiver.Transceiver.DataTransferThread;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ViewFlipper;

public class AISEntryActivity extends Activity {

    private AIMouth mMouth = null;
    private Transceiver mTransceiver = null;
    private Handler mMsgHandler = null;
    
    private ViewFlipper mViewFlipper = null;
    private GestureDetector mGestureDetector = null;
    
    private View mEntryView = null;
    private View mReaderView = null;
    
    int test = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE); // hidden title        
        setContentView(R.layout.activity_aisentry);
        
        mMsgHandler = new AISHandler();
        
        mGestureDetector = new GestureDetector(this, new AISOnGestureListener());
        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        
        mEntryView = findViewById(R.id.entryImage);
        mEntryView.setLongClickable(true);
        mEntryView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {				
				return mGestureDetector.onTouchEvent(event);
			}
		});
        
        mReaderView = findViewById(R.id.readerView);
        mReaderView.setLongClickable(true);
        mReaderView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {				
				return mGestureDetector.onTouchEvent(event);
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
    
    class AISOnGestureListener extends SimpleOnGestureListener
    {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			int fling_min_distance = 100;
			int fling_min_velocity = 200;
			
			if (Math.abs(velocityX) > fling_min_velocity)
			{
				if (e1.getX() - e2.getX() > fling_min_distance) // right in
				{
					if (mViewFlipper.getCurrentView() == mReaderView)
					{
						mViewFlipper.setInAnimation(AISEntryActivity.this, R.anim.anim_right_in);
						mViewFlipper.setOutAnimation(AISEntryActivity.this, R.anim.anim_left_out);
						mViewFlipper.showPrevious();
					}
				}
				else if (e2.getX() - e1.getX() > fling_min_distance) // left in
				{
					if (mViewFlipper.getCurrentView() == mEntryView)
					{
						mViewFlipper.setInAnimation(AISEntryActivity.this, R.anim.anim_left_in);
						mViewFlipper.setOutAnimation(AISEntryActivity.this, R.anim.anim_right_out);
						mViewFlipper.showNext();
					}
				}
			}
			
			return super.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (mViewFlipper.getCurrentView() == mEntryView)
			{
				connectToEntry();
			}
			else if (mViewFlipper.getCurrentView() == mReaderView)
			{
				Intent readerIntent = new Intent(AISEntryActivity.this, BookShelfActivity.class);
				startActivity(readerIntent);
			}
			return super.onSingleTapConfirmed(e);
		} 
		
		
    }
}
