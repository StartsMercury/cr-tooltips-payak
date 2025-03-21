package io.github.startsmercury.cr_tooltips_payak.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import finalforeach.cosmicreach.entities.ItemEntity;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.items.ItemStack;
import finalforeach.cosmicreach.items.containers.SlotContainer;
import finalforeach.cosmicreach.ui.UI;
import io.github.startsmercury.cr_tooltips_payak.impl.client.InGameExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixin class.
 *
 * @see Mixin
 */
@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @WrapOperation(
        method = "lambda$update$0",
        at = @At(
            value = "INVOKE",
            target = """
                Lfinalforeach/cosmicreach/items/containers/SlotContainer; \
                addItemStack(                                             \
                    Lfinalforeach/cosmicreach/items/ItemStack;            \
            ) Z                                                           \
            """
        )
    )
    private boolean detectHotBarChange(
        final SlotContainer instance,
        final ItemStack i,
        final Operation<Boolean> original,
        final @Local(ordinal = 0, argsOnly = true) Player player
    ) {
        if (player != InGame.getLocalPlayer()) {
            return original.call(instance, i);
        }

        var itemStack = UI.hotbar.getSelectedItemStack();

        if (!original.call(instance, i)) {
            return false;
        }

        if (itemStack == (itemStack = UI.hotbar.getSelectedItemStack())) {
            return true;
        }

        if (itemStack != null) {
            final var name = itemStack.getName();

            if (name != null) {
                ((InGameExtension) InGame.IN_GAME).cr_tooltips_payak$commitTooltip(name);
                return true;
            }
        }

        ((InGameExtension) InGame.IN_GAME).cr_tooltips_payak$hideTooltip();

        return true;
    }
}
