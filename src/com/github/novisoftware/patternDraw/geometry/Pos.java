package com.github.novisoftware.patternDraw.geometry;

import java.util.ArrayList;

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

	public double atan(Pos other) {
		return Math.atan2(other.getX() - this.getX(),
				other.getY() - this.getY());
	}

	private final static double sq(double x) {
		return x*x;
	}

	public double distance(Pos other) {
		return Math.sqrt(sq(other.getX() - this.getX()) + sq(other.getY() - this.getY()));
	}

	static public ArrayList<Pos> move_to_origin(ArrayList<Pos> src) {
		return move(src, new Pos(-src.get(0).x, -src.get(0).y));
	}

	static public ArrayList<Pos> move(ArrayList<Pos> src, Pos startPos) {
		ArrayList<Pos> ret = new ArrayList<Pos>();

		for (Pos p: src) {
			ret.add(new Pos(p.x + startPos.x, p.y + startPos.y));
		}

		return ret;
	}


	static public ArrayList<Pos> scale(ArrayList<Pos> src, double ratio) {
		ArrayList<Pos> ret = new ArrayList<Pos>();

		for (Pos p: src) {
			ret.add(new Pos(p.x * ratio, p.y * ratio));
		}

		return ret;
	}

	static public ArrayList<Pos> rotate(ArrayList<Pos> src, double th) {
		ArrayList<Pos> ret = new ArrayList<Pos>();

		for (Pos p: src) {
			double x = p.x;
			double y = p.y;
			double newX = x * Math.cos(th) - y * Math.sin(th);
			double newY = x * Math.sin(th) + y * Math.cos(th);

			ret.add(new Pos(newX, newY));
		}

		return ret;
	}

}