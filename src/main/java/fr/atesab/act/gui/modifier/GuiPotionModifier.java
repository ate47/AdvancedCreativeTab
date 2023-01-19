package fr.atesab.act.gui.modifier;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import fr.atesab.act.gui.selector.GuiButtonListSelector;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.Tuple;
import fr.atesab.act.utils.ItemUtils.PotionInformation;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;

public class GuiPotionModifier extends GuiListModifier<PotionInformation> {
	private static class CustomPotionListElement extends ListElement {
		private GuiPotionModifier parent;
		private PotionEffect effect;
		private GuiTextField duration, amplifier;
		private boolean errDur = false, errAmp = false;
		private GuiButton type, ambient, showParticles;

		public CustomPotionListElement(GuiPotionModifier parent, PotionEffect effect) {
			super(400, 50);
			this.parent = parent;
			this.effect = effect;
			int l = 5 + Math.max(
					fontRenderer.getStringWidth(I18n.format("gui.act.modifier.meta.potion.duration") + " : "),
					fontRenderer.getStringWidth(I18n.format("gui.act.modifier.meta.potion.amplifier") + " : "));
			duration = new GuiTextField(0, fontRenderer, l, 1, 150 - l, 18);
			duration.setText(String.valueOf(effect.getDuration()));
			amplifier = new GuiTextField(0, fontRenderer, l, 22, 150 - l, 18);
			amplifier.setText(String.valueOf(effect.getAmplifier()));
			buttonList.add(type = new GuiButton(0, 153, 0, I18n.format("gui.act.modifier.meta.potion.type")));
			buttonList.add(
					ambient = new GuiButton(1, 153, 21, 100, 20, I18n.format("gui.act.modifier.meta.potion.ambient")));
			buttonList.add(showParticles = new GuiButton(2, 255, 21, 99, 20,
					I18n.format("gui.act.modifier.meta.potion.showParticles")));
			buttonList.add(new RemoveElementButton(parent, 355, 0, 20, 20, this));
			buttonList.add(new AddElementButton(parent, 377, 0, 20, 20, this, parent.supplier));
			buttonList.add(new AddElementButton(parent, 355, 21, 43, 20, I18n.format("gui.act.give.copy"), this,
					() -> new CustomPotionListElement(parent, getEffect())));
			setButtonText();
		}

		@Override
		protected void actionPerformed(GuiButton button) {
			switch (button.id) {
			case 0: // type
				List<Tuple<String, Potion>> pots = new ArrayList<>();
				Potion.REGISTRY.forEach(e -> {
					Potion pot = (Potion) e;
					pots.add(new Tuple<>(I18n.format(pot.getName()), pot));
				});
				mc.displayGuiScreen(new GuiButtonListSelector<Potion>(parent, pots, pot -> {
					effect = new PotionEffect(pot, effect.getDuration(), effect.getAmplifier(), effect.getIsAmbient(),
							effect.doesShowParticles());
					setButtonText();
					return null;
				}));
				break;
			case 1: // ambient
				effect = new PotionEffect(effect.getPotion(), effect.getDuration(), effect.getAmplifier(),
						!effect.getIsAmbient(), effect.doesShowParticles());
				break;
			case 2: // showParticles
				effect = new PotionEffect(effect.getPotion(), effect.getDuration(), effect.getAmplifier(),
						effect.getIsAmbient(), !effect.doesShowParticles());
				break;
			}
			setButtonText();
			super.actionPerformed(button);
		}

		@Override
		public void draw(int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
			GuiUtils.drawRelative(amplifier, offsetX, offsetY);
			GuiUtils.drawRelative(duration, offsetX, offsetY);
			GuiUtils.drawRightString(fontRenderer, I18n.format("gui.act.modifier.meta.potion.duration") + " : ",
					duration, (errDur ? Color.RED : Color.WHITE).getRGB(), offsetX, offsetY);
			GuiUtils.drawRightString(fontRenderer, I18n.format("gui.act.modifier.meta.potion.amplifier") + " : ",
					amplifier, (errAmp ? Color.RED : Color.WHITE).getRGB(), offsetX, offsetY);
			super.draw(offsetX, offsetY, mouseX, mouseY, partialTicks);
		}

		public PotionEffect getEffect() {
			return new PotionEffect(effect);
		}

		@Override
		public void init() {
			amplifier.setFocused(false);
			duration.setFocused(false);
			super.init();
		}

		@Override
		public boolean isFocused() {
			return amplifier.isFocused() || duration.isFocused();
		}

		@Override
		public void keyTyped(char typedChar, int keyCode) {
			amplifier.textboxKeyTyped(typedChar, keyCode);
			duration.textboxKeyTyped(typedChar, keyCode);
			super.keyTyped(typedChar, keyCode);
		}

		@Override
		public boolean match(String search) {
			return I18n.format(effect.getPotion().getName()).toLowerCase().contains(search.toLowerCase());
		}

		@Override
		public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
			amplifier.mouseClicked(mouseX, mouseY, mouseButton);
			duration.mouseClicked(mouseX, mouseY, mouseButton);
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}

		private void setButtonText() {
			type.displayString = I18n.format("gui.act.modifier.meta.potion.type") + " ("
					+ I18n.format(effect.getPotion().getName()) + ")";
			ambient.packedFGColour = GuiUtils.getRedGreen(effect.getIsAmbient());
			showParticles.packedFGColour = GuiUtils.getRedGreen(effect.doesShowParticles());
		}

		@Override
		public void update() {
			amplifier.updateCursorCounter();
			duration.updateCursorCounter();
			int dur = effect.getDuration();
			int amp = effect.getAmplifier();
			try {
				dur = Integer.valueOf(duration.getText());
				errAmp = false;
			} catch (Exception e) {
				errAmp = true;
			}
			try {
				int i = Integer.valueOf(amplifier.getText());
				if (!(errAmp = i < -128 || i > 127))
					amp = i;
			} catch (Exception e) {
				errAmp = true;
			}
			effect = new PotionEffect(effect.getPotion(), dur, amp, effect.getIsAmbient(), effect.doesShowParticles());
			super.update();
		}

	}

	private static class MainPotionListElement extends ListElement {
		private static String getPotionName(PotionType pot) {
			String name = pot.getNamePrefixed("");
			return name + (pot.getRegistryName().toString().contains("long_")
					? " (" + I18n.format("gui.act.modifier.meta.potion.long") + ")"
					: pot.getRegistryName().toString().contains("strong_") ? " II" : "");
		}

		private GuiPotionModifier parent;

		private GuiButton type;

		public MainPotionListElement(GuiPotionModifier parent) {
			super(400, 29);
			this.parent = parent;
			buttonList.add(new GuiButton(0, 0, 0, I18n.format("gui.act.modifier.meta.setColor")));
			buttonList.add(type = new GuiButton(1, 201, 0, 199, 20, ""));
			defineButton();
		}

		@Override
		protected void actionPerformed(GuiButton button) {
			switch (button.id) {
			case 0:
				mc.displayGuiScreen(new GuiColorModifier(parent, i -> parent.customColor = i, parent.customColor, -1));
				break;
			case 1:
				List<Tuple<String, PotionType>> pots = new ArrayList<>();
				PotionType.REGISTRY.forEach(e -> {
					PotionType type = (PotionType) e;
					pots.add(new Tuple<String, PotionType>(getPotionName(type), type));
				});
				mc.displayGuiScreen(new GuiButtonListSelector<PotionType>(parent, pots, pot -> {
					parent.main = pot;
					defineButton();
					return null;
				}));
				break;
			}
			super.actionPerformed(button);
		}

		private void defineButton() {
			type.displayString = I18n.format("gui.act.modifier.meta.potion.type") + " (" + getPotionName(parent.main)
					+ ")";
		}

	}

	private int customColor = -1;

	private PotionType main;

	private final Supplier<ListElement> supplier = () -> new CustomPotionListElement(this,
			new PotionEffect(Potion.REGISTRY.getObjectById(1)));

	public GuiPotionModifier(GuiScreen parent, Consumer<PotionInformation> setter, PotionInformation info) {
		super(parent, new ArrayList<>(), setter);
		this.customColor = info.getCustomColor();
		this.main = info.getMain();
		elements.add(new MainPotionListElement(this));
		info.getCustomEffects().forEach(t -> elements.add(new CustomPotionListElement(this, t)));
		elements.add(new AddElementList(this, supplier));
	}

	@Override
	protected PotionInformation get() {
		List<PotionEffect> customEffects = new ArrayList<>();
		elements.stream().filter(le -> le instanceof CustomPotionListElement).map(le -> (CustomPotionListElement) le)
				.forEach(cpl -> customEffects.add(cpl.getEffect()));
		return new PotionInformation(customColor, main, customEffects);
	}

}
