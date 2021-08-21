package fr.atesab.act.gui.selector;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.atesab.act.utils.FileUtils;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

public class GuiFileSelector extends GuiListSelector<File> {
    private class FileListElement extends ListElement {
        private File f;
        private String desc;

        public FileListElement(File f) {
            this(f, f.getName());
        }

        public FileListElement(File f, String title) {
            super(200, 20);
            this.f = f;
            var ext = FileUtils.fileExt(f);
            try {
                this.desc = ext + ", " + FileUtils.sizeUnit(f.length(), I18n.get("act"));
            } catch (Exception e) {
                this.desc = ext;
            }
        }

        @Override
        public void draw(PoseStack matrixStack, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
            GuiUtils.drawRect(matrixStack, offsetX, offsetY, offsetX + getSizeX(), offsetY + getSizeY(), 0x66000000);
            GuiUtils.drawString(font, f.getName(), offsetX + 20, offsetY + 20 / 2 - font.lineHeight - 1, 0xffffffff);
            GuiUtils.drawString(font, desc, offsetX + 20, offsetY + 20 / 2 + 1, 0xffffffff);
            super.draw(matrixStack, offsetX, offsetY, mouseX, mouseY, partialTicks);
        }

        @Override
        public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
            // TODO Auto-generated method stub
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }

        @Override
        public boolean match(String search) {
            String s = search.toLowerCase();
            return f.getName().toLowerCase().contains(s);
        }
    }

    @SuppressWarnings("unchecked")
    public GuiFileSelector(Screen parent, Component name, Function<File, Screen> setter, File current) {
        super(parent, name, new ArrayList<>(), setter, new Tuple[0]);
        var c = current;
        while (c != null && !c.isDirectory())
            c = c.getParentFile();
        if (c == null) {
            c = Minecraft.getInstance().gameDirectory;
        }

        Arrays.stream(c.listFiles()).map(FileListElement::new).forEach(this::addListElement);
    }

}
