from statistics import mode
import sys
from igraph import *

def write_modularity(modularity,k):
    file_name = f"fastgreedy_electricity_modularity_k={k}.csv"
    file = open(file_name,"a")

    file.writelines(str(modularity) + ";\n")
    file.close()

def main():
    line = sys.stdin.readline()
    #print(line)

    edgeList = eval(line)
    g = Graph(edges=edgeList,directed=True)
    
    communities = Graph.community_fastgreedy(g.as_undirected(mode="mutual"))
    vertex_clustering = communities.as_clustering()

    #vertex_clustering = Graph.community_multilevel(g.as_undirected())

    #plot(vertex_clustering)

    for cluster in vertex_clustering:
        # no brackets and spaces
        str_cluster = str(cluster).strip("[]").replace(" ","")
        print(str_cluster)

main()