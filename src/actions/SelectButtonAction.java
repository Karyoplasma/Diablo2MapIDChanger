package actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import core.D2CharFile;
import core.IniFileWriter;
import gui.Diablo2MapChanger;

public class SelectButtonAction extends AbstractAction {

	private static final long serialVersionUID = -758467460481123570L;
	private Diablo2MapChanger gui;

	public SelectButtonAction(Diablo2MapChanger diablo2MapChanger) {
		putValue(Action.NAME, "Select");
		this.gui = diablo2MapChanger;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String directory = this.gui.getSavePathString();
		JFileChooser fileChooser = new JFileChooser(directory);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("D2S Files", "d2s");

		fileChooser.setFileFilter(filter);
		int result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			if (selectedFile.getName().toLowerCase().endsWith(".d2s")) {
				this.fileSelected(selectedFile);
			} else {
				System.out.println("Please select a file ending with '.d2s'.");
				JOptionPane.showMessageDialog(this.gui.getFrame(), "Please select a Diablo 2 character file.",
						"Wrong file type", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	private void fileSelected(File selectedFile) {
		((DefaultListModel<D2CharFile>) this.gui.getListCharacterList().getModel()).clear();
		this.gui.setSavePath(selectedFile.getParentFile().getAbsolutePath());
		this.gui.getLblSavePath().setText(selectedFile.getParentFile().getAbsolutePath());
		int index = 0;
		if (selectedFile.getParentFile().isDirectory()) {
			File[] files = selectedFile.getParentFile().listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isFile() && file.getName().toLowerCase().endsWith(".d2s")) {
						try {
							D2CharFile charFile = new D2CharFile(file);
							((DefaultListModel<D2CharFile>) this.gui.getListCharacterList().getModel())
									.addElement(charFile);
							if (file.equals(selectedFile)) {
								this.gui.getListCharacterList().setSelectedIndex(index);
							}
							index++;
						} catch (IOException e) {
							JOptionPane.showMessageDialog(this.gui.getFrame(),
									"Error when parsing the character files.", "File error", JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
							return;
						}
					}
				}
			}
		}
		
		if (!IniFileWriter.writeNewIniFile(this.gui)) {
			JOptionPane.showMessageDialog(this.gui.getFrame(),
					"Error when saving your settings. Default settings have not been changed.", "Ini write error",
					JOptionPane.ERROR_MESSAGE);

		}
	}
}
