package com.github.novisoftware.patternDraw.svg;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import com.github.novisoftware.patternDraw.renderer.Renderer;

public class SvgUtil {
	private static final String LF = "\n";

	public static void fileOutput(String filename, ArrayList<String> buff) {
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filename));
			OutputStreamWriter br = new OutputStreamWriter(bos);
			for (String line : buff) {
				br.write(line);
				br.write(LF);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void outSvg(SvgInstruction s, String filename, Renderer renderer) {
		ArrayList<String> out = new ArrayList<String>();

		out.add("<?xml version=\"1.0\"?>");
		out.add("<svg xmlns=\"http://www.w3.org/2000/svg\">");

		renderer.render(null, out, s);

		out.add("</svg>");

		fileOutput(filename, out);
	}
}
