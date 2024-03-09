package com.github.novisoftware.patternDraw.geometry;

import java.util.ArrayList;

public class Line {
	public double x0;
	public double y0;
	public double x1;
	public double y1;

	public Line(double x0, double y0, double x1, double y1) {
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
	}

	public Line(Pos a, Pos b) {
		this.x0 = a.getX();
		this.y0 = a.getY();
		this.x1 = b.getX();
		this.y1 = b.getY();
	}

	// y = a * x + b で表したときの a。
	public double a() {
		if (this.x0 == this.x1) {
			return Double.NaN;
		}
		return (this.y1 - this.y0) / (this.x1 - this.x0);
	}

	// y = a * x + b で表したときの b。
	public double b() {
		if (this.x0 == this.x1) {
			return Double.NaN;
		}

		return this.y0 - a() * this.x0;
	}

	public ArrayList<Pos> splitToPoints(int n) {
		ArrayList<Pos> positions = new ArrayList<Pos>();

		for (int i = 0; i<=n;i++) {
			double x = x1 * i / n + x0 * (n - i) / n;
			double y = y1 * i / n + y0 * (n - i) / n;

			positions.add(new Pos(x,y));
		}

		return positions;
	}

	/**
	 * 2本の直線の交叉点を取得する。
	 * (与えられた線分を含む直線が交叉する点を返す)
	 *
	 * @param other
	 * @return
	 */
	public Pos intersection(Line other) {
		if (this.x0 == this.x1 && other.x0 == other.x1) {
			// 垂直の線が2本。交点は無いので null を返す。
			return null;
		}

		if (this.x0 == this.x1) {
			double x = this.x0;
			double y = other.a() * x + other.b();
			return new Pos(x, y);
		}
		if (other.x0 == other.x1) {
			double x = other.x0;
			double y = this.a() * x + this.b();
			return new Pos(x, y);
		}
		else {
			// y=ax+bと,y=cx+dの交点
			// x=-(b-d)/(a-c)
			// y=(ad-bc)/(a-c)
			double a = this.a();
			double b = this.b();
			double c = other.a();
			double d = other.b();

			if (a == c) {
				// 傾きが同じ。交点は無いので null を返す。
				// (直線全体が一致か、交わらない)
				return null;
			}

			double x = -(b-d)/(a-c);
			double y = (a*d-b*c)/(a-c);

			return new Pos(x, y);
		}
	}
}