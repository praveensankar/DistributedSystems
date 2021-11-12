import matplotlib.pyplot as plt
import numpy as np

class GraphInfo:

    def __init__(self,topo, cache_size, x_coordinate, y_coordinate):
        self.topo = topo
        self.cache_size = cache_size
        self.x_coordinate = x_coordinate
        self.y_coordinate = y_coordinate
        self.label = str(self.topo) + " "+str(self.cache_size)
    




def plot_graphs(graph1, graph2, graph3, graph4, plt_title, plt_xlabel, plt_ylable, dest_filename):
    plt.title(plt_title)
    plt.xlabel(plt_xlabel)
    plt.ylabel(plt_ylable)
    plt.plot(graph1.x_coordinate, graph1.y_coordinate, c='r', label=graph1.label)
    plt.plot(graph2.x_coordinate, graph2.y_coordinate, c='g', label=graph2.label)
    plt.plot(graph3.x_coordinate, graph3.y_coordinate, c='b', label=graph3.label)
    plt.plot(graph4.x_coordinate, graph4.y_coordinate, c='y', label=graph4.label)
    plt.legend()
    plt.savefig(dest_filename)
    plt.clf()


def parse_in_degree(in_degree):
    in_degree_x = []
    in_degree_y = []
    for i in range(len(in_degree)):
        in_degree_x.append(int(in_degree[i][0]))
        in_degree_y.append(int(in_degree[i][1]))
    
    return (in_degree_x, in_degree_y)


def work():
    cluster_ring_30 = np.loadtxt("cluster_ring_30.txt", dtype=float)
    cluster_ring_50 = np.loadtxt("cluster_ring_50.txt", dtype=float)
    cluster_star_30 = np.loadtxt("cluster_star_30.txt", dtype=float)
    cluster_star_50 = np.loadtxt("cluster_star_50.txt", dtype=float)
    cluster_random_30 = np.loadtxt("cluster_random_30.txt", dtype=float)
    cluster_random_50 = np.loadtxt("cluster_random_50.txt", dtype=float)

    path_ring_30 = np.loadtxt("path_ring_30.txt",dtype=float)
    path_ring_50 = np.loadtxt("path_ring_50.txt", dtype=float)
    path_star_30 = np.loadtxt("path_star_30.txt",dtype=float)
    path_star_50 = np.loadtxt("path_star_50.txt", dtype=float)
    path_random_30 = np.loadtxt("path_random_30.txt",dtype=float)
    path_random_50 = np.loadtxt("path_random_50.txt", dtype=float)

    in_degree_ring_30 = np.loadtxt("degree_ring_30.txt", delimiter=',', dtype=str)
    in_degree_ring_50 = np.loadtxt("degree_ring_50.txt", delimiter=',', dtype=str)
    in_degree_star_30 = np.loadtxt("degree_star_30.txt", delimiter=',', dtype=str)
    in_degree_star_50 = np.loadtxt("degree_star_50.txt", delimiter=',', dtype=str)    
    in_degree_random_30 = np.loadtxt("degree_random_30.txt", delimiter=',', dtype=str)
    in_degree_random_50 = np.loadtxt("degree_random_50.txt", delimiter=',', dtype=str)

    cycles = np.array([i for i in range(1,301)])
    graph_cr30 =  GraphInfo("ring",30,cycles, cluster_ring_30)
    graph_cr50 =  GraphInfo("ring",50,cycles, cluster_ring_50)
    graph_cs30 =  GraphInfo("star",30,cycles, cluster_star_30)
    graph_cs50 =  GraphInfo("star",50,cycles, cluster_star_50)
    graph_cra30 =  GraphInfo("random",30,cycles, cluster_random_30)
    graph_cra50 =  GraphInfo("random",50,cycles, cluster_random_50)
    plot_graphs(graph_cr30,graph_cra30,graph_cr50, graph_cra50,"clustering coefficient","cycles","coefficient","cluster_coefficient_ring.png")
    plot_graphs(graph_cs30,graph_cra30,graph_cs50, graph_cra50,"clustering coefficient","cycles","coefficient","cluster_coefficient_star.png")
    plot_graphs(graph_cr30,graph_cs30,graph_cr50, graph_cs50,"clustering coefficient","cycles","coefficient","cluster_coefficient_ring_star.png")

    path_cr30 =  GraphInfo("ring",30,cycles, path_ring_30)
    path_cr50 =  GraphInfo("ring",50,cycles, path_ring_50)
    path_cs30 =  GraphInfo("star",30,cycles, path_star_30)
    path_cs50 =  GraphInfo("star",50,cycles, path_star_50)
    path_cra30 =  GraphInfo("random",30,cycles, path_random_30)
    path_cra50 =  GraphInfo("random",50,cycles, path_random_50)
    plot_graphs(path_cr30,path_cra30,path_cr50, path_cra50,"average path length","cycles","average path","average_path_length_ring.png")
    plot_graphs(path_cs30,path_cra30,path_cs50, path_cra50,"average path length","cycles","average path","average_path_length_star.png")
    plot_graphs(path_cr30,path_cs30,path_cr50, path_cs50,"average path length","cycles","average path","average_path_length_ring_star.png")

    (degree_cr30_x, degree_cr30_y) = parse_in_degree(in_degree_ring_30)
    (degree_cr50_x, degree_cr50_y) = parse_in_degree(in_degree_ring_50)
    (degree_cs30_x, degree_cs30_y) = parse_in_degree(in_degree_star_30)
    (degree_cs50_x, degree_cs50_y) = parse_in_degree(in_degree_star_50)
    (degree_cra30_x, degree_cra30_y) = parse_in_degree(in_degree_random_30)
    (degree_cra50_x, degree_cra50_y) = parse_in_degree(in_degree_random_50)

    degree_cr30 =  GraphInfo("ring",30,degree_cr30_x, degree_cr30_y)
    degree_cr50 =  GraphInfo("ring",50,degree_cr50_x, degree_cr50_y)
    degree_cs30 =  GraphInfo("star",30,degree_cs30_x, degree_cs30_y)
    degree_cs50 =  GraphInfo("star",50, degree_cs50_x, degree_cs50_y)
    degree_cra30 =  GraphInfo("random",30, degree_cra30_x, degree_cra30_y)
    degree_cra50 =  GraphInfo("random",50, degree_cra50_x, degree_cra50_y)
    plot_graphs(degree_cr30,degree_cra30,degree_cr50, degree_cra50,"in degree distribution","in degree","node count","in_degree_distribution_ring.png")
    plot_graphs(degree_cs30,degree_cra30,degree_cs50, degree_cra50,"in degree distribution","in degree","node count","in_degree_distribution_star.png")
    plot_graphs(degree_cr30,degree_cs30,degree_cr50, degree_cs50,"in degree distribution","in degree","node count","in_degree_distribution_ring_star.png")





work()

#ypoints = np.array([0, 250])

"""
cycles = np.array([i for i in range(1,301)])
plt.title('clustering coefficient')
plt.xlabel('cycles')
plt.ylabel('coefficient')
plt.plot(cycles, cluster_ring_30, c='r', label='topo: wire ring lattice, cache : 30')
plt.plot(cycles, cluster_ring_50, c='g', label='topo: wire ring lattice, cache : 50')
plt.plot(cycles, cluster_star_30, c='b', label='topo: wire star, cache : 30')
plt.legend()
plt.savefig("cluster_coefficient.png")
plt.show()


plt.title('average path length')
plt.xlabel('cycles')
plt.ylabel('average path')
plt.plot(cycles, path_ring_30, c='r', label='topo: wire ring lattice, cache : 30')
plt.plot(cycles, path_star_30, c='b', label='topo: wire star lattice, cache : 30')
plt.plot(cycles, path_ring_50, c='g', label='topo: wire ring lattice, cache : 50')
plt.legend()
plt.savefig("average_path_length.png")
plt.show()

plt.title('in degree distribution')
plt.xlabel('in degree')
plt.ylabel('node count')
in_degree_30_x = []
in_degree_30_y = []
for i in range(len(in_degree_30)):
    in_degree_30_x.append(int(in_degree_30[i][0]))
    in_degree_30_y.append(int(in_degree_30[i][1]))

in_degree_50_x = []
in_degree_50_y = []
for i in range(len(in_degree_50)):
    in_degree_50_x.append(int(in_degree_50[i][0]))
    in_degree_50_y.append(int(in_degree_50[i][1]))

print(in_degree_30_y)
plt.plot(in_degree_30_x, in_degree_30_y, c='r', label='topo: wire ring lattice, cache : 30')
plt.plot(in_degree_50_x, in_degree_50_y, c='g', label='topo: wire ring lattice, cache : 50')
plt.legend()
plt.savefig("in_degree_distribution.png")
plt.show()
"""