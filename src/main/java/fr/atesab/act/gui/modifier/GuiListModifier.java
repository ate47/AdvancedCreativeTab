package fr.atesab.act.gui.modifier;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.Supplier;

import fr.atesab.act.gui.GuiValueButton;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public abstract class GuiListModifier<T> extends GuiModifier<T> {
	public static class AddElementButton extends GuiValueButton<ListElement> {
		private Supplier<ListElement> supplier;
		private GuiListModifier<?> parent;

		public AddElementButton(GuiListModifier<?> parent, int x, int y, int widthIn, int heightIn, ListElement element,
				Supplier<ListElement> builder) {
			this(parent, x, y, widthIn, heightIn, TextFormatting.GREEN + "+", element, builder);
		}

		public AddElementButton(GuiListModifier<?> parent, int x, int y, int widthIn, int heightIn, String text,
				ListElement element, Supplier<ListElement> builder) {
			super(0, x, y, widthIn, heightIn, text, element);
			this.parent = parent;
			this.supplier = builder;
		}

		public AddElementButton(GuiListModifier<?> parent, int x, int y, ListElement element,
				Supplier<ListElement> builder) {
			super(0, x, y, TextFormatting.GREEN + "+", element);
			this.parent = parent;
		}

		private void run() {
			for (int i = 0; i < parent.elements.size(); i++)
				if (parent.elements.get(i).equals(getValue())) {
					parent.elements.add(i, supplier.get());
					break;
				}
			parent.needRedefine = true;
		}
	}

	public static class AddElementList extends ListElement {
		private Supplier<ListElement> supplier;
		private GuiListModifier<?> parent;

		public AddElementList(GuiListModifier<?> parent, Supplier<ListElement> builder) {
			this(parent, builder,
					Math.min(200, getOrDefault(parent.elements.stream().mapToInt(ListElement::getSizeX).max(), 100)),
					21);
		}

		public AddElementList(GuiListModifier<?> parent, Supplier<ListElement> builder, int sizeX, int sizeY) {
			super(sizeX, Math.max(21, sizeY));
			this.parent = parent;
			this.supplier = builder;
			buttonList.add(new GuiButton(0, 0, 0, getSizeX(), 20, TextFormatting.GREEN + "+"));
		}

		@Override
		protected void actionPerformed(GuiButton button) {
			parent.elements.add(parent.elements.size() - 1, supplier.get());
			parent.needRedefine = true;
			parent.page = parent.maxPage - 1;
			super.actionPerformed(button);
		}
	}

	public static class ButtonElementList extends ListElement {
		private Runnable left, right;

		public ButtonElementList(int sizeX, int sizeY, int buttonSizeX, int buttonSizeY, String buttonText,
				Runnable leftAction, Runnable rightAction) {
			super(sizeX, sizeY);
			buttonList.add(new GuiButton(0, 0, 0, buttonSizeX, buttonSizeY, buttonText));
			this.left = leftAction;
			this.right = rightAction;
		}

		@Override
		protected void actionPerformed(GuiButton button) {
			if (left != null)
				left.run();
			super.actionPerformed(button);
		}

		@Override
		protected void otherActionPerformed(GuiButton button, int mouseButton) {
			if (mouseButton == 1 && right != null)
				right.run();
			super.otherActionPerformed(button, mouseButton);
		}

	}

	public static class ListElement {
		protected FontRenderer fontRenderer;
		protected Minecraft mc;
		protected List<GuiButton> buttonList = new ArrayList<>();
		private int sizeX;
		private int sizeY;

		public ListElement(int sizeX, int sizeY) {
			this.sizeX = sizeX;
			this.sizeY = sizeY;
			fontRenderer = (mc = Minecraft.getMinecraft()).fontRenderer;
		}

		protected void actionPerformed(GuiButton button) {
		}

		public void draw(int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
			buttonList.forEach(b -> GuiUtils.drawRelative(mc, b, offsetX, offsetY, mouseX, mouseY, partialTicks));
		}

		public void drawNext(int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
		}

		public int getSizeX() {
			return sizeX;
		}

		public int getSizeY() {
			return sizeY;
		}

		public void setSizeX(int sizeX) {
			this.sizeX = sizeX;
		}

		public void setSizeY(int sizeY) {
			this.sizeY = sizeY;
		}

		public void init() {
		}

		public boolean isFocused() {
			return false;
		}

		public void keyTyped(char typedChar, int keyCode) {
		}

		public boolean match(String search) {
			return true;
		}

		public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
			buttonList.forEach(b -> {
				if (b.mousePressed(mc, mouseX, mouseY)) {
					if (mouseButton == 0) {
						playClick();
						if (b instanceof RemoveElementButton)
							((RemoveElementButton) b).run();
						else if (b instanceof AddElementButton)
							((AddElementButton) b).run();
						else
							actionPerformed(b);
					} else
						otherActionPerformed(b, mouseButton);
				}
			});
		}

		protected void otherActionPerformed(GuiButton button, int mouseButton) {
		}

		public void update() {
		}
	}

	public static class RemoveElementButton extends GuiValueButton<ListElement> {
		private GuiListModifier<?> parent;

		public RemoveElementButton(GuiListModifier<?> parent, int x, int y, int widthIn, int heightIn,
				ListElement element) {
			super(0, x, y, widthIn, heightIn, TextFormatting.RED + "-", element);
			this.parent = parent;
		}

		public RemoveElementButton(GuiListModifier<?> parent, int x, int y, ListElement element) {
			super(0, x, y, TextFormatting.RED + "-", element);
			this.parent = parent;
		}

		private void run() {
			parent.elements.remove(getValue());
			parent.needRedefine = true;
		}
	}

	private static int getOrDefault(OptionalInt opt, int defaultValue) {
		return opt.isPresent() ? opt.getAsInt() : defaultValue;
	}

	protected List<ListElement> elements;
	protected List<ListElement> searchedElements = new ArrayList<>();
	protected List<ListElement>[] visibleElements;
	private boolean doneButton, cancelButton;
	private int page, maxPage, sizeX;

	private GuiButton lastPage, nextPage;

	private GuiTextField search;

	protected Tuple<String, Tuple<Runnable, Runnable>>[] buttons;

	private boolean needRedefine = false;

	private int dSize = 42;

	public GuiListModifier(GuiScreen parent, List<ListElement> elements, Consumer<T> setter, boolean doneButton,
			boolean cancelButton, Tuple<String, Tuple<Runnable, Runnable>>... buttons) {
		super(parent, setter);
		this.elements = elements;
		this.buttons = buttons;
		this.doneButton = doneButton;
		this.cancelButton = cancelButton;
	}

	public GuiListModifier(GuiScreen parent, List<ListElement> elements, Consumer<T> setter, boolean doneButton,
			Tuple<String, Tuple<Runnable, Runnable>>... buttons) {
		this(parent, elements, setter, doneButton, true, buttons);
	}

	public GuiListModifier(GuiScreen parent, List<ListElement> elements, Consumer<T> setter,
			Tuple<String, Tuple<Runnable, Runnable>>... buttons) {
		this(parent, elements, setter, true, buttons);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
		case 0:
			set(get());
			mc.displayGuiScreen(parent);
			break;
		case 1:
			mc.displayGuiScreen(parent);
			break;
		case 2:
			page--;
			define();
			break;
		case 3:
			page++;
			define();
			break;
		case 4:
			((GuiValueButton<Tuple<Runnable, Runnable>>) button).getValue().a.run();
			break;
		}
		super.actionPerformed(button);
	}

	public void addListElement(int i, ListElement elem) {
		elements.add(i, elem);
		needRedefine = true;
	}

	private void define() {
		searchedElements.clear();
		elements.stream().filter(le -> le.match(search.getText())).forEach(searchedElements::add);
		sizeX = getOrDefault(searchedElements.stream().mapToInt(ListElement::getSizeX).max(), width - 20);
		visibleElements = new ArrayList[Math.max(1, (width - 20) / sizeX)];
		for (int i = 0; i < visibleElements.length; i++)
			visibleElements[i] = new ArrayList<>();
		maxPage = 0;
		int pageHeight = height - 63;
		int currentSize = 0;
		int i = 0;
		for (ListElement elm : searchedElements) {
			if (currentSize + elm.sizeY > pageHeight) {
				if ((i = (i + 1) % visibleElements.length) == 0)
					maxPage++;
				currentSize = elm.sizeY;
			} else
				currentSize += elm.sizeY;
			if (page == maxPage)
				visibleElements[i].add(elm);
		}
		maxPage = Math.max(1, maxPage + (currentSize == 0 ? 0 : 1));
		if (page >= maxPage) {
			page = maxPage - 1;
			define();
		}
		int j;
		boolean add = false;
		if (page == 0)
			mass: for (j = 0; j < visibleElements.length; j++) {
				if (visibleElements[j].isEmpty())
					break;
				for (ListElement le : visibleElements[j])
					if (le instanceof AddElementList) {
						add = true;
						break mass;
					}
			}
		else
			j = visibleElements.length;
		dSize = !add && j == 1 && maxPage == 1
				? height / 2 - visibleElements[0].stream().mapToInt(ListElement::getSizeY).sum() / 2
				: 42;
		lastPage.enabled = page != 0;
		nextPage.enabled = page + 1 < maxPage;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		RenderHelper.disableStandardItemLighting();
		drawDefaultBackground();
		for (int i = 0; i < visibleElements.length; i++) {
			int currentSize = dSize;
			for (ListElement le : visibleElements[i]) {
				int offsetX = getOffsetX() + sizeX * i - le.getSizeX() / 2;
				le.draw(offsetX, currentSize, mouseX - offsetX, mouseY - currentSize, partialTicks);
				currentSize += le.getSizeY();
			}
		}
		GlStateManager.color(0, 1, 1);

		super.drawScreen(mouseX, mouseY, partialTicks);
		search.drawTextBox();
		GuiUtils.drawRightString(fontRenderer, I18n.format("gui.act.search") + " : ", search.x, search.y,
				Color.ORANGE.getRGB(), search.height);
		if (mc.currentScreen.equals(this))
			for (int i = 0; i < visibleElements.length; i++) {
				int currentSize = dSize;
				for (ListElement le : visibleElements[i]) {
					int offsetX = getOffsetX() + sizeX * i - le.getSizeX() / 2;
					le.drawNext(offsetX, currentSize, mouseX - offsetX, mouseY - currentSize, partialTicks);
					currentSize += le.getSizeY();
				}
			}
		GlStateManager.color(1, 1, 1);
	}

	protected abstract T get();

	private int getOffsetX() {
		int i;
		if (page == 0)
			for (i = 0; i < visibleElements.length; i++) {
				if (visibleElements[i].isEmpty())
					break;
			}
		else
			i = visibleElements.length;
		return (width - sizeX * (i - 1)) / 2;
	}

	@Override
	public void initGui() {
		page = 0;
		int l = (doneButton ? 1 : 0);
		int d = (buttons.length + l + (cancelButton ? 1 : 0)) * 50;
		int dl = width / 2 - d;
		int dr = width / 2 + d;
		if (doneButton)
			buttonList.add(new GuiButton(0, dl, height - 21, 99, 20, I18n.format("gui.done")));
		int i;
		for (i = 0; i < buttons.length; i++)
			buttonList.add(new GuiValueButton<Tuple<Runnable, Runnable>>(4, dl + 100 * (i + l), height - 21, 99, 20,
					buttons[i].a, buttons[i].b));
		if (cancelButton)
			buttonList.add(new GuiButton(1, dl + 100 * (i + l), height - 21, 99, 20, I18n.format("gui.act.cancel")));
		buttonList.add(lastPage = new GuiButton(2, dl - 21, height - 21, 20, 20, "<-"));
		buttonList.add(nextPage = new GuiButton(3, dr, height - 21, 20, 20, "->"));
		int m = fontRenderer.getStringWidth(I18n.format("gui.act.search") + " : ");
		int n = Math.min(600, width - 20);
		search = new GuiTextField(0, fontRenderer, (width - n) / 2 + 6 + m, 10, n - 18 - m, 18);
		elements.forEach(ListElement::init);
		define();
		super.initGui();
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		boolean flag = false;
		for (List<ListElement> lel : visibleElements)
			for (ListElement le : lel)
				if (le.isFocused()) {
					flag = true;
					break;
				}
		for (List<ListElement> lel : visibleElements)
			lel.forEach(le -> le.keyTyped(typedChar, keyCode));
		if (!flag)
			search.setFocused(true);
		if (search.isFocused()) {
			search.textboxKeyTyped(typedChar, keyCode);
			page = 0;
			define();
		}
		super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		for (int i = 0; i < visibleElements.length; i++) {
			int currentSize = dSize;
			for (ListElement le : visibleElements[i]) {
				int offsetX = getOffsetX() + sizeX * i - le.getSizeX() / 2;
				le.mouseClicked(mouseX - offsetX, mouseY - currentSize, mouseButton);
				currentSize += le.getSizeY();
			}
		}
		search.mouseClicked(mouseX, mouseY, mouseButton);
		if (mouseButton == 1) {
			if (GuiUtils.isHover(search.x, search.y, search.width, search.height, mouseX, mouseY)) {
				search.setText("");
				page = 0;
				define();
			} else
				buttonList.stream().filter(button -> button.id == 4 && GuiUtils.isHover(button, mouseX, mouseY))
						.map(b -> (GuiValueButton<Tuple<Runnable, Runnable>>) b).forEach(b -> b.getValue().b.run());
		}
		if (needRedefine) {
			needRedefine = false;
			define();
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	public void removeListElement(ListElement elem) {
		elements.remove(elem);
		needRedefine = true;
	}

	@Override
	public void updateScreen() {
		search.updateCursorCounter();
		for (List<ListElement> lel : visibleElements)
			lel.forEach(ListElement::update);
		super.updateScreen();
	}
}
