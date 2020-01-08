package fr.atesab.act.gui.modifier;

import java.awt.Color;
import java.util.ArrayList;
import java.util.function.Consumer;

import com.mojang.blaze3d.platform.GlStateManager;

import fr.atesab.act.gui.GuiValueButton;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.GuiUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class GuiStringArrayModifier extends GuiModifier<String[]> {
	private ArrayList<String> values;
	private TextFieldWidget[] tfs;
	private Button next, last;
	private GuiValueButton<Integer>[] btsDel, btsAdd;
	private int elms;
	private int page = 0;

	public GuiStringArrayModifier(Screen parent, String name, String[] values, Consumer<String[]> setter) {
		super(parent, name, setter);
		this.values = new ArrayList<String>();
		for (String v : values)
			this.values.add(v.replaceAll(String.valueOf(ChatUtils.MODIFIER), "&"));
	}

	@SuppressWarnings("unchecked")
	private void defineMenu() {
		children.clear();
		buttons.clear();
		addButton(new Button(width / 2 - 100, height - 21, 100, 20, I18n.format("gui.done"), b -> {
			String[] result = new String[values.size()];
			for (int i = 0; i < result.length; i++)
				result[i] = values.get(i).replaceAll("&", String.valueOf(ChatUtils.MODIFIER));
			set(result);
			mc.displayGuiScreen(parent);
		}));
		addButton(new Button(width / 2 + 1, height - 21, 99, 20, I18n.format("gui.act.cancel"),
				b -> mc.displayGuiScreen(parent)));
		addButton(last = new Button(width / 2 - 121, height - 21, 20, 20, "<-", b -> {
			page--;
			b.active = page != 0;
			next.active = page + 1 <= values.size() / elms;
		}));
		addButton(next = new Button(width / 2 + 101, height - 21, 20, 20, "->", b -> {
			page++;
			last.active = page != 0;
			b.active = page + 1 <= values.size() / elms;
		}));
		last.active = page != 0;
		next.active = page + 1 <= values.size() / elms;
		tfs = new TextFieldWidget[values.size()];
		btsDel = new GuiValueButton[values.size()];
		btsAdd = new GuiValueButton[values.size() + 1];
		int i;
		for (i = 0; i < values.size(); i++) {
			tfs[i] = new TextFieldWidget(font, width / 2 - 178, 21 + 21 * i % (elms * 21) + 2, 340, 16, "");
			tfs[i].setMaxStringLength(Integer.MAX_VALUE);
			tfs[i].setText(values.get(i));
			addButton(btsDel[i] = new GuiValueButton<Integer>(width / 2 + 165, 21 + 21 * i % (elms * 21), 20, 20,
					TextFormatting.RED + "-", i, b -> {
						values.remove(b.getValue().intValue());
						defineMenu();
					}));
			addButton(btsAdd[i] = new GuiValueButton<Integer>(width / 2 + 187, 21 + 21 * i % (elms * 21), 20, 20,
					TextFormatting.GREEN + "+", i, b -> {
						values.add(b.getValue().intValue(), "");
						defineMenu();
					}));
			children.add(tfs[i]);
		}
		addButton(btsAdd[i] = new GuiValueButton<Integer>(width / 2 - 100, 21 + 21 * i % (elms * 21), 200, 20,
				TextFormatting.GREEN + "+", i, b -> {
					values.add(b.getValue().intValue(), "");
					defineMenu();
				}));

	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		GlStateManager.color3f(1.0F, 1.0F, 1.0F);
		for (int i = page * elms; i < (page + 1) * elms && i < tfs.length; i++) {
			TextFieldWidget tf = tfs[i];
			GuiUtils.drawRightString(font, i + " : ", tf.x, tf.y, Color.WHITE.getRGB(), tf.getHeight());
			tf.render(mouseX, mouseY, partialTicks);
		}
	}

	@Override
	public void init() {
		elms = (height - 42) / 21;
		defineMenu();
		super.init();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
			for (int i = page * elms; i < (page + 1) * elms && i < values.size(); i++) {
				tfs[i].setFocused2(false);
				if (mouseButton == 1 && GuiUtils.isHover(tfs[i], (int) mouseX, (int) mouseY)) {
					tfs[i].setText("");
					return true;
				}
			}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void tick() {
		for (int i = page * elms; i < (page + 1) * elms && i < values.size(); i++) {
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
