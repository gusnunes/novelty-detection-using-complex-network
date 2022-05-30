import sys
from igraph import *

def main():
    filename = sys.argv[1]
    #print(filename)

    g = Graph.Read_Edgelist(filename).as_undirected()

    #g = Graph.Read_Edgelist(filename)
    #Graph.community_fastgreedy(g)
    communities = g.community_fastgreedy()

    for cluster in communities.as_clustering():
        str_cluster = str(cluster)
        print(str_cluster.strip("[]"))

main()