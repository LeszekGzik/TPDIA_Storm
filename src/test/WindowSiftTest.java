package test;
import model.Window;
import java.util.*;
import org.apache.commons.math3.stat.descriptive.moment.*;
import org.apache.commons.math3.stat.descriptive.summary.Sum;

public class WindowSiftTest {

	
	public static void main(String[] args) {
		//losowanie liczb
		Random rand = new Random();
		double[] data = new double[1000];
		for(int i = 0; i<data.length; i++) {
			data[i] = rand.nextInt(10) + 1;
		}
		
		Mean mean = new Mean();
		Sum sum = new Sum();
		Variance var = new Variance();
		
		//podsumowanie losowych danych
		System.out.println("\tInput data properties:");
		System.out.println("Data sum: " + sum.evaluate(data));
		System.out.println("Data mean: " + mean.evaluate(data));
		System.out.println("Data var: " + var.evaluate(data));
		
		//test
		int maxWindowSize = 10;
		float userConf = 0.8f;
		Window win = new Window(maxWindowSize, userConf);
		for(double d : data) {
			win.observe((int)d);
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
}
