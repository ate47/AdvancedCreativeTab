package fr.atesab.act.gui.modifier;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import fr.atesab.act.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
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
					.read(Minecraft.getMinecraft().getResourceManager().getResource(PICKER).getInputStream());
		} catch (Exception e) {
			pickerImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
		}
	}

	@Override
	public void updateScreen() {
		tfr.updateCursorCounter();
		tfg.updateCursorCounter();
		tfb.updateCursorCounter();
		hexColor.updateCursorCounter();
		intColor.updateCursorCounter();
		advButton.packedFGColour = (r.visible = g.visible = b.visible = advanced) ? 0xffffffff : 0xffaaaaaa;
		super.updateScreen();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
		case 0:
			set(color);
		case 1:
			mc.displayGuiScreen(parent);
			break;
		case 2:
			advanced = !advanced;
			break;
		}
		super.actionPerformed(button);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		if (!advanced) {
			GlStateManager.color(1, 1, 1);
			mc.getTextureManager().bindTexture(PICKER);
			drawScaledCustomSizeModalRect(width / 2 - 100, height / 2 - 80, 0, 0, 200, 200, 200, 160, 200, 200);
		} else {
			drawRect(width / 2 - 100, height / 2 - 80, width / 2 + 100, height / 2 + 80, 0x99000000);
		}
		if (advanced) {
			GuiUtils.drawString(fontRendererObj, I18n.format("gui.act.red") + ":", r.xPosition + 1, r.yPosition - 10, 0xffffffff, 10);
			tfr.drawTextBox();
			GuiUtils.drawString(fontRendererObj, I18n.format("gui.act.green") + ":", g.xPosition + 1, g.yPosition - 10, 0xffffffff, 10);
			tfg.drawTextBox();
			GuiUtils.drawString(fontRendererObj, I18n.format("gui.act.blue") + ":", b.xPosition + 1, b.yPosition - 10, 0xffffffff, 10);
			tfb.drawTextBox();
			GuiUtils.drawString(fontRendererObj, I18n.format("gui.act.modifier.meta.setColor.intColor") + ":",
					intColor.xPosition - 1, intColor.yPosition - 11, 0xffffffff, 10);
			GuiUtils.drawString(fontRendererObj, I18n.format("gui.act.modifier.meta.setColor.hexColor") + ":",
					hexColor.xPosition - 1, hexColor.yPosition - 11, 0xffffffff, 10);

			intColor.drawTextBox();
			hexColor.drawTextBox();
		}
		if (color >= 0)
			drawRect(width / 2 - 120, height / 2 - 100, width / 2 + 120, height / 2 - 80, color + 0xff000000);
		Runnable show = () -> {
		};
		for (int i = 0; i < ItemDye.DYE_COLORS.length; ++i) {
			int x = width / 2 - 120 + (i % 2) * 220;
			int y = height / 2 - 80 + (i / 2) * 20;
			drawRect(x, y, x + 20, y + 20, 0xff000000 + ItemDye.DYE_COLORS[i]);
			if (GuiUtils.isHover(x, y, 20, 20, mouseX, mouseY)) {
				final int j = i;
				show = () -> GuiUtils.drawTextBox(fontRendererObj, mouseX, mouseY, width, height, zLevel,
						I18n.format("item.fireworksCharge." + EnumDyeColor.byDyeDamage(j).getUnlocalizedName()));
			}
			GuiUtils.drawItemStack(itemRender, zLevel, this, new ItemStack(Items.DYE, 1, i), x + 2, y + 2);
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
		show.run();
	}

	@Override
	public void initGui() {
		buttonList.add(new GuiButton(0, width / 2 - 120, height / 2 + 81, 80, 20, I18n.format("gui.done")));
		buttonList.add(
				advButton = new GuiButton(2, width / 2 - 38, height / 2 + 81, 80, 20, I18n.format("gui.act.advanced")));
		buttonList.add(new GuiButton(1, width / 2 + 43, height / 2 + 81, 79, 20, I18n.format("gui.act.cancel")));
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
		buttonList.add(r = new GuiSlider(3, width / 2 - 99, height / 2 - 70, 158, 20, I18n.format("gui.act.red"), "", 0,
				255, color >> 16 & 0xFF, false, false, changer));
		tfr = new GuiTextField(0, fontRendererObj, r.xPosition + r.width + 2, r.yPosition + 1, 36, 18);

		buttonList.add(g = new GuiSlider(4, width / 2 - 99, height / 2 - 38, 158, 20, I18n.format("gui.act.green"), "",
				0, 255, color >> 8 & 0xFF, false, false, changer));
		tfg = new GuiTextField(0, fontRendererObj, g.xPosition + g.width + 2, g.yPosition + 1, 36, 18);

		buttonList.add(b = new GuiSlider(5, width / 2 - 99, height / 2 - 3, 158, 20, I18n.format("gui.act.blue"), "", 0,
				255, color >> 0 & 0xFF, false, false, changer));
		tfb = new GuiTextField(0, fontRendererObj, b.xPosition + b.width + 2, b.yPosition + 1, 36, 18);

		intColor = new GuiTextField(0, fontRendererObj, width / 2 - 97, height / 2 + 28, 194, 18);
		hexColor = new GuiTextField(0, fontRendererObj, width / 2 - 97, height / 2 + 60, 194, 18);

		tfr.setMaxStringLength(4);
		tfg.setMaxStringLength(4);
		tfb.setMaxStringLength(4);
		r.visible = g.visible = b.visible = false;
		updateColor(color);
		super.initGui();
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (advanced) {
			tfr.textboxKeyTyped(typedChar, keyCode);
			tfg.textboxKeyTyped(typedChar, keyCode);
			tfb.textboxKeyTyped(typedChar, keyCode);
			hexColor.textboxKeyTyped(typedChar, keyCode);
			intColor.textboxKeyTyped(typedChar, keyCode);
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
		super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if (advanced) {
			if (mouseButton == 1) {
				if (GuiUtils.isHover(tfr, mouseX, mouseY))
					tfr.setText("");
				else if (GuiUtils.isHover(tfg, mouseX, mouseY))
					tfg.setText("");
				else if (GuiUtils.isHover(tfb, mouseX, mouseY))
					tfb.setText("");
				else if (GuiUtils.isHover(intColor, mouseX, mouseY))
					intColor.setText("");
				else if (GuiUtils.isHover(hexColor, mouseX, mouseY))
					hexColor.setText("#");
			}
			tfr.mouseClicked(mouseX, mouseY, mouseButton);
			tfg.mouseClicked(mouseX, mouseY, mouseButton);
			tfb.mouseClicked(mouseX, mouseY, mouseButton);
			intColor.mouseClicked(mouseX, mouseY, mouseButton);
			hexColor.mouseClicked(mouseX, mouseY, mouseButton);
		}
		drag = false;
		if (!advanced && GuiUtils.isHover(width / 2 - 100, height / 2 - 80, 200, 160, mouseX, mouseY)) {
			setColor(mouseX, mouseY);
			drag = true;
		} else if (GuiUtils.isHover(width / 2 - 120, height / 2 - 100, 240, 20, mouseX, mouseY))
			updateColor(defaultColor);
		else
			for (int i = 0; i < ItemDye.DYE_COLORS.length; ++i)
				if (GuiUtils.isHover(width / 2 - 120 + (i % 2) * 220, height / 2 - 80 + (i / 2) * 20, 20, 20, mouseX,
						mouseY))
					updateColor(ItemDye.DYE_COLORS[i]);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		if (drag)
			setColor(mouseX, mouseY);
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
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
