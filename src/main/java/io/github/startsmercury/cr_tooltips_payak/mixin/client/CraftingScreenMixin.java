package io.github.startsmercury.cr_tooltips_payak.mixin.client;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import finalforeach.cosmicreach.items.ISlotContainerParent;
import finalforeach.cosmicreach.items.screens.BaseItemScreen;
import finalforeach.cosmicreach.items.screens.CraftingScreen;
import finalforeach.cosmicreach.ui.widgets.ItemCatalogWidget;
import finalforeach.cosmicreach.ui.widgets.ItemStackWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CraftingScreen.class)
public class CraftingScreenMixin extends BaseItemScreen {
    @Shadow
    ItemCatalogWidget itemCatalogWidget;

    public CraftingScreenMixin(final int windowId, final ISlotContainerParent parent) {
        super(windowId, parent);
    }

    @Override
    public void drawTooltips(final Batch batch) {
        super.drawTooltips(batch);

        final var table = (Group) this.itemCatalogWidget.getChild(1);
        final var children = table.getChildren();
        final var items = children.items;

        for (int i = 0, end = children.size - 1; i < end; i++) {
            ((ItemStackWidget) items[i]).drawTooltip(batch);
        }
    }
}
