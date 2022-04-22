import moa.cluster.Cluster;
import moa.cluster.Clustering;

import org.apache.commons.lang3.tuple.Pair;
import java.util.ArrayList;
import java.util.Comparator;

import org.jgrapht.*;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class Knn {
    public int k;
    public Clustering microClustering;

    public Knn(int k, Clustering microClustering){
        this.k = k;
        this.microClustering = microClustering;
    }

    public void geraRede(){
        Cluster microClusterA, microClusterB;
        double[] centerA, centerB;
        double[][] distance_matrix;
        
        double distance;
        int clusters_number;

        Graph<Cluster, DefaultEdge> microClustersNetwork;
        microClustersNetwork = new SimpleGraph<>(DefaultEdge.class);
        
        // for each micro-cluster
        // (index,distance) between all others micro-clusters
        ArrayList<Pair<Integer,Double>> indexDistance = new ArrayList<>();
        
        clusters_number = microClustering.size();
        distance_matrix = new double[clusters_number][clusters_number];
        
        // build the distance matrix between micro-clusters
        for(int row=0; row<clusters_number; row++){
            microClusterA = microClustering.get(row);

            // vertex for each micro-cluster
            microClustersNetwork.addVertex(microClusterA);
            
            for(int column=0; column<clusters_number; column++){
                if(row != column){
                    microClusterB = microClustering.get(column);
                    
                    if(column > row){
                        centerA = microClusterA.getCenter();
                        centerB = microClusterB.getCenter();

                        distance = euclideanDistance(centerA,centerB);
                        distance_matrix[row][column] = distance;
                    }
                    else { 
                        distance = distance_matrix[column][row]; 
                    }

                    indexDistance.add(Pair.of(column,distance));
                }
            }

            indexDistance.sort(Comparator.comparing(Pair::getRight));
            indexDistance.clear();
        }
    }

    private static double euclideanDistance(double[] pointA, double [] pointB) {
		double distance = 0.0;
		for (int i = 0; i < pointA.length; i++) {
			double d = pointA[i] - pointB[i];
			distance += d * d;
		}
		return Math.sqrt(distance);
	}

    public static void main(String[] args){
    }
}
