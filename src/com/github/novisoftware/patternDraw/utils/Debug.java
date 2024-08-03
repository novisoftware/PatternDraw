package com.github.novisoftware.patternDraw.utils;

/**
 * デバッグ用出力。
 *
 */
public class Debug {
	public static boolean ENABLE_DEVELOP_FEATURE = true;
	public static boolean SHOW_DEBUG_SWITCH = true;
	public static boolean enable = false;

	static boolean kindFilter( String s ) {
		// if( s.indexOf( "sorter" )  != -1 ) return false;
		// if( s.indexOf( "grep" )  != -1 ) return false;
		// if( s.indexOf( "dnd" )  != -1 ) return false;

		// return true;
		return false;
	}

	public static void println( String kind, String s ) {
		if (!enable) {
			return;
		}

		if( kindFilter(kind) ) {
			return;
		}
		System.out.println( "--debug-- [" + kind + "] " + s );
	}

	static void print( String kind, String s ) {
		if (!enable) {
			return;
		}
		if( kindFilter(kind) ) {
			return;
		}
		System.out.print( "--debug--   [" + kind + "]" + s );
	}


	static boolean isDisabled( String s ) {
		// return true;
		return false;
	}


	public static void println( String s ) {
		if (!enable) {
			return;
		}
		System.out.println( "--debug--" + s );
	}

	public static void println() {
		if (!enable) {
			return;
		}
		System.out.println();
	}

	public static void print( String s ) {
		if (!enable) {
			return;
		}
		System.out.print( s );
	}

}
