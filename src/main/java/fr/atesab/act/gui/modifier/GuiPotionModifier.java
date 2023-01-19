package fr.atesab.act.gui.modifier;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import fr.atesab.act.gui.selector.GuiButtonListSelector;
import fr.atesab.act.gui.selector.GuiTypeListSelector;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.Tuple;
import fr.atesab.act.utils.ItemUtils.PotionInformation;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.StatCollector;

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
				Potion.getPotionLocations().forEach(rl -> {
					Potion pot = Potion.getPotionFromResourceLocation(rl.toString());
					pots.add(new Tuple<>(I18n.format(pot.getName()), pot));
				});
				mc.displayGuiScreen(new GuiButtonListSelector<Potion>(parent, pots, pot -> {
					effect = new PotionEffect(pot.id, effect.getDuration(), effect.getAmplifier(),
							effect.getIsAmbient(), effect.getIsShowParticles());
					setButtonText();
					return null;
				}));
				break;
			case 1: // ambient
				effect = new PotionEffect(effect.getPotionID(), effect.getDuration(), effect.getAmplifier(),
						!effect.getIsAmbient(), effect.getIsShowParticles());
				break;
			case 2: // showParticles
				effect = new PotionEffect(effect.getPotionID(), effect.getDuration(), effect.getAmplifier(),
						effect.getIsAmbient(), !effect.getIsShowParticles());
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
			return I18n.format(effect.getEffectName()).toLowerCase().contains(search.toLowerCase());
		}

		@Override
		public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
			amplifier.mouseClicked(mouseX, mouseY, mouseButton);
			duration.mouseClicked(mouseX, mouseY, mouseButton);
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}

		private void setButtonText() {
			type.displayString = I18n.format("gui.act.modifier.meta.potion.type") + " ("
					+ I18n.format(effect.getEffectName()) + ")";
			ambient.packedFGColour = GuiUtils.getRedGreen(effect.getIsAmbient());
			showParticles.packedFGColour = GuiUtils.getRedGreen(effect.getIsShowParticles());
		}

		@Override
		public void update() {
			amplifier.updateCursorCounter();
			duration.updateCursorCounter();
			int dur = effect.getDuration();
			int amp = effect.getAmplifier();
			try {
				dur = Integer.valueOf(duration.getText());
				errDur = false;
			} catch (Exception e) {
				errDur = true;
			}
			try {
				int i = Integer.valueOf(amplifier.getText());
				if (!(errAmp = i < -128 || i > 127))
					amp = i;
			} catch (Exception e) {
				errAmp = true;
			}
			effect = new PotionEffect(effect.getPotionID(), dur, amp, effect.getIsAmbient(),
					effect.getIsShowParticles());
			super.update();
		}

	}

	private static class MainPotionListElement extends ListElement {
		private static String getPotionName(int meta) {
			List<PotionEffect> list = PotionHelper.getPotionEffects(meta, false);
			if (list == null || list.isEmpty())
				return null;
			return StatCollector.translateToLocal(((PotionEffect) list.get(0)).getEffectName() + ".postfix").trim();
		}

		private GuiPotionModifier parent;

		private GuiButton type;

		public MainPotionListElement(GuiPotionModifier parent) {
			super(200, 29);
			this.parent = parent;
			buttonList.add(type = new GuiButton(1, 0, 0, 199, 20, ""));
			defineButton();
		}

		@Override
		protected void actionPerformed(GuiButton button) {
			switch (button.id) {
			case 1:
				List<ItemStack> pots = new ArrayList<>();
				for (int i = 0; i < 15; i++) {
					if (i != 7)
						pots.add(new ItemStack(Items.potionitem, 1, i | (parent.meta & (~15))));
				}
				mc.displayGuiScreen(new GuiTypeListSelector(parent, pot -> {
					parent.meta = pot.getItemDamage();
					defineButton();
					return null;
				}, pots));
				break;
			}
			super.actionPerformed(button);
		}

		private void defineButton() {
			String name = getPotionName(parent.meta);
			type.displayString = I18n.format("gui.act.modifier.meta.potion.type")
					+ (name != null ? " (" + getPotionName(parent.meta) + ")" : "");
		}

	}

	private int meta;

	private final Supplier<ListElement> supplier = () -> new CustomPotionListElement(this,
			new PotionEffect(Potion.moveSpeed.id, 0));

	public GuiPotionModifier(GuiScreen parent, Consumer<PotionInformation> setter, PotionInformation info) {
		super(parent, new ArrayList<>(), setter);
		this.meta = info.getMeta();
		elements.add(new MainPotionListElement(this));
		info.getCustomEffects().forEach(t -> elements.add(new CustomPotionListElement(this, t)));
		elements.add(new AddElementList(this, supplier));
	}

	@Override
	protected PotionInformation get() {
		List<PotionEffect> customEffects = new ArrayList<>();
		elements.stream().filter(le -> le instanceof CustomPotionListElement)
				.forEach(le -> customEffects.add(((CustomPotionListElement) le).getEffect()));
		return new PotionInformation(meta, customEffects);
	}

}
