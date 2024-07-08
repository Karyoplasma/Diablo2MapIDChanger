package actions;

import gui.Diablo2MapChanger;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

public class CopyToClipboardMenuAction extends AbstractAction {

	private static final long serialVersionUID = -5079483960190701934L;
	private Diablo2MapChanger gui;

	public CopyToClipboardMenuAction(Diablo2MapChanger gui) {
		putValue(Action.NAME, "Copy map seed to clipboard");
		this.gui = gui;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		StringSelection mapID = new StringSelection(
				Integer.toString(gui.getListCharacterList().getSelectedValue().getMapIDReverse()));
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(mapID, null);
	}

}
