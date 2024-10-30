package io.github.startsmercury.cr_tooltips_payak.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import finalforeach.cosmicreach.items.ItemCatalog;
import finalforeach.cosmicreach.items.ItemStack;
import io.github.startsmercury.cr_tooltips_payak.impl.client.ItemCatalogExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin class.
 *
 * @see Mixin
 */
@Mixin(ItemCatalog.class)
public class ItemCatalogMixin implements ItemCatalogExtension {
    @Unique
    private String lastHoveredItemStackName;

    @Inject(
        method = "drawItems",
        at = @At(
            value = "INVOKE",
            shift = At.Shift.AFTER,
            target = "Lcom/badlogic/gdx/utils/viewport/Viewport;setScreenBounds(IIII)V",
            ordinal = 0
        )
    )
    private void captureLastHoveredItemStackName(
        final CallbackInfo callback,
        final @Local(ordinal = 0) ItemStack itemStack
    ) {
        this.lastHoveredItemStackName = itemStack.getName();
    }

    @Inject(method = "drawItems", at = @At("HEAD"))
    private void forgetLastHoveredItemStackName(final CallbackInfo callback) {
        this.lastHoveredItemStackName = null;
    }

    @Override
    @Unique
    public String getHoveredItemStackName() {
        return this.lastHoveredItemStackName;
    }
}
