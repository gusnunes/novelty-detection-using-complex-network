import java.util.Scanner;

import moa.clusterers.clustream.WithKmeans;
import moa.clusterers.denstream.WithDBSCAN;
import moa.streams.clustering.FileStream;
import moa.streams.clustering.RandomRBFGeneratorEvents;

public class Main {

	public static void main(String[] args) throws Exception {
		
		String file = "_artificial";
        int totalInstances = 100000;
		
		boolean[] measureCollection = {false,true,false,true,true,true,true,true};

		/*String file = "poker";
        String data_set = "datasets\\" + file + ".arff";
        
        FileStream stream = new FileStream();
		stream.arffFileOption.setValue(data_set);
		stream.restart();
        
        int totalInstances = -1;*/
		
		for(int exec=1; exec<=10; exec++){
			for(int kNearest=1; kNearest<=20; kNearest++){
				WithKmeans cluStream = new WithKmeans();
				cluStream.resetLearningImpl();

				RandomRBFGeneratorEvents stream = new RandomRBFGeneratorEvents();
				stream.eventFrequencyOption.setValue(10000);
				stream.eventDeleteCreateOption.setValue(true);
				stream.eventMergeSplitOption.setValue(true);

				DataClustering task = new DataClustering(stream,cluStream,kNearest,measureCollection,totalInstances);
				task.run();
				
				String result_path = "results\\CluStream_exec="+ exec + file + "_infomap" + "_k=" + kNearest + ".csv";
				task.exportResultCSV(result_path);
			}
		}
	}
}