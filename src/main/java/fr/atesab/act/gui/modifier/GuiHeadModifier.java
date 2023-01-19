package fr.atesab.act.gui.modifier;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;

public class GuiHeadModifier extends GuiModifier<ItemStack> {
	private ItemStack stack;
	private GuiTextField name;
	private GuiTextField uuid;
	private GuiTextField link;
	private GuiButton save, loadName, loadLink;
	private Thread saveThread = null;
	private AtomicReference<String> errType = new AtomicReference<String>(null);
	private AtomicReference<String> err = new AtomicReference<String>(null);

	public GuiHeadModifier(GuiScreen parent, Consumer<ItemStack> setter, ItemStack stack) {
		super(parent, setter);
		this.stack = stack.copy();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
		case 0: // done
			set(stack);
		case 1: // cancel
			mc.displayGuiScreen(parent);
			break;
		case 2: // my head
			name.setText(mc.getSession().getUsername());
		case 3: // load by name
			try {
				err.set(EnumChatFormatting.GOLD + I18n.format("gui.act.modifier.head.loading") + "...");
				ItemUtils.getHead(stack, name.getText());
				loadHead();
			} catch (Exception e) {
				e.printStackTrace();
				errType.set(e.getClass().getSimpleName());
				String s = e.getMessage();
				if (s.length() > 50) {
					s = s.substring(0, 50) + "...";
				}
				err.set(s);
			}
			break;
		case 4: // load by link
			err.set(EnumChatFormatting.GOLD + I18n.format("gui.act.modifier.head.loading") + "...");
			ItemUtils.getHead(stack, uuid.getText(), link.getText(), name.getText().isEmpty() ? null : name.getText());
			loadHead();
			break;
		case 5: // save
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
					fileChooser
							.setSelectedFile(new File(name.getText().isEmpty() ? "skin.png" : name.getText() + ".png"));
					if (fileChooser.showDialog(null, I18n.format("gui.act.save")) == JFileChooser.APPROVE_OPTION) {
						try {
							err.set(EnumChatFormatting.GOLD + I18n.format("gui.act.modifier.head.loading") + "...");
							URL url = new URL(link.getText());
							InputStream stream = url.openStream();
							byte[] buffer = new byte[stream.available()];
							stream.read(buffer);
							File f = fileChooser.getSelectedFile();
							OutputStream writer = new FileOutputStream(f);
							writer.write(buffer);
							writer.close();
							errType.set(EnumChatFormatting.GREEN + I18n.format("gui.act.modifier.head.fileSaved"));
							String s = f.toString();
							if (s.length() > 50)
								s = s.substring(0, 50) + "...";
							err.set(EnumChatFormatting.GREEN + s);
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
			break;
		}
		super.actionPerformed(button);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
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
			GuiUtils.drawCenterString(fontRendererObj, err.get(i), width / 2,
					name.yPosition - 2 - (fontRendererObj.FONT_HEIGHT + 1) * (i + 1), Color.RED.getRGB());
		fontRendererObj.drawString(I18n.format("gui.act.config.name") + " : ", width / 2 - 178,
				name.yPosition + 10 - fontRendererObj.FONT_HEIGHT / 2, (flagName ? Color.RED : Color.WHITE).getRGB());
		fontRendererObj.drawString(I18n.format("gui.act.uuid") + " : ", width / 2 - 178,
				uuid.yPosition + 10 - fontRendererObj.FONT_HEIGHT / 2, (flagUuid ? Color.RED : Color.WHITE).getRGB());
		fontRendererObj.drawString(I18n.format("gui.act.link") + " : ", width / 2 - 178,
				link.yPosition + 10 - fontRendererObj.FONT_HEIGHT / 2, (flagLink ? Color.RED : Color.WHITE).getRGB());
		name.drawTextBox();
		uuid.drawTextBox();
		link.drawTextBox();
		GuiUtils.drawItemStack(itemRender, zLevel, this, stack, uuid.xPosition + uuid.width + 10,
				uuid.yPosition + uuid.height / 2 - 8);
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (GuiUtils.isHover(uuid.xPosition + uuid.width + 10, uuid.yPosition + uuid.height / 2 - 16 / 2, 16, 16,
				mouseX, mouseY))
			renderToolTip(stack, mouseX, mouseY);
	}

	@Override
	public void initGui() {

		int l = Math.max(fontRendererObj.getStringWidth(I18n.format("gui.act.config.name") + " : "),
				Math.max(fontRendererObj.getStringWidth(I18n.format("gui.act.uuid") + " : "),
						fontRendererObj.getStringWidth(I18n.format("gui.act.link") + " : ")))
				+ 3;
		name = new GuiTextField(0, fontRendererObj, width / 2 - 178 + l, height / 2 - 61, 356 - l, 16);
		uuid = new GuiTextField(0, fontRendererObj, width / 2 - 178 + l, height / 2 - 40, 356 - l, 16);
		link = new GuiTextField(0, fontRendererObj, width / 2 - 178 + l, height / 2 - 19, 356 - l, 16);
		name.setMaxStringLength(16);
		link.setMaxStringLength(Integer.MAX_VALUE);
		uuid.setMaxStringLength(Integer.MAX_VALUE);
		buttonList.add(new GuiButton(2, width / 2 - 180, height / 2, 180, 20, I18n.format("gui.act.modifier.head.me")));
		buttonList.add(save = new GuiButton(5, width / 2 + 1, height / 2, 179, 20,
				I18n.format("gui.act.modifier.head.saveSkin")));
		buttonList.add(loadName = new GuiButton(3, width / 2 - 180, height / 2 + 21, 180, 20,
				I18n.format("gui.act.modifier.head.load.name")));
		buttonList.add(loadLink = new GuiButton(4, width / 2 + 1, height / 2 + 21, 179, 20,
				I18n.format("gui.act.modifier.head.load.link")));
		if (setter != null)
			buttonList.add(new GuiButton(1, width / 2 - 180, height / 2 + 42, 180, 20, I18n.format("gui.act.cancel")));
		buttonList.add(new GuiButton(0, width / 2 + 1, height / 2 + 42, 179, 20, I18n.format("gui.done")));
		loadHead();
		super.initGui();
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		link.textboxKeyTyped(typedChar, keyCode);
		name.textboxKeyTyped(typedChar, keyCode);
		uuid.textboxKeyTyped(typedChar, keyCode);
		super.keyTyped(typedChar, keyCode);
	}

	private void loadHead() {
		NBTTagCompound tag = ItemUtils.getOrCreateSubCompound(stack, "SkullOwner");
		if (tag.getKeySet().contains("Name")) {
			name.setText(tag.getString("Name"));
		}
		if (tag.getKeySet().contains("Id")) {
			uuid.setText(tag.getString("Id"));
			if (tag.getKeySet().contains("Properties")
					&& tag.getCompoundTag("Properties").getKeySet().contains("textures")) {
				NBTTagList list = tag.getCompoundTag("Properties").getTagList("textures", 10);
				for (int i = 0; i < list.tagCount(); i++) {
					NBTTagCompound text = list.getCompoundTagAt(i);
					if (text.getKeySet().contains("Value")) {
						try {
							NBTTagCompound texCompound = ItemUtils
									.toJson(new String(Base64.getDecoder().decode(text.getString("Value"))));
							if (texCompound.getKeySet().contains("profileName"))
								name.setText(texCompound.getString("profileName"));
							if (texCompound.getKeySet().contains("textures")
									&& ((NBTTagCompound) texCompound.getTag("textures")).getKeySet().contains("SKIN")
									&& ((NBTTagCompound) ((NBTTagCompound) texCompound.getTag("textures"))
											.getTag("SKIN")).getKeySet().contains("url")) {
								link.setText(((NBTTagCompound) ((NBTTagCompound) texCompound.getTag("textures"))
										.getTag("SKIN")).getString("url"));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		err.set(null);
		errType.set(null);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		link.mouseClicked(mouseX, mouseY, mouseButton);
		uuid.mouseClicked(mouseX, mouseY, mouseButton);
		name.mouseClicked(mouseX, mouseY, mouseButton);
		if (GuiUtils.isHover(link, mouseX, mouseY) && mouseButton == 1)
			link.setText("");
		if (GuiUtils.isHover(uuid, mouseX, mouseY) && mouseButton == 1)
			uuid.setText("");
		if (GuiUtils.isHover(name, mouseX, mouseY) && mouseButton == 1)
			name.setText("");
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void updateScreen() {
		name.updateCursorCounter();
		uuid.updateCursorCounter();
		name.updateCursorCounter();
		loadName.enabled = !name.getText().isEmpty();
		loadLink.enabled = !uuid.getText().isEmpty() && !link.getText().isEmpty();
		save.enabled = !link.getText().isEmpty();
		super.updateScreen();
	}

}
