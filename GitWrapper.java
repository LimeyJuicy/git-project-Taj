import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GitWrapper {

    /**
     * Initializes a new Git repository.
     * Creates git/, git/objects/, git/index, and git/HEAD.
     */

    private static final gitRepository repo = new gitRepository();

    public static void init() throws IOException {
        try {
            repo.initializeGit();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * Stages a file for the next commit.
     * Throws IOException if the path does not exist or is a directory.
     * Does nothing if the file's content is already staged for that path.
     *
     * @param filePath The path to the file to stage.
     */
    public static void add(String filePath) throws IOException {
        repo.createBlob(filePath);
        gitRepository.writeInIndex(filePath);
    }

    /*
     * eates a commit captu g the current state of the index.
     * ilds trees, writes the commit file, updates HEAD.
     * 
     * mmit format:
     * tree: <tree_sha>
     * parent: <parent_sha> (omit line for initial commit)
     * author: <author>
     * date: <date>
     * message: <message>
     *
     * @param author The author name.
     * 
     * @param message The commit message.
     * 
     * @return The SHA-1 hash of the new commit.
     */
    public static String commit(String author, String message) throws IOException {
        try {
            return repo.commit(author, message);
        } catch (IOException e) {
            throw new IOException(e);
        }

    }

    /**
     * EXTRA CREDIT: Restores the working directory to the state of a specific
     * commit.
     * Traces the commit's root tree and recreates all files and directories.
     *
     * @param commitHash The SHA-1 hash of the commit to check out.
     */
    public static void checkout(String commitHash) {
        try {
            // first line of a commit is "tree: <hash>"
            restoreTree(Files.readAllLines(Path.of("git/objects/" + commitHash)).get(0).substring(6), "");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // Recursively rebuilds one tree. Each tree line is "blob|tree <sha> <name>".
    private static void restoreTree(String treeHash, String folder) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("git/objects/" + treeHash));
        for (String e : lines) {
            String type = e.substring(0, 4);
            String hash = e.substring(5, 45);
            String path = folder + e.substring(46);

            if (type.equals("tree")) {
                Files.createDirectories(Path.of(path));
                restoreTree(hash, path + "/");
            } else {
                // blobs have the file's original bytes, so copy them back out
                Files.write(Path.of(path), Files.readAllBytes(Path.of("git/objects/" + hash)));
            }
        }
    }
}