package cn.aiseminar.aisentry.reader;

import cn.aiseminar.aisentry.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.file_reader);
		
		mViewFlipper = (ViewFlipper) findViewById(R.id.viewflipper_reader);
		
		try {
			mFilePath = this.getIntent().getStringExtra(SelectFileActivity.EXTRA_FILEPATH);
			refreshGUI(defaultCode);
		} catch (Exception e) {
		}
	}
	
	private void refreshGUI(String code) {
		TextView tv = (TextView) createLayoutView(R.layout.reader_page);
		String fileContent = getStringFromFile(code);
		tv.setText(fileContent);
		mViewFlipper.addView(tv);
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

}
