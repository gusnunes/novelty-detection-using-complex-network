import java.util.ArrayList;
import java.util.Scanner;

import com.yahoo.labs.samoa.instances.Instance;
import moa.gui.visualization.DataPoint;
import moa.streams.clustering.ClusteringStream;
import moa.clusterers.AbstractClusterer;
import com.yahoo.labs.samoa.instances.DenseInstance;
import moa.cluster.Clustering;

public class Teste{
	private ClusteringStream stream;
    private AbstractClusterer clusterer;
    
    private int totalInstances;
	private NearestClusters nearestClusters;

    public Teste(ClusteringStream stream, AbstractClusterer clusterer, int totalInstances, int kNearest){
        this.stream = stream;
        this.clusterer = clusterer;

		nearestClusters = new NearestClusters(kNearest);

        if(totalInstances == -1)
			this.totalInstances = Integer.MAX_VALUE;
		else
			this.totalInstances = totalInstances;
        
        stream.prepareForUse();
        clusterer.prepareForUse();
    }
    
    // Aqui será o metodo que gera os micro-grupos
    // O KNN vai pegar esse resultado pra gerar a rede
    public void run() throws Exception{
        ArrayList<DataPoint> pointBuffer0 = new ArrayList<DataPoint>();
		int m_timestamp = 0;
		int decayHorizon = stream.getDecayHorizon();

		double decay_threshold = stream.getDecayThreshold();
		double decay_rate = (-1*Math.log(decay_threshold)/decayHorizon);

		int counter = decayHorizon;

		while(m_timestamp < totalInstances && stream.hasMoreInstances()){
			m_timestamp++;
			counter--;

			Instance next = stream.nextInstance().getData();
			DataPoint point0 = new DataPoint(next,m_timestamp);
			pointBuffer0.add(point0);

			Instance traininst0 = new DenseInstance(point0);
			
			traininst0.deleteAttributeAt(point0.classIndex());

			clusterer.trainOnInstanceImpl(traininst0);

			if(counter <= 0){
				for(DataPoint p:pointBuffer0)
					p.updateWeight(m_timestamp, decay_rate);

				Clustering gtClustering0;
				Clustering clustering0 = null;

				gtClustering0 = new Clustering(pointBuffer0);

				clustering0 = clusterer.getClusteringResult();
				
				if(clusterer.implementsMicroClusterer()){

					Clustering microC = clusterer.getMicroClusteringResult();

					nearestClusters.findNearestClusters(microC);
					nearestClusters.exportTXT();

					// Aqui tem que vir o codigo python agora
					PythonProcess uai = new PythonProcess(NearestClusters.OUT_FILE);
					uai.givenPythonScript_whenPythonProcessInvoked_thenSuccess();

					Scanner x = new Scanner(System.in);
					String ue = x.nextLine();

					if(clusterer.evaluateMicroClusteringOption.isSet()){
						clustering0 = microC;
					}
					else{
						if(clustering0 == null && microC != null)
							// metodo de formacao de rede(KNN)
							// metodo de deteccao de comunidade
							clustering0 = moa.clusterers.KMeans.gaussianMeans(gtClustering0, microC);
					}
				}

				pointBuffer0.clear();
				counter = decayHorizon;
            }
        }
    }    
}
