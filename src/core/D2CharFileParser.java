package core;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class D2CharFileParser {
	private File charFile;

	public D2CharFileParser(File charFile) {
		this.charFile = charFile;
	}

	public String getCharacterName() throws IOException {
		RandomAccessFile charRAF = new RandomAccessFile(this.charFile, "r");
		byte[] name = new byte[16];

		charRAF.seek(20L);
		charRAF.read(name);
		charRAF.close();

		return new String(name).trim();
	}

	public int getCharacterLevel() throws IOException {
		RandomAccessFile charRAF = new RandomAccessFile(this.charFile, "r");
		byte level = -1;

		charRAF.seek(43L);
		level = charRAF.readByte();
		charRAF.close();

		return (int) level;
	}

	public String getCharacterDifficulty() throws IOException {
		RandomAccessFile charRAF = new RandomAccessFile(this.charFile, "r");
		byte[] temp = new byte[3];
		String difficulty;

		charRAF.seek(168L);
		charRAF.read(temp);
		charRAF.close();
		for (int i = 0; i < temp.length; i++) {
			if (temp[i] == 0) {
				continue;
			} else {
				switch (i) {
				case 0:
					difficulty = "Normal";
					return this.getAct(difficulty, temp[i]);
				case 1:
					difficulty = "Nightmare";
					return this.getAct(difficulty, temp[i]);
				case 2:
					difficulty = "Hell";
					return this.getAct(difficulty, temp[i]);
				}
			}
		}

		return "Undefined";
	}

	private String getAct(String difficulty, int i) {
		switch (i + 128) {
		case 0:
			return difficulty + " (Act 1)";
		case 1:
			return difficulty + " (Act 2)";
		case 2:
			return difficulty + " (Act 3)";
		case 3:
			return difficulty + " (Act 4)";
		case 4:
			return difficulty + " (Act 5)";
		}
		return difficulty;
	}

	public String getCharacterClass() throws IOException {
		RandomAccessFile charRAF = new RandomAccessFile(this.charFile, "r");
		byte temp = -1;

		charRAF.seek(40L);
		temp = charRAF.readByte();
		charRAF.close();
		switch (temp) {
		case 0:
			return "Amazon";
		case 1:
			return "Sorceress";
		case 2:
			return "Necromancer";
		case 3:
			return "Paladin";
		case 4:
			return "Barbarian";
		case 5:
			return "Druid";
		case 6:
			return "Assassin";
		default:
			return "Undefined";
		}
	}

	public int getMapID() throws IOException {
		RandomAccessFile charRAF = new RandomAccessFile(this.charFile, "r");
		int mapIDInt = 0;

		charRAF.seek(171L);
		mapIDInt = charRAF.readInt();
		charRAF.close();

		return mapIDInt;
	}

	public int getChecksum() throws IOException {
		RandomAccessFile charRAF = new RandomAccessFile(this.charFile, "r");
		int checksum = 0;

		charRAF.seek(12L);
		checksum = charRAF.readInt();
		charRAF.close();

		return checksum;
	}
}
