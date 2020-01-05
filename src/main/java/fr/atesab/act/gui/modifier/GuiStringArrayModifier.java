package fr.atesab.act.gui.modifier;

import java.awt.Color;
import java.util.ArrayList;
import java.util.function.Consumer;

import fr.atesab.act.gui.GuiValueButton;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.GuiUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class GuiStringArrayModifier extends GuiModifier<String[]> {
	private ArrayList<String> values;
	private GuiTextField[] tfs;
	private GuiButton next, last;
	private GuiValueButton<Integer>[] btsDel, btsAdd;
	private int elms;
	private int page = 0;

	public GuiStringArrayModifier(GuiScreen parent, String[] values, Consumer<String[]> setter) {
		super(parent, setter);
		this.values = new ArrayList<String>();
		for (String v : values)
			this.values.add(v.replaceAll(String.valueOf(ChatUtils.MODIFIER), "&"));
	}

	@SuppressWarnings("unchecked")
	private void defineMenu() {
		children.removeIf(g -> g instanceof GuiButton);
		buttons.clear();
		addButton(new GuiButton(0, width / 2 - 100, height - 21, 100, 20, I18n.format("gui.done")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				String[] result = new String[values.size()];
				for (int i = 0; i < result.length; i++)
					result[i] = values.get(i).replaceAll("&", String.valueOf(ChatUtils.MODIFIER));
				set(result);
				mc.displayGuiScreen(parent);
				super.onClick(mouseX, mouseY);
			}
		});
		addButton(new GuiButton(7, width / 2 + 1, height - 21, 99, 20, I18n.format("gui.act.cancel")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				mc.displayGuiScreen(parent);
				super.onClick(mouseX, mouseY);
			}
		});
		addButton(last = new GuiButton(1, width / 2 - 121, height - 21, 20, 20, "<-") {
			@Override
			public void onClick(double mouseX, double mouseY) {
				page--;
				enabled = page != 0;
				next.enabled = page + 1 <= values.size() / elms;
				super.onClick(mouseX, mouseY);
			}
		});
		addButton(next = new GuiButton(2, width / 2 + 101, height - 21, 20, 20, "->") {
			@Override
			public void onClick(double mouseX, double mouseY) {
				page++;
				last.enabled = page != 0;
				enabled = page + 1 <= values.size() / elms;
				super.onClick(mouseX, mouseY);
			}
		});
		last.enabled = page != 0;
		next.enabled = page + 1 <= values.size() / elms;
		tfs = new GuiTextField[values.size()];
		btsDel = new GuiValueButton[values.size()];
		btsAdd = new GuiValueButton[values.size() + 1];
		int i;
		for (i = 0; i < values.size(); i++) {
			tfs[i] = new GuiTextField(0, fontRenderer, width / 2 - 178, 21 + 21 * i % (elms * 21) + 2, 340, 16);
			tfs[i].setMaxStringLength(Integer.MAX_VALUE);
			tfs[i].setText(values.get(i));
			addButton(btsDel[i] = new GuiValueButton<Integer>(5, width / 2 + 165, 21 + 21 * i % (elms * 21), 20, 20,
					TextFormatting.RED + "-", i) {
				@Override
				public void onClick(double mouseX, double mouseY) {
					values.remove(getValue().intValue());
					defineMenu();
					super.onClick(mouseX, mouseY);
				}
			});
			addButton(btsAdd[i] = new GuiValueButton<Integer>(6, width / 2 + 187, 21 + 21 * i % (elms * 21), 20, 20,
					TextFormatting.GREEN + "+", i) {
				@Override
				public void onClick(double mouseX, double mouseY) {
					values.add(getValue().intValue(), "");
					defineMenu();
					super.onClick(mouseX, mouseY);
				}
			});
		}
		addButton(btsAdd[i] = new GuiValueButton<Integer>(6, width / 2 - 100, 21 + 21 * i % (elms * 21), 200, 20,
				TextFormatting.GREEN + "+", i) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				values.add(getValue().intValue(), "");
				defineMenu();
				super.onClick(mouseX, mouseY);
			}
		});

	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.render(mouseX, mouseY, partialTicks);
		GlStateManager.color3f(1.0F, 1.0F, 1.0F);
		for (int i = page * elms; i < (page + 1) * elms && i < tfs.length; i++) {
			GuiTextField tf = tfs[i];
			GuiUtils.drawRightString(fontRenderer, i + " : ", tf.x, tf.y, Color.WHITE.getRGB(), tf.height);
			tf.drawTextField(mouseX, mouseY, partialTicks);
		}
	}

	@Override
	public void initGui() {
		elms = (height - 42) / 21;
		defineMenu();
		super.initGui();
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		for (int i = page * elms; i < (page + 1) * elms && i < values.size(); i++)
			if (tfs[i].keyPressed(key, scanCode, modifiers))
				return true;
		return super.keyPressed(key, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char key, int modifiers) {
		for (int i = page * elms; i < (page + 1) * elms && i < values.size(); i++)
			if (tfs[i].charTyped(key, modifiers))
				return true;
		return super.charTyped(key, modifiers);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		for (int i = page * elms; i < (page + 1) * elms && i < values.size(); i++) {
			tfs[i].mouseClicked(mouseX, mouseY, mouseButton);
			if (mouseButton == 1 && GuiUtils.isHover(tfs[i], (int) mouseX, (int) mouseY))
				tfs[i].setText("");
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void tick() {
		for (int i = page * elms; i < (page + 1) * elms && i < values.size(); i++) {
			tfs[i].tick();
			values.set(i, tfs[i].getText());
		}
		for (int i = 0; i < btsAdd.length; i++)
			if (i < (page + 1) * elms && i >= page * elms) {
				if (btsAdd[i] != null)
					btsAdd[i].visible = true;
				if (i < btsDel.length && btsDel[i] != null)
					btsDel[i].visible = true;
			} else {
				if (btsAdd[i] != null)
					btsAdd[i].visible = false;
				if (i < btsDel.length && btsDel[i] != null)
					btsDel[i].visible = false;
			}
		super.tick();
	}
}
