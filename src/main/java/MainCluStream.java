import moa.clusterers.clustream.WithKmeans;
import moa.clusterers.denstream.WithDBSCAN;
import moa.streams.clustering.FileStream;

import moa.clusterers.AbstractClusterer;

public class MainCluStream {
	private FileStream file;
	private AbstractClusterer clusterer;

	public MainCluStream(AbstractClusterer clusterer, int numInstances, String stream, int kNearestClusters) throws Exception {
		this.clusterer = clusterer;
		this.clusterer.resetLearningImpl();
		
		// define o arquivo arff para stream
		file = new FileStream();
		file.arffFileOption.setValue(stream);

		file.restart();

		
		

		//clusterer.maxNumKernelsOption.setValue(50);

        // executa a tarefa de agrupar os dados
        DataClustering task = new DataClustering(file,clusterer,numInstances,kNearestClusters);
        task.run();
	}

	public static void main(String[] args) throws Exception {
        // Nome do arquivo
        String file = "elecNormNew";
		
		// Arquivo de entrada (caminho)
		String input = "datasets\\" + file + ".arff";

		int kNearestClusters = 10;

		WithKmeans cluStream = new WithKmeans();

		new MainCluStream(cluStream,-1,input,kNearestClusters);

	}
}