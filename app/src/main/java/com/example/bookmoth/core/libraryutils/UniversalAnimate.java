package com.example.bookmoth.core.libraryutils;

import android.animation.ObjectAnimator;
import android.view.ViewGroup;

public class UniversalAnimate {
    public static final int PLACEMENT_TOP = 0;
    public static final int PLACEMENT_END = 1;
    public static final int PLACEMENT_BOTTOM = 2;
    public static final int PLACEMENT_START = 3;
    public static void animateWallHiding(ViewGroup tb, int placement, boolean doHiding) {
        switch (placement) {
            case PLACEMENT_TOP:
                if (!doHiding) tb.setTranslationY(-tb.getHeight());
                ObjectAnimator.ofFloat(tb, "translationY", doHiding ? -tb.getHeight() : 0).setDuration(200).start();
                break;
            case PLACEMENT_END:
                if (!doHiding) tb.setTranslationY(tb.getWidth());
                ObjectAnimator.ofFloat(tb, "translationX", doHiding ? tb.getWidth() : 0).setDuration(200).start();
                break;
            case PLACEMENT_BOTTOM:
                if (!doHiding) tb.setTranslationY(tb.getHeight());
                ObjectAnimator.ofFloat(tb, "translationY", doHiding ? tb.getHeight() : 0).setDuration(200).start();
                break;
            case PLACEMENT_START:
                if (!doHiding) tb.setTranslationY(-tb.getWidth());
                ObjectAnimator.ofFloat(tb, "translationX", doHiding ? -tb.getWidth() : 0).setDuration(200).start();
                break;
        }
    }
}
