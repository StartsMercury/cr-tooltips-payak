package io.github.startsmercury.cr_tooltips_payak.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import finalforeach.cosmicreach.ui.actions.FollowMouseAction;
import finalforeach.cosmicreach.ui.widgets.ItemCatalogWidget;
import io.github.startsmercury.cr_tooltips_payak.impl.client.FollowMouseActionImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemCatalogWidget.class)
public class ItemCatalogWidgetMixin {
    @WrapOperation(method = "<init>()V", at = @At(value = "NEW", target = "()Lfinalforeach/cosmicreach/ui/actions/FollowMouseAction;"))
    private FollowMouseAction replaceAction(final Operation<FollowMouseAction> original) {
        return new FollowMouseActionImpl();
    }
}
