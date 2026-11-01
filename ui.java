import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;

import java.io.File;

public class ui {

	private static DefaultTableModel eventmodel;
	private static JLabel Folderpath;

	private static JPanel createtoppanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton choosefolder = new JButton("choose Folder");
		Folderpath = new JLabel("Select a folder");
		choosefolder.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int result = fileChooser.showOpenDialog(panel);
			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFolder = fileChooser.getSelectedFile();
				if (Main.addfolder(selectedFolder.toString())) {
					clearevents();
					updatefolderlabel("Monitering : " + selectedFolder.toString());
				}
			}
		});
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		panel.add(choosefolder);
		panel.add(Folderpath);
		return panel;
	}

	private static JPanel createcenterpanel() {
		JPanel panel = new JPanel(new BorderLayout());
		String[] columns = { "Filename", "Event", "Time" };

		eventmodel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		JTable table = new JTable(eventmodel);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		JScrollPane scrollPane = new JScrollPane(table);
		panel.add(scrollPane, BorderLayout.CENTER);
		return panel;
	}

	public static void addevent(String filename, String event, String time) {
		eventmodel.addRow(new Object[] { filename, event, time });
	}

	public static void updatefolderlabel(String text) {
		Folderpath.setText(text);
	}

	public static void clearevents() {
		eventmodel.setRowCount(0);
	}

	public static void initialize() {
		JFrame frame = new JFrame("File Activity moniter");
		frame.setSize(1200, 720);
		frame.setLayout(new BorderLayout());
		JPanel toppanel = createtoppanel();
		JPanel centerpanel = createcenterpanel();
		frame.add(toppanel, BorderLayout.NORTH);
		frame.add(centerpanel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
