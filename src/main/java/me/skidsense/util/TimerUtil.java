package me.skidsense.util;

public class TimerUtil {
    private long lastMS;

    public boolean sleep(final Double double1) {
        if (lastMS >= double1) {
            reset();
            return true;
        }
        return false;
    }
    
    private long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }

	public boolean hit(long milliseconds) {
		return getCurrentMS() - this.lastMS >= milliseconds;
	}
	
    public boolean hasReached(double milliseconds) {
        if ((double)(this.getCurrentMS() - this.lastMS) >= milliseconds) {
            return true;
        }
        return false;
    }

    public void reset() {
        this.lastMS = this.getCurrentMS();
    }

    public boolean delay(float milliSec) {
        if ((float)(this.getTime() - this.lastMS) >= milliSec) {
            return true;
        }
        return false;
    }
    
    public boolean delay(Double double1) {
        if ((float)(this.getTime() - this.lastMS) >= double1) {
            return true;
        }
        return false;
    }

    public long getTime() {
        return System.nanoTime() / 1000000L;
    }

    public boolean isDelayComplete(long delay) {
        if (System.currentTimeMillis() - this.lastMS > delay) {
            return true;
        }
        return false;
    }

    public boolean check(float milliseconds) {
        return getTime() >= milliseconds;
    }
}

