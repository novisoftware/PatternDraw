package com.github.novisoftware.patternDraw.core.langSpec.typeSystem;

import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;

public class ValueTransform extends Value {
	public ValueTransform(double[][] paraList) {
		super(ValueType.TRANSFORM);
		internal = paraList;
	}

	public ValueTransform() {
		super(ValueType.TRANSFORM);
		internal = new double[3][3];
	}

	@Override
	public String toString() {
		return internal.toString();
	}

	/**
	 * 行列と以下のように対応します。
	 * <pre>
	 * internal[0][0] internal[0][1] internal[0][2]
	 * internal[1][0] internal[1][1] internal[1][2]
	 * internal[2][0] internal[2][1] internal[2][2]
	 * </pre>
	 */
	private double[][] internal;

	public double[][] getInternal() {
		return internal;
	}

	/**
	 *
	 * @param other this × other の other を指定する
	 * @return 行列の積
	 */
	public ValueTransform multiply(ValueTransform other) {
		ValueTransform transformed = new ValueTransform();

		double[][] m1 = this.getInternal();
		double[][] m2 = other.getInternal();

		double[][] o = transformed.getInternal();
		for (int i=0; i<3;i++) {
			for (int j=0; j<3;j++) {
				for (int k=0; k<3;k++) {
					o[i][j] += m1[i][k] * m2[k][j];
				}
			}
		}

		return transformed;
	}

	/**
	 * sameToは一応実装する。
	 * (浮動小数点の同一性を行列に対して判定しても、そんなに意味はない)
	 *
	 * @return 注: 返り値にほぼ意味はない
	 */
	public boolean sameTo(Object o) {
		if (! (o instanceof ValueTransform)) {
			return false;
		}

		double[][] oi = ((ValueTransform)o).getInternal();

		for (int i=0; i<3;i++) {
			for (int j=0; j<3;j++) {
				if (internal[i][j] != oi[i][j]) {
					return false;
				}
			}
		}

		return true;
	}

	public Pos transform(Pos p) {
		double x = p.getX();
		double y = p.getY();

		double[] from = {x, y, 1};
		double[] to = new double[3];

		double[][] m = this.getInternal();

		for (int j=0; j<3;j++) {
			for (int k=0; k<3;k++) {
				to[j] += from[k] * m[k][j];
			}
		}

		return new Pos(to[0], to[1]);
	}

	static public ValueTransform createRotate(double th) {
		ValueTransform transform = new ValueTransform();
		double[][] m = transform.getInternal();
		/*
		m[0][0] = Math.cos(th);
		m[0][1] = -Math.sin(th);
		m[1][0] = Math.sin(th);
		m[1][1] = Math.cos(th);
		m[2][2] = 1;
		*/
		m[0][0] = Math.cos(th);
		m[0][1] = Math.sin(th);
		m[1][0] = -Math.sin(th);
		m[1][1] = Math.cos(th);
		m[2][2] = 1;

		return transform;
	}

	static public ValueTransform createMove(double x, double y) {
		ValueTransform transform = new ValueTransform();
		double[][] m = transform.getInternal();
		m[0][2] = x;
		m[1][2] = y;
		m[2][2] = 1;

		return transform;
	}

	static public ValueTransform createScale(double a, double d) {
		ValueTransform transform = new ValueTransform();
		double[][] m = transform.getInternal();
		m[0][0] = a;
		m[1][1] = d;
		m[2][2] = 1;

		return transform;
	}

	static public ValueTransform createSkew(double th) {
		ValueTransform transform = new ValueTransform();
		double[][] m = transform.getInternal();
		m[0][0] = 1;
		m[0][1] = -Math.tan(th);
		m[1][1] = 1;
		m[2][2] = 1;

		return transform;
	}
}
