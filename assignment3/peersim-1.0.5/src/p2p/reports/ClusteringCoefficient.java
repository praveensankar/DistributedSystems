package p2p.reports;

import peersim.config.Configuration;

import peersim.core.Control;
import peersim.core.Linkable;
import peersim.core.Node;
import peersim.core.Network;

import java.util.Map;
import java.util.HashMap;

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
    // So for each node: go through the cache and check the cache of the
    // neighbor.
    // Maybe I can use <contains> in the BasicShuffle class
    // Well, it is actually in Linkable interface,
    // but it is implemented in the BasicShuffle class
    // Not sure how the Simulator ties it together
    public boolean execute() {

        // The key is the relation (count of mutual neighbors)/(total neighbor count),
        // and the value is the number of nodes that have that specific value
        // The maximum size of the map is the size of the cache
        Map<Double, Integer> clustering = new HashMap<>();

        // int test = 0;

        for (int i = 0; i < Network.size(); i++) {

            Node n = Network.get(i);

            if (n.isUp()) {

                Linkable nodeLink = (Linkable) n.getProtocol(pid);

                int mutualNeighbors = 0;
                int totalNeighbors = nodeLink.degree();
                // test = mutualNeighbors;

                // Wondering if we should have nodeLink.degree() directly,
                // in case the node has disappeared.
                for (int j = 0; j < totalNeighbors; j++) {

                    Node neighbor = nodeLink.getNeighbor(j);
                    Linkable neighborLink = (Linkable) neighbor.getProtocol(pid);

                    if (neighborLink.contains(n))
                        mutualNeighbors++;

                }

                int count = 1;
                double clusteringCoefficient = mutualNeighbors / totalNeighbors;

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
