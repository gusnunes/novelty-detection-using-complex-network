import moa.cluster.Cluster;
import moa.cluster.Clustering;

import org.apache.commons.lang3.tuple.Pair;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

import org.jgrapht.*;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class Knn {
    public int k_Nearest;
    public Clustering microClustering;

    private Graph<Cluster, DefaultEdge> microClustersNetwork;

    public Knn(int k, Clustering microClustering){
        k_Nearest = k;
        this.microClustering = microClustering;

        microClustersNetwork = new SimpleGraph<>(DefaultEdge.class);

        addVertices();
        geraRede();
    }

    public void geraRede(){
        Cluster microClusterA, microClusterB;
        double[] centerA, centerB;
        double[][] distance_matrix;
        
        double distance;
        int clusters_number;
        
        // for each micro-cluster
        // (index,distance) between all others micro-clusters
        ArrayList<Pair<Integer,Double>> indexDistance = new ArrayList<>();
        
        clusters_number = microClustering.size();
        distance_matrix = new double[clusters_number][clusters_number];
        
        // build the distance matrix between micro-clusters
        for(int row=0; row<clusters_number; row++){
            microClusterA = microClustering.get(row);
            
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
            connectNearestNeighbours(microClusterA, indexDistance);
            
            indexDistance.clear();
        }
    }

    private void addVertices(){
        for(Cluster microCluster: microClustering.getClustering()){
            microClustersNetwork.addVertex(microCluster);
        }
    }

    private void connectNearestNeighbours(Cluster source, ArrayList<Pair<Integer,Double>> Neighbours){
        int index_target;
        Cluster target;
        
        for(int i=0; i<k_Nearest; i++){
            index_target = Neighbours.get(i).getLeft();

            //System.out.println(index_target);
            
            target = microClustering.get(index_target);
            microClustersNetwork.addEdge(source, target);
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
