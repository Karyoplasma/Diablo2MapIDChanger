package core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import gui.Diablo2MapChanger;

public class IniFileWriter {

	public IniFileWriter() {
		// TODO Auto-generated constructor stub
	}

	public static boolean createDefaultIniFile() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File("D2MapChanger.ini")));
			
			writer.write("SavePath=");
			writer.newLine();
			writer.write("AutoBackup=1");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean writeNewIniFile(Diablo2MapChanger gui) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File("D2MapChanger.ini")));
			
			writer.write("SavePath=");
			writer.write(gui.getSavePathString());
			writer.newLine();
			writer.write("AutoBackup=1");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
		
	}

}
