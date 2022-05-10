import sys
from igraph import *

def main():
    filename = sys.argv[1]
    print(filename)

    g = Graph.Read_Edgelist(filename).as_undirected()

    #g = Graph.Read_Edgelist(filename)
    x = Graph.community_multilevel(g)

    print(x)

main()