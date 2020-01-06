package fr.atesab.act.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import fr.atesab.act.gui.modifier.GuiColorModifier;
import fr.atesab.act.gui.modifier.GuiModifier;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class ColorList {
	private GuiScreen parent;
	private Minecraft mc;
	private FontRenderer fontRenderer;
	private List<Integer> list;
	private String title;
	private int maxElement;
	private int sizeX;
	public int x;
	public int y;

	public ColorList(GuiScreen parent, int x, int y, int sizeX, int[] list, String title, int maxElement) {
		this.list = new ArrayList<>(list.length);
		Arrays.stream(list).forEach(this.list::add);
		this.title = title;
		if (maxElement < 1)
			throw new IllegalArgumentException("maxElement must be positive");
		this.maxElement = maxElement;
		this.sizeX = sizeX;
		this.x = x;
		this.y = y;
		this.mc = Minecraft.getInstance();
		this.fontRenderer = this.mc.fontRenderer;
		this.parent = parent;
	}

	public int[] getColors() {
		return list.stream().mapToInt(i -> i).toArray();
	}

	public void drawNext(int mouseX, int mouseY, float zLevel) {
		drawNext(mouseX, mouseY, zLevel, 0, 0);
	}

	public void drawNext(int mouseX, int mouseY, float zLevel, int offsetX, int offsetY) {
		offsetX += this.x;
		offsetY += this.y + fontRenderer.FONT_HEIGHT + 1;
		int i;
		for (i = 0; i < list.size(); i++) {
			int x = offsetX + (16) * (i % sizeX);
			int y = offsetY + (16) * (i / sizeX);
			if (GuiUtils.isHover(x, y, 15, 15, mouseX, mouseY)) {
				List<String> text = Arrays.asList(
						TextFormatting.GOLD + "[" + TextFormatting.YELLOW + I18n.format("gui.act.leftClick")
								+ TextFormatting.GOLD + "] " + TextFormatting.YELLOW + I18n.format("gui.act.edit"),
						TextFormatting.GOLD + "[" + TextFormatting.YELLOW + I18n.format("gui.act.rightClick")
								+ TextFormatting.GOLD + "] " + TextFormatting.YELLOW + I18n.format("gui.act.delete"));
				int width = text.stream().mapToInt(fontRenderer::getStringWidth).max().getAsInt(),
						height = text.size() * (fontRenderer.FONT_HEIGHT + 1);
				Tuple<Integer, Integer> pos = GuiUtils.getRelativeBoxPos(mouseX, mouseY, width, height, parent.width,
						parent.height);
				GuiUtils.drawBox(pos.a, pos.b, width, height, zLevel);
				pos.b += 1;
				text.forEach(s -> {
					fontRenderer.drawString(s, pos.a, pos.b, 0xffffffff);
					pos.b += 1 + fontRenderer.FONT_HEIGHT;
				});
			}
		}
	}

	public void draw(int mouseX, int mouseY, float zLevel) {
		draw(mouseX, mouseY, zLevel, 0, 0);
	}

	public void draw(int mouseX, int mouseY, float zLevel, int offsetX, int offsetY) {
		offsetX += this.x;
		offsetY += this.y;
		GuiUtils.drawCenterString(fontRenderer, title, offsetX + (sizeX * (16)) / 2, offsetY, 0xffffffff);
		offsetY += fontRenderer.FONT_HEIGHT + 1;
		int i;
		for (i = 0; i < list.size(); i++) {
			int x = offsetX + (16) * (i % sizeX);
			int y = offsetY + (16) * (i / sizeX);
			int c = list.get(i) + 0xff000000;
			GuiUtils.drawGradientRect(x, y, x + 15, y + 15, c, c, zLevel);
		}
		if (i < maxElement) {
			int x = offsetX + (16) * (i % sizeX);
			int y = offsetY + (16) * (i / sizeX);
			boolean flag = GuiUtils.isHover(x, y, 15, 15, mouseX, mouseY);
			int c = flag ? 0xffbbbbbb : 0xff999999;
			GuiUtils.drawGradientRect(x, y, x + 15, y + 15, c, c, zLevel);
			GuiUtils.drawCenterString(fontRenderer, "+", x + 8, y, flag ? 0xff00ff00 : 0xff00aa00, 16);
		}
	}

	public void mouseClick(int mouseX, int mouseY, int mouseButton) {
		int i;
		for (i = 0; i < list.size(); i++) {
			int x = this.x + (16) * (i % sizeX);
			int y = this.y + fontRenderer.FONT_HEIGHT + 1 + (16) * (i / sizeX);
			int c = list.get(i);
			if (GuiUtils.isHover(x, y, 15, 15, mouseX, mouseY)) {
				if (mouseButton == 0) {
					int fi = i;
					mc.displayGuiScreen(new GuiColorModifier(parent, nc -> list.set(fi, nc), c, 0xffffff));
				} else if (mouseButton == 1)
					list.remove(i);
				else
					return;
				GuiModifier.playClick();
				return;
			}
		}
		if (i < maxElement && mouseButton == 0) {
			int x = this.x + (16) * (i % sizeX);
			int y = this.y + fontRenderer.FONT_HEIGHT + 1 + (16) * (i / sizeX);
			if (GuiUtils.isHover(x, y, 15, 15, mouseX, mouseY)) {
				GuiModifier.playClick();
				list.add(0xff000000 | new Random().nextInt(0x1000000));
			}
		}
	}
}
