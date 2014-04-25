package cn.aiseminar.aisentry;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

public class AISystemLoader {	
	public static final String SPEECH_SERVICE_NAME_IFLYTEK = "com.iflytek.speechcloud";
	
	private Context mContext = null;
	
	public AISystemLoader(Context context)
	{
		mContext = context;
	}

	public void checkEnvironment()
	{
		if (! checkSpeechService())
		{
			installSpeechServiceDialog();
		}
	}
	
	public boolean checkSpeechService()
	{
		boolean bInstalled = false;
		try {
			PackageInfo pgInfo = mContext.getPackageManager().getPackageInfo(SPEECH_SERVICE_NAME_IFLYTEK, 0);
			if (null != pgInfo)
			{
				bInstalled = true;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		return bInstalled;
	}
	
	public void installSpeechServiceDialog()
	{
		AlertDialog.Builder dgBuilder = new AlertDialog.Builder(mContext);
		dgBuilder.setMessage(R.string.msg_goto_iflytek);
		dgBuilder.setPositiveButton(android.R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Intent urlIntent = new Intent(Intent.ACTION_VIEW);
				urlIntent.setData(Uri.parse("http://open.voicecloud.cn/speechservice"));
				urlIntent = Intent.createChooser(urlIntent, null);
				mContext.startActivity(urlIntent);
			}			
		});
		dgBuilder.setNegativeButton(android.R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
			}
		});
		dgBuilder.create().show();
	}
}
