package test;
import model.Window;
import java.util.*;

public class WindowSiftTest {

	
	public static void main(String[] args) {
		//losowanie liczb
		Random rand = new Random();
		int[] data = new int[1000];
		for(int i = 0; i<data.length; i++) {
			data[i] = rand.nextInt(10) + 1;
		}
		
		//podsumowanie losowych danych
		System.out.println("\tInput data properties:");
		System.out.println("Data sum: " + calculateSum(data));
		System.out.println("Data mean: " + calculateMean(data));
		System.out.println("Data var: " + calculateVariance(data));
		
		//test
		int maxWindowSize = 10;
		float userConf = 0.8f;
		Window win = new Window(maxWindowSize, userConf);
		for(int d : data) {
			win.observe(d);
		}
		System.out.println("\n\tAs observed by window class:");
		win.debug();
		
		//test dopasowania
		win.reset();
		int samples = 10;
		List<Integer> matches = new ArrayList<Integer>();
		
		for(int i = 0; i<samples; i++) {
			int next = rand.nextInt(10) + 1;
			win.observe(next);
			if(win.count(next)>1) {
				matches.add(next);
			}
		}
		
		System.out.println("\n\tMatching test:");
		win.debug();
		System.out.println("Match count: " + matches.size());
		System.out.println("\t" + matches);
	}
	
	private static float calculateVariance(int[] data) {
		float var = 0.0f;
		float temp;
		float mean = calculateMean(data);
		
		for(int i=0; i<data.length; i++) {
			temp = (float)data[i] - mean;
			var += temp*temp;
		}
		return var/data.length;
	}
	
	private static float calculateMean(int[] data) {
		return calculateSum(data)/data.length;
	}

	private static float calculateSum(int[] data) {
		float sum = 0.0f;
		for(int i=0; i<data.length; i++) {
			sum+=data[i];
		}
		return sum;
	}
}
