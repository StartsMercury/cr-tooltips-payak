package io.github.startsmercury.cr_tooltips_payak.impl.client;

/**
 * Injected extensions for {@link InGameExtension}.
 */
public interface InGameExtension {
    /**
     * Commit, show, and fade out tooltip.
     *
     * @param text the text to display
     */
    void cr_tooltips_payak$commitTooltip(String text);

    /**
     * Immediately hide shown tooltip.
     */
    void cr_tooltips_payak$hideTooltip();
}
