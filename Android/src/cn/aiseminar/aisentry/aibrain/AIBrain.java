package cn.aiseminar.aisentry.aibrain;

public class AIBrain {
	private static AIBrain mBrain = null;
	
	public static AIBrain getAIBrain()
	{
		if (null == mBrain)
		{
			mBrain = new AIBrain();
		}
		return mBrain;
	}
}
