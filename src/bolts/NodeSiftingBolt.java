package bolts;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import model.Node;

public class NodeSiftingBolt implements IRichBolt{
	private OutputCollector collector;
	Node node;
	
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
		node = new Node(0.0f, 10.0f, 10001);
	}
	@Override
	public void execute(Tuple input) {
		Float value = input.getFloat(0);
		if(!node.isDuplicate(value)) {
			collector.emit(new Values(value));
			System.out.println(value + " is not a duplicate");
		}
		else {
			System.out.println(value + " is a duplicate!");
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
