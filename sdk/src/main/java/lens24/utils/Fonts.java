package lens24.utils;

import android.content.Context;
import android.graphics.Typeface;

public final class Fonts {

    private static volatile Typeface sCardFont;

    private Fonts(Context context) { }

    public static Typeface getCardFont(Context context) {
        // Font loading removed. Return default Typeface instead.
        return Typeface.DEFAULT;
    }
}
