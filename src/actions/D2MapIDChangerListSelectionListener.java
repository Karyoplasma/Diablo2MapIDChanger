package actions;

import java.io.IOException;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import core.D2CharFile;
import gui.Diablo2MapChanger;

public class D2MapIDChangerListSelectionListener implements ListSelectionListener {

	private Diablo2MapChanger gui;

	public D2MapIDChangerListSelectionListener(Diablo2MapChanger diablo2MapChanger) {
		super();
		this.gui = diablo2MapChanger;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		@SuppressWarnings("unchecked")
		JList<D2CharFile> source = (JList<D2CharFile>) e.getSource();
		if (!e.getValueIsAdjusting()) {
			if (source.getSelectedValue() == null) {
				this.gui.getTextAreaCharacterSummary().setText("");
				return;
			}
			try {
				this.gui.getTextAreaCharacterSummary().setText(source.getSelectedValue().getCharacterSummary());
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this.gui.getFrame(), "Could not parse character data.", "Data error",
						JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}
	}

}
