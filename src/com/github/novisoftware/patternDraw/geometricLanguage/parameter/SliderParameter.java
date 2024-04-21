package com.github.novisoftware.patternDraw.geometricLanguage.parameter;

public class SliderParameter extends Parameter implements Int2Double {
	/**
	 * スライダー最小値
	 */
	private double sliderMin;
	/**
	 * スライダー最大値
	 */
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

	/**
	 * int → doubleの変換。
	 *
	 * 欲しいのはdouleの値だがSwingのJSliderはint値を返すので、
	 * オブジェクト作成時に与えられたdoubleの最小値・最大値を使い
	 * double値に変換する。
	 */
	public double int2double(int value, int max) {
		return (sliderMax - sliderMin)*(1.0 * value / max) + sliderMin;
	}
}
