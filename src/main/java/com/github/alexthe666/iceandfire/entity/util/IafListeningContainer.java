package com.github.alexthe666.iceandfire.entity.util;

import net.minecraft.world.SimpleContainer;

/**
 * 1.18 {@link SimpleContainer} exposed addListener/removeListener. 26.1 dropped that API, so
 * inventory entities keep the same setChanged -&gt; updateAttributes / saddle-equip callback by
 * attaching a listener after the initial item copy (matching the old addListener-after-copy order).
 */
public class IafListeningContainer extends SimpleContainer {
    private Runnable listener;

    public IafListeningContainer(int size) {
        super(size);
    }

    public void setListener(Runnable listener) {
        this.listener = listener;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.listener != null) {
            this.listener.run();
        }
    }
}
