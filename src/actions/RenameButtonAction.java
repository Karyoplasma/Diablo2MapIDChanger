package actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import core.D2CharFile;
import gui.Diablo2MapChanger;

public class RenameButtonAction extends AbstractAction {

	private static final long serialVersionUID = -5139680709038246L;

	private Diablo2MapChanger gui;

	public RenameButtonAction(Diablo2MapChanger diablo2MapChanger) {
		putValue(Action.NAME, "Rename character");
		this.gui = diablo2MapChanger;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		D2CharFile selectedCharFile = this.gui.getListCharacterList().getSelectedValue();
		if (selectedCharFile == null) {
			return;
		}
		String name = this.getNewName();
		if (name == null) {
			return;
		}
		D2CharFile newCharacterFile = selectedCharFile.renameCharacter(name);
		if (newCharacterFile != null) {
			((DefaultListModel<D2CharFile>) this.gui.getListCharacterList().getModel()).addElement(newCharacterFile);
			gui.getListCharacterList().setSelectedValue(newCharacterFile, true);
		}
	}

	private String getNewName() {
		String userInput = JOptionPane.showInputDialog(
				"Enter a new name.\nValid names start with a letter and are between 2 and 15 characters of length. Can contain up to 1 hyphen or underscore.\nNames that currently exist within the directory are rejected.");
		if (userInput == null) {
			return null;
		}
		if (this.isValidCharacterName(userInput)) {
			return userInput;
		} else {
			JOptionPane.showMessageDialog(gui.getFrame(), "Invalid character name!", "Error",
					JOptionPane.ERROR_MESSAGE);
			return getNewName();
		}

	}

	private boolean isValidCharacterName(String name) {
		if (name == null || name.length() < 2 || name.length() > 15) {
			return false;
		}

		String regex = "^[a-zA-Z][a-zA-Z-_]*$";
		if (!name.matches(regex)) {
			return false;
		}

		if (this.countSpecials(name) > 1) {
			return false;
		}

		for (int i = 0; i < gui.getListCharacterList().getModel().getSize(); i++) {
			if (gui.getListCharacterList().getModel().getElementAt(i).getName().equals(name)) {
				return false;
			}
		}

		return true;
	}

	private int countSpecials(String string) {
		int res = 0;

		for (int i = 0; i < string.length(); i++) {
			if (string.charAt(i) == '_' || string.charAt(i) == '-') {
				res++;
			}
		}
		return res;
	}
}
