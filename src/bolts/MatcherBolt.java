package bolts;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import model.FileLoader;
import model.Matcher;
import model.PSRCATEntry;
import model.PSRCATParser;
import test.EntryComparator;

public class MatcherBolt implements IRichBolt {

	private OutputCollector collector;
	//private List<PSRCATEntry> atnfSources;
	private FileLoader loader;
	private Matcher m;
	
	
	public MatcherBolt(FileLoader loader) {
		this.loader = loader;
	}
	
	
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
		this.m = new Matcher("OutputMatch.txt");
		//loader = (FileLoader) stormConf.get("loader");
	}

	@Override
	public void execute(Tuple input) {
		
		
		try {
//			System.out.println("Matcher bolt");
//	        PSRCATEntry entry = (PSRCATEntry)input.getValue(0);
//	        //FileLoader loader = (FileLoader)input.getValue(1);
//	        // do your bolt processing with the bean
//			System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXX");
//			System.out.println(entry.__str__());
//			System.out.println(loader.atnf_sources.get(0));
//			System.out.println(loader.name);
			System.out.println("XXXXXXXXXXXXXXXXXXXXXx");
			
			
			// Create fake candidate
			String name = "Candidate";
			PSRCATEntry candidate = (PSRCATEntry)input.getValue(0);

			// Just debug new candidate:
			System.out.println("\nNow attempting to match single candidate...");
			System.out.println("Test candidate details...");
			System.out.println(candidate.__str__());

			// ******************************
			//
			// Now test the matching....
			//
			// ******************************

			//Matcher m = new Matcher(outputFilePath);

			// First try and match only the candidate created above.
			int index = m.findSearchIndex(candidate, loader.getAtnfSources());
			m.compare(candidate, loader.getAtnfSources(), index, (float) 0.0001);

			System.out.println("Possible matches for J0006+1834 Duplicate: " + m.getPossibleMatches());
			System.out.println("Total detailed comparisons: " + m.getTotalComparisons());
			System.out.println("Check output file for details of match. Should be one match (J0006+1834).\n\n");
			// Now check output file - the match should be there.

			// Next we match all atnf sources, to all atnf sources.
			// First reset match count:
			m.setPossibleMatches(0);
			m.setTotalComparisons(0);
			float test_separation = (float) 1.0;

			System.out.println("Now matching the ATNF catalog against itself...");
			System.out.println("This may take some time (5 minutes or so on an intel i7 machine).");
			System.out.println("Matching using angular separation = " + test_separation);

			// Now iterate over all known sources. This loop can
			// run on-line, very easily.
			for (int ks = 0; ks < loader.getAtnfSources().size(); ks++) {

				// Find the search index
				index = m.findSearchIndex(loader.getAtnfSources().get(ks), loader.getAtnfSources());

				// Then do the matching
				m.compare(loader.getAtnfSources().get(ks), loader.getAtnfSources(), index, test_separation);
			}
			// There should be at least as many matches as there are sources.
			// This is because each source should match to itself. In reality
			// there will also be a couple of extra matches, as some sources
			// are similar enough to be considered genuine matches. These extra
			// matches are not errors, this is the algorithm doing exactly as
			// it should in practice. There should not be too many additional
			// matches however.
			System.out.println("Possible matches: " + m.getPossibleMatches());
			System.out.println("Total detailed comparisons: " + m.getTotalComparisons());
			System.out.println("If using brute force " + (loader.getAtnfSources().size() * loader.getAtnfSources().size())
					+ " comparisons would be undertaken.");

			if (m.getPossibleMatches() > loader.getAtnfSources().size())
				System.out.println("Extra matches: " + (m.getPossibleMatches() - loader.getAtnfSources().size()));
			else
				System.out.println("Fewer matches than sources - something is not working correctly.");

			
			
			
			collector.ack(input);
	    } catch (Exception e) {
	        collector.reportError(e);
	    }    
		
		

		
		
		
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("entry"));

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}
