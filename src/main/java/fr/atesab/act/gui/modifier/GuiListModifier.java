package fr.atesab.act.gui.modifier;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.atesab.act.gui.GuiValueButton;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;

public abstract class GuiListModifier<T> extends GuiModifier<T> {
	/**
	 * a button to add an element to the list
	 */
	public static class AddElementButton extends RunElementButton {
		public AddElementButton(GuiListModifier<?> parent, int x, int y, int widthIn, int heightIn, ListElement element,
				Supplier<ListElement> builder) {
			this(parent, x, y, widthIn, heightIn, new TextComponent("+"), element, builder);
		}

		public AddElementButton(GuiListModifier<?> parent, int x, int y, int widthIn, int heightIn, Component text,
				ListElement element, Function<Integer, ListElement> builder) {
			super(x, y, widthIn, heightIn, text, () -> {
				for (int i = 0; i < parent.elements.size(); i++)
					if (parent.elements.get(i).equals(element)) {
						ListElement elm = builder.apply(i);
						if (elm != null)
							parent.addListElement(i, elm);
						break;
					}
			}, null);
			setFGColor(ChatFormatting.GREEN.getColor());
		}

		public AddElementButton(GuiListModifier<?> parent, int x, int y, int widthIn, int heightIn, Component text,
				ListElement element, Supplier<ListElement> builder) {
			this(parent, x, y, widthIn, heightIn, text, element, i -> builder.get());
		}

		@Override
		protected MutableComponent createNarrationMessage() {
			return new TranslatableComponent("gui.narrate.button", I18n.get("gui.act.new"));
		}

	}

	/**
	 * a simple {@link ListElement} with only an {@link AddElementButton} in it
	 */
	public static class AddElementList extends ListElement {

		public AddElementList(GuiListModifier<?> parent, Supplier<ListElement> builder) {
			this(parent, builder,
					Math.min(200, parent.elements.stream().mapToInt(ListElement::getSizeX).max().orElse(100)), 21);
		}

		public AddElementList(GuiListModifier<?> parent, Supplier<ListElement> builder, int sizeX, int sizeY) {
			super(sizeX, Math.max(21, sizeY));
			buttonList.add(new AddElementButton(parent, 0, (getSizeY() - 20) / 2, getSizeX(), 20, this, builder));
		}
	}

	/**
	 * a simple {@link ListElement} with only an {@link Button} in it
	 */
	public static class ButtonElementList extends ListElement {
		private Runnable right;

		public ButtonElementList(int sizeX, int sizeY, int buttonSizeX, int buttonSizeY, Component buttonText,
				Runnable leftAction, Runnable rightAction) {
			super(sizeX, sizeY);
			buttonList.add(new Button(0, 0, buttonSizeX, buttonSizeY, buttonText, b -> {
				if (leftAction != null)
					leftAction.run();
			}));
			this.right = rightAction;
		}

		@Override
		protected void otherActionPerformed(Widget button, int mouseButton) {
			if (mouseButton == 1 && right != null)
				right.run();
			super.otherActionPerformed(button, mouseButton);
		}

	}

	/**
	 * a element of the list
	 */
	public static abstract class ListElement {
		protected Font font;
		protected Minecraft mc;
		protected List<AbstractWidget> buttonList = new ArrayList<>();
		protected List<EditBox> fieldList = new ArrayList<>();
		private int sizeX;
		private int sizeY;

		public ListElement(int sizeX, int sizeY) {
			this.sizeX = sizeX;
			this.sizeY = sizeY;
			mc = Minecraft.getInstance();
			font = mc.font;
		}

		public boolean charTyped(char key, int modifiers) {
			for (EditBox field : fieldList)
				if (field.isVisible() && field.charTyped(key, modifiers))
					return true;
			return false;
		}

		public void draw(PoseStack matrixStack, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
			buttonList.forEach(
					b -> GuiUtils.drawRelative(matrixStack, b, offsetX, offsetY, mouseX, mouseY, partialTicks));
			fieldList.stream().filter(EditBox::isVisible).forEach(
					tf -> GuiUtils.drawRelative(matrixStack, tf, offsetX, offsetY, mouseX, mouseY, partialTicks));
		}

		public void drawNext(PoseStack matrixStack, int offsetX, int offsetY, int mouseX, int mouseY,
				float partialTicks) {
			buttonList.stream().filter(AbstractWidget::isHovered).forEach(
					b -> GuiUtils.drawRelativeToolTip(matrixStack, b, offsetX, offsetY, mouseX, mouseY, partialTicks));
		}

		public int getSizeX() {
			return sizeX;
		}

		public int getSizeY() {
			return sizeY;
		}

		public void init() {
			fieldList.forEach(tf -> tf.setFocus(false));
		}

		/**
		 * Check if this element is focused for key typing
		 * 
		 * @return true if an element in this element is focused
		 */
		public boolean isFocused() {
			for (EditBox field : fieldList)
				if (field.isFocused())
					return true;
			return false;
		}

		public boolean keyPressed(int key, int scanCode, int modifiers) {
			for (EditBox field : fieldList)
				if (field.isVisible() && field.keyPressed(key, scanCode, modifiers))
					return true;
			return false;
		}

		/**
		 * Check if this element match with the text in the search bar
		 * 
		 * @param search Search bar text
		 * @return true if this element match
		 */
		public boolean match(String search) {
			return true;
		}

		public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
			buttonList.forEach(b -> {
				if (GuiUtils.isHover(b, mouseX, mouseY)) {
					if (b instanceof RunElementButton) {
						if (((RunElementButton) b).getLeft() != null && mouseButton == 0) {
							playClick();
							((RunElementButton) b).getLeft().run();
						} else if (((RunElementButton) b).getLeft() != null && mouseButton == 1) {
							playClick();
							((RunElementButton) b).getRight().run();
						}
					} else if (mouseButton == 0) {
						playClick();
						b.onClick(mouseX, mouseY);
					} else
						otherActionPerformed(b, mouseButton);
				}
			});
			fieldList.stream().filter(EditBox::isVisible).forEach(tf -> {
				if (mouseButton == 1 && GuiUtils.isHover(tf, mouseX, mouseY)) {
					tf.setValue("");
					tf.setFocus(true);
				} else
					tf.mouseClicked(mouseX, mouseY, mouseButton);
			});
		}

		protected void otherActionPerformed(Widget button, int mouseButton) {
		}

		public void setSizeX(int sizeX) {
			this.sizeX = sizeX;
		}

		public void setSizeY(int sizeY) {
			this.sizeY = sizeY;
		}

		public void update() {
			fieldList.stream().filter(EditBox::isVisible).forEach(EditBox::tick);
		}
	}

	/**
	 * a button to remove a {@link ListElement}
	 */
	public static class RemoveElementButton extends RunElementButton {
		public RemoveElementButton(GuiListModifier<?> parent, int x, int y, int widthIn, int heightIn,
				ListElement element) {
			super(x, y, widthIn, heightIn, new TextComponent("-"), () -> {
				parent.elements.remove(element);
				parent.needRedefine = true;
			}, null);
			setFGColor(ChatFormatting.RED.getColor());
		}

		public RemoveElementButton(GuiListModifier<?> parent, int x, int y, ListElement element) {
			this(parent, x, y, 200, 20, element);
		}

		@Override
		protected MutableComponent createNarrationMessage() {
			return new TranslatableComponent("gui.narrate.button", I18n.get("gui.act.delete"));
		}
	}

	/**
	 * a button that run left and right mouse button action
	 */
	public static class RunElementButton extends Button {
		private Runnable left;
		private Runnable right;

		public RunElementButton(int x, int y, int widthIn, int heightIn, Component text, Runnable left,
				Runnable right) {
			super(x, y, widthIn, heightIn, text, b -> {
			});
			this.left = left;
			this.right = right;
		}

		/**
		 * @return the left mouse {@link Runnable}
		 */
		public Runnable getLeft() {
			return left;
		}

		/**
		 * @return the right mouse {@link Runnable}
		 */
		public Runnable getRight() {
			return right;
		}
	}

	private List<ListElement> elements;
	private List<ListElement> searchedElements = new ArrayList<>();
	private List<ListElement>[] visibleElements;
	private boolean doneButton, cancelButton;
	private int page, maxPage, sizeX;

	private Button lastPage, nextPage;

	private EditBox search;

	protected Tuple<String, Tuple<Runnable, Runnable>>[] buttons;

	private boolean needRedefine = false;

	private int dSize = 42, paddingLeft = 0, paddingTop = 0;
	private boolean noAdaptativeSize = false;

	private boolean justStart = true;

	public GuiListModifier(Screen parent, Component name, List<ListElement> elements, Consumer<T> setter,
			boolean doneButton, boolean cancelButton, Tuple<String, Tuple<Runnable, Runnable>>[] buttons) {
		super(parent, name, setter);
		this.elements = elements;
		this.buttons = buttons;
		this.doneButton = doneButton;
		this.cancelButton = cancelButton;
		this.setter = setter;
		this.parent = parent;
		search = new EditBox(Minecraft.getInstance().font, 0, 0, 0, 0, new TextComponent(""));
	}

	public GuiListModifier(Screen parent, Component name, List<ListElement> elements, Consumer<T> setter,
			boolean doneButton, Tuple<String, Tuple<Runnable, Runnable>>[] buttons) {
		this(parent, name, elements, setter, doneButton, true, buttons);
	}

	public GuiListModifier(Screen parent, Component name, List<ListElement> elements, Consumer<T> setter,
			Tuple<String, Tuple<Runnable, Runnable>>[] buttons) {
		this(parent, name, elements, setter, true, buttons);
	}

	public void addListElement(int i, ListElement elem) {
		elements.add(i, elem);
		needRedefine = true;
	}

	public void addListElement(ListElement elem) {
		elements.add(elem);
		needRedefine = true;
	}

	@Override
	public boolean charTyped(char key, int modifiers) {
		boolean flag = false;
		for (List<ListElement> lel : visibleElements)
			for (ListElement le : lel)
				if (le.isFocused()) {
					flag = true;
					break;
				}
		for (List<ListElement> lel : visibleElements)
			lel.forEach(le -> le.charTyped(key, modifiers));
		if (!flag && !justStart)
			search.setFocus(true);
		else
			justStart = false;
		if (search.isFocused()) {
			search.charTyped(key, modifiers);
			page = 0;
			define();
		}
		return super.charTyped(key, modifiers);

	}

	@SuppressWarnings("unchecked")
	private void define() {
		// removing current visible elements
		searchedElements.clear();
		// search...
		elements.stream().filter(le -> le.match(search.getValue())).forEach(searchedElements::add);
		sizeX = searchedElements.stream().mapToInt(ListElement::getSizeX).max().orElse(width - 20 - paddingLeft)
				+ paddingLeft;
		visibleElements = new ArrayList[Math.max(1, (width - 20) / sizeX)];
		for (int i = 0; i < visibleElements.length; i++)
			visibleElements[i] = new ArrayList<>();
		maxPage = 0;
		int pageHeight = height - 63;
		int currentSize = 0;
		int i = 0;
		for (ListElement elm : searchedElements) {
			int sy = paddingTop + elm.sizeY;
			if (currentSize + sy > pageHeight) {
				if ((i = (i + 1) % visibleElements.length) == 0)
					maxPage++;
				currentSize = sy;
			} else
				currentSize += sy;
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
		dSize = (!(add || noAdaptativeSize) && j == 1 && maxPage == 1
				? height / 2 - visibleElements[0].stream().mapToInt(ListElement::getSizeY).sum() / 2
				: 42) + paddingTop;
		lastPage.active = page != 0;
		nextPage.active = page + 1 < maxPage;
	}

	protected abstract T get();

	public List<ListElement> getElements() {
		return Collections.unmodifiableList(elements);
	}

	private int getOffsetX() {
		int i;
		if (page == 0)
			for (i = 0; i < visibleElements.length; i++) {
				if (visibleElements[i].isEmpty())
					break;
			}
		else
			i = visibleElements.length;
		return (width - sizeX * (i - 1) + paddingLeft) / 2;
	}

	public int getPaddingLeft() {
		return paddingLeft;
	}

	public int getPaddingTop() {
		return paddingTop;
	}

	@Override
	public void init() {
		page = 0;
		int l = (doneButton ? 1 : 0);
		int d = (buttons.length + l + (cancelButton ? 1 : 0)) * 50;
		int dl = width / 2 - d;
		int dr = width / 2 + d;
		if (doneButton)
			addRenderableWidget(new Button(dl, height - 21, 99, 20, new TranslatableComponent("gui.done"), b -> {
				set(get());
				getMinecraft().setScreen(parent);
			}));
		int i;
		for (i = 0; i < buttons.length; i++)
			addRenderableWidget(new GuiValueButton<Tuple<Runnable, Runnable>>(dl + 100 * (i + l), height - 21, 99, 20,
					new TextComponent(buttons[i].a), buttons[i].b, b -> {
						b.getValue().a.run();
					}));
		if (cancelButton)
			addRenderableWidget(new Button(dl + 100 * (i + l), height - 21, 99, 20,
					new TranslatableComponent("gui.act.cancel"), b -> getMinecraft().setScreen(parent)));
		addRenderableWidget(lastPage = new Button(dl - 21, height - 21, 20, 20, new TextComponent("<-"), b -> {
			page--;
			define();
		}) {
			@Override
			protected MutableComponent createNarrationMessage() {
				return new TranslatableComponent("gui.narrate.button", I18n.get("gui.act.leftArrow"));
			}
		});
		addRenderableWidget(nextPage = new Button(dr, height - 21, 20, 20, new TextComponent("->"), b -> {
			page++;
			define();
		}) {

			@Override
			protected MutableComponent createNarrationMessage() {
				return new TranslatableComponent("gui.narrate.button", I18n.get("gui.act.rightArrow"));
			}
		});

		int m = font.width(I18n.get("gui.act.search") + " : ");
		int n = Math.min(600, width - 20);
		search.x = (width - n) / 2 + 6 + m;
		search.y = 18;
		search.setWidth(n - 18 - m);
		search.setHeight(18);
		elements.forEach(ListElement::init);

		define();
		super.init();

	}

	public boolean isNoAdaptativeSize() {
		return noAdaptativeSize;
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		boolean flag = false;
		for (List<ListElement> lel : visibleElements)
			for (ListElement le : lel)
				if (le.isFocused()) {
					flag = true;
					break;
				}
		for (List<ListElement> lel : visibleElements)
			lel.forEach(le -> le.keyPressed(key, scanCode, modifiers));
		if (!flag)
			search.setFocus(true);
		if (search.isFocused()) {
			search.keyPressed(key, scanCode, modifiers);
			page = 0;
			define();
		}
		return super.keyPressed(key, scanCode, modifiers);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		for (int i = 0; i < visibleElements.length; i++) {
			int currentSize = dSize;
			for (ListElement le : visibleElements[i]) {
				int offsetX = getOffsetX() + sizeX * i - le.getSizeX() / 2;
				le.mouseClicked((int) mouseX - offsetX, (int) mouseY - currentSize, mouseButton);
				currentSize += le.getSizeY() + paddingTop;
			}
		}
		search.mouseClicked(mouseX, mouseY, mouseButton);
		if (mouseButton == 1) {
			if (GuiUtils.isHover(search.x, search.y, search.getWidth(), search.getHeight(), (int) mouseX,
					(int) mouseY)) {
				search.setValue("");
				page = 0;
				define();
			} else
				children().stream()
						.filter(button -> button instanceof GuiValueButton
								&& GuiUtils.isHover(((Button) button), (int) mouseX, (int) mouseY))
						.map(b -> (GuiValueButton<Tuple<Runnable, Runnable>>) b).forEach(b -> b.getValue().b.run());
		}
		if (needRedefine) {
			needRedefine = false;
			define();
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	public void removeListElement(ListElement elem) {
		elements.remove(elem);
		needRedefine = true;
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		for (int i = 0; i < visibleElements.length; i++) {
			int currentSize = dSize;
			for (ListElement le : visibleElements[i]) {
				int offsetX = getOffsetX() + sizeX * i - le.getSizeX() / 2;
				le.draw(matrixStack, offsetX, currentSize, mouseX - offsetX, mouseY - currentSize, partialTicks);
				currentSize += le.getSizeY() + paddingTop;
			}
		}
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		search.render(matrixStack, mouseX, mouseY, partialTicks);
		GuiUtils.drawCenterString(font, getStringTitle(), width / 2, 2, 0xFFFFFFFF, 10);
		GuiUtils.drawRightString(font, I18n.get("gui.act.search") + " : ", search.x, search.y, Color.ORANGE.getRGB(),
				search.getHeight());
		if (getMinecraft().screen.equals(this))
			for (int i = 0; i < visibleElements.length; i++) {
				int currentSize = dSize;
				for (ListElement le : visibleElements[i]) {
					int offsetX = getOffsetX() + sizeX * i - le.getSizeX() / 2;
					le.drawNext(matrixStack, offsetX, currentSize, mouseX - offsetX, mouseY - currentSize,
							partialTicks);
					currentSize += le.getSizeY() + paddingTop;
				}
			}
	}

	public void setNoAdaptativeSize(boolean noAdaptativeSize) {
		this.noAdaptativeSize = noAdaptativeSize;
	}

	public void setPaddingLeft(int paddingLeft) {
		this.paddingLeft = paddingLeft;
	}

	public void setPaddingTop(int paddingTop) {
		this.paddingTop = paddingTop;
	}

	@Override
	public void tick() {
		search.tick();
		for (List<ListElement> lel : visibleElements)
			lel.forEach(ListElement::update);
		super.tick();
	}

	@Override
	protected void generateDev(List<ACTDevInfo> entries, int mouseX, int mouseY) {
		entries.add(new ACTDevInfo("List", this.elements.size() + "e", page + "/" + maxPage));
		super.generateDev(entries, mouseX, mouseY);
	}
}
