package test;
import java.util.Random;

import model.Node;

public class NodeSiftTest {

	public static void main(String[] args) {
		Random rand = new Random();
		Node n1 = new Node(0.0f,1.0f);
		Node n2 = new Node(1.0f,2.0f);
		Node n3 = new Node(2.0f,3.0f);
		
		int n1duplicates = 0;
		int n2duplicates = 0;
		int n3duplicates = 0;
		
		float[] distData1 = {0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f};
		float[] distData2 = {1.1f, 1.1f, 1.1f, 1.1f, 1.1f, 1.1f, 1.1f, 1.1f, 1.1f};
		float[] distData3 = {2.1f, 2.2f, 2.3f, 2.4f, 2.4f, 2.5f, 2.6f, 2.7f, 2.8f};
		
		System.out.println("Now testing the sift algorithm on a small sample of examples\n");
		for(float x : distData1) {
			if(n1.isDuplicate(x))
				n1duplicates++;
		}
		for(float x : distData2) {
			if(n2.isDuplicate(x))
				n2duplicates++;
		}
		for(float x : distData3) {
			if(n3.isDuplicate(x))
				n3duplicates++;
		}
		System.out.println("Node 1 Duplicates: " + n1duplicates + ", should be 0 duplicates");
		System.out.println("Node 2 Duplicates: " + n2duplicates + ", should be 8 duplicates");
		System.out.println("Node 3 Duplicates: " + n3duplicates + ", should be 1 duplicate");
		
		n1.reset();
		n2.reset();
		n3.reset();
		n1duplicates = 0;
		n2duplicates = 0;
		n3duplicates = 0;
		
		System.out.println("\nNow moving on to larger scale test\n");
		int randomSamples = 100000;
		float[] distData = new float[randomSamples];
		System.out.println("Creating randomly sampled period data...");
		for(int i=0; i<randomSamples; i++) {
			distData[i] = rand.nextFloat()*3.0f;
		}
		java.util.Arrays.parallelSort(distData);
		System.out.println("Now sifting " + randomSamples + " randomly sampled periods.");
		
		for(float x : distData) {
			if(x < 1.0f) {
				if(n1.isDuplicate(x))
					n1duplicates++;
			}
			else if(x < 2.0f) {
				if(n2.isDuplicate(x))
					n2duplicates++;
			}
			else if(x < 3.0f) {
				if(n3.isDuplicate(x))
					n3duplicates++;
			}
		}
		System.out.println("Node 1 Duplicates: " + n1duplicates);
		System.out.println("Node 2 Duplicates: " + n2duplicates);
		System.out.println("Node 3 Duplicates: " + n3duplicates);
		int dsum = n1duplicates + n2duplicates + n3duplicates;
		System.out.println("Total duplicates: " + dsum);
		System.out.println("Unique periods: " + (randomSamples - dsum));
	}
}
