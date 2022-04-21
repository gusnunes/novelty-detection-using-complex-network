import moa.cluster.Cluster;
import moa.cluster.Clustering;

import org.jgrapht.*;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class Knn {
    public int k;
    public Clustering microClusters;

    public Knn(int k, Clustering microClusters){
        this.k = k;
        this.microClusters = microClusters;
    }

    // por enquanto, só transformar cada micro-grupo em vertice de um grafo
    // testar a biblioteca
    public void geraRede(){
        Graph<Cluster, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);

        // Vertice para cada micro_grupo
        for(Cluster microCluster: microClusters.getClustering()){
            g.addVertex(microCluster);
        }
    }

    private static double distance(double[] pointA, double [] pointB) {
		double distance = 0.0;
		for (int i = 0; i < pointA.length; i++) {
			double d = pointA[i] - pointB[i];
			distance += d * d;
		}
		return Math.sqrt(distance);
	}

    public static void main(String[] args){
        System.out.println("Teste");
    }
}
