package gui;

import java.awt.EventQueue;
import javax.swing.JFrame;
import net.miginfocom.swing.MigLayout;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

import actions.CopyToClipboardMenuAction;
import actions.EditButtonAction;
import actions.SelectButtonAction;
import core.D2CharFile;
import core.D2SaveFileDirectoryBrowser;
import core.IniFileReader;
import core.IniFileWriter;
import listeners.CharacterListMouseAdapter;
import listeners.D2MapIDChangerListSelectionListener;

import javax.swing.JTextArea;
import javax.swing.JLabel;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class Diablo2MapChanger {

	private JFrame frmDsMapidChanger;
	private JTextArea textAreaCharacterSummary;
	private JList<D2CharFile> listCharacterList;
	private JLabel lblSavePath, contextMenuTitle;
	private JButton btnSelect;
	private String savePathString;
	private boolean autoBackup;
	private JCheckBox chckbxAutoBackup;
	private JPopupMenu contextMenu;
	private JMenu transferMenu;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Diablo2MapChanger window = new Diablo2MapChanger();
					window.frmDsMapidChanger.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Diablo2MapChanger() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		this.readIni();

		frmDsMapidChanger = new JFrame();
		frmDsMapidChanger.setTitle("D2S MapID Changer");
		frmDsMapidChanger.setBounds(100, 100, 600, 300);
		frmDsMapidChanger.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDsMapidChanger.getContentPane().setLayout(new MigLayout("", "[][grow][200px:n,fill]", "[][][grow][][]"));

		contextMenu = new JPopupMenu();
		contextMenu.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contextMenuTitle = new JLabel();
		contextMenuTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
		contextMenuTitle.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		contextMenu.add(contextMenuTitle);
		contextMenu.addSeparator();
		JMenuItem menuItemEdit = new JMenuItem(new CopyToClipboardMenuAction(this));
		transferMenu = new JMenu("Transfer map to:");
		contextMenu.add(menuItemEdit);
		contextMenu.add(transferMenu);

		JScrollPane scrollPaneCharList = new JScrollPane();
		frmDsMapidChanger.getContentPane().add(scrollPaneCharList, "cell 0 0 2 4,grow");

		listCharacterList = new JList<D2CharFile>(new DefaultListModel<D2CharFile>());
		listCharacterList.addListSelectionListener(new D2MapIDChangerListSelectionListener(this));
		listCharacterList.addMouseListener(new CharacterListMouseAdapter(this));
		listCharacterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPaneCharList.setViewportView(listCharacterList);

		JScrollPane scrollPaneSummary = new JScrollPane();
		frmDsMapidChanger.getContentPane().add(scrollPaneSummary, "cell 2 2,grow");

		textAreaCharacterSummary = new JTextArea();
		textAreaCharacterSummary.setEditable(false);
		scrollPaneSummary.setViewportView(textAreaCharacterSummary);

		JLabel lblSavePathIndicator = new JLabel("Save Path:");
		lblSavePathIndicator.setFont(new Font("Tahoma", Font.PLAIN, 14));
		frmDsMapidChanger.getContentPane().add(lblSavePathIndicator, "cell 0 4");

		lblSavePath = new JLabel(this.savePathString);
		lblSavePath.setFont(new Font("Tahoma", Font.BOLD, 14));
		frmDsMapidChanger.getContentPane().add(lblSavePath, "cell 1 4");

		btnSelect = new JButton(new SelectButtonAction(this));
		btnSelect.setFont(new Font("Tahoma", Font.PLAIN, 14));
		frmDsMapidChanger.getContentPane().add(btnSelect, "flowx,cell 2 4");

		chckbxAutoBackup = new JCheckBox("Auto Backup");
		chckbxAutoBackup.setFont(new Font("Tahoma", Font.PLAIN, 14));
		chckbxAutoBackup.setSelected(this.autoBackup);
		frmDsMapidChanger.getContentPane().add(chckbxAutoBackup, "cell 2 4");

		JPanel panelEditButton = new JPanel();
		frmDsMapidChanger.getContentPane().add(panelEditButton, "cell 2 3");

		JButton btnEdit = new JButton(new EditButtonAction(this));
		panelEditButton.add(btnEdit);
		btnEdit.setFont(new Font("Tahoma", Font.PLAIN, 14));

		if (!(this.savePathString == null) && !this.savePathString.isEmpty()) {
			this.loadSaveDirectory();
		}
	}

	private void loadSaveDirectory() {
		List<File> charFiles;
		try {
			charFiles = D2SaveFileDirectoryBrowser.returnCharFileList(new File(this.savePathString));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this.frmDsMapidChanger,
					"Error when loading default directory. Please change the directory.", "Read error",
					JOptionPane.ERROR_MESSAGE);
			charFiles = new ArrayList<File>();
			e.printStackTrace();
		}
		for (File file : charFiles) {
			try {
				D2CharFile charFile = new D2CharFile(file);
				((DefaultListModel<D2CharFile>) this.listCharacterList.getModel()).addElement(charFile);

			} catch (IOException e) {
				JOptionPane.showMessageDialog(this.frmDsMapidChanger, "Error when parsing character files.",
						"Parse error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
		this.listCharacterList.setSelectedIndex(0);
	}

	private void readIni() {
		IniFileReader iniReader = new IniFileReader(new File("D2MapChanger.ini"));
		if (iniReader.getSettings() != null) {
			this.savePathString = iniReader.getSettings().get("SavePath");
			if (iniReader.getSettings().get("AutoBackup").equals("0")) {
				this.autoBackup = false;
			} else {
				this.autoBackup = true;
			}
		} else {
			if (!IniFileWriter.createDefaultIniFile()) {
				JOptionPane.showMessageDialog(this.frmDsMapidChanger, "Default ini file creation failed.", "Warning",
						JOptionPane.WARNING_MESSAGE);
			}
			this.savePathString = "";
			this.autoBackup = true;
		}
	}

	public String getSavePathString() {
		return this.savePathString;
	}

	public JFrame getFrame() {
		return this.frmDsMapidChanger;
	}

	public void setSavePath(String savePathString) {
		this.savePathString = savePathString;
	}

	public JLabel getLblSavePath() {
		return lblSavePath;
	}

	public JTextArea getTextAreaCharacterSummary() {
		return textAreaCharacterSummary;
	}

	public JList<D2CharFile> getListCharacterList() {
		return listCharacterList;
	}

	public JCheckBox getChckbxAutoBackup() {
		return chckbxAutoBackup;
	}

	public JLabel getContextMenuTitleLabel() {
		return contextMenuTitle;
	}

	public JPopupMenu getContextMenu() {
		return contextMenu;
	}

	public JMenu getTransferMenu() {
		return transferMenu;
	}

}
