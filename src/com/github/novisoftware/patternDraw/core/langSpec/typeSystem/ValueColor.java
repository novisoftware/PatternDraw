package com.github.novisoftware.patternDraw.core.langSpec.typeSystem;

public class ValueColor extends Value {
	public ValueColor(java.awt.Color c) {
		super(ValueType.COLOR);
		internal = c;
	}

	@Override
	public String toString() {
		return ValueColor.colorToString(internal);
	}

	private java.awt.Color internal;

	public java.awt.Color getInternal() {
		return internal;
	}

	public boolean sameTo(Object o) {
		if (!(o instanceof java.awt.Color)) {
			return false;
		}
		return this.internal.equals((java.awt.Color)o);
	}
	
	/**
	 * @param color
	 * @return HTMLカラー形式の文字列に変換
	 */
	public static String colorToString(java.awt.Color color) {
		if ((color.getAlpha() & 0xFF) == 0xFF) {
			return String.format("#%06X", (color.getRGB() & 0xFFFFFF));
		} else {
			return String.format("#%02X%06X", (color.getAlpha() & 0xFF), (color.getRGB() & 0xFFFFFF));
		}
	}
}
