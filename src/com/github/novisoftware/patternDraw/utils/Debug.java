package com.github.novisoftware.patternDraw.utils;

/**
 * デバッグ用出力。
 *
 */
public class Debug {
	static boolean kindFilter( String s ) {
		// if( s.indexOf( "sorter" )  != -1 ) return false;
		// if( s.indexOf( "grep" )  != -1 ) return false;
		// if( s.indexOf( "dnd" )  != -1 ) return false;

		// return true;
		return false;
	}

	public static void println( String kind, String s ) {
		if( kindFilter(kind) ) {
			return;
		}
		System.out.println( "--debug-- [" + kind + "] " + s );
	}

	static void print( String kind, String s ) {
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
		if( isDisabled(s) ) {
			return;
		}
		System.out.println( "--debug--" + s );
	}

	public static void println() {
		System.out.println();
	}

	public static void print( String s ) {
		if( isDisabled(s ) ) {
			return;
		}
		System.out.print( s );
	}

}
