package com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem;


public class TypeUtil {
	public static class TwoValues {
		public Value a;
		public Value b;

		public TwoValues(Value a, Value b) {
			this.a = a;
			this.b = b;
		}
	}

	/**
	 * ValueInteger → ValueNumeric → ValueFloat の順に昇格する
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	static public TwoValues upCast(Value a, Value b) {
		if (a instanceof ValueAbstractScalar && b instanceof ValueAbstractScalar) {
			if (a.valueType.equals(b.valueType)) {
				return new TwoValues(a, b);
			}

			if (a instanceof ValueInteger) {
				if (b instanceof ValueFloat) {
					Value a2 = ((ValueInteger) a).toValueFloat();
					return new TwoValues(a2, b);
				}
				if (b instanceof ValueNumeric) {
					Value a2 = ((ValueInteger) a).toValueNumeric();
					return new TwoValues(a2, b);
				}
			}
			if (a instanceof ValueNumeric) {
				if (b instanceof ValueInteger) {
					Value b2 = ((ValueInteger) b).toValueNumeric();
					return new TwoValues(a, b2);
				}
				if (b instanceof ValueFloat) {
					Value a2 = ((ValueNumeric) a).toValueFloat();
					return new TwoValues(a2, b);
				}
			}
			if (a instanceof ValueFloat) {
				if (b instanceof ValueInteger) {
					Value b2 = ((ValueInteger) b).toValueFloat();
					return new TwoValues(a, b2);
				}
				if (b instanceof ValueNumeric) {
					Value b2 = ((ValueNumeric) b).toValueFloat();
					return new TwoValues(a, b2);
				}
			}
		}

		return null;
	}
}