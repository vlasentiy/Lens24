package lens24.utils;

import androidx.annotation.RestrictTo;

import lens24.sdk.BuildConfig;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class Constants {

    private Constants() { }

    public static final boolean DEBUG = BuildConfig.DEBUG;

    public static final String ASSETS_DIR = "data";

    public static final String MODEL_DIR = "data/model";

    public static final int NEURO_DATA_VERSION = 9;

}
