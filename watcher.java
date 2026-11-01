
import java.nio.file.*;

public class watcher {

	private static String eventToString(WatchEvent.Kind<?> kind) {

		if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
			return "CREATE";
		} else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
			return "MODIFY";
		} else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
			return "DELETE";
		}

		return "UNKNOWN";
	}

	private static WatchService currentWatcher = null;

	public static void watch(String path, int folderid) {
		try {
			if (currentWatcher != null) {
				currentWatcher.close();
			}

			WatchService ws = FileSystems.getDefault().newWatchService();
			currentWatcher = ws;

			Path folder = Paths.get(path);

			folder.register(
					ws,
					StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.ENTRY_DELETE);

			while (true) {
				WatchKey key = ws.take();

				for (WatchEvent<?> event : key.pollEvents()) {
					Path filename = (Path) event.context();

					Main.updateevent(
							folderid,
							filename.toString(),
							eventToString(event.kind()));
				}

				if (!key.reset())
					break;
			}

		} catch (ClosedWatchServiceException e) {
			System.out.println("Previous watcher stopped");

		} catch (Exception e) {
			System.out.println("Watcher error: " + e.getMessage());
		}
	}

}
