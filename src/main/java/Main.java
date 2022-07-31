import moa.clusterers.clustream.WithKmeans;
import moa.clusterers.denstream.WithDBSCAN;
import moa.streams.clustering.FileStream;

public class Main {

	public static void main(String[] args) throws Exception { // executa a tarefa
        String dataset_name = "elecNormNew";
		String dataset_path = "datasets\\" + dataset_name + ".arff";

		FileStream file = new FileStream();
		file.arffFileOption.setValue(dataset_path);
		file.restart();

		int kNearestClusters = 10;

		// CluStream as clusterer
		WithKmeans cluStream = new WithKmeans();
		cluStream.resetLearningImpl();
		
		DataClustering task = new DataClustering(file,cluStream,kNearestClusters);
		task.run();
	}
}