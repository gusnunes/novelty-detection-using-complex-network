from igraph import *

PATH = "python_scripts\\"
FILE = "edge_list.txt"

def main():
    print("deu certo")
    g = Graph.Read_Edgelist(PATH + FILE)
    print(g)

main()