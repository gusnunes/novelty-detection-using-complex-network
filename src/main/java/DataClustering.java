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

public class DataClustering {
	private int totalInstances;

	private ClusteringStream stream;
    private AbstractClusterer clusterer;

	private kNearestNeighbors nearestClusters;
	private CommunityDetection pythonProcess;

    public DataClustering(ClusteringStream stream, AbstractClusterer clusterer, int kNearest){	
		this.stream = stream;
		this.clusterer = clusterer;

		nearestClusters = new kNearestNeighbors(kNearest);
		pythonProcess = new CommunityDetection();

		totalInstances = Integer.MAX_VALUE;
		
		stream.prepareForUse();
		clusterer.prepareForUse();
    }
    
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

					/*Scanner xu = new Scanner(System.in);
					String ue = xu.nextLine();*/

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

	private Clustering macroClustering(ArrayList<List<String>> communities, Clustering microClustering){
		int communitiesNumber = communities.size();
		
		// convert community dectection result to CFClusters
		CFCluster[] converted;
		converted = new CFCluster[communitiesNumber];
		
		for(int i=0; i<communitiesNumber; i++){
			List<String> community = communities.get(i);

			for(String microCluster_index: community){
				int index = Integer.parseInt(microCluster_index);
				Cluster microCluster = microClustering.get(index);

				if(microCluster instanceof CFCluster){
					if(converted[i] == null)
						converted[i] = (CFCluster)microCluster.copy();
					else
						converted[i].add((CFCluster)microCluster);
				}
				else {
					String x = "Unsupported Cluster Type:";
					String y = ". Cluster needs to extend moa.cluster.CFCluster";
					System.out.println(x + microClustering.get(i).getClass() + y);
				}
			}
		}

		return new Clustering(converted);
	}
}
