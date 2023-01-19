package fr.atesab.act.gui.modifier;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import fr.atesab.act.gui.ColorList;
import fr.atesab.act.gui.selector.GuiButtonListSelector;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import fr.atesab.act.utils.ItemUtils.ExplosionInformation;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;

public class GuiFireworksModifer extends GuiListModifier<NBTTagCompound> {
	private static class FireworkMainListElement extends ListElement {
		private GuiTextField flight;
		private int value;
		private boolean err;
		private String title;

		public FireworkMainListElement(int flight) {
			super(200, 29);
			title = I18n.format("item.fireworks.flight");
			if (title.endsWith(":"))
				title = title.substring(0, title.length() - 1);
			title += " : ";
			int l = fontRenderer.getStringWidth(title + " : ") + 5;
			this.flight = new GuiTextField(0, fontRenderer, l, 2, 196 - l, 16);
			this.flight.setMaxStringLength(6);
			this.flight.setText("" + flight);

		}

		@Override
		public void init() {
			flight.setFocused(false);
			super.init();
		}

		@Override
		public boolean isFocused() {
			return flight.isFocused();
		}

		@Override
		public void draw(int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
			GuiUtils.drawRelative(flight, offsetX, offsetY);
			GuiUtils.drawString(fontRenderer, title, offsetX, offsetY, (err ? Color.RED : Color.WHITE).getRGB(),
					flight.height);
			super.draw(offsetX, offsetY, mouseX, mouseY, partialTicks);
		}

		@Override
		public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
			flight.mouseClicked(mouseX, mouseY, mouseButton);
			if (GuiUtils.isHover(flight, mouseX, mouseY) && mouseButton == 1)
				flight.setText("");
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}

		@Override
		public void update() {
			flight.updateCursorCounter();
			try {
				value = flight.getText().isEmpty() ? 0 : Integer.valueOf(flight.getText());
				err = false;
			} catch (NumberFormatException e) {
				err = true;
			}
			super.update();
		}

		@Override
		public void keyTyped(char typedChar, int keyCode) {
			flight.textboxKeyTyped(typedChar, keyCode);
			super.keyTyped(typedChar, keyCode);
		}
	}

	private static class ExplosionListElement extends ListElement {
		private ExplosionInformation exp;
		private GuiFireworksModifer parent;

		public ExplosionListElement(GuiFireworksModifer parent, NBTTagCompound expData) {
			super(204, 29);
			this.exp = ItemUtils.getExplosionInformation(expData);
			this.parent = parent;
			buttonList.add(new GuiButton(0, 0, 0, 100, 20, I18n.format("gui.act.modifier.meta.explosion")));
			buttonList.add(new RemoveElementButton(parent, 101, 0, 20, 20, this));
			buttonList.add(new AddElementButton(parent, 122, 0, 20, 20, this, parent.builder));
			buttonList.add(new AddElementButton(parent, 143, 0, 60, 20, I18n.format("gui.act.give.copy"), this,
					() -> new ExplosionListElement(parent, exp.getTag())));
		}

		@Override
		public boolean match(String search) {
			String s = search.toLowerCase();
			return I18n.format("gui.act.modifier.type").toLowerCase().contains(s)
					|| I18n.format("item.fireworksCharge.type." + exp.getType()).toLowerCase().contains(s)
					|| I18n.format("item.fireworksCharge.trail").toLowerCase().contains(s)
					|| I18n.format("item.fireworksCharge.flicker").toLowerCase().contains(s)
					|| I18n.format("gui.act.modifier.meta.explosion.color").toLowerCase().contains(s)
					|| I18n.format("gui.act.modifier.meta.explosion.fadeColor").toLowerCase().contains(s);
		}

		@Override
		protected void actionPerformed(GuiButton button) {
			mc.displayGuiScreen(new GuiExplosionModifier(parent, exp -> this.exp = exp, exp));
			super.actionPerformed(button);
		}

		@Override
		public void drawNext(int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
			if (GuiUtils.isHover(0, 0, 200, 20, mouseX, mouseY)) {
				List<String> data = new ArrayList<>();
				String type = I18n.format("gui.act.modifier.type") + " : " + EnumChatFormatting.YELLOW
						+ I18n.format("item.fireworksCharge.type." + exp.getType());
				String trail = I18n.format("item.fireworksCharge.trail");
				String flicker = I18n.format("item.fireworksCharge.flicker");
				String color = I18n.format("gui.act.modifier.meta.explosion.color") + " : ";
				String fadeColor = I18n.format("gui.act.modifier.meta.explosion.fadeColor") + " : ";
				data.add(type);
				int width = fontRenderer.getStringWidth(type);
				if (exp.isTrail()) {
					data.add(trail);
					width = Math.max(width, fontRenderer.getStringWidth(trail));
				}
				if (exp.isFlicker()) {
					data.add(flicker);
					width = Math.max(width, fontRenderer.getStringWidth(flicker));
				}
				if (exp.getColors().length != 0) {
					data.add(color);
					width = Math.max(width, (fontRenderer.FONT_HEIGHT + 1) * exp.getColors().length
							+ fontRenderer.getStringWidth(color));
				}
				if (exp.getFadeColors().length != 0) {
					data.add(fadeColor);
					width = Math.max(width, (fontRenderer.FONT_HEIGHT + 1) * exp.getFadeColors().length
							+ fontRenderer.getStringWidth(fadeColor));
				}
				int height = data.size() * (1 + fontRenderer.FONT_HEIGHT) + 2;
				width += 2;
				Tuple<Integer, Integer> pos = GuiUtils.getRelativeBoxPos(mouseX + offsetX, mouseY + offsetY, width,
						height, parent.width, parent.height);
				GlStateManager.disableLighting();
				GlStateManager.disableAlpha();
				GlStateManager.disableDepth();
				GlStateManager.disableFog();
				GuiUtils.drawBox(pos.a, pos.b, width, height, parent.zLevel);
				pos.a++;
				pos.b += 2;
				int i;
				for (i = 0; i < data.size(); i++)
					fontRenderer.drawString(data.get(i), pos.a, pos.b + i * (fontRenderer.FONT_HEIGHT + 1), 0xffffffff);
				if (exp.getFadeColors().length != 0) {
					i -= 1;
					int x = pos.a + fontRenderer.getStringWidth(fadeColor);
					int y = pos.b + i * (fontRenderer.FONT_HEIGHT + 1);
					for (int j = 0; j < exp.getFadeColors().length; j++) {
						GuiUtils.drawGradientRect(x, y, x + fontRenderer.FONT_HEIGHT, y + fontRenderer.FONT_HEIGHT,
								0xff000000 | exp.getFadeColors()[j], 0xff000000 | exp.getFadeColors()[j],
								parent.zLevel);
						x += fontRenderer.FONT_HEIGHT + 1;
					}
				}
				if (exp.getColors().length != 0) {
					i -= 1;
					int x = pos.a + fontRenderer.getStringWidth(color);
					int y = pos.b + i * (fontRenderer.FONT_HEIGHT + 1);
					for (int j = 0; j < exp.getColors().length; j++) {
						GuiUtils.drawGradientRect(x, y, x + fontRenderer.FONT_HEIGHT, y + fontRenderer.FONT_HEIGHT,
								0xff000000 | exp.getColors()[j], 0xff000000 | exp.getColors()[j], parent.zLevel);
						x += fontRenderer.FONT_HEIGHT + 1;
					}
				}

			}
			super.drawNext(offsetX, offsetY, mouseX, mouseY, partialTicks);
		}
	}

	public static class GuiExplosionModifier extends GuiModifier<ExplosionInformation> {
		private ExplosionInformation exp;
		private ColorList colors, fadeColors;
		private GuiButton trail, flicker, type;

		public GuiExplosionModifier(GuiScreen parent, Consumer<ExplosionInformation> setter, ExplosionInformation exp) {
			super(parent, setter);
			this.exp = exp.clone();
			colors = new ColorList(this, 0, 0, 6, exp.getColors(), I18n.format("gui.act.modifier.meta.explosion.color"),
					24);
			fadeColors = new ColorList(this, 0, 0, 6, exp.getFadeColors(),
					I18n.format("gui.act.modifier.meta.explosion.fadeColor"), 24);
		}

		private void defineButton() {
			type.displayString = I18n.format("item.fireworksCharge.type." + exp.getType());
		}

		@Override
		public void initGui() {
			colors.x = width / 2;
			colors.y = height / 2 - 42;
			fadeColors.x = width / 2 + 100;
			fadeColors.y = height / 2 - 42;
			buttonList.add(type = new GuiButton(2, width / 2 - 200, height / 2 - 42, 199, 20, I18n.format("")));
			buttonList.add(trail = new GuiButton(3, width / 2 - 200, height / 2 - 21, 199, 20,
					I18n.format("item.fireworksCharge.trail")));
			buttonList.add(flicker = new GuiButton(4, width / 2 - 200, height / 2, 199, 20,
					I18n.format("item.fireworksCharge.flicker")));

			buttonList.add(new GuiButton(0, width / 2 - 100, height / 2 + 21, 99, 20, I18n.format("gui.done")));
			buttonList.add(new GuiButton(1, width / 2 - 200, height / 2 + 21, 99, 20, I18n.format("gui.act.cancel")));
			defineButton();
			super.initGui();
		}

		@Override
		protected void actionPerformed(GuiButton button) throws IOException {
			switch (button.id) {
			case 0:
				exp.setColors(colors.getColors());
				exp.setFadeColors(fadeColors.getColors());
				set(exp);
			case 1:
				mc.displayGuiScreen(parent);
				break;
			case 2:
				List<Tuple<String, Integer>> elements = Arrays.asList(
						new Tuple<>(I18n.format("item.fireworksCharge.type.0"), 0),
						new Tuple<>(I18n.format("item.fireworksCharge.type.1"), 1),
						new Tuple<>(I18n.format("item.fireworksCharge.type.2"), 2),
						new Tuple<>(I18n.format("item.fireworksCharge.type.3"), 3),
						new Tuple<>(I18n.format("item.fireworksCharge.type.4"), 4));
				mc.displayGuiScreen(new GuiButtonListSelector<Integer>(this, elements, t -> {
					exp.setType(t);
					defineButton();
					return null;
				}));
				break;
			case 3:
				exp.setTrail(!exp.isTrail());
				break;
			case 4:
				exp.setFlicker(!exp.isFlicker());
				break;
			}
			super.actionPerformed(button);
		}

		@Override
		public void updateScreen() {
			trail.packedFGColour = GuiUtils.getRedGreen(exp.isTrail());
			flicker.packedFGColour = GuiUtils.getRedGreen(exp.isFlicker());
			super.updateScreen();
		}

		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks) {
			drawDefaultBackground();
			colors.draw(mouseX, mouseY, zLevel);
			fadeColors.draw(mouseX, mouseY, zLevel);
			super.drawScreen(mouseX, mouseY, partialTicks);
			colors.drawNext(mouseX, mouseY, zLevel);
			fadeColors.drawNext(mouseX, mouseY, zLevel);
		}

		@Override
		protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
			colors.mouseClick(mouseX, mouseY, mouseButton);
			fadeColors.mouseClick(mouseX, mouseY, mouseButton);
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	private FireworkMainListElement main;
	private Supplier<ListElement> builder = () -> new ExplosionListElement(this, null);

	public GuiFireworksModifer(GuiScreen parent, Consumer<NBTTagCompound> setter, NBTTagCompound tag) {
		super(parent, new ArrayList<>(), setter);
		elements.add(main = new FireworkMainListElement(tag.hasKey("Flight") ? tag.getInteger("Flight") : 1));
		ItemUtils.forEachInNBTTagList(tag.getTagList("Explosions", 10),
				base -> elements.add(new ExplosionListElement(this, (NBTTagCompound) base)));
		elements.add(new AddElementList(this, builder));
	}

	@Override
	protected NBTTagCompound get() {
		NBTTagCompound newTag = new NBTTagCompound();
		newTag.setInteger("Flight", main.value);
		NBTTagList explosions = new NBTTagList();
		elements.stream().filter(le -> le instanceof ExplosionListElement)
				.forEach(le -> explosions.appendTag(((ExplosionListElement) le).exp.getTag()));
		newTag.setTag("Explosions", explosions);
		return newTag;
	}

}
