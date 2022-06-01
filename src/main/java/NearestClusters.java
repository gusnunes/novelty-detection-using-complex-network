import moa.cluster.Cluster;
import moa.cluster.Clustering;

import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class NearestClusters {
    private int kNearest;
    
    // (source,target) -- clusters index pair
    // target is one of the k nearest cluster of source
    private ArrayList<Pair<Integer,Integer>> nearestClusters;
    
    // used in python code to generate network
    public static final String OUT_FILE = "edge_list.txt";

    public NearestClusters(int kNearest){
        this.kNearest = kNearest;
    }

    public void findNearestClusters(Clustering microClustering){
        Cluster microClusterA, microClusterB;
        double[] centerA, centerB;
        double[][] distance_matrix;
        
        double distance;
        int clusters_number;

        nearestClusters = new ArrayList<>();
        
        // for each micro-cluster
        // (index,distance) between all others micro-clusters
        ArrayList<Pair<Integer,Double>> indexDistance = new ArrayList<>();
        
        clusters_number = microClustering.size();
        distance_matrix = new double[clusters_number][clusters_number];
        
        // build the distance matrix between micro-clusters
        for(Integer row=0; row<clusters_number; row++){
            microClusterA = microClustering.get(row);
            centerA = microClusterA.getCenter();
            
            for(Integer column=0; column<clusters_number; column++){
                if(row != column){
                    if(column > row){
                        microClusterB = microClustering.get(column);
                        centerB = microClusterB.getCenter();

                        distance = euclideanDistance(centerA,centerB);
                        distance_matrix[row][column] = distance;
                    }
                    else
                        distance = distance_matrix[column][row];

                    indexDistance.add(Pair.of(column,distance));
                }
            }

            indexDistance.sort(Comparator.comparing(Pair::getRight));
            addNearestClusters(row,indexDistance);
            
            indexDistance.clear();
        }
    }

    public double euclideanDistance(double[] pointA, double[] pointB) {
		double distance = 0.0;

		for(int i = 0; i < pointA.length; i++){
			double d = pointA[i] - pointB[i];
			distance += d * d;
		}
		
        return Math.sqrt(distance);
	}

    private void addNearestClusters(Integer source, ArrayList<Pair<Integer,Double>> indexDistance){
        Integer target;
            
        for(int i=0; i<kNearest; i++){
            target = indexDistance.get(i).getLeft();
            nearestClusters.add(Pair.of(source,target));
        }
    }

    public ArrayList<Pair<Integer,Integer>> getNearestClusters(){
        return nearestClusters;
    }

    public void exportTXT() throws IOException{
        String left, right;

        File file = new File(OUT_FILE);
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);

        for(Pair<Integer,Integer> pair: nearestClusters){
            left = pair.getLeft().toString();
            right = pair.getRight().toString();

            bw.write(left + " " + right + '\n');
        }

        bw.close();
    }
}
