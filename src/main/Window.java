package main;

import java.util.ArrayList;
import java.util.List;

/** I znowu nie wiem czy contents powinno by� Float czy Int */
public class Window {

	int wMax, defaultWMax;
	Float delta;
	int length;
	Boolean adaptive;
	List<Integer> contents;
	int windowSum;
	Float windowMean;
	Float windowVar;
	int observed;
	//Float index;
	Float overallSum, overallMean, overallVar, overallM2;
	
	public Window(int maxSize, Float conf) {
		this(maxSize, conf, false);
	}
	
	public Window(int maxSize, Float conf, Boolean adapt) {
		defaultWMax = maxSize;
		wMax = maxSize;
		delta = conf;
		length = 0;
		adaptive = adapt;
		contents = new ArrayList<Integer>();
		
		windowSum = 0;
		windowMean = 0.0f;
		windowVar = 0.0f;
		
		observed = 0;
		overallSum = 0.0f;
		overallMean = 0.0f;
		overallVar = 0.0f;
		overallM2 = 0.0f;
	}
	
	public void observe(int x) {
		observed++;
		if(canGrow()) {
			contents.add(x);
			length++;
		}
		else {
			removeOldest();
			contents.add(x);
			length++;
			updateStats(x, false);
		}
		
		if(adaptive) {
			adapt();
		}
	}

	private void adapt() {
		if(hasChanged()) {
			System.out.println("There has been a change.");
		}
		
	}

	//?????
	private boolean hasChanged() {
		return false;
	}

	private void updateStats(int x, boolean remove) {
		if(observed > 2) {
			if(remove) {
			windowSum -= x;
			windowMean = windowSum / (float)length;
			//windowVar = WARIANCJA Z LISTY "contents", TODO
			}
			else {
				windowSum += x;
				windowMean = windowSum / (float)length;
				//windowVar = JAK WY�EJ, TODO
				overallSum += x;
				delta = (float)x - overallMean;
				overallMean += delta * ((float)x - overallMean);
				overallM2 += delta * ((float)x - overallMean);
				overallVar = overallM2 / (float)(observed-1);
			}
		}
		else {
			windowSum += x;
			overallSum += x;
		}
	}

	private void removeOldest() {
		if(length>0) {
			Integer oldest = contents.remove(0);
			length--;
			updateStats(oldest, true);
		}
		
	}

	private boolean canGrow() {
		return (length < wMax);
	}
	
	public void reset() {
		wMax = defaultWMax;
		length = 0;
		contents = new ArrayList<Integer>();
		
		windowSum = 0;
		windowMean = 0.0f;
		windowVar = 0.0f;
		
		observed = 0;
		overallSum = 0.0f;
		overallMean = 0.0f;
		overallVar = 0.0f;
		overallM2 = 0.0f;
	}
	
	public void debug() {
		System.out.println("Window Sum: " + windowSum);
		System.out.println("Window Mean: " + windowMean);
		System.out.println("Window Var: " + windowVar);
		System.out.println("Overall Sum: " + overallSum);
		System.out.println("Overall Mean: " + overallMean);
		System.out.println("Overall Var: " + overallVar);
		System.out.println("Window Contents:\n\t" + contents);
	}
	
	public void printWindow() {
		System.out.println(contents);
	}
}
