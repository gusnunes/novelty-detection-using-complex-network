import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import java.lang.ProcessBuilder;
import java.lang.Process;

public class PythonProcess {
    public static final String IN_FILE = "teste.py";

    public void givenPythonScript_whenPythonProcessInvoked_thenSuccess() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder("python", scriptPath(IN_FILE));
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        List<String> results = readProcessOutput(process.getInputStream());

        /*assertThat("Results should not be empty", results, is(not(empty())));
        assertThat("Results should contain output of script: ", results, hasItem(
        containsString("Hello Baeldung Readers!!")));*/

        int exitCode = process.waitFor();
        System.out.println(exitCode);
        System.out.println(results);

        //assertEquals("No errors should be detected", 0, exitCode);
    }

    private List<String> readProcessOutput(InputStream inputStream) throws IOException {
        try (BufferedReader output = new BufferedReader(new InputStreamReader(inputStream))) {
            return output.lines()
                .collect(Collectors.toList());
        }
    }

    private String scriptPath(String filename) {
        File file = new File("python_scripts\\" + filename);
        return file.getAbsolutePath();
    }

    public static void main(String[] args) throws Exception {
        PythonProcess teste = new PythonProcess();
        teste.givenPythonScript_whenPythonProcessInvoked_thenSuccess();
    }
}
