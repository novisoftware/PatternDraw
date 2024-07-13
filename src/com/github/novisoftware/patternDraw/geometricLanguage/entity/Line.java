package com.github.novisoftware.patternDraw.geometricLanguage.entity;

import java.util.ArrayList;

public class Line {
	public final double x0;
	public final double y0;
	public final double x1;
	public final double y1;

	public final Pos from;
	public final Pos to;
	
	public Line(double x0, double y0, double x1, double y1) {
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
		this.from = new Pos(x0, y0);
		this.to = new Pos(x0, y0);
	}

	public Line(Pos a, Pos b) {
		this.x0 = a.getX();
		this.y0 = a.getY();
		this.x1 = b.getX();
		this.y1 = b.getY();
		this.from = a;
		this.to = b;
	}

	public Line translateLine(double x, double y) {
		return new Line(this.x0 + x, this.y0 + y, this.x1 + x, this.y1 + y);
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

	private Double sectionOnLine(double a0_, double a1_, double b0_, double b1_) {
		double a0 = 0;
		double a1 = 0;
		if (a0_ < a1_) {
			a0 = a0_;
			a1 = a1_;
		} else {
			a0 = a1_;
			a1 = a0_;
		}
		
		double b0 = 0;
		double b1 = 0;
		if (b0_ < b1_) {
			b0 = b0_;
			b1 = b1_;
		} else {
			b0 = b1_;
			b1 = b0_;
		}
		
		if (a1 < b0) {
			return null;
		}
		if (b1 < a0) {
			return null;
		}
		
		return (b0 + a1) / 2;
	}
	
	private double min(double a, double b) {
		if (a < b) {
			return a;
		}
		else {
			return b;
		}
	}

	private double max(double a, double b) {
		if (a > b) {
			return a;
		}
		else {
			return b;
		}
	}

	double MY_EPSILON = 1e-9;
	
	private boolean aprox_eq(double a, double b) {
		if ( Math.abs(a - b) <  MY_EPSILON ) {
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * 2本の直線の交叉点を取得する。
	 * (線分を延長しないと交わらない場合は null を返す)
	 *
	 * @param other
	 * @return
	 */
	public Pos crossPoint(Line other) {
		if (this.x0 == this.x1 && other.x0 == other.x1) {
			// 垂直の線が2本

			if (this.x0 == other.x0) {
				// 同一直線上の線分
				Double y = sectionOnLine(this.y0, this.y1, other.y0, other.y1);
				if (y == null) {
					return null;
				}
				return new Pos(this.x0, y);
			}
			// 交点は無いので null を返す。
			return null;
		}

		// 片方が垂直の線
		if (this.x0 == this.x1 || other.x0 == other.x1) {
			if ((this.x0 == this.x1 && this.y0 == this.y1) ||
					 (other.x0 == other.x1 && other.y0 == other.y1 )) {
				// 片方が点の場合、とりあえず 0 を返す。
				return null;
			}
			// x と y を入れ替えて計算を呼び出す。
			
			Line wkLine1 = new Line(this.y0, this.x0, this.y1, this.x1);
			Line wkLine2 = new Line(other.y0, other.x0, other.y1, other.x1);
			Pos wkPos = wkLine1.crossPoint(wkLine2);
			if (wkPos == null) {
				return null;
			}
			return new Pos(wkPos.getY(), wkPos.getX());
		}
		
		/*
		if (this.x0 == this.x1) {
			double x = this.x0;
			double y = other.a() * x + other.b();
			
			if (min(other.y0, other.y1) <= y && y <= max(other.y0, other.y1) ) {
				return new Pos(x, y);
			} else {
				return null;
			}
		}
		if (other.x0 == other.x1) {
			double x = other.x0;
			double y = this.a() * x + this.b();

			if (min(this.y0, this.y1) <= y && y <= max(this.y0, this.y1) ) {
				return new Pos(x, y);
			} else {
				return null;
			}
		}
		else
		*/
		
		{
			// y=ax+bと,y=cx+dの交点
			// x=-(b-d)/(a-c)
			// y=(ad-bc)/(a-c)
			double a = this.a();
			double b = this.b();
			double c = other.a();
			double d = other.b();

			if (a == c) {
				// 傾きが同じ。

				Double x = sectionOnLine(this.x0, this.x1, other.x0, other.x1);
				Double y = sectionOnLine(this.y0, this.y1, other.y0, other.y1);
				if (x != null && y != null) {
					// 仮に作成した「交点」の傾きが一致していれば交点を返す。

					double aa0 = new Line(x, y, this.x0, this.y0).a();
					double aa1 = new Line(x, y, this.x1, this.y1).a();
					if (this.aprox_eq(aa0, a) || this.aprox_eq(aa1, a)) {
						return new Pos(x, y);
					}
					else {
						return null;
					}
				}
				
				// 交点は無いので null を返す。
				// (直線全体が一致か、交わらない)
				return null;
			}

			double x = -(b-d)/(a-c);
			double y = (a*d-b*c)/(a-c);

			if (min(this.x0, this.x1) - MY_EPSILON <= x && x <= max(this.x0, this.x1) + MY_EPSILON
				&& min(this.y0, this.y1) - MY_EPSILON <= y && y <= max(this.y0, this.y1) + MY_EPSILON
				&& min(other.x0, other.x1) - MY_EPSILON <= x && x <= max(other.x0, other.x1) + MY_EPSILON
				&& min(other.y0, other.y1) - MY_EPSILON <= y && y <= max(other.y0, other.y1) + MY_EPSILON) {
				return new Pos(x, y);
			} else {
				return null;
			}
		}
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