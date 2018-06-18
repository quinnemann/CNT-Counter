package utils;

import java.util.ArrayList;

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
	
	public static int max(int[] arr) {
		int max = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] > max)
				max = arr[i];
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
	
	public static double roundThousandths(double x) {
		x *= 1000;
		long y = Math.round(x);
		x = y / 1000.0;
		return x;
	}
}
