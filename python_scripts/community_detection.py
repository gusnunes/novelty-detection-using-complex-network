import sys
from igraph import *

def main():
    line = sys.stdin.readline()
    #print(line)

    edgeList = eval(line)
    g = Graph(edges=edgeList, directed=True)
    
    """communities = Graph.community_fastgreedy(g.as_undirected())
    vertex_clustering = communities.as_clustering()"""

    """communities = Graph.community_edge_betweenness(g)
    vertex_clustering = communities.as_clustering()"""

    vertex_clustering = Graph.community_multilevel(g.as_undirected())

    #plot(vertex_clustering)

    for cluster in vertex_clustering:
        # no brackets and spaces
        str_cluster = str(cluster).strip("[]").replace(" ","")
        print(str_cluster)

main()