import java.io.IOException;
import java.net.URISyntaxException;

import moa.clusterers.clustream.WithKmeans;
import moa.streams.clustering.FileStream;

public class Main {

	public void run(int numInstances, String stream, int kNearestClusters) throws IOException {
		// setar o arquivo arff para stream
		FileStream file = new FileStream();
		file.arffFileOption.setValue(stream);

		file.restart();

		WithKmeans clusterer = new WithKmeans();
		clusterer.resetLearningImpl();

		clusterer.maxNumKernelsOption.setValue(5);

        // executa a tarefa
        Teste oxe = new Teste(file,clusterer,numInstances,kNearestClusters);
        oxe.run();
	}

	public static void main(String[] args) throws IOException, URISyntaxException {
		Main exp = new Main();

        // Nome do arquivo
        String file = "elecNormNew";
		
		// Arquivo de entrada (caminho)
		String input = "datasets\\" + file + ".arff";

		int kNearestClusters = 3;
	
		exp.run(-1,input,kNearestClusters);
	}
}