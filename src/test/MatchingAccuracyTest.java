package test;
import java.util.ArrayList;
import java.util.List;

import model.PSRCATEntry;
import model.Matcher;

public class MatchingAccuracyTest {

	public static void main(String[] args) {
		PSRCATEntry ks1 = new PSRCATEntry("J0006+1834 Duplicate");
		ks1.process_atnf_formatted_line("RAJ   23:46:50.45     2    cn95");
		ks1.process_atnf_formatted_line("DECJ  -6:09:59.5     4    cn95");
		ks1.process_atnf_formatted_line("P0    1.18146338297  14   cn95");
		ks1.process_atnf_formatted_line("DM    22.504          6    cn95");
		ks1.getRefSep();
		
		System.out.println("\tSource name   : " + ks1.getName());
		System.out.println("\tSource RA     : " + ks1.get_parameter("RAJ"));
		System.out.println("\tSource DEC    : " + ks1.get_parameter("DECJ"));
		System.out.println("\tSource P0 (s) : " + ks1.get_parameter("P0"));
		System.out.println("\tSource P0 (ms): " + Float.parseFloat(ks1.get_parameter("P0"))*1000);
		System.out.println("\tSource F0 (Hz): " + ks1.get_parameter("F0"));
		System.out.println("\tSource DM     : " + ks1.get_parameter("DM"));
		System.out.println("\tSource ref sep: " + ks1.getRefSep());
		
		float test_min_period = 1.06f;
		float test_max_period = 1.3f;
		float[] period_permutations = new float[241];
		for(int i = 0; i<period_permutations.length; i++) {
			period_permutations[i] = test_min_period+i*0.001f;
		}
		System.out.println("Total period permutations: " + period_permutations.length);
		
		float test_min_dm = 18.0f;
		float test_max_dm = 27.0f;
		float[] dm_permutations = new float[91];
		for(int i = 0; i<dm_permutations.length; i++) {
			dm_permutations[i] = test_min_dm+i*0.1f;
		}
		System.out.println("Total DM permutations: " + dm_permutations.length);
		
		List<PSRCATEntry> test_cand_list = new ArrayList<PSRCATEntry>();
		String temp_ra = ks1.get_parameter("RAJ");
		String temp_dec = ks1.get_parameter("DECJ");
		for(float p0 : period_permutations) {
			for(float dm : dm_permutations) {
				String nme = ks1.getName() + "_" + p0 + "_" + dm;
				PSRCATEntry c = new PSRCATEntry(nme);
				c.process_atnf_formatted_line("RAJ   " + temp_ra  + "  2    cn95");
		        c.process_atnf_formatted_line("DECJ  " + temp_dec + "  4    cn95");
		        c.process_atnf_formatted_line("P0    " + p0  + "  14   cn95");
		        c.process_atnf_formatted_line("DM    " + dm  + "  6    cn95");
		        c.setRefSep(ks1.getRefSep());
		        test_cand_list.add(c);
			}
		}
		System.out.println("Test candidate list entries: " + test_cand_list.size());
		System.out.println(test_cand_list.get(0));
		System.out.println(test_cand_list.get(1));
		System.out.println(test_cand_list.get(2));
		System.out.println(test_cand_list.get(test_cand_list.size()-1));
		
		Matcher tm = new Matcher("matching_output.txt");
		tm.setAccuracy(1.0f);
		tm.setDMPercentAccuracy(1.0f);
		int totalMatches = 0;
		for(PSRCATEntry c : test_cand_list) {
			tm.compareToKnownSource(c, ks1, 1.0f);
			totalMatches += tm.getPossibleMatches();
		}
		
		System.out.println("Total matches: " + totalMatches);
		System.out.println("Candidates filtered: " + (100-(float)totalMatches/test_cand_list.size()) + "%");
	}

}
