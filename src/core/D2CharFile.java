package core;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

public class D2CharFile {

	private File charFile;
	private int level, mapID, checksum;
	private String name, charClass, difficulty;

	public D2CharFile(File charFile) throws IOException {
		this.charFile = charFile;
		this.setCharacterInfo();
	}

	private void setCharacterInfo() {
		ByteBuffer buffer = this.readCharacterInformation();

		if (buffer == null) {
			throw new NullPointerException("Cannot read character file.");
		}

		byte[] nameBytes = new byte[16];
		buffer.get(nameBytes);
		this.name = new String(nameBytes).trim();

		this.charClass = this.getCharClass(buffer.get());

		this.level = (int) buffer.get();

		byte[] difficultyBytes = new byte[3];
		buffer.get(difficultyBytes);
		this.difficulty = this.getDifficulty(difficultyBytes);

		this.mapID = buffer.getInt();

		this.checksum = buffer.getInt();
	}

	private ByteBuffer readCharacterInformation() {
		ByteBuffer buffer = ByteBuffer.allocate(29);
		try (FileChannel fileChannel = FileChannel.open(this.charFile.toPath(), StandardOpenOption.READ)) {
			fileChannel.position(20L);
			ByteBuffer nameBuffer = ByteBuffer.allocate(16);
			fileChannel.read(nameBuffer);
			nameBuffer.flip();
			buffer.put(nameBuffer);

			fileChannel.position(40L);
			ByteBuffer classBuffer = ByteBuffer.allocate(1);
			fileChannel.read(classBuffer);
			classBuffer.flip();
			buffer.put(classBuffer);

			fileChannel.position(43L);
			ByteBuffer levelBuffer = ByteBuffer.allocate(1);
			fileChannel.read(levelBuffer);
			levelBuffer.flip();
			buffer.put(levelBuffer);

			fileChannel.position(168L);
			ByteBuffer difficultyBuffer = ByteBuffer.allocate(3);
			fileChannel.read(difficultyBuffer);
			difficultyBuffer.flip();
			buffer.put(difficultyBuffer);

			fileChannel.position(171L);
			ByteBuffer mapBuffer = ByteBuffer.allocate(4);
			fileChannel.read(mapBuffer);
			mapBuffer.flip();
			buffer.put(mapBuffer);

			fileChannel.position(12L);
			ByteBuffer checksumBuffer = ByteBuffer.allocate(4);
			fileChannel.read(checksumBuffer);
			checksumBuffer.flip();
			buffer.put(checksumBuffer);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		buffer.flip();
		return buffer;
	}

	private String getCharClass(byte classByte) {
		switch (classByte) {
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

	private String getDifficulty(byte[] difficultyBytes) {
		for (int i = 0; i < 3; i++) {
			if (difficultyBytes[i] == 0) {
				continue;
			} else {
				switch (i) {
				case 0:
					difficulty = "Normal";
					return this.getAct(difficulty, difficultyBytes[i]);
				case 1:
					difficulty = "Nightmare";
					return this.getAct(difficulty, difficultyBytes[i]);
				case 2:
					difficulty = "Hell";
					return this.getAct(difficulty, difficultyBytes[i]);
				}
			}
		}

		return "Undefined";
	}

	private String getAct(String difficulty, byte b) {
		switch (b + 128) {
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

	public boolean replaceMapID(int newMapID) {
		this.mapID = newMapID;
		boolean mapSuccess = this.writeNewMapID(newMapID);
		boolean checksumSuccess = this.writeNewChecksum();
		return mapSuccess && checksumSuccess;
	}

	private boolean writeNewMapID(int newMapID) {
		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
		buffer.putInt(newMapID);
		buffer.flip();

		try (FileChannel fileChannel = FileChannel.open(charFile.toPath(), StandardOpenOption.WRITE)) {
			fileChannel.position(171L);
			fileChannel.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private boolean writeNewChecksum() {
		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);

		int newChecksum = this.recalculateChecksum();
		this.checksum = newChecksum;
		buffer.putInt(newChecksum);
		buffer.flip();

		try (FileChannel fileChannel = FileChannel.open(charFile.toPath(), StandardOpenOption.WRITE)) {
			fileChannel.position(12L);
			fileChannel.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public D2CharFile renameCharacter(String name) {
		// copy all character files
		try {
			this.copyRelatedFiles(this.charFile.toPath(), name);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		// change name of new character
		File dir = this.getFile().getParentFile();
		D2CharFile renamed = null;
		try {
			renamed = new D2CharFile(new File(dir + "\\" + name + ".d2s"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (renamed == null) {
			return null;
		}
		
		ByteBuffer buffer = ByteBuffer.allocate(16);
		byte[] nameBytes = name.getBytes();

		for (int i = 0; i < Math.min(nameBytes.length, 16); i++) {
			buffer.put(nameBytes[i]);
		}

		for (int i = nameBytes.length; i < 16; i++) {
			buffer.put((byte) 0x00);
		}

		buffer.flip();
		
		try (FileChannel fileChannel = FileChannel.open(renamed.getFile().toPath(), StandardOpenOption.WRITE)){
			fileChannel.position(20L);
			fileChannel.write(buffer);		
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		renamed.name = name;
		renamed.writeNewChecksum();
		return renamed;
	}

	private void copyRelatedFiles(Path originalFile, String newName) throws IOException {
		Path originalDir = originalFile.getParent();
		String originalBaseName = this.getName();

		Path newD2SFile = originalDir.resolve(newName + ".d2s");
		Path newKeyFile = originalDir.resolve(newName + ".key");
		Path newD2XFile = originalDir.resolve(newName + ".d2x");
		Path newMapFile = originalDir.resolve(newName + ".map");

		this.copyFile(originalDir.resolve(originalBaseName + ".d2s"), newD2SFile);
		this.copyFile(originalDir.resolve(originalBaseName + ".key"), newKeyFile);
		this.copyFile(originalDir.resolve(originalBaseName + ".d2x"), newD2XFile);
		this.copyFile(originalDir.resolve(originalBaseName + ".map"), newMapFile);

		for (int i = 0; i < 4; i++) {
			Path originalMAFile = originalDir.resolve(originalBaseName + ".ma" + i);
			Path newMAFile = originalDir.resolve(newName + ".ma" + i);
			if (Files.exists(originalMAFile)) {
				this.copyFile(originalMAFile, newMAFile);
			}
		}
	}

	private void copyFile(Path sourceFile, Path targetFile) throws IOException {
		if (Files.exists(sourceFile)) {
			Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
		}
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

	public int getMapIDReverse() {
		return Integer.reverseBytes(mapID);
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return String.format("%s - %s", this.name, this.charClass);
	}

	public int getMapID() {
		return mapID;
	}
}
