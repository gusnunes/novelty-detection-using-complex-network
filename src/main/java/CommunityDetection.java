import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.lang.ProcessBuilder;
import java.lang.Process;

public class CommunityDetection {
    public static final String SCRIPT_NAME = "teste.py";
    private ArrayList<List<String>> communities;

    public void detectCommunities(String edgeList) throws Exception {
        List<String> command = new ArrayList<String>();
        command.add("python");
        command.add(scriptPath(SCRIPT_NAME));

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        
        Process process = processBuilder.start();
        writeProcessInput(process.getOutputStream(),edgeList);
        readProcessOutput(process.getInputStream());

        /*assertThat("Results should not be empty", results, is(not(empty())));
        assertThat("Results should contain output of script: ", results, hasItem(
        containsString("Hello Baeldung Readers!!")));*/
        // colocar um except aqui, caso nao retorne 0 de sucesso
        int exitCode = process.waitFor();
        //System.out.println(exitCode);

        //assertEquals("No errors should be detected", 0, exitCode);
    }

    private String scriptPath(String filename){
        File file = new File("python_scripts\\" + filename);
        return file.getAbsolutePath();
    }

    private void writeProcessInput(OutputStream outputStream, String edgeList) throws IOException {
        try (BufferedWriter input = new BufferedWriter(new OutputStreamWriter(outputStream))){
            input.write(edgeList);
            input.flush();
        }
    }

    private void readProcessOutput(InputStream inputStream) throws IOException {
        communities = new ArrayList<>();

        String community;
        String[] vertexIndex;
        List<String> vertexIndexList;
        
        try (BufferedReader output = new BufferedReader(new InputStreamReader(inputStream))){
            while ((community = output.readLine()) != null) {
                System.out.println(community); 
                vertexIndex = community.split(",");
                vertexIndexList = Arrays.asList(vertexIndex); 
                communities.add(vertexIndexList);
            }
        }
    }

    public ArrayList<List<String>> getCommunities(){
        return communities;
    }
}
