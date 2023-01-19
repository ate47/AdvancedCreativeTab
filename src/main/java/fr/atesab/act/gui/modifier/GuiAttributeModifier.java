package fr.atesab.act.gui.modifier;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import fr.atesab.act.ACTMod;
import fr.atesab.act.gui.selector.GuiButtonListSelector;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;

public class GuiAttributeModifier extends GuiListModifier<List<AttributeModifier>> {

	static class AttributeListElement extends ListElement {
		private GuiTextField amount;
		private boolean errAmount = false;
		private double amountValue;
		private int operationValue;
		private String name;
		private GuiListModifier<?> parent;
		private GuiButton typeButton;
		private GuiButton operationButton;

		public AttributeListElement(GuiAttributeModifier parent, AttributeModifier attr) {
			super(400, 50);
			this.parent = parent;
			name = attr.getName();
			int l = 5 + fontRenderer.getStringWidth(I18n.format("gui.act.modifier.attr.amount") + " : ");
			amount = new GuiTextField(0, fontRenderer, 202 + l, 1, 154 - l, 18);
			amount.setMaxStringLength(8);
			amount.setText(String.valueOf(amountValue = attr.getAmount()));
			operationValue = attr.getOperation();
			// buttonList.add(slotButton = new GuiButton(0, 2, 0, 198, 20, ""));
			buttonList.add(typeButton = new GuiButton(1, 2, 0, 198, 20, ""));
			buttonList.add(operationButton = new GuiButton(2, 202, 21, 157, 20, ""));
			buttonList.add(new RemoveElementButton(parent, 359, 0, 20, 20, this));
			buttonList.add(new AddElementButton(parent, 381, 0, 20, 20, this, parent.supplier));
			buttonList.add(new AddElementButton(parent, 359, 21, 43, 20, I18n.format("gui.act.give.copy"), this,
					() -> new AttributeListElement(parent, attr)));
			defineButtonText();
		}

		@Override
		protected void actionPerformed(GuiButton button) {
			switch (button.id) {
			case 1:
				List<Tuple<String, IAttribute>> attributes = new ArrayList<>();
				ACTMod.getAttributes().forEach(atr -> attributes
						.add(new Tuple<>(I18n.format("attribute.name." + atr.getAttributeUnlocalizedName()), atr)));
				mc.displayGuiScreen(new GuiButtonListSelector<>(parent, attributes, atr -> {
					this.name = atr.getAttributeUnlocalizedName();
					defineButtonText();
					return null;
				}));
				break;
			case 2:
				List<Tuple<String, Integer>> operations = new ArrayList<>();
				operations.add(new Tuple<>(I18n.format("gui.act.modifier.attr.operation.0") + " (0)", 0));
				operations.add(new Tuple<>(I18n.format("gui.act.modifier.attr.operation.1") + " (1)", 1));
				operations.add(new Tuple<>(I18n.format("gui.act.modifier.attr.operation.2") + " (2)", 2));
				mc.displayGuiScreen(new GuiButtonListSelector<>(parent, operations, i -> {
					this.operationValue = i;
					defineButtonText();
					return null;
				}));
				break;
			}
			super.actionPerformed(button);
		}

		private void defineButtonText() {
			typeButton.displayString = I18n.format("gui.act.modifier.attr.type") + " - "
					+ I18n.format("attribute.name." + name);
			operationButton.displayString = I18n.format("gui.act.modifier.attr.operation") + " - "
					+ I18n.format("gui.act.modifier.attr.operation." + operationValue) + " (" + operationValue + ")";
		}

		@Override
		public void draw(int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
			GuiUtils.drawRelative(amount, offsetX, offsetY);
			GuiUtils.drawRightString(fontRenderer, I18n.format("gui.act.modifier.attr.amount") + " : ", amount,
					(errAmount ? Color.RED : Color.WHITE).getRGB(), offsetX, offsetY);
			super.draw(offsetX, offsetY, mouseX, mouseY, partialTicks);
		}

		private AttributeModifier getModifier() {
			return new AttributeModifier(name, amountValue, operationValue);
		}

		public void init() {
			amount.setFocused(false);
		}

		@Override
		public boolean isFocused() {
			return amount.isFocused();
		}

		@Override
		public void keyTyped(char typedChar, int keyCode) {
			amount.textboxKeyTyped(typedChar, keyCode);
			super.keyTyped(typedChar, keyCode);
		}

		@Override
		public boolean match(String search) {
			return typeButton.displayString.toLowerCase().contains(search.toLowerCase())
					|| operationButton.displayString.toLowerCase().contains(search.toLowerCase());
		}

		@Override
		public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
			amount.mouseClicked(mouseX, mouseY, mouseButton);
			if (mouseButton == 1) {
				if (GuiUtils.isHover(amount, mouseX, mouseY))
					amount.setText("");
			}
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}

		@Override
		public void update() {
			amount.updateCursorCounter();
			try {
				amountValue = amount.getText().isEmpty() ? 0 : Double.parseDouble(amount.getText());
				errAmount = false;
			} catch (NumberFormatException e) {
				errAmount = true;
			}
			super.update();
		}
	}

	private final Supplier<ListElement> supplier = () -> new AttributeListElement(this,
			new AttributeModifier(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), 0, 0));

	public GuiAttributeModifier(GuiScreen parent, List<AttributeModifier> attr,
			Consumer<List<AttributeModifier>> setter) {
		super(parent, new ArrayList<>(), setter);
		attr.forEach(tuple -> elements.add(new AttributeListElement(this, tuple)));
		elements.add(new AddElementList(this, supplier));
	}

	@Override
	protected List<AttributeModifier> get() {
		List<AttributeModifier> result = new ArrayList<>();
		elements.stream().filter(le -> le instanceof AttributeListElement).map(le -> (AttributeListElement) le)
				.forEach(ale -> result.add(ale.getModifier()));
		return result;
	}

}
