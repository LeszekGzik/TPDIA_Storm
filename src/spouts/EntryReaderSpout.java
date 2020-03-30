package spouts;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import model.FileLoader;
import model.PSRCATEntry;

public class EntryReaderSpout implements IRichSpout{

	private SpoutOutputCollector collector;
	private boolean completed = false;
	private TopologyContext context;
	private FileLoader loader; 
	private ArrayList<PSRCATEntry> atnfSources; 
	
	
	public EntryReaderSpout(FileLoader loader) {
		this.loader=loader;
		
	}
	
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		System.out.println("Opened 1");
		this.context = context;
		//atnfSources = (ArrayList<PSRCATEntry>) conf.get("atnf_sources");
		//loader = (FileLoader) conf.get("loader");
		//prepare(conf,context);
		//this.loader = new FileLoader();
		this.collector = collector;
		System.out.println("Spout opened");
	}

	@Override
	public void nextTuple() {
		System.out.println("nextTuple called");
		if (completed) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {

			}
		}
		PSRCATEntry output;
		try {
			while ((output = loader.getTuple()) != null) {
				System.out.println("Emitting entry:"+output.getName());
				this.collector.emit(new Values(output), output);//emit(dana,ID)
				//this.collector.emit(new Values("AAA BBB CCC"), "AAA BBB CCC");
			}
		} catch (Exception e) {
			throw new RuntimeException("Error reading tuple", e);
		} finally {
			completed = true;
		}

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("output"));//declare(ID danej)
	}

	@Override
	public void close() {

	}

	public boolean isDistributed() {
		return false;
	}

	@Override
	public void activate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub

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
