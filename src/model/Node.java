package model;

import java.util.ArrayList;
import java.util.List;

/** nie jestem pewien czy te periods powinny byæ Float czy Int */
public class Node {
	int bins;
	Float minPeriod;
	Float maxPeriod;
	List<Float> periods;
	int observed;
	Float index;
	Float overallSum, overallMean, overallVar, overallM2;
	
	public Node(Float minPeriod, Float maxPeriod, int bins) {
		this.bins = bins;
		this.maxPeriod = maxPeriod;
		this.minPeriod = minPeriod;
		periods = new ArrayList<Float>();
		
		if(bins>0) {
			for(int x = 0; x<bins; x++) {
				periods.add(0.0f);
			}
		}
		else {
			System.out.println("Bins must be > 0!");
		}
		
		observed = 0;
		overallSum = 0.0f;
		overallMean = 0.0f;
		overallVar = 0.0f;
		overallM2 = 0.0f;
	}
	
	public Node(Float minPeriod, Float maxPeriod) {
		this(minPeriod, maxPeriod, 1000001);
	}
	
	public boolean isDuplicate(Float x) {
		observed++;
		updateStats(x);
		if(bins>0) {
			if(x < minPeriod) {
				System.out.println("Cannot sift, value outside period range!");
			}
			else if (x > maxPeriod) {
				System.out.println("Cannot sift, value outside period range!");
			}
			else {
				int periodMicroseconds = Math.round(x*1000);
				
				if((minPeriod < 0) || (maxPeriod > 1.0f)) {
					index = scale(periodMicroseconds, minPeriod, maxPeriod, 0.0f, 1000000.0f);
				}
				else {
					index = (float)periodMicroseconds;
				}
				
				if(periods.get(periodMicroseconds) > 0 ) {
					periods.set(periodMicroseconds, periods.get(periodMicroseconds) +1);
					return true;
				}
				else {
					periods.set(periodMicroseconds, periods.get(periodMicroseconds) +1);
					return false;
				}
			}
		}
		return false;
	}

	private float scale(int x, Float dataMin, Float dataMax, float floor, float ceil) {
		return ((ceil - floor) * (x-dataMin) / (dataMax - dataMin)) + floor;
	}

	private void updateStats(Float x) {
		if(observed > 2) {
			overallSum += x;
			Float delta = x - overallMean;
			overallMean += delta / (float)observed;
			overallM2 += delta * (x - overallMean);
			overallVar = overallM2 / (float)(observed-1);
		}
		else {
			overallSum += x;
		}
	}
	
	public void reset() {
		periods.clear();
		periods = new ArrayList<Float>();
		
		if(bins>0) {
			for(int x = 0; x<bins; x++) {
				periods.add(0.0f);
			}
		}
		else {
			System.out.println("Bins must be > 0!");
		}
		
		observed = 0;
		overallSum = 0.0f;
		overallMean = 0.0f;
		overallVar = 0.0f;
		overallM2 = 0.0f;
	}
	
	public void debug() {
		System.out.println("Observed: " + observed);
		System.out.println("Overall Sum: " + overallSum);
		System.out.println("Overall Mean: " + overallMean);
		System.out.println("Overall Var: " + overallVar);
	}
}
