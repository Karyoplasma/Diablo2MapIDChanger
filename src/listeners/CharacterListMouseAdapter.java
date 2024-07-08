package listeners;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import core.D2CharFile;
import gui.Diablo2MapChanger;

public class CharacterListMouseAdapter extends MouseAdapter {

	private Diablo2MapChanger gui;

	public CharacterListMouseAdapter(Diablo2MapChanger gui) {
		this.gui = gui;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			int index = gui.getListCharacterList().locationToIndex(e.getPoint());
			if (index != -1) {
				gui.getListCharacterList().setSelectedIndex(index);
				gui.getContextMenuTitleLabel()
						.setText(gui.getListCharacterList().getModel().getElementAt(index).toString());
				this.updateTransferMenu(index);
				gui.getContextMenu().show(gui.getListCharacterList(), e.getX(), e.getY());
			}
		}
	}

	private void updateTransferMenu(int selectedIndex) {
		gui.getTransferMenu().removeAll();
		D2CharFile selectedItem = gui.getListCharacterList().getModel().getElementAt(selectedIndex);
		for (int i = 0; i < gui.getListCharacterList().getModel().getSize(); i++) {
			if (i != selectedIndex) {
				JMenuItem transferItem = new JMenuItem(
						gui.getListCharacterList().getModel().getElementAt(i).toString());
				transferItem.addActionListener(new TransferMenuActionListener(gui, selectedItem,
						gui.getListCharacterList().getModel().getElementAt(i)));
				gui.getTransferMenu().add(transferItem);
			}
		}
	}

}
