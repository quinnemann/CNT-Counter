package dataStructures;

public class Extremum {
	private double val;
	private boolean isMax;
	private int x;
	
	public Extremum(double val, boolean isMax) {
		super();
		this.val = val;
		this.isMax = isMax;
	}
	
	public Extremum(double val, boolean isMax, int x) {
		super();
		this.val = val;
		this.isMax = isMax;
		this.x = x;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public double getVal() {
		return val;
	}

	public void setVal(double val) {
		this.val = val;
	}

	public boolean isMax() {
		return isMax;
	}

	public void setMin(boolean isMax) {
		this.isMax = isMax;
	}
}
