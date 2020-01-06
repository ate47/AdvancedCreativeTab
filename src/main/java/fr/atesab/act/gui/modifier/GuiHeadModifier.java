package fr.atesab.act.gui.modifier;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.TextFormatting;

public class GuiHeadModifier extends GuiModifier<ItemStack> {
	private ItemStack stack;
	private TextFieldWidget name;
	private TextFieldWidget uuid;
	private TextFieldWidget link;
	private Button save, loadName, loadLink;
	private Thread saveThread = null;
	private AtomicReference<String> errType = new AtomicReference<String>(null);
	private AtomicReference<String> err = new AtomicReference<String>(null);

	public GuiHeadModifier(Screen parent, Consumer<ItemStack> setter, ItemStack stack) {
		super(parent, setter);
		this.stack = stack.copy();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		renderBackground();
		List<String> err = new ArrayList<>();
		boolean flagLink = !((!link.getText().isEmpty()
				&& link.getText().matches("http://textures.minecraft.net/texture/[a-zA-Z0-9]+"))
				|| link.getText().isEmpty());
		boolean flagUuid = !((!uuid.getText().isEmpty()
				&& uuid.getText().matches("[0-9A-Fa-f]+-[0-9A-Fa-f]+-[0-9A-Fa-f]+-[0-9A-Fa-f]+-[0-9A-Fa-f]+"))
				|| uuid.getText().isEmpty());
		boolean flagName = !((!name.getText().isEmpty() && name.getText().matches("([0-9A-Za-z_]|(-))+"))
				|| name.getText().isEmpty());
		if (flagLink)
			err.add(I18n.format("gui.act.modifier.head.link.warning"));
		if (flagUuid)
			err.add(I18n.format("gui.act.modifier.head.uuid.warning"));
		if (flagName)
			err.add(I18n.format("gui.act.modifier.head.name.warning"));
		if (this.err.get() != null)
			err.add(this.err.get());
		if (this.errType.get() != null)
			err.add(this.errType.get() + ": ");
		for (int i = 0; i < err.size(); i++)
			GuiUtils.drawCenterString(font, err.get(i), width / 2, name.y - 2 - (font.FONT_HEIGHT + 1) * (i + 1),
					Color.RED.getRGB());
		font.drawString(I18n.format("gui.act.config.name") + " : ", width / 2 - 178, name.y + 10 - font.FONT_HEIGHT / 2,
				(flagName ? Color.RED : Color.WHITE).getRGB());
		font.drawString(I18n.format("gui.act.uuid") + " : ", width / 2 - 178, uuid.y + 10 - font.FONT_HEIGHT / 2,
				(flagUuid ? Color.RED : Color.WHITE).getRGB());
		font.drawString(I18n.format("gui.act.link") + " : ", width / 2 - 178, link.y + 10 - font.FONT_HEIGHT / 2,
				(flagLink ? Color.RED : Color.WHITE).getRGB());
		name.render(mouseX, mouseY, partialTicks);
		uuid.render(mouseX, mouseY, partialTicks);
		link.render(mouseX, mouseY, partialTicks);
		GuiUtils.drawItemStack(itemRenderer, this, stack, uuid.x + uuid.getWidth() + 10,
				uuid.y + uuid.getHeight() / 2 - 8);
		super.render(mouseX, mouseY, partialTicks);
		if (GuiUtils.isHover(uuid.x + uuid.getWidth() + 10, uuid.y + uuid.getHeight() / 2 - 16 / 2, 16, 16, mouseX,
				mouseY))
			renderTooltip(stack, mouseX, mouseY);
	}

	@Override
	public void init() {

		int l = Math.max(font.getStringWidth(I18n.format("gui.act.config.name") + " : "),
				Math.max(font.getStringWidth(I18n.format("gui.act.uuid") + " : "),
						font.getStringWidth(I18n.format("gui.act.link") + " : ")))
				+ 3;
		name = new TextFieldWidget(font, width / 2 - 178 + l, height / 2 - 61, 356 - l, 16, "");
		uuid = new TextFieldWidget(font, width / 2 - 178 + l, height / 2 - 40, 356 - l, 16, "");
		link = new TextFieldWidget(font, width / 2 - 178 + l, height / 2 - 19, 356 - l, 16, "");
		name.setMaxStringLength(16);
		link.setMaxStringLength(Integer.MAX_VALUE);
		uuid.setMaxStringLength(Integer.MAX_VALUE);
		addButton(new Button(width / 2 - 180, height / 2, 180, 20, I18n.format("gui.act.modifier.head.me"), b -> {
			name.setText(getMinecraft().getSession().getUsername());
		}));
		addButton(save = new Button(width / 2 + 1, height / 2, 179, 20, I18n.format("gui.act.modifier.head.saveSkin"),
				b -> {
					if (!(saveThread != null && saveThread.isAlive()))
						(saveThread = new Thread(() -> {
							JFileChooser fileChooser = new JFileChooser();
							fileChooser.setFileFilter(new FileFilter() {
								@Override
								public boolean accept(File f) {
									return f.isDirectory() || f.getName().endsWith(".png");
								}

								@Override
								public String getDescription() {
									return "skin (*.png)";
								}
							});
							fileChooser.setSelectedFile(
									new File(name.getText().isEmpty() ? "skin.png" : name.getText() + ".png"));
							if (fileChooser.showDialog(null,
									I18n.format("gui.act.save")) == JFileChooser.APPROVE_OPTION) {
								try {
									err.set(TextFormatting.GOLD + I18n.format("gui.act.modifier.head.loading") + "...");
									URL url = new URL(link.getText());
									InputStream stream = url.openStream();
									byte[] buffer = new byte[stream.available()];
									stream.read(buffer);
									File f = fileChooser.getSelectedFile();
									OutputStream writer = new FileOutputStream(f);
									writer.write(buffer);
									writer.close();
									errType.set(TextFormatting.GREEN + I18n.format("gui.act.modifier.head.fileSaved"));
									String s = f.toString();
									if (s.length() > 50)
										s = s.substring(0, 50) + "...";
									err.set(TextFormatting.GREEN + s);
								} catch (Exception e) {
									errType.set(e instanceof FileNotFoundException
											? I18n.format("gui.act.modifier.head.fileNotFound")
											: e.getClass().getSimpleName());
									String s = e.getMessage();
									if (s.length() > 50) {
										s = s.substring(0, 50) + "...";
									}
									err.set(s);
								}
							}
						})).start();
				}));
		addButton(loadName = new Button(width / 2 - 180, height / 2 + 21, 180, 20,
				I18n.format("gui.act.modifier.head.load.name"), b -> {
				try {
					err.set(TextFormatting.GOLD + I18n.format("gui.act.modifier.head.loading") + "...");
					ItemUtils.getHead(stack, name.getText());
					loadHead();
				} catch (Exception e) {
					errType.set(e.getClass().getSimpleName());
					String s = e.getMessage();
					if (s.length() > 50) {
						s = s.substring(0, 50) + "...";
					}
					err.set(s);
				}
		}));
		addButton(loadLink = new Button(width / 2 + 1, height / 2 + 21, 179, 20,
				I18n.format("gui.act.modifier.head.load.link"), b -> {
					err.set(TextFormatting.GOLD + I18n.format("gui.act.modifier.head.loading") + "...");
					ItemUtils.getHead(stack, uuid.getText(), link.getText(),
							name.getText().isEmpty() ? null : name.getText());
					loadHead();
				}));
		if (setter != null)
			addButton(new Button(width / 2 - 180, height / 2 + 42, 180, 20, I18n.format("gui.act.cancel"),
					b -> getMinecraft().displayGuiScreen(parent)));
		addButton(new Button(width / 2 + 1, height / 2 + 42, 179, 20, I18n.format("gui.done"), b -> {
			set(stack);
			getMinecraft().displayGuiScreen(parent);
		}));
		loadHead();
		super.init();
	}

	@Override
	public boolean charTyped(char key, int modifiers) {
		return link.charTyped(key, modifiers) || name.charTyped(key, modifiers) || uuid.charTyped(key, modifiers)
				|| super.charTyped(key, modifiers);
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		return link.keyPressed(key, scanCode, modifiers) || name.keyPressed(key, scanCode, modifiers)
				|| uuid.keyPressed(key, scanCode, modifiers) || super.keyPressed(key, scanCode, modifiers);
	}

	private void loadHead() {
		CompoundNBT tag = stack.getOrCreateChildTag("SkullOwner");
		if (tag.contains("Name", 8)) {
			name.setText(tag.getString("Name"));
		}
		if (tag.contains("Id", 8)) {
			uuid.setText(tag.getString("Id"));
			if (tag.contains("Properties", 10) && tag.getCompound("Properties").contains("textures", 9)) {
				ListNBT list = tag.getCompound("Properties").getList("textures", 10);
				for (int i = 0; i < list.size(); i++) {
					CompoundNBT text = list.getCompound(i);
					if (text.contains("Value", 8)) {
						try {
							String s = new String(Base64.getDecoder().decode(text.getString("Value")));
							CompoundNBT texCompound = JsonToNBT.getTagFromJson(s);
							if (texCompound.contains("profileName", 8))
								name.setText(texCompound.getString("profileName"));
							if (texCompound.contains("textures", 10)
									&& texCompound.getCompound("textures").contains("SKIN", 10)
									&& texCompound.getCompound("textures").getCompound("SKIN").contains("url", 8))
								link.setText(texCompound.getCompound("textures").getCompound("SKIN").getString("url"));
						} catch (Exception e) {
						}
					}
				}
			}
		}
		err.set(null);
		errType.set(null);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		link.mouseClicked(mouseX, mouseY, mouseButton);
		uuid.mouseClicked(mouseX, mouseY, mouseButton);
		name.mouseClicked(mouseX, mouseY, mouseButton);
		if (GuiUtils.isHover(link, (int) mouseX, (int) mouseY) && mouseButton == 1)
			link.setText("");
		if (GuiUtils.isHover(uuid, (int) mouseX, (int) mouseY) && mouseButton == 1)
			uuid.setText("");
		if (GuiUtils.isHover(name, (int) mouseX, (int) mouseY) && mouseButton == 1)
			name.setText("");
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void tick() {
		name.tick();
		uuid.tick();
		name.tick();
		loadName.active = !name.getText().isEmpty();
		loadLink.active = !uuid.getText().isEmpty() && !link.getText().isEmpty();
		save.active = !link.getText().isEmpty();
		super.tick();
	}

}
