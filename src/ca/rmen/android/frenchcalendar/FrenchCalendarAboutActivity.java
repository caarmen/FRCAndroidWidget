package ca.rmen.android.frenchcalendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FrenchCalendarAboutActivity extends Activity { // NO_UCD (use default)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        View view = findViewById(R.id.aboutview);
        setFontForAllTextViews(this, view);
    }

    private static Typeface sTypeFace;

    private static void ensureTypeFace(final Context context) {
        if (sTypeFace == null) {
            sTypeFace = Typeface.createFromAsset(context.getAssets(), "Gabrielle.ttf");
        }
    }

    private static void setFontForAllTextViews(final Context context, final View parent) {
        ensureTypeFace(context);
        if (parent instanceof TextView) {
            ((TextView) parent).setTypeface(sTypeFace);
        } else if (parent instanceof ViewGroup) {
            final ViewGroup parentGroup = (ViewGroup) parent;
            final int childCount = parentGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View v = parentGroup.getChildAt(i);
                // recurses
                setFontForAllTextViews(context, v);
            }
        }
    }
}
