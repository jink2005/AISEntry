package cn.aiseminar.aisentry.reader;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import cn.aiseminar.aisentry.*;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BookShelfActivity extends Activity {
	public static final String SP_BOOKS_PATH = "SP_BOOKS_PATH";
	
	private BookShelfGridView mBookShelfView = null;
	private ArrayList<String> mBooksPathArray = null;
	private BookShelfAdapter mShelfAdapter = null;
	private SharedPreferences mSPBooksPath = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_reader);
		
		mSPBooksPath = getSharedPreferences(SP_BOOKS_PATH, MODE_PRIVATE);
		mBooksPathArray = new ArrayList<String>();	
		
		mShelfAdapter = new BookShelfAdapter();
		mBookShelfView = (BookShelfGridView) findViewById(R.id.bookShelfView);
		mBookShelfView.setAdapter(mShelfAdapter);
		mBookShelfView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position < mBooksPathArray.size())
				{
					String path = mBooksPathArray.get(position);
					startFileReader(path);
				}
				else
				{
					browserAndSelectFiles();
				}
			}
			
		});
		syncBooksPath();
	}
	
	@Override
	protected void onDestroy() {
		Editor editor = mSPBooksPath.edit();
		for (int i = 0; i < mBooksPathArray.size(); i ++)
		{
			editor.putString(String.valueOf(i), mBooksPathArray.get(i));
		}
		editor.commit();
		
		super.onDestroy();
	}

	public void startFileReader(String path)
	{
		if (null != path)
		{
			Intent intent = new Intent(this, FileReader.class);
			intent.putExtra(SelectFileActivity.EXTRA_FILEPATH, path);
			startActivity(intent);
		}
	}
	
	public void browserAndSelectFiles()
	{
		Intent intent = new Intent(BookShelfActivity.this, SelectFileActivity.class);
		startActivityForResult(intent, SelectFileActivity.PICKFILE_REQUEST_CODE);
	}
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (SelectFileActivity.PICKFILE_REQUEST_CODE == requestCode)
		{
			if (RESULT_OK == resultCode)
			{
				String path = data.getStringExtra(SelectFileActivity.EXTRA_FILEPATH).toString();
				if (! mBooksPathArray.contains(path))
				{
					mBooksPathArray.add(path);
					mShelfAdapter.notifyDataSetChanged();					
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void syncBooksPath()
	{
		// read path list from storage
		mBooksPathArray.clear();
		Map<String, ?> spPaths = mSPBooksPath.getAll();
		for (Entry<String, ?> entry : spPaths.entrySet())
		{
			String path = (String) entry.getValue();
			if (! mBooksPathArray.contains(path))
			{
				mBooksPathArray.add(path);
			}
		}
		mShelfAdapter.notifyDataSetChanged();
	}

	class BookShelfAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			if (null != mBooksPathArray)
			{
				return mBooksPathArray.size() + 1; // one more for add button
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (null != mBooksPathArray && position < getCount() - 1)
			{
				return mBooksPathArray.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position < getCount() - 1) {
				convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_book_item, null);
				TextView tv = (TextView) convertView.findViewById(R.id.bookItemView);
				String fileName = new File(mBooksPathArray.get(position)).getName();
				tv.setText(fileName);
			}
			else {
				convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_book_add, null);
			}
						
			return convertView;
		}
		
	}
}
