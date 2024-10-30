package io.github.startsmercury.cr_tooltips_payak.impl.client;

import finalforeach.cosmicreach.items.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Injected extensions for {@link finalforeach.cosmicreach.items.ItemCatalog}.
 */
public interface ItemCatalogExtension {
    /**
     * Returns the {@linkplain ItemStack#getName name} of currently hovered
     * {@link ItemStack} or {@code null} if there is none.
     *
     * @return hovered item stack name
     */
    @Nullable String getHoveredItemStackName();
}
