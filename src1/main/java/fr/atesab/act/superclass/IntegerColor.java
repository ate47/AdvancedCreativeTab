package fr.atesab.act.superclass;

public class IntegerColor {
	private int Red;
	private int Blue;
	private int Green;

	public IntegerColor(int red, int green, int blue) {
		Red = red;
		Green = green;
		Blue = blue;
	}

	public IntegerColor(String HexColor) {
		Red = Integer.valueOf(HexColor.substring(1, 3), 16);
		Green = Integer.valueOf(HexColor.substring(3, 5), 16);
		Blue = Integer.valueOf(HexColor.substring(5, 7), 16);
	}

	public int getBlue() {
		return Blue;
	}

	public int getGreen() {
		return Green;
	}

	public int getRed() {
		return Red;
	}

	public void setBlue(int value) {
		Blue = value;
	}

	public void setGreen(int value) {
		Green = value;
	}

	public void setRed(int value) {
		Red = value;
	}

	public int toInteger() {
		return Blue + 256 * Green + 256 * 256 * Red;
	}
}
