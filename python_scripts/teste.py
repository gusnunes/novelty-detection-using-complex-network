import sys
from igraph import *

def main():
    line = sys.stdin.readline()
    print(line)

    edgeList = eval(line)
    g = Graph(edges=edgeList,directed=True)

    g = g.as_undirected()

    #communities = Graph.community_fastgreedy(g)
    communities = g.community_fastgreedy()

    for cluster in communities.as_clustering():
        # no brackets and spaces
        str_cluster = str(cluster).strip("[]").replace(" ","")
        print(str_cluster)

main()