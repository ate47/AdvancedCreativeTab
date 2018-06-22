package fr.atesab.act.gui.modifier;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import fr.atesab.act.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GuiColorModifier extends GuiModifier<Integer> {
	private final ResourceLocation PICKER = new ResourceLocation("textures/gui/picker.png");
	private BufferedImage pickerImage;
	private int color;
	private boolean drag;
	private int defaultColor;

	public GuiColorModifier(GuiScreen parent, Consumer<Integer> setter, int color) {
		this(parent, setter, color, 10511680);
	}

	public GuiColorModifier(GuiScreen parent, Consumer<Integer> setter, int color, int defaultColor) {
		super(parent, setter);
		this.color = new Color(color).getRGB();
		this.defaultColor = defaultColor < 0 ? defaultColor : 0xff000000 | defaultColor;
		try {
			pickerImage = ImageIO
					.read(Minecraft.getMinecraft().getResourceManager().getResource(PICKER).getInputStream());
		} catch (Exception e) {
			pickerImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
		case 0:
			set(color < 0 ? color : color - 0xff000000);
			mc.displayGuiScreen(parent);
			break;
		case 1:
			mc.displayGuiScreen(parent);
			break;
		}
		super.actionPerformed(button);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		GlStateManager.color(1, 1, 1);
		mc.getTextureManager().bindTexture(PICKER);
		drawScaledCustomSizeModalRect(width / 2 - 100, height / 2 - 100, 0, 0, 200, 200, 200, 180, 200, 200);
		if (color != defaultColor)
			drawRect(width / 2 - 100, height / 2 - 120, width / 2 + 100, height / 2 - 100, color);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void initGui() {
		buttonList.add(new GuiButton(0, width / 2 - 101, height / 2 + 81, 100, 20, I18n.format("gui.done")));
		buttonList.add(new GuiButton(1, width / 2 + 1, height / 2 + 81, 100, 20, I18n.format("gui.act.cancel")));
		super.initGui();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		drag = false;
		if (GuiUtils.isHover(width / 2 - 100, height / 2 - 100, 200, 180, mouseX, mouseY)) {
			setColor(mouseX, mouseY);
			drag = true;
		} else if (GuiUtils.isHover(width / 2 - 100, height / 2 - 120, 200, 20, mouseX, mouseY))
			color = defaultColor;
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		if (drag)
			setColor(mouseX, mouseY);
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}

	private void setColor(int mouseX, int mouseY) {
		int rx = MathHelper.clamp(mouseX - (width / 2 - 100), 0, 199);
		int ry = MathHelper.clamp(mouseY - (height / 2 - 100), 0, 179) * 20 / 18;
		int[] data = new int[3];
		pickerImage.getRaster().getPixel(rx, ry, data);
		color = new Color(data[0], data[1], data[2], 255).getRGB();
	}

}
