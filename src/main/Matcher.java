package main;

import java.io.*;
import java.util.List;

import javax.xml.transform.Source;

public class Matcher {
	Float[] harmonics = {1.0f, 0.5f, 0.3f, 0.25f, 0.2f, 0.16f, 0.125f, 0.0625f, 0.03125f};
	Float accuracy = 0.5f;
	Float DMPercentAccuracy = 0.5f;
	String outputFile;
	int possibleMatches = 0;
	int totalComparisons = 0;
	
	public Matcher(String output) {
		outputFile = output;
	}
	
	public int findSearchIndex(Source candidate, List<Source> knownsources) {
		return divideAndConquerSearch(0, knownsources.size(), candidate, knownsources);
	}
	
	/* Source = placeholder */
	public int divideAndConquerSearch(int start, int end, Source candidate, List<Source> knownsources) {
		if(end - start == 2) {
			return start+1;
		}
		else if(end-start == 1) {
			return start;
		}
		
		int midpoint = (int)(Math.ceil(((double)end + (double)start))/2);
		Source sourceAtMidpoint = knownsources.get(midpoint);
		int knownSourceSortAttribute = sourceAtMidpoint.refstep; //placeholder
		
		if (candidate.refsep < knownSourceSortAttribute) {
			return divideAndConquerSearch(start,midpoint,candidate,knownsources);
		}
		else if (candidate.refsep > knownSourceSortAttribute) {
			return divideAndConquerSearch(midpoint, end, candidate, knownsources);
		}
		else {
			return midpoint;
		}
	}
	
	/* Source = placeholder */
	public void compare(Source candidate, List<Source> knownsources, int index, float maxSep) {
		Source knownSource = knownsources.get(index);
		float sourceSep = (float)(candidate.calcstep(knownSource.coord));
		
		if(sourceSep <= 2*maxSep) {
			compareToKnownSource(candidate,knownSource,sourceSep);
			compareRight(candidate, knownsources, index, maxSep);
			compareLeft(candidate, knownsources, index, maxSep);
		}
	}
	
	public void compareRight(Source candidate, List<Source> knownsources, int index, float maxSep) {
		if((index + 1 < knownsources.size()) && (index+1 > -1)) {
			Source knownSource = knownsources.get(index + 1);
			float sourceSep = (float)(candidate.calcstep(knownSource.coord));
			if(sourceSep <= 2*maxSep) {
				compareToKnownSource(candidate,knownSource,sourceSep);
				compareRight(candidate, knownsources, index, maxSep);
			}
		}
	}
	
	public void compareLeft(Source candidate, List<Source> knownsources, int index, float maxSep) {
		if((index - 1 < knownsources.size()) && (index-1 > -1)) {
			Source knownSource = knownsources.get(index + 1);
			float sourceSep = (float)(candidate.calcstep(knownSource.coord));
			if(sourceSep <= 2*maxSep) {
				compareToKnownSource(candidate,knownSource,sourceSep);
				compareLeft(candidate, knownsources, index, maxSep);
			}
		}
	}
	
	public void compareToKnownSource(Source candidate, Source knownSource, float maxSep) {
		totalComparisons++;
		String ksPO = knownSource.getParameter("PO");
		String ksRA = knownSource.getParameter("RAJ");
		String ksDEC = knownSource.getParameter("DEC");
		String ksDM = knownSource.getParameter("DM");
		String ksName = knownSource.sourceName;
		
		if(ksDM==null) {
			ksDM = "*";
		}
		
		String candPO = candidate.getParameter("PO");
		String candRA = candidate.getParameter("RAJ");
		String candDEC = candidate.getParameter("DEC");
		String candDM = candidate.getParameter("DM");
		String candName = candidate.sourceName;
		
		/* co to kurwa za dziwne zmiany z liczb na stringi, jebaæ Pythona */
		if(candPO==null) {
			candPO = "0.0";
		}
		else if (candPO.contains("*")) {
			candPO = "0.0";
		}
		
		if(candDM==null) {
			candDM = "*";
		}
		
		if(!(ksPO == null)&&!(candPO == null)) {
			float acc = (float)(accuracy/100 * Float.parseFloat(candPO)); 
			for(int i=0; i<harmonics.length; i++) {
				if(!ksPO.equals("*")) {
					
					//?????!!?
					boolean searchCond = (Float.parseFloat(candPO) > Float.parseFloat(ksPO)*harmonics[i] - acc)
							&& (Float.parseFloat(candPO) < Float.parseFloat(ksPO)*harmonics[i] + acc);
					if((!candDM.equals("unknown")) &&  (!candDM.equals("*")) && (!ksDM.equals("unknown")) && (!candDM.equals("*"))) {
						float dmAcc = DMPercentAccuracy / 100 * Float.parseFloat(candDM);
						searchCond = searchCond && (Float.parseFloat(ksDM) > Float.parseFloat(candDM) - dmAcc) && (Float.parseFloat(ksDM) < Float.parseFloat(candDM) + dmAcc);
						if(searchCond) {
							recordPossibleMatch(candName, candRA, candDEC, candPO, candDM, ksName, ksRA, ksDEC, ksPO, ksDM, harmonics[i], maxSep);
						}
					}
				}
			}
		}
		
	}

	private void recordPossibleMatch(String candName, String candRA, String candDEC, String candPO, String candDM,
			String ksName, String ksRA, String ksDEC, String ksPO, String ksDM, Float harmonicN, float thetaSep) {
		possibleMatches++;
		Float harmonicNumber = 1/harmonicN;
		Float harmonicPeriod = Float.parseFloat(ksPO) * harmonicN;
		Float harmonicPeriodDivCandidatePeriod = Float.parseFloat(ksPO) * harmonicN / Float.parseFloat(candPO);
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(outputFile), true)));
			out.println("POSSIBLE MATCH FOR: \n" + candName + "\n");
			out.println("Candidate -> RAJ: " + candRA + " DECJ:" + candDEC +
                    " P0:" + candPO + " DM:" + candDM + "\n");
			out.println("PSR: " + ksName + " -> RAJ: " + ksRA + " DECJ:" + ksDEC +
                    " P0:" + ksPO + " DM:" + ksDM + "\n");
			out.println("Harmonic Number = " + harmonicNumber + "\n");
			out.println("Harmonic Period = " + harmonicPeriod + "\n");
			out.println("Harmonic Period/Candidate Period = " + harmonicPeriodDivCandidatePeriod + "\n");
			out.println("Angular separation of psr and cand (deg): " + thetaSep + "\n");
			out.println("@-----------------------------------------------------------------" + "\n");
			out.close();
		} catch(IOException ioe) {
			
		}
	}
}
