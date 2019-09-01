package test;



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

/*
*********************************************************************************

 KnownSourceMatherApp.py

*********************************************************************************
 Description:

 Runs the known source matcher tests.

*********************************************************************************
 Author: Rob Lyon
 Email : robert.lyon@manchester.ac.uk
 web   : www.scienceguyrob.com
*********************************************************************************
 License:

 Code made available under the GPLv3 (GNU General Public License), that
 allows you to copy, modify and redistribute the code as you see fit
 (http://www.gnu.org/copyleft/gpl.html). Though a mention to the
 original author using the citation above in derivative works, would be
 very much appreciated.
*********************************************************************************
*/

//import os
//import tarfile
//import urllib
// shutil
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import model.Matcher;
import model.PSRCATParser;
import model.PSRCATEntry;

//import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
//import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
//import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

// The class below simply launches the application.


public class KnownSourceMatcherTest {

	public static void main(String args[]) {
		
		// Clean workspace (removes previously downloaded catalogs).
		
		
		
		//FileUtils.deleteDirectory(new File("psrcat_pkg.tar")); 
				
		// Path to pulsar catalog file...
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
	    
	    List<PSRCATEntry> atnf_sources = PSRCATParser.parse_as_list(database_file_path);

	    System.out.println("ATNF Entries: " + atnf_sources.size());
	    
	    // Now sort the list in place according to separation 
	    // from the reference point...
	    atnf_sources.sort(new EntryComparator());
	   
	    // Create fake candidate
	    String name = "Candidate";
	    PSRCATEntry candidate = new PSRCATEntry("J0006+1834 Duplicate");

	    // Here are some fake lines that will be fed to the entry. These
	    // lines are taken from the pulsar catalog.
	    // RAJ      00:06:04.8               2    cn95
	    // DECJ     +18:34:59                4    cn95
	    // P0       0.69374767047            14   cn95
	    // P1       2.097E-15                12   cn95
	    // PEPOCH   49079.5                       cn95
	    // DM       11.41                    6    cn95
	    candidate.process_atnf_formatted_line("RAJ   00:06:04.8     2    cn95");
	    candidate.process_atnf_formatted_line("DECJ  +18:34:59      4    cn95");
	    candidate.process_atnf_formatted_line("P0    0.69374767047  14   cn95");
	    candidate.process_atnf_formatted_line("DM    11.41          6    cn95");
	    candidate.getRefSep();
	    
	    // Just debug new candidate:
	    System.out.println("\nNow attempting to match single candidate...");
	    System.out.println("Test candidate details...");
	    System.out.println(candidate.__str__());

	    // ******************************
	    //
	    // Now test the matching....
	    //
	    // ******************************
	    
	    Matcher m = new Matcher(outputFilePath);
	    
	    // First try and match only the candidate created above.
	    int index = m.findSearchIndex(candidate, atnf_sources);
	    m.compare(candidate, atnf_sources,index,(float)0.0001);
	    
	    System.out.println("Possible matches for J0006+1834 Duplicate: "+ m.getPossibleMatches() );
	    System.out.println("Total detailed comparisons: "+ m.getTotalComparisons() );
	    System.out.println("Check output file for details of match. Should be one match (J0006+1834).\n\n");
	    // Now check output file - the match should be there.
	 
	    // Next we match all atnf sources, to all atnf sources.
	    // First reset match count:
	    m.setPossibleMatches(0);
	    m.setTotalComparisons(0);
	    float test_separation = (float)1.0;
	    
	    System.out.println("Now matching the ATNF catalog against itself...");
	    System.out.println("This may take some time (5 minutes or so on an intel i7 machine).");
	    System.out.println("Matching using angular separation = " + test_separation);
	    
	    // Now iterate over all known sources. This loop can
	    // run on-line, very easily.
	    for(int ks = 0; ks < atnf_sources.size(); ks++) {
	        
	        // Find the search index
	        index = m.findSearchIndex(atnf_sources.get(ks), atnf_sources);
	        
	        // Then do the matching
	        m.compare(atnf_sources.get(ks), atnf_sources,index,test_separation);
	    }
	    // There should be at least as many matches as there are sources.
	    // This is because each source should match to itself. In reality
	    // there will also be a couple of extra matches, as some sources
	    // are similar enough to be considered genuine matches. These extra
	    // matches are not errors, this is the algorithm doing exactly as
	    // it should in practice. There should not be too many additional
	    // matches however.
	    System.out.println("Possible matches: "+ m.getPossibleMatches());
	    System.out.println("Total detailed comparisons: "+ m.getTotalComparisons());
	    System.out.println("If using brute force "+ (atnf_sources.size() * atnf_sources.size()) +" comparisons would be undertaken.");
	    
	    if(m.getPossibleMatches() > atnf_sources.size())
	    	System.out.println("Extra matches: "+ (m.getPossibleMatches() - atnf_sources.size()));
	    else
	    	System.out.println("Fewer matches than sources - something is not working correctly.");
	       
	}
		
		
		
}
