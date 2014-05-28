package cn.aiseminar.aisentry.reader;

import cn.aiseminar.aisentry.*;
import cn.aiseminar.aisentry.aimouth.AIMouth;
import cn.aiseminar.aisentry.aimouth.AIMouth.TTS_State;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewFlipper;

/**
 * 
 * @author Administrator
 * 
 */
public class FileReader extends Activity {
	private static final String gb2312 = "GB2312";
	private static final String utf8 = "UTF-8";
	private static final String defaultCode = gb2312;
	
	private ViewFlipper mViewFlipper = null;
	private GestureDetector mGestureDetector = null;
	// for speak control
	private View mBtnGroupView = null;
	private ImageButton mPlayBtn = null;
	private ImageButton mPauseBtn = null;
	private boolean mbSpeaking = false;
	private AIMouth mMouth = null;
	private Handler mMsgHandler = null;
	// for file reading
	private String mFilePath;
	private int mSpeakOffset = 0;
	private int mSpeakingLength = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.file_reader);
		
		mMsgHandler = new AISHandler();
		
		mViewFlipper = (ViewFlipper) findViewById(R.id.viewflipper_reader);
		mGestureDetector = new GestureDetector(this, new PageOnGestureListener());
		
		initButtonGroup();
		
		try {
			mFilePath = this.getIntent().getStringExtra(SelectFileActivity.EXTRA_FILEPATH);
			refreshGUI(defaultCode);
		} catch (Exception e) {
		}
	}
	
	@Override
	protected void onStart() {
		if (null == mMouth)
		{
			mMouth = AIMouth.getMouth(this);
		}
		mMouth.setMsgHandler(mMsgHandler);
		
		super.onStart();
	}
	
	private void refreshGUI(String code) {
		String fileContent = getStringFromFile(code);
		int curOffset = 0;
		
		for (int i = 0; i < 1; i ++)
		{
			TextView tv = (TextView) createLayoutView(R.layout.reader_page);
			tv.setText(fileContent.substring(curOffset));
			setGestureListenerForView(tv);
			mViewFlipper.addView(tv);
		}
	}
	
	private void initButtonGroup()
	{
		mBtnGroupView = findViewById(R.id.readerBtnLayout);
		mPlayBtn = (ImageButton) findViewById(R.id.ibtnPlay);
		mPlayBtn.setVisibility(View.VISIBLE);
		mPauseBtn = (ImageButton) findViewById(R.id.ibtnPause);
		mPauseBtn.setVisibility(View.INVISIBLE);
		mbSpeaking = false;
		mBtnGroupView.setVisibility(View.INVISIBLE);
	}
	
	private void showButtonGroup()
	{
		int bShow = mBtnGroupView.getVisibility();
		mBtnGroupView.setVisibility((View.VISIBLE == bShow) ? View.INVISIBLE : View.VISIBLE);
		mPlayBtn.setVisibility(mbSpeaking ? View.INVISIBLE : View.VISIBLE);
		mPauseBtn.setVisibility(mbSpeaking ? View.VISIBLE : View.INVISIBLE);
		mPlayBtn.setOnClickListener(new PlayButtonOnClickListener());
		mPauseBtn.setOnClickListener(new PlayButtonOnClickListener());
	}
	
	private void setGestureListenerForView(View targetView)
	{
		targetView.setLongClickable(true);
		targetView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {				
				return mGestureDetector.onTouchEvent(event);
			}
		});
	}
	
	private View createLayoutView(int layoutId)
	{
		LayoutInflater inflater = getLayoutInflater();
		if (null == inflater)
			return null;
		
		return inflater.inflate(layoutId, null);
	}
	
	public String getStringFromFile(String code) {
		try {
			if (! new File(mFilePath).exists()) {
				return null;
			}
			
			StringBuffer sBuffer = new StringBuffer();
			FileInputStream fInputStream = new FileInputStream(mFilePath);			
			InputStreamReader inputStreamReader = new InputStreamReader(fInputStream, code);
			BufferedReader in = new BufferedReader(inputStreamReader);
			
			while (in.ready()) {
				sBuffer.append(in.readLine() + "\n");
			}
			in.close();
			return sBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void speakNextString()
	{
		mSpeakOffset += mSpeakingLength;
		TextView tv = (TextView) mViewFlipper.getCurrentView();
		String content = tv.getText().toString().trim();
		
		int endPos = content.indexOf('\n', mSpeakOffset);
		while (endPos == mSpeakOffset) // skip blank line
		{
			mSpeakOffset ++;
			endPos = content.indexOf('\n', mSpeakOffset);
		}
		
		if (-1 == endPos && mSpeakOffset < content.length()) // the last line of file
		{
			endPos = content.length();
		}
		
		if (endPos > mSpeakOffset)
		{
			content = content.substring(mSpeakOffset, endPos);
			mSpeakingLength = content.length();
			mMouth.speak(content);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
//		case R.id.gb2312:
//			refreshGUI(defaultCode);
//			break;
//		case R.id.utf8:
//			refreshGUI(utf8);
//			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}	
	
	public byte[] readFile(String fileName) throws Exception {
		byte[] result = null;
		FileInputStream fis = null;
		try {
			File file = new File(fileName);
			fis = new FileInputStream(file);
			result = new byte[fis.available()];
			fis.read(result);
		} catch (Exception e) {
		} finally {
			fis.close();
		}
		return result;
	}
	
	/* help classes */
	
	class PageOnGestureListener extends SimpleOnGestureListener
	{
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (mViewFlipper.getChildCount() > 1)
			{
				int fling_min_distance = 100;
				int fling_min_velocity = 200;
				
				if (Math.abs(velocityX) > fling_min_velocity)
				{
					if (e1.getX() - e2.getX() > fling_min_distance) // right in
					{
						mViewFlipper.setInAnimation(FileReader.this, R.anim.anim_right_in);
						mViewFlipper.setOutAnimation(FileReader.this, R.anim.anim_left_out);
						mViewFlipper.showPrevious();
					}
					else if (e2.getX() - e1.getX() > fling_min_distance) // left in
					{
						mViewFlipper.setInAnimation(FileReader.this, R.anim.anim_left_in);
						mViewFlipper.setOutAnimation(FileReader.this, R.anim.anim_right_out);
						mViewFlipper.showNext();
					}
				}
			}
			
			return super.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			showButtonGroup();
			return super.onSingleTapConfirmed(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}
	
	class PlayButtonOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v) {
			if (mbSpeaking)
			{
				// now user paused
				mSpeakingLength = 0; // will reading from last offset
				mMouth.stop();
			}
			else
			{
				TextView tv = (TextView) mViewFlipper.getCurrentView();
				String content = tv.getText().toString().trim();
				if (mSpeakOffset >= content.length())
				{
					mSpeakOffset = 0;
					mSpeakingLength = 0;
				}
				speakNextString();
			}
			
			mbSpeaking = ! mbSpeaking;
			mPlayBtn.setVisibility(mbSpeaking ? View.INVISIBLE : View.VISIBLE);
			mPauseBtn.setVisibility(mbSpeaking ? View.VISIBLE : View.INVISIBLE);
		}		
	}
	
	@SuppressLint("HandlerLeak")
	class AISHandler extends Handler
    {
		@Override
		public void handleMessage(Message msg) {
			if (AISMessageCode.MOUTH_MSG_BASE + TTS_State.TTS_SPEAK_COMPLETED.ordinal() == msg.what)
			{
				if (null != mMouth)
				{
					speakNextString();
				}
				return;
			}
			
			super.handleMessage(msg);
		}
    }
}
