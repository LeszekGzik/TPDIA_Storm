package main;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import bolts.NodeSiftingBolt;
import spouts.RandomNumberSpout;
import test.*;

public class TPDIAStorm {

	public static void main(String[] args) {
		FeaturesExtraction fe = new FeaturesExtraction();
		fe.FeatureExtractionTest();
		
		NodeSiftTest.main(args);
		WindowSiftTest.main(args);
		//MatchingAccuracyTest.main(args);
		
		//System.out.println("Interpreter created");
		//KnownSourceMatcherTest.main(args);
		//System.out.println("TEST END");
		
		
		
		/*Config config = new Config();
		config.setDebug(true);
		config.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);
		
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("random-number-spout", new RandomNumberSpout());
		builder.setBolt("node-sifting", new NodeSiftingBolt()).shuffleGrouping("random-number-spout");
		
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("TPDIAStorm", config, builder.createTopology());
		try {
			Thread.sleep(10000);
		} catch(Exception e) {
		
		}
		
		cluster.shutdown();*/

	}

}
