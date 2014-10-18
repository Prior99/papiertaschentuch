package de.cronosx.papiertaschentuch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Config {

	private boolean parsed;
	private File file;
	private Map<String, String> assocs;

	public Config(String filename) throws FileNotFoundException, IOException {
		file = new File(filename);
		parsed = false;
		assocs = new HashMap<>();
		if (!file.exists()) {
			throw new FileNotFoundException("Configfile not found. Configuration will be empty.");
		}
	}

	public void parse() throws IOException {
		BufferedReader rd = new BufferedReader(new FileReader(file));
		String line;
		int lineNum = 1;
		Parser p = new Parser();
		while ((line = rd.readLine()) != null) {
			line = line.trim();
			if (!line.startsWith("#")) {
				try {
					if(line.length() > 0) {
						p.parse(line);
						assocs.put(p.getKey(), p.getValue());
					}
				} catch (IllegalArgumentException e) {
					Log.warn("Error parsing line " + lineNum + " of config file " + file.getName() + ": " + e.getMessage());
				}
			}
			lineNum++;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("Configfile parsed! Pairs:\n");
		assocs.keySet().stream().forEach((s) -> {
			sb.append(s).append(" -> ").append(assocs.get(s)).append("\n");
		});
		Log.debug(sb.toString());
		parsed = true;
	}

	public static class Parser {

		StringBuilder sbKey, sbValue;
		boolean lastWasBackspace, colonPassed;
		char stringCharacter, cur;
		String source;
		int index;

		private void appendChar(char c) {
			if (colonPassed) {
				sbValue.append(c);
			} else {
				sbKey.append(c);
			}
		}

		public void parse(String source) {
			this.source = source;
			index = 0;
			stringCharacter = 0;
			sbKey = new StringBuilder();
			sbValue = new StringBuilder();
			lastWasBackspace = false;
			colonPassed = false;
			parseRaw();
		}

		public String getKey() {
			return sbKey.toString();
		}

		public String getValue() {
			return sbValue.toString();
		}

		private char next() {
			if (index < source.length()) {
				cur = source.charAt(index++);
			} else {
				cur = 0;
			}
			return cur;
		}

		private void parseRaw() {
			next();
			if (cur == '"' || cur == '\'') {
				stringCharacter = cur;
				parseString();
			} else if (cur == ':') {
				parseColon();
			} else if (cur == 0 || cur == '#') {
				parseEOF();
			} else if (cur == '\t' || cur == ' ') {
				parseWhitespace();
			}
			else {
				appendChar(cur);
				parseRaw();
			}
		}

		private void parseWhitespace() {
			parseRaw();
		}

		private void parseColon() {
			colonPassed = true;
			parseRaw();
		}

		private void parseEOF() {
			if (!colonPassed) {
				throw new IllegalArgumentException("Syntaxerror. Unexpected end of input.");
			}
		}

		private void parseBackslash() {
			next();
			appendChar(cur);
			parseString();
		}

		private void parseString() {
			next();
			if (cur == '"' || cur == '\'') {
				if ((cur == '"' && stringCharacter == cur) || (cur == '\'' && stringCharacter == cur)) {
					parseRaw();
				} else {
					appendChar(cur);
					parseString();
				}
			} else if (cur == 0 || cur == '#') {
				throw new IllegalArgumentException("String was not closed.");
			} else if (cur == '\\') {
				parseBackslash();
			} else {
				appendChar(cur);
				parseString();
			}
		}

	}

	public int getInt(String key, int def) {
		if (!parsed) {
			return def;
		}
		if (assocs.containsKey(key)) {
			try {
				int i = Integer.parseInt(assocs.get(key));
				return i;
			} catch (NumberFormatException e) {
				Log.warn("Tried to parse " + key + " as integer which it is not. In configfile " + file.getName() + ". Returning default value.");
				return def;
			}
		} else {
			return def;
		}
	}

	public String getString(String key, String def) {
		if (!parsed) {
			return def;
		}
		if (assocs.containsKey(key)) {
			return assocs.get(key);
		} else {
			return def;
		}
	}

	public float getFloat(String key, float def) {
		if (!parsed) {
			return def;
		}
		if (assocs.containsKey(key)) {
			try {
				float f = Float.parseFloat(assocs.get(key));
				return f;
			} catch (NumberFormatException e) {
				Log.warn("Tried to parse " + key + " as float which it is not. In configfile " + file.getName() + ". Returning default value.");
				return def;
			}
		} else {
			return def;
		}
	}

	public boolean getBool(String key, boolean def) {
		if (!parsed) {
			return def;
		}
		if (assocs.containsKey(key)) {
			String s = assocs.get(key);
			if (s.toLowerCase().equals("true") || s.equals("1") || s.toLowerCase().equals("yes")) {
				return true;
			} else if (s.toLowerCase().equals("false") || s.equals("0") || s.toLowerCase().equals("no")) {
				return false;
			} else {
				Log.warn("Tried to parse " + key + " as boolean which it is not. Allowed values are true, false, 1, 0, yes and no. In configfile " + file.getName() + ". Returning default value.");
				return def;
			}
		} else {
			return def;
		}
	}
}
