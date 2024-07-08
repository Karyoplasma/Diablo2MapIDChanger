package actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import core.D2CharFile;
import gui.Diablo2MapChanger;

public class EditButtonAction extends AbstractAction {

	private static final long serialVersionUID = 1084211109110931223L;
	private Diablo2MapChanger gui;

	public EditButtonAction(Diablo2MapChanger diablo2MapChanger) {
		putValue(Action.NAME, "Change map ID");
		this.gui = diablo2MapChanger;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		D2CharFile selectedCharFile = this.gui.getListCharacterList().getSelectedValue();
		Integer newMapSeed = this.getNewMapSeed();
		if (newMapSeed != null) {
			newMapSeed = Integer.reverseBytes(newMapSeed);
			if (selectedCharFile != null) {
				if (this.gui.getChckbxAutoBackup().isSelected()) {
					if (this.backupCharacter(selectedCharFile.getFile())) {
						if (selectedCharFile.replaceMapID(newMapSeed)) {
							try {
								this.gui.getTextAreaCharacterSummary().setText(selectedCharFile.getCharacterSummary());
							} catch (IOException e1) {
								JOptionPane.showMessageDialog(gui.getFrame(),
										"This should never happen. If it does, let me know!");
							}
							JOptionPane.showMessageDialog(gui.getFrame(), "Map ID change successful.", "Success",
									JOptionPane.INFORMATION_MESSAGE);
						} else {
							JOptionPane.showMessageDialog(gui.getFrame(), "Map ID change failed.", "Error",
									JOptionPane.ERROR_MESSAGE);
						}
					} else {
						JOptionPane.showMessageDialog(gui.getFrame(), "Backup failed. No files have been changed.",
								"Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}

	private Integer getNewMapSeed() {
		String userInput = JOptionPane
				.showInputDialog("Enter a new map seed.\nYou can enter a hex string by prefixing with \"0x\":");
		if (userInput == null) {
			return null;
		}

		try {
			if (userInput.startsWith("-")) {
				return Integer.parseInt(userInput.trim());
			}
			if (userInput.startsWith("0x") || userInput.startsWith("0X")) {
				return Integer.parseUnsignedInt(userInput.substring(2).trim(), 16);
			} else {
				return Integer.parseUnsignedInt(userInput.trim());
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this.gui.getFrame(), "Invalid input. Please enter a valid seed.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return getNewMapSeed();
		}
	}

	private boolean backupCharacter(File characterFile) {
		if (!characterFile.exists()) {
			return false;
		}
		File parentDirectory = characterFile.getParentFile();
		File backupFolder = new File(parentDirectory, "backupMapIDChanger");
		if (!backupFolder.exists() && !backupFolder.mkdir()) {
			JOptionPane.showMessageDialog(gui.getFrame(),
					"Failed to create backup folder. Please create a folder within your save folder and name it \"backupMapIDChanger\".",
					"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		File backupFile = new File(backupFolder, characterFile.getName() + "_" + Instant.now().getEpochSecond());
		try {
			// Copy the character file to the backup file
			Path sourcePath = characterFile.toPath();
			Path destinationPath = backupFile.toPath();
			Files.copy(sourcePath, destinationPath);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
