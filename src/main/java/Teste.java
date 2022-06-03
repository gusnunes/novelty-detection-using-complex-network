import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.yahoo.labs.samoa.instances.Instance;

import org.apache.commons.lang3.tuple.Pair;

import moa.gui.visualization.DataPoint;
import moa.streams.clustering.ClusteringStream;
import moa.clusterers.AbstractClusterer;
import com.yahoo.labs.samoa.instances.DenseInstance;

import moa.cluster.CFCluster;
import moa.cluster.Cluster;
import moa.cluster.Clustering;

public class Teste {
	private int totalInstances;

	private ClusteringStream stream;
    private AbstractClusterer clusterer;

	private kNearestNeighbors nearestClusters;
	private CommunityDetection pythonProcess;

    public Teste(ClusteringStream stream, AbstractClusterer clusterer, int totalInstances, int kNearest){	
		this.stream = stream;
		this.clusterer = clusterer;

		nearestClusters = new kNearestNeighbors(kNearest);
		pythonProcess = new CommunityDetection();

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
					Clustering microClustering;	
					microClustering = clusterer.getMicroClusteringResult();
					
					ArrayList<Pair<Integer,Integer>> nearestResult;
					nearestClusters.find(microClustering);
					nearestResult = nearestClusters.getNearestNeighbors();
	
					ArrayList<List<String>> communities;
					pythonProcess.detectCommunities(nearestResult.toString());
					communities = pythonProcess.getCommunities();

					//Clustering opa = macroClustering(communities,microClustering);

					Scanner xu = new Scanner(System.in);
					String ue = xu.nextLine();

					if(clusterer.evaluateMicroClusteringOption.isSet()){
						clustering0 = microClustering;
					}
					else{
						if(clustering0 == null && microClustering != null)
							// metodo de formacao de rede(KNN)
							// metodo de deteccao de comunidade
							clustering0 = moa.clusterers.KMeans.gaussianMeans(gtClustering0, microClustering);
					}
				}

				pointBuffer0.clear();
				counter = decayHorizon;
            }
        }
    }
}
