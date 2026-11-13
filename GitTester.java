import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GitTester {
    public static void main(String[] args) throws IOException {
        GitWrapper.init();
        GitWrapper.add("myProgram/hello.txt");
        GitWrapper.add("myProgram/inner/world.txt");
        String first = GitWrapper.commit("John Doe", "Initial commit");
        System.out.println("Commit 1: " + first);

        // second commit should include a parent line
        Files.writeString(Path.of("myProgram/hello.txt"), "hello again");
        GitWrapper.add("myProgram/hello.txt");
        String second = GitWrapper.commit("John Doe", "Edit hello");
        System.out.println("Commit 2: " + second);
        System.out.println(Files.readString(Path.of("git/objects/" + second)));

        // checkout should bring back the old text
        GitWrapper.checkout(first);
        System.out.println("After checkout: " + Files.readString(Path.of("myProgram/hello.txt")));
    }
}