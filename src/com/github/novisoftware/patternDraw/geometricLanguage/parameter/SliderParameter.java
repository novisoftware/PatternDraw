package com.github.novisoftware.patternDraw.geometricLanguage.parameter;

public class SliderParameter extends Parameter implements Int2Double {
	private double sliderMin;
	private double sliderMax;

	/**
	 *
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param sliderMin
	 * @param sliderMax
	 */
	public SliderParameter(String name, String description, String defaultValue, double sliderMin, double sliderMax) {
		super(name, description, defaultValue);
		this.sliderMin = sliderMin;
		this.sliderMax = sliderMax;
	}

	public double int2double(int value, int max) {
		return (sliderMax - sliderMin)*(1.0 * value / max) + sliderMin;
	}
}
