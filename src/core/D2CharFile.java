package core;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;

public class D2CharFile {

	private File charFile;
	private int level, mapID, checksum;
	private String name, charClass, difficulty;

	public D2CharFile(File charFile) throws IOException {
		this.charFile = charFile;
		D2CharFileParser parser = new D2CharFileParser(charFile);
		this.level = parser.getCharacterLevel();
		this.mapID = parser.getMapID();
		this.name = parser.getCharacterName();
		this.charClass = parser.getCharacterClass();
		this.difficulty = parser.getCharacterDifficulty();
		this.checksum = parser.getChecksum();
	}

	public boolean replaceMapID(int newMapID) {
		this.mapID = newMapID;
		boolean mapSuccess = this.writeNewMapID(newMapID);
		this.checksum = this.recalculateChecksum();
		boolean checksumSuccess = this.writeNewChecksum();
		return mapSuccess && checksumSuccess;
	}

	private boolean writeNewMapID(int newMapID) {
		try {
			RandomAccessFile charRAF = new RandomAccessFile(this.charFile, "rw");

			charRAF.seek(171L);
			charRAF.writeInt(newMapID);
			charRAF.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean writeNewChecksum() {
		try {
			RandomAccessFile charRAF = new RandomAccessFile(this.charFile, "rw");

			charRAF.seek(12L);
			charRAF.writeInt(this.recalculateChecksum());
			charRAF.close();

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private int recalculateChecksum() {
		int checksum = 0;
		try {
			byte[] ba = Files.readAllBytes(this.charFile.toPath());

			ba[12] = 0;
			ba[13] = 0;
			ba[14] = 0;
			ba[15] = 0;

			for (int i = 0; i < ba.length; i++) {
				checksum = Integer.rotateLeft(checksum, 1);
				checksum += ba[i] & 0xFF;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}

		return Integer.reverseBytes(checksum);
	}

	public String getCharacterSummary() throws IOException {
		StringBuilder builder = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		builder.append(this.name).append(newLine);
		builder.append(this.charClass).append(" - Level ").append(this.level).append(newLine);
		builder.append(this.difficulty).append(newLine);
		builder.append(String.format("Map ID: %08X", Integer.reverseBytes(this.mapID))).append(newLine);
		builder.append(String.format("Checksum: %08X", Integer.reverseBytes(this.checksum))).append(newLine);

		return builder.toString();
	}

	public File getFile() {
		return this.charFile;
	}

	@Override
	public String toString() {
		return String.format("%s - %s", this.name, this.charClass);
	}
}
