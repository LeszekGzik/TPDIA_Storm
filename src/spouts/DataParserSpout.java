package spouts;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import model.PSRCATEntry;
import model.PSRCATParser;

public class DataParserSpout implements IRichSpout {
	private SpoutOutputCollector collector;
	private TopologyContext context;
	String dataFilePath = "random path";
	PSRCATParser parser = new PSRCATParser();
	List<PSRCATEntry> candidateList;
	
	@Override
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		this.context = context;
		this.collector = collector;
		//Code of logic
		if(parser.is_catalogue_file(dataFilePath)) {
			if(parser.file_exists(dataFilePath)) {
				candidateList = parser.parse_as_list(dataFilePath);
			}
		}
		
	}

	@Override 
	public void nextTuple() {
		//collector.emit
		
		for(Iterator<PSRCATEntry> i = candidateList.iterator(); i.hasNext();) {
			//collector.emit(i.next());
		}
		
//		for(int i=0; i<10; i++) {
//			Float num = rng.nextFloat()*10;
//			collector.emit(new Values(num), num);
//		}
	}
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("num"));
	}

	@Override
	public void close() {
	}
	
	public boolean isDistributed() {
		return false;
	}
	@Override
	public void activate() {
	}
	@Override
	public void deactivate() {
	}
	@Override
	public void ack(Object msgId) {
	}
	@Override
	public void fail(Object msgId) {
	}
	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}
}
