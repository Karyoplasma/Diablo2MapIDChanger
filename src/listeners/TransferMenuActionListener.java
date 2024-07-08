package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import javax.swing.JOptionPane;

import core.D2CharFile;
import gui.Diablo2MapChanger;

public class TransferMenuActionListener implements ActionListener {

	private D2CharFile source, destination;
	private Diablo2MapChanger gui;

	public TransferMenuActionListener(Diablo2MapChanger gui, D2CharFile source, D2CharFile destination) {
		this.gui = gui;
		this.source = source;
		this.destination = destination;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Integer newMapID = this.source.getMapID();
		if (backupCharacter(destination.getFile())) {
			if (destination.replaceMapID(newMapID)) {
				JOptionPane
						.showMessageDialog(
								gui.getFrame(), String.format("Map successfully tranferred from %s to %s!",
										source.getName(), destination.getName()),
								"Success", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(gui.getFrame(), "Map ID change failed.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(gui.getFrame(), "Backup failed. No files have been changed.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private boolean backupCharacter(File characterFile) {
		if (!characterFile.exists()) {
			return false;
		}
		File parentDirectory = characterFile.getParentFile();
		File backupFolder = new File(parentDirectory, "backupMapIDChanger");
		if (!backupFolder.exists() && !backupFolder.mkdir()) {
			JOptionPane.showMessageDialog(this.gui.getFrame(),
					"Failed to create backup folder. Please create a folder within your save folder and name it \"backupMapIDChanger\".",
					"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		File backupFile = new File(backupFolder, characterFile.getName() + "_" + Instant.now().getEpochSecond());
		try {
			Path sourcePath = characterFile.toPath();
			Path destinationPath = backupFile.toPath();
			Files.copy(sourcePath, destinationPath);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
