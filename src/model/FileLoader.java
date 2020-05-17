package model;

import java.io.File;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import model.PSRCATEntry;
import model.PSRCATParser;
import test.EntryComparator;

public class FileLoader implements Serializable {

	//private static final long serialVersionUID = 2L;
	
	//public File outputFile;
	public String outputFilePath = "OutputMatch.txt";
	//public File inputFile;
	File outputFile = new File("OutputMatch.txt");
	private ArrayList<PSRCATEntry> outputList = new ArrayList<PSRCATEntry>();
	private ArrayList<PSRCATEntry> atnf_sources;
	private int atnfIndex = 0;
	public String name = "This is test name";
	
	public FileLoader() {
		
		File file_tmp = new File("psrcat_tar");
		try {
			URL url = new URL("http://www.atnf.csiro.au/people/pulsar/psrcat/downloads/psrcat_pkg.tar.gz");
		}
		catch(Exception e)
		{ 
			e.printStackTrace();
		}
		String full_path = file_tmp.getAbsolutePath();
		String database_file_path = full_path + "\\psrcat.db";
		System.out.println("Database file at: " + database_file_path);
		
		// Build path to the output file...
		String outputFilePath = "OutputMatch.txt";
	    File outputFile = new File(outputFilePath);
	    
	    // Clear output file
	    try {
	    	PrintWriter writer = new PrintWriter(outputFile);
	    	writer.print("");
	    	writer.close();
	    }
		catch(Exception e)
		{
			e.printStackTrace();
		}
	    // Load known sources as list
	    System.out.println("Loading ATNF catalog...");
	    
	    this.atnf_sources = PSRCATParser.parse_as_list(database_file_path);

	    System.out.println("ATNF Entries: " + atnf_sources.size());
	    
	    // Now sort the list in place according to separation 
	    // from the reference point...
	    atnf_sources.sort(new EntryComparator());
	    this.atnfIndex =  atnf_sources.size() -1;
	    
	    //TEST matching
//	    PSRCATEntry tmp1 = atnf_sources.get(1);
//	    PSRCATEntry tmp2 = atnf_sources.get(1);
//	    PSRCATEntry tmp3 = atnf_sources.get(1);
//	    PSRCATEntry tmp4 = atnf_sources.get(1);
//	    
//	    atnf_sources.clear();
//	    atnf_sources.add(tmp1);
//	    atnf_sources.add(tmp1);
//	    atnf_sources.add(tmp1);
//	    atnf_sources.add(tmp1);
//	    
//	    System.out.println("1:  "+atnf_sources.get(0).__str__());
//	    System.out.println("2:  "+atnf_sources.get(1).__str__());
//	    System.out.println("3:  "+atnf_sources.get(2).__str__());
//	    System.out.println("4:  "+atnf_sources.get(3).__str__());
//	    this.atnfIndex =  atnf_sources.size() -1;
	}
	
	public PSRCATEntry getTuple()
	{
		System.out.println("getTuple");
		if (atnfIndex == 0) {
			System.out.println("ret null");
			return null;
		}
		else {
			PSRCATEntry output = atnf_sources.get(atnfIndex);
			//atnf_sources.remove(atnfIndex);
			atnfIndex--;
			System.out.println("ret obj");
			return output;
		}
	}
	
	public void addOutputEntry(PSRCATEntry entry) {
		outputList.add(entry);
	}
	
	public void saveOutput() {
		System.out.println("outputList Size:"+outputList.size());
		File outputFile = new File(outputFilePath);
		System.out.println("outputList Size:"+outputList.size());
	    try {
	    	PrintWriter writer = new PrintWriter(outputFile);
	    	writer.print("");
	    	for(int i=0; i<outputList.size(); i++)
	    	{
	    		writer.println(outputList.get(i).__str__());
	    	}
	    	writer.close();
	    }
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void saveToFile(PSRCATEntry entry) {
		try {
			PrintWriter writer = new PrintWriter(outputFile);
			writer.println(entry.__str__());
			writer.close();
		 }
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public ArrayList<PSRCATEntry>getAtnfSources(){
		return this.atnf_sources;
	}
	
}
