package cn.aiseminar.aisentry.aimouth;

import android.content.Context;
import android.os.RemoteException;
import android.os.SystemClock;

import com.iflytek.speech.*;

public class AIMouth extends Object {
	public static final String mAppId = "5350daf2";
	public static enum TTS_State
	{
		TTS_INITING,
		TTS_READY,
		TTS_ERROR,
	};
	
	private Context mContext = null;
	private SpeechSynthesizer mSpeechSynthesizer = null;
	private TTS_State mSynthesizerState = TTS_State.TTS_INITING;
	
	public AIMouth(Context context)
	{
		mContext = context;
		SpeechUtility.getUtility(mContext).setAppid(mAppId);
		
		mSpeechSynthesizer = new SpeechSynthesizer(mContext, new InitListener() {
			@Override
			public void onInit(ISpeechModule arg0, int arg1) {
				if (arg1 == ErrorCode.SUCCESS)
				{
					mSynthesizerState = TTS_State.TTS_READY;
				}	
			}
		});
		setVoiceReference();
	}
		
	@Override
	protected void finalize() throws Throwable {
		mSpeechSynthesizer.stopSpeaking(mSynthesizerListener);
		mSpeechSynthesizer.destory();
		super.finalize();
	}

	private void setVoiceReference()
	{
		mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, "local");
		mSpeechSynthesizer.setParameter(SpeechSynthesizer.VOICE_NAME, "xiaoyan");
		mSpeechSynthesizer.setParameter(SpeechSynthesizer.SPEED, "50");
		mSpeechSynthesizer.setParameter(SpeechSynthesizer.PITCH, "50");
	}
	
	public void speak(String sentence)
	{
//		while (mSynthesizerState != TTS_State.TTS_READY)
//		{
//			SystemClock.sleep(100);
//		}
		mSpeechSynthesizer.startSpeaking(sentence, mSynthesizerListener);
	}
	
	private SynthesizerListener mSynthesizerListener = new SynthesizerListener.Stub() {
		
		@Override
		public void onSpeakResumed() throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onSpeakProgress(int arg0) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onSpeakPaused() throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onSpeakBegin() throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onCompleted(int arg0) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onBufferProgress(int arg0) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
	};
}
