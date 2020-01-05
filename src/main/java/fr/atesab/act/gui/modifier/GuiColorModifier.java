package fr.atesab.act.gui.modifier;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import fr.atesab.act.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiSlider;

public class GuiColorModifier extends GuiModifier<Integer> {
	private final ResourceLocation PICKER = new ResourceLocation("textures/gui/picker.png");
	private BufferedImage pickerImage;
	private int color;
	private boolean drag;
	private boolean advanced = false;
	private GuiButton advButton;
	private GuiTextField tfr, tfg, tfb, intColor, hexColor;
	private GuiSlider r, g, b;
	private int defaultColor;

	public GuiColorModifier(GuiScreen parent, Consumer<Integer> setter, int color) {
		this(parent, setter, color, 10511680);
	}

	public GuiColorModifier(GuiScreen parent, Consumer<Integer> setter, int color, int defaultColor) {
		super(parent, setter);
		this.color = color > 0xffffff ? color - 0xff000000 : color;
		this.defaultColor = defaultColor;
		try {
			pickerImage = ImageIO
					.read(Minecraft.getInstance().getResourceManager().getResource(PICKER).getInputStream());
		} catch (Exception e) {
			pickerImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
		}
	}

	@Override
	public void tick() {
		tfr.tick();
		tfg.tick();
		tfb.tick();
		hexColor.tick();
		intColor.tick();
		advButton.packedFGColor = (r.visible = g.visible = b.visible = advanced) ? 0xffffffff : 0xffaaaaaa;
		super.tick();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		if (!advanced) {
			GlStateManager.color3f(1, 1, 1);
			mc.getTextureManager().bindTexture(PICKER);
			drawScaledCustomSizeModalRect(width / 2 - 100, height / 2 - 80, 0, 0, 200, 200, 200, 160, 200, 200);
		} else {
			drawRect(width / 2 - 100, height / 2 - 80, width / 2 + 100, height / 2 + 80, 0x99000000);
		}
		if (advanced) {
			GuiUtils.drawString(fontRenderer, I18n.format("gui.act.red") + ":", r.x + 1, r.y - 10, 0xffffffff, 10);
			tfr.drawTextField(mouseX, mouseY, partialTicks);
			GuiUtils.drawString(fontRenderer, I18n.format("gui.act.green") + ":", g.x + 1, g.y - 10, 0xffffffff, 10);
			tfg.drawTextField(mouseX, mouseY, partialTicks);
			GuiUtils.drawString(fontRenderer, I18n.format("gui.act.blue") + ":", b.x + 1, b.y - 10, 0xffffffff, 10);
			tfb.drawTextField(mouseX, mouseY, partialTicks);
			GuiUtils.drawString(fontRenderer, I18n.format("gui.act.modifier.meta.setColor.intColor") + ":",
					intColor.x - 1, intColor.y - 11, 0xffffffff, 10);
			GuiUtils.drawString(fontRenderer, I18n.format("gui.act.modifier.meta.setColor.hexColor") + ":",
					hexColor.x - 1, hexColor.y - 11, 0xffffffff, 10);

			intColor.drawTextField(mouseX, mouseY, partialTicks);
			hexColor.drawTextField(mouseX, mouseY, partialTicks);
		}
		if (color >= 0)
			drawRect(width / 2 - 120, height / 2 - 100, width / 2 + 120, height / 2 - 80, color + 0xff000000);
		Runnable show = () -> {
		};
		for (int i = 0; i < EnumDyeColor.values().length; ++i) {
			int x = width / 2 - 120 + (i % 2) * 220;
			int y = height / 2 - 80 + (i / 2) * 20;
			drawRect(x, y, x + 20, y + 20, 0xff000000 | EnumDyeColor.values()[i].func_196060_f());
			if (GuiUtils.isHover(x, y, 20, 20, mouseX, mouseY)) {
				final int j = i;
				show = () -> GuiUtils.drawTextBox(fontRenderer, mouseX, mouseY, width, height, zLevel,
						I18n.format("item.minecraft.firework_star." + EnumDyeColor.values()[j].getTranslationKey()));
			}
			GuiUtils.drawItemStack(itemRender, zLevel, this, new ItemStack(ItemDye.getItem(EnumDyeColor.values()[i])),
					x + 2, y + 2);
		}
		super.render(mouseX, mouseY, partialTicks);
		show.run();
	}

	@Override
	public void initGui() {
		addButton(new GuiButton(0, width / 2 - 120, height / 2 + 81, 80, 20, I18n.format("gui.done")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				set(color);
				mc.displayGuiScreen(parent);
				super.onClick(mouseX, mouseY);
			}
		});
		addButton(
				advButton = new GuiButton(2, width / 2 - 38, height / 2 + 81, 80, 20, I18n.format("gui.act.advanced")) {
					@Override
					public void onClick(double mouseX, double mouseY) {
						advanced = !advanced;
						super.onClick(mouseX, mouseY);
					}
				});
		addButton(new GuiButton(1, width / 2 + 43, height / 2 + 81, 79, 20, I18n.format("gui.act.cancel")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				mc.displayGuiScreen(parent);
				super.onClick(mouseX, mouseY);
			}
		});
		GuiSlider.ISlider changer = new GuiSlider.ISlider() {

			@Override
			public void onChangeSliderValue(GuiSlider slider) {
				switch (slider.id) {
				case 3:
					updateRed(slider.getValueInt());
					break;
				case 4:
					updateGreen(slider.getValueInt());
					break;
				case 5:
					updateBlue(slider.getValueInt());
					break;
				}
			}
		};
		addButton(r = new GuiSlider(3, width / 2 - 99, height / 2 - 70, 158, 20, I18n.format("gui.act.red"), "", 0, 255,
				color >> 16 & 0xFF, false, false, changer));
		tfr = new GuiTextField(0, fontRenderer, r.x + r.width + 2, r.y + 1, 36, 18);

		addButton(g = new GuiSlider(4, width / 2 - 99, height / 2 - 38, 158, 20, I18n.format("gui.act.green"), "", 0,
				255, color >> 8 & 0xFF, false, false, changer));
		tfg = new GuiTextField(0, fontRenderer, g.x + g.width + 2, g.y + 1, 36, 18);

		addButton(b = new GuiSlider(5, width / 2 - 99, height / 2 - 3, 158, 20, I18n.format("gui.act.blue"), "", 0, 255,
				color >> 0 & 0xFF, false, false, changer));
		tfb = new GuiTextField(0, fontRenderer, b.x + b.width + 2, b.y + 1, 36, 18);

		intColor = new GuiTextField(0, fontRenderer, width / 2 - 97, height / 2 + 28, 194, 18);
		hexColor = new GuiTextField(0, fontRenderer, width / 2 - 97, height / 2 + 60, 194, 18);

		tfr.setMaxStringLength(4);
		tfg.setMaxStringLength(4);
		tfb.setMaxStringLength(4);
		r.visible = g.visible = b.visible = false;
		updateColor(color);
		super.initGui();
	}

	@Override
	public boolean charTyped(char key, int modifiers) {
		if (advanced) {
			tfr.charTyped(key, modifiers);
			tfg.charTyped(key, modifiers);
			tfb.charTyped(key, modifiers);
			hexColor.charTyped(key, modifiers);
			intColor.charTyped(key, modifiers);
			if (tfr.isFocused())
				try {
					updateRed(tfr.getText().isEmpty() ? 0 : Integer.valueOf(tfr.getText()));
				} catch (Exception e) {
				}
			else if (tfg.isFocused())
				try {
					updateGreen(tfg.getText().isEmpty() ? 0 : Integer.valueOf(tfg.getText()));
				} catch (Exception e) {
				}
			else if (tfb.isFocused())
				try {
					updateBlue(tfb.getText().isEmpty() ? 0 : Integer.valueOf(tfb.getText()));
				} catch (Exception e) {
				}
			else if (hexColor.isFocused())
				try {
					String s = hexColor.getText().substring(1);
					updateColor(s.isEmpty() ? 0 : Integer.valueOf(s, 16));
				} catch (Exception e) {
				}
			else if (intColor.isFocused())
				try {
					updateColor(intColor.getText().isEmpty() ? 0 : Integer.valueOf(intColor.getText()));
				} catch (Exception e) {
				}
		}
		return super.charTyped(key,  modifiers);
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		if (advanced) {
			tfr.keyPressed(key, scanCode, modifiers);
			tfg.keyPressed(key, scanCode, modifiers);
			tfb.keyPressed(key, scanCode, modifiers);
			hexColor.keyPressed(key, scanCode, modifiers);
			intColor.keyPressed(key, scanCode, modifiers);
			if (tfr.isFocused())
				try {
					updateRed(tfr.getText().isEmpty() ? 0 : Integer.valueOf(tfr.getText()));
				} catch (Exception e) {
				}
			else if (tfg.isFocused())
				try {
					updateGreen(tfg.getText().isEmpty() ? 0 : Integer.valueOf(tfg.getText()));
				} catch (Exception e) {
				}
			else if (tfb.isFocused())
				try {
					updateBlue(tfb.getText().isEmpty() ? 0 : Integer.valueOf(tfb.getText()));
				} catch (Exception e) {
				}
			else if (hexColor.isFocused())
				try {
					String s = hexColor.getText().substring(1);
					updateColor(s.isEmpty() ? 0 : Integer.valueOf(s, 16));
				} catch (Exception e) {
				}
			else if (intColor.isFocused())
				try {
					updateColor(intColor.getText().isEmpty() ? 0 : Integer.valueOf(intColor.getText()));
				} catch (Exception e) {
				}
		}
		return super.keyPressed(key, scanCode, modifiers);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (advanced) {
			if (mouseButton == 1) {
				if (GuiUtils.isHover(tfr, (int) mouseX, (int) mouseY))
					tfr.setText("");
				else if (GuiUtils.isHover(tfg, (int) mouseX, (int) mouseY))
					tfg.setText("");
				else if (GuiUtils.isHover(tfb, (int) mouseX, (int) mouseY))
					tfb.setText("");
				else if (GuiUtils.isHover(intColor, (int) mouseX, (int) mouseY))
					intColor.setText("");
				else if (GuiUtils.isHover(hexColor, (int) mouseX, (int) mouseY))
					hexColor.setText("#");
			}
			tfr.mouseClicked(mouseX, mouseY, mouseButton);
			tfg.mouseClicked(mouseX, mouseY, mouseButton);
			tfb.mouseClicked(mouseX, mouseY, mouseButton);
			intColor.mouseClicked(mouseX, mouseY, mouseButton);
			hexColor.mouseClicked(mouseX, mouseY, mouseButton);
		}
		drag = false;
		if (!advanced && GuiUtils.isHover(width / 2 - 100, height / 2 - 80, 200, 160, (int) mouseX, (int) mouseY)) {
			setColor((int) mouseX, (int) mouseY);
			drag = true;
		} else if (GuiUtils.isHover(width / 2 - 120, height / 2 - 100, 240, 20, (int) mouseX, (int) mouseY))
			updateColor(defaultColor);
		else
			for (int i = 0; i < EnumDyeColor.values().length; ++i)
				if (GuiUtils.isHover(width / 2 - 120 + (i % 2) * 220, height / 2 - 80 + (i / 2) * 20, 20, 20,
						(int) mouseX, (int) mouseY))
					updateColor(EnumDyeColor.values()[i].func_196060_f()); // getColorCode
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int clickedMouseButton, double dx, double dy) {
		if (drag)
			setColor((int) mouseX, (int) mouseY);
		return super.mouseDragged(mouseX, mouseY, clickedMouseButton, dx, dy);
	}

	private void updateColor(int value) {
		color = value == defaultColor ? value : MathHelper.clamp(value, 0, 0xffffff);
		int r = color >> 16 & 0xFF;
		int g = color >> 8 & 0xFF;
		int b = color >> 0 & 0xFF;
		this.r.setValue(r);
		this.g.setValue(g);
		this.b.setValue(b);
		this.tfr.setText("" + r);
		this.tfg.setText("" + g);
		this.tfb.setText("" + b);
		this.intColor.setText("" + color);
		String s = Integer.toHexString(color);
		this.hexColor.setText("#" + s);
	}

	private void setColor(int mouseX, int mouseY) {
		int rx = MathHelper.clamp(mouseX - (width / 2 - 100), 0, 199);
		int ry = MathHelper.clamp(MathHelper.clamp(mouseY - (height / 2 - 100), 0, 160) * 20 / 16, 0, 199);
		int[] data = new int[3];
		pickerImage.getRaster().getPixel(rx, ry, data);
		updateColor((data[0] & 0xFF) << 16 | (data[1] & 0xFF) << 8 | (data[2] & 0xFF) << 0);
	}

	private void updateRed(int v) {
		v = MathHelper.clamp(v, 0, 255);
		updateColor((v & 0xFF) << 16 | ((color >> 8 & 0xFF) & 0xFF) << 8 | ((color >> 0 & 0xFF) & 0xFF) << 0);
	}

	private void updateGreen(int v) {
		v = MathHelper.clamp(v, 0, 255);
		updateColor(((color >> 16 & 0xFF) & 0xFF) << 16 | (v & 0xFF) << 8 | ((color >> 0 & 0xFF) & 0xFF) << 0);

	}

	private void updateBlue(int v) {
		v = MathHelper.clamp(v, 0, 255);
		updateColor(((color >> 16 & 0xFF) & 0xFF) << 16 | ((color >> 8 & 0xFF) & 0xFF) << 8 | (v & 0xFF) << 0);
	}
}
