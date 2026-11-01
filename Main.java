import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.SwingUtilities;

public class Main {
	public static boolean addfolder(String path) {
		try {
			int folderid = database.addfolder(path);
			Thread watcherThread = new Thread(() -> {
				watcher.watch(path, folderid);
			});

			watcherThread.setDaemon(true);
			watcherThread.start();
			return true;
		} catch (SQLException e) {
			System.out.println("addfolder Main Sql Error: " + e.getMessage());
			return false;
		}
	}

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

	public static void updateevent(int folderid, String filename, String event) {
		database.updateevent(folderid, filename, event);
		String time = LocalDateTime.now().format(formatter);

		SwingUtilities.invokeLater(() -> {
			ui.addevent(filename, event, time);
		});
	}

	// public static void loadevents(int folderid) {
	public static void loadevents() throws SQLException {
		ResultSet resultset = database.getevent();
		while (resultset.next()) {
			String filename = resultset.getString("filename");
			String event = resultset.getString("event");
			String time = resultset.getTimestamp("time").toLocalDateTime()
					.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
			ui.addevent(filename, event, time);
		}
	}

	public static void main(String args[]) {
		database.initialize();
		ui.initialize();

		database.folder initail = database.getfolder();
		if (initail != null) {
			ui.updatefolderlabel("watching : " + initail.path);
			new Thread(() -> watcher.watch(initail.path, initail.id)).start();
			try {
				loadevents();
			} catch (SQLException e) {
				System.err.println("load events error" + e.getMessage());
			}
		}
	}
}
