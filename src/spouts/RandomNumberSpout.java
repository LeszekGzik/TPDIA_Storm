package spouts;
import java.util.Map;
import java.util.Random;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class RandomNumberSpout implements IRichSpout {
	private SpoutOutputCollector collector;
	private TopologyContext context;
	Random rng;
	@Override
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		this.context = context;
		this.collector = collector;
		rng = new Random();
	}

	@Override
	public void nextTuple() {
		for(int i=0; i<10; i++) {
			Float num = rng.nextFloat()*10;
			collector.emit(new Values(num), num);
		}
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
