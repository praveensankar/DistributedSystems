package example.reports;


import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.graph.GraphAlgorithms;
import peersim.reports.GraphObserver;
import peersim.util.IncrementalStats;
import peersim.core.Network;


import java.sql.SQLOutput;


/*The average path length is a metric of the number of hops (and hence, communication costs and time)
to reach nodes from a given source.
A small average path length is therefore essential for broadcasting
or, generally, information dissemination  applications.*/

public class ShortestPathObserver extends GraphObserver {

    private final static String PAR_PID = "protocol";
    private static final String PAR_START_PROTOCOL = "starttime";
    private static final String PAR_END_PROTOCOL = "endtime";

    private final int pid;
    private final long startTime;
    private final long endTime;

    public ShortestPathObserver(String prefix)
    {
        super(prefix);
        this.pid = Configuration.getPid(prefix + "." + PAR_PID);
        this.startTime = Configuration.getLong(prefix + "." + PAR_START_PROTOCOL, Long.MIN_VALUE);
        this.endTime = Configuration.getLong(prefix + "." + PAR_END_PROTOCOL, Long.MAX_VALUE);
    }

    /**
     * Standard constructor that reads the configuration parameters.
     * Invoked by the simulation engine.
     *
     */


    @Override
    public boolean execute() {

        if ((CommonState.getTime() >= endTime) || (CommonState.getTime() < startTime))
            return false;

        IncrementalStats stats = new IncrementalStats();
        updateGraph();

        stats.reset();
        Double counter = 0.0;
        Double sum = 0.0;
        System.out.println("g: " + g);
        for (int i = 0; i < g.size(); ++i) {
            ga.dist(g, i);
        }
        for(int j = 0; j < ga.d.length; j++){
            System.out.println(ga.d[j]);
            counter +=1;
            sum += ga.d[j];
        }
        System.out.println("Shortes path: " + sum/counter);
        System.out.println(name + ": " + stats);

        return false;


    }

}
