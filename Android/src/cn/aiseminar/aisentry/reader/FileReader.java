package cn.aiseminar.aisentry.reader;

import cn.aiseminar.aisentry.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.text.Layout;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
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
	
	private String mFilePath;
	private ViewFlipper mViewFlipper = null;
	private GestureDetector mGestureDetector = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.file_reader);
		
		mViewFlipper = (ViewFlipper) findViewById(R.id.viewflipper_reader);
		mGestureDetector = new GestureDetector(this, new PageOnGestureListener());
		
		try {
			mFilePath = this.getIntent().getStringExtra(SelectFileActivity.EXTRA_FILEPATH);
			refreshGUI(defaultCode);
		} catch (Exception e) {
		}
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
		
	}
}
