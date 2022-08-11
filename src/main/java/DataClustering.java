import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.yahoo.labs.samoa.instances.Instance;

import org.apache.commons.lang3.tuple.Pair;

import moa.gui.BatchCmd;
import moa.gui.visualization.DataPoint;
import moa.streams.clustering.ClusterEvent;
import moa.streams.clustering.ClusteringStream;
import moa.streams.clustering.RandomRBFGeneratorEvents;
import moa.clusterers.AbstractClusterer;
import moa.evaluation.MeasureCollection;

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

	private MeasureCollection[] measures;

    public DataClustering(ClusteringStream stream, AbstractClusterer clusterer, int kNearest, 
			boolean[] measureCollection, int totalInstances){	
		
		this.stream = stream;
		this.clusterer = clusterer;

		if(totalInstances == -1)
			this.totalInstances = Integer.MAX_VALUE;
		else
			this.totalInstances = totalInstances;

		nearestClusters = new kNearestNeighbors(kNearest);
		pythonProcess = new CommunityDetection();
		
		stream.prepareForUse();
		clusterer.prepareForUse();

		measures = ClusteringMeasures.getMeasures(measureCollection);
    }
    
    public void run() throws Exception{
        ArrayList<DataPoint> pointBuffer = new ArrayList<DataPoint>();
		int m_timestamp = 0;
		int decayHorizon = stream.getDecayHorizon();

		double decay_threshold = stream.getDecayThreshold();
		double decay_rate = (-1*Math.log(decay_threshold)/decayHorizon);

		int counter = decayHorizon;

		while(m_timestamp < totalInstances && stream.hasMoreInstances()){
			m_timestamp++;
			counter--;

			Instance next = stream.nextInstance().getData();
			DataPoint point = new DataPoint(next,m_timestamp);
			pointBuffer.add(point);

			Instance trainInst = new DenseInstance(point);
			
			trainInst.deleteAttributeAt(point.classIndex());

			clusterer.trainOnInstanceImpl(trainInst);

			if(counter <= 0){
				for(DataPoint p:pointBuffer)
					p.updateWeight(m_timestamp, decay_rate);

				Clustering gtClustering;
				Clustering clustering = null;

				gtClustering = new Clustering(pointBuffer);
				
				if(clusterer.implementsMicroClusterer()){
					Clustering microClustering;	
					microClustering = clusterer.getMicroClusteringResult();
					
					ArrayList<Pair<Integer,Integer>> nearestResult;
					nearestClusters.find(microClustering);
					nearestResult = nearestClusters.getNearestNeighbors();
	
					ArrayList<List<String>> communities;
					pythonProcess.detectCommunities(nearestResult.toString());
					communities = pythonProcess.getCommunities();

					clustering = macroClustering(communities,microClustering);

					/*Scanner xu = new Scanner(System.in);
					String ue = xu.nextLine();*/

					if(clustering == null && microClustering != null){
						// Depois testar se entra aqui alguma vez
						clustering = moa.clusterers.KMeans.gaussianMeans(gtClustering, microClustering);
					}
				}

				//evaluate
				for(int i=0; i<measures.length; i++) {
					try {
						measures[i].evaluateClusteringPerformance(clustering, gtClustering, pointBuffer);
					} 
					catch (Exception ex) { ex.printStackTrace(); }
				}

				pointBuffer.clear();
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

	public void exportResultCSV(String filepath){
		// it's not a instance of RandomRBFGeneratorEvents, for now
		ArrayList<ClusterEvent> clusterEvents = null;
		
		int horizon = stream.decayHorizonOption.getValue();
		
		BatchCmd.exportCSV(filepath, clusterEvents, measures, horizon);
	}
}
