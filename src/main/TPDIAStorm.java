package main;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import bolts.*;
import model.*;
import spouts.*;
import test.*;

public class TPDIAStorm {

	public static void main(String[] args) {
		
		// TESTING 
//		FeaturesExtraction fe = new FeaturesExtraction();
//		fe.FeatureExtractionTest();
//		
//		NodeSiftTest.main(args);
//		WindowSiftTest.main(args);
//		MatchingAccuracyTest.main(args);
//		KnownSourceMatcherTest.main(args);
		//TESTING END
		
		FileLoader loader = new FileLoader();

		Config config = new Config();
		// config.put("inputFile", args[0]);
		config.registerSerialization(PSRCATEntry.class);
		config.put("inputFile", "test.txt");
		config.setDebug(true);
		config.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("entry-reader-spout", new EntryReaderSpout(loader));
		builder.setBolt("sifting-bolt", new PeriodSiftingBolt(loader),4).shuffleGrouping("entry-reader-spout");
		builder.setBolt("sifting-bolt-2", new FreqSiftingBolt(loader),4).shuffleGrouping("sifting-bolt");
		builder.setBolt("matching-bolt", new MatchingBolt(loader),3).shuffleGrouping("sifting-bolt-2");
		builder.setBolt("output-bolt", new OutputBolt(loader), 1).shuffleGrouping("matching-bolt");

		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("HelloStorm", config, builder.createTopology());

		try {
			Thread.sleep(10000);
		} catch (Exception e) {

		}

		cluster.shutdown();

	}

}
