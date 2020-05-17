package bolts;
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
import model.Node;
import model.PSRCATEntry;

public class FreqSiftingBolt implements IRichBolt{
	private OutputCollector collector;
	private FileLoader loader;
	Node node;
	
	public FreqSiftingBolt(FileLoader loader) {
		this.loader = loader;
	}
	
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
		node = new Node(0.0f, 1000.0f, 1000001);
	}
	@Override
	public void execute(Tuple input) {
		PSRCATEntry entry = (PSRCATEntry)input.getValue(0);
		System.out.println("HELLO " + entry.getName());
		String value = entry.get_parameter("F0");
		System.out.println("FREQ is " + value);
		if(value != null) {
			if(!node.isDuplicate(Float.parseFloat(value))) {
				collector.emit(new Values(entry));
				System.out.println(entry.getName() + " is not a duplicate");
			}
			else {
				System.out.println(entry.getName() + " is a duplicate!");
			}
		}
		else {
			System.out.println(entry.getName() + " has a null frequency.");
			collector.emit(new Values(entry));
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
