package fr.atesab.act.gui.modifier;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import fr.atesab.act.gui.selector.GuiButtonListSelector;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils.PotionInformation;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.registry.Registry;

public class GuiPotionModifier extends GuiListModifier<PotionInformation> {
	private static class CustomPotionListElement extends ListElement {
		private TextFieldWidget duration, amplifier;
		private Effect potion;
		private int durationTime, amplifierValue;
		private boolean ambient, showParticles, showIcon;
		private boolean errDur = false, errAmp = false;
		private Button type;

		@SuppressWarnings("deprecation")
		public CustomPotionListElement(GuiPotionModifier parent, EffectInstance potionEffect) {
			super(400, 50);
			int l = 5 + Math.max(
					fontRenderer.getStringWidth(I18n.format("gui.act.modifier.meta.potion.duration") + " : "),
					fontRenderer.getStringWidth(I18n.format("gui.act.modifier.meta.potion.amplifier") + " : "));
			potion = potionEffect.getPotion();
			durationTime = potionEffect.getDuration();
			amplifierValue = potionEffect.getAmplifier();
			ambient = potionEffect.isAmbient();
			showParticles = potionEffect.doesShowParticles();
			showIcon = potionEffect.isShowIcon();

			duration = new TextFieldWidget(fontRenderer, l, 1, 150 - l, 18, "");
			duration.setText(String.valueOf(durationTime));
			amplifier = new TextFieldWidget(fontRenderer, l, 22, 150 - l, 18, "");
			amplifier.setText(String.valueOf(amplifierValue));
			buttonList.add(type = new Button(153, 0, 200, 20, I18n.format("gui.act.modifier.meta.potion.type"), b -> {
				List<Tuple<String, Effect>> pots = new ArrayList<>();
				// Potion.REGISTRY
				Registry.EFFECTS.forEach(pot -> pots.add(new Tuple<>(I18n.format(pot.getName()), pot)));
				mc.displayGuiScreen(
						new GuiButtonListSelector<Effect>(parent, "gui.act.modifier.meta.potion.type", pots, pot -> {
							potion = pot;
							setButtonText();
							return null;
						}));
			}));
			buttonList.add(new GuiBooleanButton(153, 21, 100, 20, I18n.format("gui.act.modifier.meta.potion.ambient"),
					b -> ambient = b, () -> ambient));
			buttonList.add(
					new GuiBooleanButton(255, 21, 99, 20, I18n.format("gui.act.modifier.meta.potion.showParticles"),
							b -> showParticles = b, () -> showParticles));
			buttonList.add(new RemoveElementButton(parent, 355, 0, 20, 20, this));
			buttonList.add(new AddElementButton(parent, 377, 0, 20, 20, this, parent.supplier));
			buttonList.add(new AddElementButton(parent, 355, 21, 43, 20, I18n.format("gui.act.give.copy"), this,
					() -> new CustomPotionListElement(parent, getEffect())));
			setButtonText();
		}

		@Override
		public void draw(int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
			GuiUtils.drawRelative(amplifier, offsetX, offsetY, mouseY, mouseY, partialTicks);
			GuiUtils.drawRelative(duration, offsetX, offsetY, mouseY, mouseY, partialTicks);
			GuiUtils.drawRightString(fontRenderer, I18n.format("gui.act.modifier.meta.potion.duration") + " : ",
					duration, (errDur ? Color.RED : Color.WHITE).getRGB(), offsetX, offsetY);
			GuiUtils.drawRightString(fontRenderer, I18n.format("gui.act.modifier.meta.potion.amplifier") + " : ",
					amplifier, (errAmp ? Color.RED : Color.WHITE).getRGB(), offsetX, offsetY);
			super.draw(offsetX, offsetY, mouseX, mouseY, partialTicks);
		}

		public EffectInstance getEffect() {
			return new EffectInstance(potion, durationTime, amplifierValue, ambient, showParticles, showIcon);
		}

		@Override
		public void init() {
			amplifier.setFocused2(false);
			duration.setFocused2(false);
			super.init();
		}

		@Override
		public boolean isFocused() {
			return amplifier.isFocused() || duration.isFocused();
		}

		@Override
		public boolean charTyped(char key, int modifiers) {
			return amplifier.charTyped(key, modifiers) || duration.charTyped(key, modifiers)
					|| super.charTyped(key, modifiers);
		}

		@Override
		public boolean keyPressed(int key, int scanCode, int modifiers) {
			return amplifier.keyPressed(key, scanCode, modifiers) || duration.keyPressed(key, scanCode, modifiers)
					|| super.keyPressed(key, scanCode, modifiers);
		}

		@Override
		public boolean match(String search) {
			return I18n.format(potion.getName()).toLowerCase().contains(search.toLowerCase());
		}

		@Override
		public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
			amplifier.mouseClicked(mouseX, mouseY, mouseButton);
			duration.mouseClicked(mouseX, mouseY, mouseButton);
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}

		private void setButtonText() {
			type.setMessage(
					I18n.format("gui.act.modifier.meta.potion.type") + " (" + I18n.format(potion.getName()) + ")");
		}

		@Override
		public void update() {
			amplifier.tick();
			duration.tick();
			try {
				durationTime = Integer.valueOf(duration.getText());
				errDur = false;
			} catch (Exception e) {
				errDur = true;
			}
			try {
				int i = Integer.valueOf(amplifier.getText());
				if (!(errAmp = i < -128 || i > 127))
					amplifierValue = i;
			} catch (Exception e) {
				errAmp = true;
			}
			super.update();
		}

	}

	private static class MainPotionListElement extends ListElement {
		private static String getPotionName(Potion pot) {
			String name = pot.getNamePrefixed("");
			return name + (pot.getRegistryName().toString().contains("long_")
					? " (" + I18n.format("gui.act.modifier.meta.potion.long") + ")"
					: pot.getRegistryName().toString().contains("strong_") ? " II" : "");
		}

		private GuiPotionModifier parent;

		private Button type;

		@SuppressWarnings("deprecation")
		public MainPotionListElement(GuiPotionModifier parent) {
			super(400, 29);
			this.parent = parent;
			buttonList.add(
					new Button(0, 0, 200, 20, I18n.format("gui.act.modifier.meta.setColor"), b -> mc.displayGuiScreen(
							new GuiColorModifier(parent, i -> parent.customColor = i, parent.customColor, -1))));
			buttonList.add(type = new Button(201, 0, 199, 20, "", b -> {
				List<Tuple<String, Potion>> pots = new ArrayList<>();
				// PotionType.REGISTRY
				Registry.POTION.forEach(type -> pots.add(new Tuple<>(getPotionName(type), type)));
				mc.displayGuiScreen(new GuiButtonListSelector<Potion>(parent, "gui.act.modifier.meta.potion.type", pots, pot -> {
					parent.main = pot;
					defineButton();
					return null;
				}));
			}));
			defineButton();
		}

		private void defineButton() {
			type.setMessage(I18n.format("gui.act.modifier.meta.potion.type") + " (" + getPotionName(parent.main) + ")");
		}

	}

	private int customColor = -1;

	private Potion main;

	@SuppressWarnings("deprecation")
	private final Supplier<ListElement> supplier = () -> new CustomPotionListElement(this,
			new EffectInstance(Registry.EFFECTS.getByValue(1)));

	@SuppressWarnings("unchecked")
	public GuiPotionModifier(Screen parent, Consumer<PotionInformation> setter, PotionInformation info) {
		super(parent, "gui.act.modifier.meta.potion", new ArrayList<>(), setter, new Tuple[0]);
		this.customColor = info.getCustomColor();
		this.main = info.getMain();
		elements.add(new MainPotionListElement(this));
		info.getCustomEffects().forEach(t -> elements.add(new CustomPotionListElement(this, t)));
		elements.add(new AddElementList(this, supplier));
	}

	@Override
	protected PotionInformation get() {
		List<EffectInstance> customEffects = new ArrayList<>();
		elements.stream().filter(le -> le instanceof CustomPotionListElement).map(le -> (CustomPotionListElement) le)
				.forEach(cpl -> customEffects.add(cpl.getEffect()));
		return new PotionInformation(customColor, main, customEffects);
	}

}
