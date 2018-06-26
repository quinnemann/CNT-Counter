package utils;

import java.util.ArrayList;
import java.util.Collections;

public class GenUtils {
	
	public static double average(int[] arr) {
		int sum = 0;
		for(int n : arr) {
			sum += n;
		}
		return sum / (double)arr.length;
	}
	
	public static double average(ArrayList<Integer> arr) {
		int sum = 0;
		for(int n : arr) {
			sum += n;
		}
		return sum / (double)arr.size();
	}
	
	public static double averageDiff(ArrayList<Integer> arr) {
		Collections.sort(arr);
		
		ArrayList<Integer> diffs = new ArrayList<Integer>();
		for (int i = 1; i < arr.size(); i++) {
			diffs.add(arr.get(i) - arr.get(i - 1));
		}
		
		return average(diffs);
	}
	
	public static int max(int[] arr) {
		int max = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] > max)
				max = arr[i];
		}
		return max;
	}
	
	public static int max(ArrayList<Integer> arr) {
		int max = arr.get(0);
		for (int i = 1; i < arr.size(); i++) {
			if (arr.get(i) > max)
				max = arr.get(i);
		}
		return max;
	}
	
	public static int min(int[] arr) {
		int min = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] < min)
				min = arr[i];
		}
		return min;
	}
	
	public static int min(ArrayList<Integer> arr) {
		int min = arr.get(0);
		for (int i = 1; i < arr.size(); i++) {
			if (arr.get(i) < min)
				min = arr.get(i);
		}
		return min;
	}
	
	public static double roundThousandths(double x) {
		x *= 1000;
		long y = Math.round(x);
		x = y / 1000.0;
		return x;
	}
}
