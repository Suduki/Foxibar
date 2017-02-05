package utils;

public class FPSLimiter {
	private double mSimulationFps;
	private double mFrameStartTime;
	private double mFrameEndTime;
	private double mWantedFrameTime;
	
	public FPSLimiter(double pWantedFps)
	{
		mWantedFrameTime = 1000d/pWantedFps;
		mFrameStartTime = System.currentTimeMillis();
	}
	
	public void waitForNextFrame() {
		mFrameEndTime = System.currentTimeMillis();
		double actualFrameTimeMs = mFrameEndTime - mFrameStartTime;

		double sleepTimeMs = mWantedFrameTime - actualFrameTimeMs;

		if (sleepTimeMs > 0) {
			mSimulationFps = 1000d/sleepTimeMs;
			try {
				Thread.sleep(Math.round(sleepTimeMs));
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		else {
			mSimulationFps = 1000d/(actualFrameTimeMs);
		}

		mFrameStartTime = System.currentTimeMillis();
	}
}
