import moa.cluster.Cluster;
import moa.cluster.Clustering;

import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

import org.jgrapht.*;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.nio.matrix.MatrixExporter;

public class Knn {
    public int k_Nearest;
    public Clustering microClustering;

    private Graph<Integer, DefaultEdge> microClustersNetwork;

    public Knn(int k, Clustering microClustering){
        k_Nearest = k;
        this.microClustering = microClustering;

        microClustersNetwork = new SimpleDirectedGraph<>(DefaultEdge.class);

        addVertices();
        buildNetwork();
    }

    private void addVertices(){
        int clusters_number;

        clusters_number = microClustering.size();

        for(int clusterIndex=0; clusterIndex<clusters_number; clusterIndex++){
            microClustersNetwork.addVertex(clusterIndex);
        }
    }

    private void buildNetwork(){
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
            connectVertices(row,indexDistance);
            
            indexDistance.clear();
        }

        MatrixExporter<Integer,DefaultEdge> x;
        x = new MatrixExporter<>(MatrixExporter.Format.SPARSE_ADJACENCY_MATRIX, v -> v.toString());

        File file = new File("teste.txt");
        x.exportGraph(microClustersNetwork,file);

        Scanner teste = new Scanner(System.in);
        String opa = teste.nextLine();
    }

    public double euclideanDistance(double[] pointA, double [] pointB) {
		double distance = 0.0;
		for (int i = 0; i < pointA.length; i++) {
			double d = pointA[i] - pointB[i];
			distance += d * d;
		}
		
        return Math.sqrt(distance);
	}

    
    // connect nearest neighbours
    private void connectVertices(int source, ArrayList<Pair<Integer,Double>> Neighbours){
        int target;
        
        for(int i=0; i<k_Nearest; i++){
            target = Neighbours.get(i).getLeft();
            microClustersNetwork.addEdge(source,target);
        }
    }

    public static void main(String[] args){
    }
}
