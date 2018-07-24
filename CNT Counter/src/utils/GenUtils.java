package utils;

import java.util.ArrayList;
import java.util.Collections;

public class GenUtils {
	
	//get the average of an int array
	public static double average(int[] arr) {
		int sum = 0;
		for(int n : arr) {
			sum += n;
		}
		return sum / (double)arr.length;
	}
	
	//get the average of an int arraylist
	public static double average(ArrayList<Integer> arr) {
		int sum = 0;
		for(int n : arr) {
			sum += n;
		}
		return sum / (double)arr.size();
	}
	
	//get the average of a double array
	public static double average(double[] arr) {
		double sum = 0;
		for(double n : arr) {
			sum += n;
		}
		return sum / arr.length;
	}
	
	//get the average differents between elements of an int array
	public static double averageDiff(ArrayList<Integer> arr) {
		Collections.sort(arr);
		
		ArrayList<Integer> diffs = new ArrayList<Integer>();
		for (int i = 1; i < arr.size(); i++) {
			diffs.add(arr.get(i) - arr.get(i - 1));
		}
		
		return average(diffs);
	}
	
	//get the maximum element of an int array
	public static int max(int[] arr) {
		int max = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] > max)
				max = arr[i];
		}
		return max;
	}
	
	//get the maximum element of a double array
	public static double max(double[] arr) {
		double max = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] > max)
				max = arr[i];
		}
		return max;
	}
	
	//get the maximum element of an int arraylist
	public static int max(ArrayList<Integer> arr) {
		int max = arr.get(0);
		for (int i = 1; i < arr.size(); i++) {
			if (arr.get(i) > max)
				max = arr.get(i);
		}
		return max;
	}
	
	//get the minimum element of an int array
	public static int min(int[] arr) {
		int min = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] < min)
				min = arr[i];
		}
		return min;
	}
	
	//get the mimumum element of a double array
	public static double min(double[] arr) {
		double min = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] < min)
				min = arr[i];
		}
		return min;
	}
	
	//get the minimum element of an integer arraylist
	public static int min(ArrayList<Integer> arr) {
		int min = arr.get(0);
		for (int i = 1; i < arr.size(); i++) {
			if (arr.get(i) < min)
				min = arr.get(i);
		}
		return min;
	}
	
	//round a double to 3 decimal points
	public static double roundThousandths(double x) {
		x *= 1000;
		long y = Math.round(x);
		x = y / 1000.0;
		return x;
	}
	
	//get radians from degrees
	public static double degreeToRadian(double degree) {
		return degree * Math.PI / 180.0;
	}
	
	//get the number of peaks in an array
	public static int numPeaks(double[] vals) {
		int count = 0;
		for (int i = 1; i < vals.length - 1; i++) {
			if (vals[i] > vals[i - 1] && vals[i] > vals[i+1]) {
				count++;
			}
		}
		
		return count;
	}
 }
