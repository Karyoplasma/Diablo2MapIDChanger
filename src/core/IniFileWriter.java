package core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import gui.Diablo2MapChanger;

public class IniFileWriter {

	private IniFileWriter() {
	}

	public static boolean createDefaultIniFile() {
		File iniFile = new File("D2MapChanger.ini");
		try (BufferedWriter writer = Files.newBufferedWriter(iniFile.toPath(), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING)) {
			writer.write("SavePath=");
			writer.newLine();
			writer.write("AutoBackup=1");
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean writeNewIniFile(Diablo2MapChanger gui) {
		File iniFile = new File("D2MapChanger.ini");
		try (BufferedWriter writer = Files.newBufferedWriter(iniFile.toPath(), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING)) {
			writer.write("SavePath=");
			writer.write(gui.getSavePathString());
			writer.newLine();
			writer.write("AutoBackup=1");
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

}
