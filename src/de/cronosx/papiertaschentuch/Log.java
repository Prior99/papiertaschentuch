package de.cronosx.papiertaschentuch;

import static de.cronosx.papiertaschentuch.Log.Level.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Log extends Thread {

	private static final Log instance = new Log();
	private final LinkedBlockingQueue<Message> queue;
	private static final List<LogEventListener> fatalListeners = new ArrayList<>();
	private static final List<LogEventListener> errorListeners = new ArrayList<>();

	public Log() {
		super("Loggerthread");
		queue = new LinkedBlockingQueue<>();
		this.start();
	}

	public static void onError(LogEventListener l) {
		errorListeners.add(l);
	}

	public static void onFatal(LogEventListener l) {
		fatalListeners.add(l);
	}

	public void log(String text, Level level) {
		Message msg = new Message(text, level);
		queue.add(msg);
	}

	public static void info(String text) {
		if (Papiertaschentuch.getConfig().getBool("Log Info", true)) {
			instance.log(text, INFO);
		}
	}

	public static void debug(String text) {
		if (Papiertaschentuch.getConfig().getBool("Log Debug", true)) {
			instance.log(text, DEBUG);
		}
	}

	public static void warn(String text) {
		if (Papiertaschentuch.getConfig().getBool("Log Warnings", true)) {
			instance.log(text, WARNING);
		}
	}

	public static void error(String text) {
		instance.log(text, ERROR);
		errorListeners.stream().forEach((l) -> {
			l.onLogEvent(text);
		});
	}

	public static void fatal(String text) {
		instance.log(text, FATAL);
		fatalListeners.stream().forEach((l) -> {
			l.onLogEvent(text);
		});
	}

	public static void shutdown() {
		instance.interrupt();
		instance.flush();
	}

	private void flush() {
		while (!queue.isEmpty()) {
			print(queue.poll());
		}
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			try {
				Message msg = queue.take();
				print(msg);
			} catch (InterruptedException e) {
				shutdown();
				break;
			}
		}
	}

	private void print(Message msg) {
		String string = msg.toString();
		System.out.println(string);
	}

	private static class Message {

		private final Level level;
		private final String text;
		private final long time;

		public Message(String text, Level level) {
			this.text = text;
			this.level = level;
			this.time = System.currentTimeMillis();
		}

		public String getText() {
			return text;
		}

		public Level getLevel() {
			return level;
		}

		public long getTime() {
			return time;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			Calendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(getTime());
			sb.append("(").append(calendar.get(Calendar.YEAR)).append("-")
					.append(String.format("%02d", calendar.get(Calendar.MONTH))).append("-")
					.append(String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH))).append(" ")
					.append(String.format("%02d", calendar.get(Calendar.HOUR))).append(":")
					.append(String.format("%02d", calendar.get(Calendar.MINUTE))).append(":")
					.append(String.format("%02d", calendar.get(Calendar.SECOND)))
					.append(") [");
			switch (getLevel()) {
				case ERROR:
					sb.append("ERROR");
					break;
				case INFO:
					sb.append("INFO");
					break;
				case DEBUG:
					sb.append("DEBUG");
					break;
				case WARNING:
					sb.append("WARNING");
					break;
				case FATAL:
					sb.append("FATAL");
					break;
			}
			sb.append("] ");
			int length = sb.length();
			String[] lines = getText().split("\r?\n");
			for (int i = 0; i < lines.length; i++) {
				sb.append(lines[i]);
				if (i < lines.length - 1) {
					sb.append("\n");
					int j = 0;
					while (j++ < length) {
						sb.append(" ");
					}
				}
			}
			return sb.toString();
		}
	}

	public enum Level {

		INFO,
		DEBUG,
		WARNING,
		ERROR,
		FATAL
	}

	public static interface LogEventListener {

		public void onLogEvent(String message);
	}
}
