package example.reports;

import peersim.config.Configuration;

import peersim.core.Control;
import peersim.core.Linkable;
import peersim.core.Node;
import peersim.core.Network;

import java.util.Map;
import java.util.HashMap;

//import peersim.reports.Clustering;

// public class ClusteringCoefficient extends Clustering {
//
//     public ClusteringCoefficient(String name) {
//         super(name);
//     }
//
//     public boolean execute() {
//         return super.execute();
//     }
// }

public class ClusteringCoefficient implements Control {

    private static final String PAR_PID = "protocol";

    private final int pid;

    // Not really sure what the prefix is doing
    public ClusteringCoefficient(String prefix) {
        this.pid = Configuration.getPid(prefix + "." + PAR_PID);
    }

    // TODO: calculate the clustering coefficient.
    // This should show the relationship between the total
    // number of neighbors and the number of neighbors who has a
    // link back.
    //
    // WRONG: I think the clustering coefficient should be the
    // total number of possible connections (i.e the size of the cache)
    // and the number of a nodes' neighbors that are also neighbors with
    // each other.
    //
    // So for each node: go through the cache and check the cache of the
    // neighbor.
    // Maybe I can use <contains> in the BasicShuffle class
    // Well, it is actually in Linkable interface,
    // but it is implemented in the BasicShuffle class
    // Not sure how the Simulator ties it together
    //
    // The local clustering coefficient c for a node n in a directed graph is
    // the number of connectictions between the neighbors of a node.
    // 1. Sum up the connections to each other for all the neighboring nodes
    // 2. Divide by the total number of neighbors
    public boolean execute() {
        System.out.println("\nExecuting\n");

        // The key is the relation (count of mutual neighbors)/(total neighbor count),
        // and the value is the number of nodes that have that specific value
        // The maximum size of the map is the size of the cache
        Map<Double, Integer> clustering = new HashMap<>();

        // int test = 0;

        for (int i = 0; i < Network.size(); i++) {

            Node n = Network.get(i);

            if (n.isUp()) {
                System.out.println();
                Linkable nodeLink = (Linkable) n.getProtocol(pid);

                int mutualNeighbors = 0;
                // Question: will this always be the cache size?
                // Getting the total number of connections from n
                int totalNeighbors = nodeLink.degree();
                // System.out.println("totalNeighbors = " + totalNeighbors);
                // test = mutualNeighbors;

                // int sumOfConnections = 0;

                // Wondering if we should have nodeLink.degree() directly,
                // in case the node has disappeared.
                // Looping through all the neighbors
                for (int j = 0; j < totalNeighbors; j++) {

                    // Obtaining j-th neighbor of n
                    Node neighbor = nodeLink.getNeighbor(j);
                    Linkable neighborLink = (Linkable) neighbor.getProtocol(pid);
                    // not sure if should be cache size or not
                    // not even sure if this is needed
                    // I think the number of possible connections should be
                    // equal to total neighbors
                    int totalNeighborsofNeighbor = neighborLink.degree();

                    // totalNeighbors += totalNeighborsofNeighbor;

                    // Running through the neighbor j's links
                    for (int k = 0; k < totalNeighborsofNeighbor; k++) {

                        // Obtaining the k-th neighbor of the neighbor j
                        Node distantNeighbor = neighborLink.getNeighbor(k);
                        // Linkable distantNeighborLink = (Linkable) distantNeighbor.getProtocol(pid);

                        // Checking if the neighbor of the neighbor contains
                        // the nodes' neighbor
                        // if (distantNeighborLink.contains(neighbor))
                        if (nodeLink.contains(distantNeighbor))
                            // sumOfConnections++;
                            mutualNeighbors++;
                    }

                }

                int count = 1;
                double clusteringCoefficient = (double) mutualNeighbors / (totalNeighbors * (totalNeighbors /*- 1 */));

                if (clustering.containsKey(clusteringCoefficient))
                    count += clustering.get(clusteringCoefficient);

                clustering.put(clusteringCoefficient, count);

            }

        }

        double averageClustering = average(clustering);
        // System.out.println("test = " + test);
        System.out.println("ClusteringCoefficient = " + averageClustering);

        return false;
    }

    private double average(Map<Double, Integer> clustering) {

        int totalNodes = 0; // or use Network.size() ?
        double accumulatedCoefficient = 0;

        for (Map.Entry<Double, Integer> entry : clustering.entrySet()) {
            accumulatedCoefficient += entry.getKey() * entry.getValue();
            totalNodes += entry.getValue();
        }

        System.out.println("clustering.size() = " + clustering.size());
        System.out.println("totalNodes = " + totalNodes);
        System.out.println("accumulatedCoefficient = " + accumulatedCoefficient);

        // Average clustering coefficient
        return accumulatedCoefficient / totalNodes;
    }
}
