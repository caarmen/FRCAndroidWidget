package ca.rmen.android.frenchcalendar.render;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Font {

    private static Typeface sTypeface;

    /**
     * Lazy initialize the font.
     */
    public static Typeface getTypeface(Context context) {
        if (sTypeface == null) sTypeface = Typeface.createFromAsset(context.getAssets(), "Gabrielle.ttf");
        return sTypeface;
    }

    /**
     * Sets the TypeFace for all TextViews in the given parent view, to our custom font.
     */
    public static void applyFont(final Context context, final View parent) {
        Typeface font = getTypeface(context);
        if (parent instanceof TextView) {
            ((TextView) parent).setTypeface(font);
        } else if (parent instanceof ViewGroup) {
            final ViewGroup parentGroup = (ViewGroup) parent;
            final int childCount = parentGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View v = parentGroup.getChildAt(i);
                // recurses
                applyFont(context, v);
            }
        }
    }

}
