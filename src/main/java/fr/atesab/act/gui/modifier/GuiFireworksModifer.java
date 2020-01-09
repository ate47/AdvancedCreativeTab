package fr.atesab.act.gui.modifier;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.mojang.blaze3d.platform.GlStateManager;

import fr.atesab.act.gui.ColorList;
import fr.atesab.act.gui.selector.GuiButtonListSelector;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import fr.atesab.act.utils.ItemUtils.ExplosionInformation;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.FireworkRocketItem.Shape;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.TextFormatting;

public class GuiFireworksModifer extends GuiListModifier<CompoundNBT> {
	private static class FireworkMainListElement extends ListElement {
		private TextFieldWidget flight;
		private int value;
		private boolean err;
		private String title;

		public FireworkMainListElement(int flight) {
			super(200, 29);
			title = I18n.format("item.minecraft.firework_rocket.flight");
			if (title.endsWith(":"))
				title = title.substring(0, title.length() - 1);
			title += " : ";
			int l = font.getStringWidth(title + " : ") + 5;
			this.flight = new TextFieldWidget(font, l, 2, 196 - l, 16, "");
			this.flight.setMaxStringLength(6);
			this.flight.setText("" + flight);

		}

		@Override
		public void init() {
			flight.setFocused2(false);
			super.init();
		}

		@Override
		public boolean isFocused() {
			return flight.isFocused();
		}

		@Override
		public void draw(int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
			GuiUtils.drawRelative(flight, offsetX, offsetY, mouseY, mouseY, partialTicks);
			GuiUtils.drawString(font, title, offsetX, offsetY, (err ? Color.RED : Color.WHITE).getRGB(),
					flight.getHeight());
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
			flight.tick();
			try {
				value = flight.getText().isEmpty() ? 0 : Integer.valueOf(flight.getText());
				err = false;
			} catch (NumberFormatException e) {
				err = true;
			}
			super.update();
		}

		@Override
		public boolean charTyped(char key, int modifiers) {
			return flight.charTyped(key, modifiers) || super.charTyped(key, modifiers);
		}

		@Override
		public boolean keyPressed(int key, int scanCode, int modifiers) {
			return flight.keyPressed(key, scanCode, modifiers) || super.keyPressed(key, scanCode, modifiers);
		}
	}

	private static class ExplosionListElement extends ListElement {
		private ExplosionInformation exp;
		private GuiFireworksModifer parent;

		public ExplosionListElement(GuiFireworksModifer parent, CompoundNBT expData) {
			super(204, 29);
			this.exp = ItemUtils.getExplosionInformation(expData);
			this.parent = parent;
			buttonList.add(
					new Button(0, 0, 100, 20, I18n.format("gui.act.modifier.meta.explosion"), b -> mc.displayGuiScreen(
							new GuiExplosionModifier(parent, exp -> ExplosionListElement.this.exp = exp, exp))));
			buttonList.add(new RemoveElementButton(parent, 101, 0, 20, 20, this));
			buttonList.add(new AddElementButton(parent, 122, 0, 20, 20, this, parent.builder));
			buttonList.add(new AddElementButton(parent, 143, 0, 60, 20, I18n.format("gui.act.give.copy"), this,
					() -> new ExplosionListElement(parent, exp.getTag())));
		}

		@Override
		public boolean match(String search) {
			String s = search.toLowerCase();
			return I18n.format("gui.act.modifier.type").toLowerCase().contains(s)
					|| I18n.format("item.minecraft.firework_star.shape." + exp.getType().func_196068_b())
							/* getFromId (...).getShapeName */.toLowerCase().contains(s)
					|| I18n.format("item.minecraft.firework_star.trail").toLowerCase().contains(s)
					|| I18n.format("item.minecraft.firework_star.flicker").toLowerCase().contains(s)
					|| I18n.format("gui.act.modifier.meta.explosion.color").toLowerCase().contains(s)
					|| I18n.format("gui.act.modifier.meta.explosion.fadeColor").toLowerCase().contains(s);
		}

		@Override
		public void drawNext(int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
			if (GuiUtils.isHover(0, 0, 200, 20, mouseX, mouseY)) {
				List<String> data = new ArrayList<>();
				String type = I18n.format("gui.act.modifier.type") + " : " + TextFormatting.YELLOW
						+ I18n.format("item.minecraft.firework_star.shape." + exp.getType().func_196068_b());
				String trail = I18n.format("item.minecraft.firework_star.trail");
				String flicker = I18n.format("item.minecraft.firework_star.flicker");
				String color = I18n.format("gui.act.modifier.meta.explosion.color") + " : ";
				String fadeColor = I18n.format("gui.act.modifier.meta.explosion.fadeColor") + " : ";
				data.add(type);
				int width = font.getStringWidth(type);
				if (exp.isTrail()) {
					data.add(trail);
					width = Math.max(width, font.getStringWidth(trail));
				}
				if (exp.isFlicker()) {
					data.add(flicker);
					width = Math.max(width, font.getStringWidth(flicker));
				}
				if (exp.getColors().length != 0) {
					data.add(color);
					width = Math.max(width,
							(font.FONT_HEIGHT + 1) * exp.getColors().length + font.getStringWidth(color));
				}
				if (exp.getFadeColors().length != 0) {
					data.add(fadeColor);
					width = Math.max(width,
							(font.FONT_HEIGHT + 1) * exp.getFadeColors().length + font.getStringWidth(fadeColor));
				}
				int height = data.size() * (1 + font.FONT_HEIGHT) + 2;
				width += 2;
				Tuple<Integer, Integer> pos = GuiUtils.getRelativeBoxPos(mouseX + offsetX, mouseY + offsetY, width,
						height, parent.width, parent.height);
				GlStateManager.disableLighting();
				GlStateManager.disableAlphaTest();
				GlStateManager.disableDepthTest();
				GlStateManager.disableFog();
				GuiUtils.drawBox(pos.a, pos.b, width, height, parent.getZLevel());
				pos.a++;
				pos.b += 2;
				int i;
				for (i = 0; i < data.size(); i++)
					font.drawString(data.get(i), pos.a, pos.b + i * (font.FONT_HEIGHT + 1), 0xffffffff);
				if (exp.getFadeColors().length != 0) {
					i -= 1;
					int x = pos.a + font.getStringWidth(fadeColor);
					int y = pos.b + i * (font.FONT_HEIGHT + 1);
					for (int j = 0; j < exp.getFadeColors().length; j++) {
						GuiUtils.drawGradientRect(x, y, x + font.FONT_HEIGHT, y + font.FONT_HEIGHT,
								0xff000000 | exp.getFadeColors()[j], 0xff000000 | exp.getFadeColors()[j],
								parent.getZLevel());
						x += font.FONT_HEIGHT + 1;
					}
				}
				if (exp.getColors().length != 0) {
					i -= 1;
					int x = pos.a + font.getStringWidth(color);
					int y = pos.b + i * (font.FONT_HEIGHT + 1);
					for (int j = 0; j < exp.getColors().length; j++) {
						GuiUtils.drawGradientRect(x, y, x + font.FONT_HEIGHT, y + font.FONT_HEIGHT,
								0xff000000 | exp.getColors()[j], 0xff000000 | exp.getColors()[j], parent.getZLevel());
						x += font.FONT_HEIGHT + 1;
					}
				}

			}
			super.drawNext(offsetX, offsetY, mouseX, mouseY, partialTicks);
		}
	}

	public static class GuiExplosionModifier extends GuiModifier<ExplosionInformation> {
		private ExplosionInformation exp;
		private ColorList colors, fadeColors;
		private Button type;

		public GuiExplosionModifier(Screen parent, Consumer<ExplosionInformation> setter, ExplosionInformation exp) {
			super(parent, "gui.act.modifier.meta.fireworks", setter);
			this.exp = exp.clone();
			colors = new ColorList(this, 0, 0, 6, exp.getColors(), I18n.format("gui.act.modifier.meta.explosion.color"),
					24);
			fadeColors = new ColorList(this, 0, 0, 6, exp.getFadeColors(),
					I18n.format("gui.act.modifier.meta.explosion.fadeColor"), 24);
		}

		private void defineButton() {
			type.setMessage(I18n.format("item.minecraft.firework_star.shape." + exp.getType().func_196068_b()));
		}

		@Override
		public void init() {
			colors.x = width / 2;
			colors.y = height / 2 - 42;
			fadeColors.x = width / 2 + 100;
			fadeColors.y = height / 2 - 42;
			addButton(type = new Button(width / 2 - 200, height / 2 - 42, 199, 20, I18n.format(""), b -> {
				List<Tuple<String, Shape>> elements = new ArrayList<>(Shape.values().length);
				for (Shape s : Shape.values())
					elements.add(new Tuple<>(I18n.format("item.minecraft.firework_star.shape." + s.func_196068_b()), // getShapeName
							s));
				mc.displayGuiScreen(new GuiButtonListSelector<Shape>(GuiExplosionModifier.this,
						"gui.act.modifier.meta.explosion.shape", elements, s -> {
							exp.type(s);
							defineButton();
							return null;
						}));
			}));
			addButton(new GuiBooleanButton(width / 2 - 200, height / 2 - 21, 199, 20,
					I18n.format("item.minecraft.firework_star.trail"), exp::trail, exp::isTrail));
			addButton(new GuiBooleanButton(width / 2 - 200, height / 2, 199, 20,
					I18n.format("item.minecraft.firework_star.flicker"), exp::flicker, exp::isFlicker));

			addButton(new Button(width / 2 - 100, height / 2 + 21, 99, 20, I18n.format("gui.done"), b -> {
				set(exp.colors(colors.getColors()).fadeColors(fadeColors.getColors()));
				getMinecraft().displayGuiScreen(parent);
			}));
			addButton(new Button(width / 2 - 200, height / 2 + 21, 99, 20, I18n.format("gui.act.cancel"),
					b -> getMinecraft().displayGuiScreen(parent)));
			defineButton();
			super.init();
		}

		@Override
		public void render(int mouseX, int mouseY, float partialTicks) {
			renderBackground();
			colors.draw(mouseX, mouseY, getZLevel());
			fadeColors.draw(mouseX, mouseY, getZLevel());
			super.render(mouseX, mouseY, partialTicks);
			colors.drawNext(mouseX, mouseY, getZLevel());
			fadeColors.drawNext(mouseX, mouseY, getZLevel());
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
			colors.mouseClick((int) mouseX, (int) mouseY, mouseButton);
			fadeColors.mouseClick((int) mouseX, (int) mouseY, mouseButton);
			return super.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	private FireworkMainListElement main;
	private Supplier<ListElement> builder = () -> new ExplosionListElement(this, null);

	@SuppressWarnings("unchecked")
	public GuiFireworksModifer(Screen parent, Consumer<CompoundNBT> setter, CompoundNBT tag) {
		super(parent, "gui.act.modifier.meta.fireworks", new ArrayList<>(), setter, new Tuple[0]);
		addListElement(main = new FireworkMainListElement(tag.contains("Flight") ? tag.getInt("Flight") : 1));
		tag.getList("Explosions", 10)
				.forEach(base -> addListElement(new ExplosionListElement(this, (CompoundNBT) base)));
		addListElement(new AddElementList(this, builder));
	}

	@Override
	protected CompoundNBT get() {
		CompoundNBT newTag = new CompoundNBT();
		newTag.putInt("Flight", main.value);
		ListNBT explosions = new ListNBT();
		getElements().stream().filter(le -> le instanceof ExplosionListElement)
				.forEach(le -> explosions.add(((ExplosionListElement) le).exp.getTag()));
		newTag.put(ItemUtils.NBT_CHILD_EXPLOSIONS, explosions);
		return newTag;
	}

}
