package cn.aiseminar.aisentry.reader;

import cn.aiseminar.aisentry.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SelectFileActivity extends ListActivity {	
	public static final int PICKFILE_REQUEST_CODE = 0;
	public static final String EXTRA_FILEPATH = "extra_filePath";
	
	private List<File> fileNameList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_select_file);
		initFileList();
	}

	private void initFileList() {
		File path = android.os.Environment.getExternalStorageDirectory();
		File[] f = path.listFiles();
		fill(f);
	}	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		File file = fileNameList.get(position);
		if (file.isDirectory()) {
			File[] f = file.listFiles();
			fill(f);
		} else {
			Intent intent = new Intent();
			intent.putExtra(EXTRA_FILEPATH, file.getAbsolutePath());
			setResult(RESULT_OK, intent);
			finish();
		}
	}
	
	private void fill(File[] files) {
		fileNameList = new ArrayList<File>();
		for (File file : files) {
			if (isValidFileOrDir(file)) {
				fileNameList.add(file);
			}
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, fileToStrArr(fileNameList));
		setListAdapter(adapter);
	}
	
	private boolean isValidFileOrDir(File file) {
		if (file.isDirectory()) {
			return true;
		} else {
			String fileName = file.getName().toLowerCase(Locale.getDefault());
			if (fileName.endsWith(".txt")) {
				return true;
			}
		}
		return false;
	}

	private String[] fileToStrArr(List<File> fl) {
		ArrayList<String> fnList = new ArrayList<String>();
		for (int i = 0; i < fl.size(); i++) {
			String nameString = fl.get(i).getName();
			fnList.add(nameString);
		}
		return fnList.toArray(new String[0]);
	}
}
