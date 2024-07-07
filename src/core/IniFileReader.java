package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class IniFileReader {

	private File iniFile;
	private Map<String, String> settings;

	public IniFileReader(File iniFile) {
		this.iniFile = iniFile;
		if (iniFile.exists()) {
			this.settings = (this.readSettings());
		}
	}

	private Map<String, String> readSettings() {
		Map<String, String> settingsMap = new HashMap<String, String>();
		try (BufferedReader reader = Files.newBufferedReader(this.iniFile.toPath())){
			String in;
			
			while (!((in = reader.readLine()) == null)) {
				String[] inSplit = in.split("=");
				if (inSplit.length != 2) {
					continue;
				}
				settingsMap.put(inSplit[0].trim(), inSplit[1].trim());
			}
		} catch (IOException e) {
			return null;
		}
		return settingsMap;
	}

	public Map<String, String> getSettings() {
		return this.settings;
	}
}
