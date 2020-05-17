package bolts;

import java.io.File;
import java.util.ArrayList;
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

public class OutputBolt implements IRichBolt{
	private OutputCollector collector;
	private FileLoader loader;
	//Node node;
	
	public OutputBolt(FileLoader loader) {
		this.loader = loader;
	}
	
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
		//node = new Node(0.0f, 1000.0f, 1000001);
	}
	@Override
	public void execute(Tuple input) {
		PSRCATEntry entry = (PSRCATEntry)input.getValue(0);
		System.out.println();
		System.out.println("XXXXXXXXXXXXXXXXXX SAVE XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		System.out.println();
		loader.addOutputEntry(entry);
		//loader.saveToFile(entry);
		//collector.emit(input,new Values(entry));
		collector.ack(input);
		

	}
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("value"));
	}

	@Override
	public void cleanup() {
		
		System.out.println("XXXXXXXXXXXXXXXXXX CLEANUP XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		loader.saveOutput();
		System.out.println("XXXXXXXXXXXXXXXXXX CLEANUP END XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
	}
	
	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}
}
