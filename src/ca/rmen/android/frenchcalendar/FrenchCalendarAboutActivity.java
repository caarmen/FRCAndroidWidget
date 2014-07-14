package ca.rmen.android.frenchcalendar;

import ca.rmen.android.frenchcalendar.render.Font;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class FrenchCalendarAboutActivity extends Activity { // NO_UCD (use default)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        View view = findViewById(R.id.aboutview);
        Font.applyFont(this, view);
    }

}
