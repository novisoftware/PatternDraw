package com.github.novisoftware.patternDraw.geometry;

public class Pos {
	private double x;
	private double y;

	public Pos(double x, double y) {
		this.setX(x);
		this.setY(y);
	}

	/**
	 * @return x
	 */
	public double getX() {
		return x;
	}

	/**
	 * @param x セットする x
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return y
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param y セットする y
	 */
	public void setY(double y) {
		this.y = y;
	}


	public Pos mix(Pos pos2, double ratio) {
		double r0 = 1 - ratio;
		return new Pos(this.x*r0 + pos2.x*ratio,
				this.y*r0 + pos2.y*ratio);
	}
}