package me.skidsense.notifications;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.spi.AbstractLogger;

public class N12nLogger extends AbstractLogger {


	@Override
	protected boolean isEnabled(Level level, Marker marker, Message message, Throwable throwable) {
		return false;
	}

	@Override
	protected boolean isEnabled(Level level, Marker marker, Object o, Throwable throwable) {
		return false;
	}

	@Override
	protected boolean isEnabled(Level level, Marker marker, String s) {
		return false;
	}

	@Override
	protected boolean isEnabled(Level level, Marker marker, String s, Object... objects) {
		return false;
	}

	@Override
	protected boolean isEnabled(Level level, Marker marker, String s, Throwable throwable) {
		return false;
	}

	@Override
	public void log(Marker marker, String s, Level level, Message message, Throwable throwable) {

	}
}
