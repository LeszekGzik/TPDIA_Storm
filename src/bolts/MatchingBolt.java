package bolts;
import java.io.File;
import java.util.ArrayList;
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
import model.Node;
import model.PSRCATEntry;
import model.PSRCATParser;

public class MatchingBolt implements IRichBolt{
	private OutputCollector collector;
	private FileLoader loader;
	Matcher matcher;
	private ArrayList<PSRCATEntry> atnf_sources;
	
	public MatchingBolt(FileLoader loader) {
		this.loader = loader;
	}
	
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
		System.out.println("TEST");
		File file_tmp = new File("psrcat_tar");
		String full_path = file_tmp.getAbsolutePath();
//		String database_file_path = full_path + "\\psrcat.db";
//		this.atnf_sources = PSRCATParser.parse_as_list(database_file_path);
		this.atnf_sources = loader.getAtnfSources();
		//TEST matching
		int size = atnf_sources.size()*9/10;
		for(int i = 0; i < size; i++)	//tworzenie listy znanych obiektów sk³adaj¹cej siê z 10% zbioru danych testowych
		{
			atnf_sources.remove(0);
		}
		String outputFilePath = "OutputMatch.txt";
		File outputFile = new File(outputFilePath);
		matcher = new Matcher(outputFilePath);
	}
	
	@Override
	public void execute(Tuple input) {
		PSRCATEntry entry = (PSRCATEntry)input.getValue(0); 
		System.out.println("MATCHER received " + entry.getName());
		
		for (int ks = 0; ks < atnf_sources.size(); ks++) {

			// Find the search index
			int index = matcher.findSearchIndex(entry, atnf_sources);
			matcher.compare(entry, atnf_sources,index,(float)0.0001);
		}
		
		System.out.println("Possible matches for " + entry.getName() + ": "+ matcher.getPossibleMatches() );
		//TEST matching
//		if(matcher.getPossibleMatches() != 0) {
		if(matcher.getPossibleMatches() == 0) {
			System.out.println(entry.getName() + " has no matches!");
			collector.emit(input, new Values(entry));
		}
		collector.ack(input);
	}
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("value"));
	}

	@Override
	public void cleanup() {
	}
	
	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}
}
