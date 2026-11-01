import java.sql.*;

public class database {

	public static class folder {
		public int id;
		public String path;

		public folder(int id, String path) {
			this.id = id;
			this.path = path;
		}
	}

	private static Connection getconnection() throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/filemonitor", "root", "1548");
	}

	public static void initialize() {
		try {
			Connection connection = getconnection();
			Statement statement = connection.createStatement();
			statement.execute("""
							CREATE TABLE IF NOT EXISTS folders (
						    id INT AUTO_INCREMENT PRIMARY KEY,
						    folder_path VARCHAR(512) NOT NULL UNIQUE
						);
					""");

			statement.execute("""
							CREATE TABLE IF NOT EXISTS events (
						    id INT AUTO_INCREMENT PRIMARY KEY,
						    folderid INT NOT NULL,
						    filename VARCHAR(255) NOT NULL,
						    event VARCHAR(50) NOT NULL,
						    time DATETIME DEFAULT CURRENT_TIMESTAMP,
						    FOREIGN KEY (folderid) REFERENCES folders(id)
						);
					""");
		} catch (SQLException e) {
			System.out.println("initialize Sql Error: " + e.getMessage());
		}
	}

	public static int addfolder(String path) throws SQLException {
		try {
			Connection connection = getconnection();
			String sql = "INSERT INTO folders (id,folder_path) VALUES (1,?) ON DUPLICATE KEY UPDATE folder_path = VALUES(folder_path);";

			PreparedStatement preparedstatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			preparedstatement.setString(1, path);
			preparedstatement.executeUpdate();

			ResultSet resultset = preparedstatement.getGeneratedKeys();
			deleteevents();
			if (resultset.next()) {
				return resultset.getInt(1);
			}
			throw new SQLException("insertion didin't happen");
		} catch (SQLException e) {
			System.out.println("addfolder Sql Error: " + e.getMessage());
			throw e;
		}
	}

	public static folder getfolder() {
		try {
			Connection connection = getconnection();
			String sql = "SELECT id,folder_path FROM folders;";
			PreparedStatement preparedstatement = connection.prepareStatement(sql);
			ResultSet result = preparedstatement.executeQuery();
			if (result.next()) {
				return new folder(result.getInt("id"), result.getString("folder_path"));
			}
			return null;
		} catch (SQLException e) {
			System.out.println("getfolder Sql Error: " + e.getMessage());
			return null;
		}
	}

	public static void updateevent(int folderid, String filename, String event) {
		try {
			Connection connection = getconnection();
			String sql = "INSERT INTO events (folderid,filename,event) VALUES (?,?,?)";
			PreparedStatement preparedstatement = connection.prepareStatement(sql);
			preparedstatement.setInt(1, folderid);
			preparedstatement.setString(2, filename);
			preparedstatement.setString(3, event);
			preparedstatement.executeUpdate();

		} catch (SQLException e) {
			System.out.println("updateevent Sql Error: " + e.getMessage());
		}
	}

	public static ResultSet getevent() {
		try {
			Connection connection = getconnection();
			// String sql = "SELECT filename,event,time FROM folders where folderid = ?;";
			String sql = "SELECT filename,event,time FROM events;";
			PreparedStatement preparedstatement = connection.prepareStatement(sql);
			// preparedstatement.setInt(1, folderid);
			return preparedstatement.executeQuery();
		} catch (SQLException e) {
			System.out.println("getfolder Sql Error: " + e.getMessage());
			return null;
		}
	}

	public static ResultSet getevent(int id) {
		try {
			Connection connection = getconnection();
			String sql = "SELECT filename,event,time FROM folders where id = ?;";
			PreparedStatement preparedstatement = connection.prepareStatement(sql);
			preparedstatement.setInt(1, id);
			return preparedstatement.executeQuery();
		} catch (SQLException e) {
			System.out.println("getfolder Sql Error: " + e.getMessage());
			return null;
		}
	}

	public static void deleteevents() throws SQLException {
		String sql = "DELETE FROM events";

		try (Connection connection = getconnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.executeUpdate();
		}
	}
}
