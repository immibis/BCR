package buildcraft.energy.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import buildcraft.core.DefaultProps;
import buildcraft.core.gui.BuildCraftContainer;
import buildcraft.core.gui.GuiBuildCraft;
import buildcraft.energy.Engine;
import buildcraft.energy.TileEngine;

public abstract class GuiEngine extends GuiBuildCraft {

	protected class EngineLedger extends Ledger {

		Engine engine;
		int headerColour = 0xe1c92f;
		int subheaderColour = 0xaaafb8;
		int textColour = 0x000000;

		public EngineLedger(Engine engine) {
			this.engine = engine;
			maxHeight = 94;
			overlayColor = 0xd46c1f;
		}

		@Override
		public void draw(int x, int y) {

			// Draw background
			drawBackground(x, y);

			// Draw icon
			drawIcon(DefaultProps.TEXTURE_ICONS, 0, x + 3, y + 4);

			if (!isFullyOpened())
				return;

			fontRenderer.drawStringWithShadow(I18n.getString("gui.energy"), x + 22, y + 8, headerColour);
			fontRenderer.drawStringWithShadow(I18n.getString("gui.currentOutput") + ":", x + 22, y + 20, subheaderColour);
			fontRenderer.drawString(engine.getCurrentOutput() + " MJ/t", x + 22, y + 32, textColour);
			fontRenderer.drawStringWithShadow(I18n.getString("gui.stored") + ":", x + 22, y + 44, subheaderColour);
			fontRenderer.drawString(engine.getEnergyStored() + " MJ", x + 22, y + 56, textColour);
			fontRenderer.drawStringWithShadow(I18n.getString("gui.heat") + ":", x + 22, y + 68, subheaderColour);
			fontRenderer.drawString(((double) engine.getHeat() / (double) 10) + " \u00B0C", x + 22, y + 80, textColour);

		}

		@Override
		public String getTooltip() {
			return engine.getCurrentOutput() + " MJ/t";
		}

	}

	public GuiEngine(BuildCraftContainer container, IInventory inventory) {
		super(container, inventory);
	}

	@Override
	protected void initLedgers(IInventory inventory) {
		super.initLedgers(inventory);
		ledgerManager.add(new EngineLedger(((TileEngine) tile).engine));
	}

}
