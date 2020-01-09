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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.inventory.EquipmentSlotType;

public class GuiAttributeModifier extends GuiListModifier<List<Tuple<EquipmentSlotType, AttributeModifier>>> {

	static class AttributeListElement extends ListElement {
		private TextFieldWidget amount;
		private boolean errAmount = false;
		private double amountValue;
		private int operationValue;
		private String name;
		private EquipmentSlotType slot;
		private Button slotButton;
		private Button typeButton;
		private Button operationButton;

		public AttributeListElement(GuiAttributeModifier parent, Tuple<EquipmentSlotType, AttributeModifier> tuple) {
			super(400, 50);
			slot = tuple.a;
			name = tuple.b.getName();
			int l = 5 + font.getStringWidth(I18n.format("gui.act.modifier.attr.amount") + " : ");
			amount = new TextFieldWidget(font, 202 + l, 1, 154 - l, 18, "");
			amount.setMaxStringLength(8);
			amount.setText(String.valueOf(amountValue = tuple.b.getAmount()));
			operationValue = tuple.b.getOperation().getId();
			buttonList.add(slotButton = new Button(2, 0, 198, 20, "", b -> {
				List<Tuple<String, EquipmentSlotType>> slots = new ArrayList<>();
				slots.add(new Tuple<>(I18n.format("gui.act.none"), null));
				for (EquipmentSlotType slot : EquipmentSlotType.values()) {
					String s = I18n.format("item.modifiers." + slot.getName());
					slots.add(new Tuple<>(s.endsWith(":") ? s.substring(0, s.length() - 1) : s, slot));
				}
				mc.displayGuiScreen(new GuiButtonListSelector<>(parent, "gui.act.modifier.attr.slot", slots, slot -> {
					AttributeListElement.this.slot = slot;
					defineButtonText();
					return null;
				}));
			}));
			buttonList.add(typeButton = new Button(2, 21, 198, 20, "", b -> {
				List<Tuple<String, IAttribute>> attributes = new ArrayList<>();
				ACTMod.getAttributes().forEach(
						atr -> attributes.add(new Tuple<>(I18n.format("attribute.name." + atr.getName()), atr)));
				mc.displayGuiScreen(
						new GuiButtonListSelector<>(parent, "gui.act.modifier.attr.type", attributes, atr -> {
							AttributeListElement.this.name = atr.getName();
							defineButtonText();
							return null;
						}));
			}));
			buttonList.add(operationButton = new Button(202, 21, 157, 20, "", b -> {
				List<Tuple<String, Integer>> operations = new ArrayList<>();
				operations.add(new Tuple<>(I18n.format("gui.act.modifier.attr.operation.0") + " (0)", 0));
				operations.add(new Tuple<>(I18n.format("gui.act.modifier.attr.operation.1") + " (1)", 1));
				operations.add(new Tuple<>(I18n.format("gui.act.modifier.attr.operation.2") + " (2)", 2));
				mc.displayGuiScreen(
						new GuiButtonListSelector<>(parent, "gui.act.modifier.attr.operation", operations, i -> {
							AttributeListElement.this.operationValue = i;
							defineButtonText();
							return null;
						}));
			}));
			buttonList.add(new RemoveElementButton(parent, 359, 0, 20, 20, this));
			buttonList.add(new AddElementButton(parent, 381, 0, 20, 20, this, parent.supplier));
			buttonList.add(new AddElementButton(parent, 359, 21, 43, 20, I18n.format("gui.act.give.copy"), this,
					() -> new AttributeListElement(parent, new Tuple<>(slot, getModifier()))));
			defineButtonText();
		}

		private void defineButtonText() {
			String s = (slot == null ? I18n.format("gui.act.none") : I18n.format("item.modifiers." + slot.getName()));
			slotButton.setMessage(I18n.format("gui.act.modifier.attr.slot") + " - "
					+ (s.endsWith(":") ? s.substring(0, s.length() - 1) : s));
			typeButton.setMessage(
					I18n.format("gui.act.modifier.attr.type") + " - " + I18n.format("attribute.name." + name));
			operationButton.setMessage(I18n.format("gui.act.modifier.attr.operation") + " - "
					+ I18n.format("gui.act.modifier.attr.operation." + operationValue) + " (" + operationValue + ")");
		}

		@Override
		public void draw(int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
			GuiUtils.drawRelative(amount, offsetX, offsetY, mouseX, mouseY, partialTicks);
			GuiUtils.drawRightString(font, I18n.format("gui.act.modifier.attr.amount") + " : ", amount,
					(errAmount ? Color.RED : Color.WHITE).getRGB(), offsetX, offsetY);
			super.draw(offsetX, offsetY, mouseX, mouseY, partialTicks);
		}

		private AttributeModifier getModifier() {
			return new AttributeModifier(name, amountValue, Operation.byId(operationValue));
		}

		@Override
		public void init() {
			amount.setFocused2(false);
		}

		@Override
		public boolean isFocused() {
			return amount.isFocused();
		}

		@Override
		public boolean charTyped(char key, int modifiers) {
			return amount.charTyped(key, modifiers);
		}

		@Override
		public boolean keyPressed(int key, int scanCode, int modifiers) {
			amount.keyPressed(key, scanCode, modifiers);
			return super.keyPressed(key, scanCode, modifiers);
		}

		@Override
		public boolean match(String search) {
			return slotButton.getMessage().toLowerCase().contains(search.toLowerCase())
					|| typeButton.getMessage().toLowerCase().contains(search.toLowerCase())
					|| operationButton.getMessage().toLowerCase().contains(search.toLowerCase());
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
			amount.tick();
			try {
				amountValue = amount.getText().isEmpty() ? 0 : Double.parseDouble(amount.getText());
				errAmount = false;
			} catch (NumberFormatException e) {
				errAmount = true;
			}
			super.update();
		}
	}

	private final Supplier<ListElement> supplier = () -> new AttributeListElement(this, new Tuple<>(null,
			new AttributeModifier(SharedMonsterAttributes.ARMOR.getName(), 0, AttributeModifier.Operation.ADDITION)));

	@SuppressWarnings("unchecked")
	public GuiAttributeModifier(Screen parent, List<Tuple<EquipmentSlotType, AttributeModifier>> attr,
			Consumer<List<Tuple<EquipmentSlotType, AttributeModifier>>> setter) {
		super(parent, "gui.act.modifier.attr", new ArrayList<>(), setter, new Tuple[0]);
		attr.forEach(tuple -> addListElement(new AttributeListElement(this, tuple)));
		addListElement(new AddElementList(this, supplier));
	}

	@Override
	protected List<Tuple<EquipmentSlotType, AttributeModifier>> get() {
		List<Tuple<EquipmentSlotType, AttributeModifier>> result = new ArrayList<>();
		getElements().stream().filter(le -> le instanceof AttributeListElement).map(le -> (AttributeListElement) le)
				.forEach(ale -> result.add(new Tuple<>(ale.slot, ale.getModifier())));
		return result;
	}

}
