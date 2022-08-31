package lens24.intent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;
import androidx.core.content.ContextCompat;
import lens24.sdk.R;
import lens24.ui.ScanCardActivity;
import lens24.ui.ScanCardRequest;

import static lens24.ui.ScanCardRequest.DEFAULT_ENABLE_VIBRATION;
import static lens24.ui.ScanCardRequest.DEFAULT_GRAB_CARD_IMAGE;
import static lens24.ui.ScanCardRequest.DEFAULT_SCAN_CARD_HOLDER;
import static lens24.ui.ScanCardRequest.DEFAULT_SCAN_EXPIRATION_DATE;

public final class ScanCardIntent {

    public static final int RESULT_CODE_ERROR = Activity.RESULT_FIRST_USER;

    public static final String RESULT_CARD_DATA = "RESULT_CARD_DATA";
    public static final String RESULT_CARD_IMAGE = "RESULT_CARD_IMAGE";
    public static final String RESULT_CANCEL_REASON = "RESULT_CANCEL_REASON";

    public static final int BACK_PRESSED = 1;
    public static final int ADD_MANUALLY_PRESSED = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {BACK_PRESSED, ADD_MANUALLY_PRESSED})
    public @interface CancelReason {
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public static final String KEY_SCAN_CARD_REQUEST = "lens24.ui.ScanCardActivity.SCAN_CARD_REQUEST";

    private ScanCardIntent() {
    }

    public final static class Builder {

        private final Context mContext;

        private boolean mEnableVibration = DEFAULT_ENABLE_VIBRATION;

        private boolean mScanExpirationDate = DEFAULT_SCAN_EXPIRATION_DATE;

        private boolean mScanCardHolder = DEFAULT_SCAN_CARD_HOLDER;

        private boolean mGrabCardImage = DEFAULT_GRAB_CARD_IMAGE;

        private String mHint;

        private String mTitle;

        private String mManualInputButton;

        private String mLottieJsonAnimation;

        private int mMainColor;

        private String mBottomHint;

        public Builder(Context context) {
            mContext = context;
        }

        /**
         * Scan expiration date. Default: <b>true</b>
         */
        public Builder setScanExpirationDate(boolean scanExpirationDate) {
            mScanExpirationDate = scanExpirationDate;
            return this;
        }

        /**
         * Scan the card holder's name. Default: <b>true</b>
         */
        public Builder setScanCardHolder(boolean scanCardHolder) {
            mScanCardHolder = scanCardHolder;
            return this;
        }


        /**
         * Enables or disables vibration in the library.<Br> Default: <b>true</b>
         */
        public Builder setVibrationEnabled(boolean enableVibration) {
            mEnableVibration = enableVibration;
            return this;
        }


        /**
         * Defines if the card image will be captured.
         *
         * @param enable Defines if the card image will be captured. Default: <b>false</b>
         */
        public Builder setSaveCard(boolean enable) {
            mGrabCardImage = enable;
            return this;
        }

        /**
         * Defines hint under card frame.
         *
         * @param text Defines hint text under card frame. Default: <b>null</b>
         */
        public Builder setHint(String text) {
            mHint = text;
            return this;
        }

        /**
         * Defines toolbar title.
         *
         * @param text Defines toolbar title text. Default: <b>null</b>
         */
        public Builder setToolbarTitle(String text) {
            mTitle = text;
            return this;
        }

        /**
         * Defines if the manual input button exists.
         *
         * @param label Defines manual input button text. If null button will be invisible. Default:
         *              <b>null</b>
         */
        public Builder setManualInputButtonText(String label) {
            mManualInputButton = label;
            return this;
        }

        /**
         * Defines Lottie animation instead ProgressBar.
         *
         * @param jsonAnimation Defines json animation data for Lottie animation. Default:
         *                      <b>null</b>
         */
        public Builder setLottieJsonAnimation(String jsonAnimation) {
            mLottieJsonAnimation = jsonAnimation;
            return this;
        }

        /**
         * Defines main color of UI.
         *
         * @param mainColor Defines color of frame borders, frame lines, button text and
         *                  progressBar. Not recommended to use color with transparency. Default:
         *                  <b>#8DC641</b>
         */
        public Builder setMainColor(int mainColor) {
            mMainColor = mainColor;
            return this;
        }

        /**
         * Defines bottom hint below card frame.
         *
         * @param text Defines hint text under card frame. Default: <b>null</b>
         */
        public Builder setBottomHint(String text) {
            mBottomHint = text;
            return this;
        }

        public Intent build() {
            Intent intent = new Intent(mContext, ScanCardActivity.class);
            ScanCardRequest request = new ScanCardRequest(
                    mEnableVibration,
                    mScanExpirationDate,
                    mScanCardHolder,
                    mGrabCardImage,
                    mHint,
                    mTitle,
                    mManualInputButton,
                    mLottieJsonAnimation,
                    ContextCompat.getColor(mContext, mMainColor == 0 ? R.color.primary_color : mMainColor),
                    mBottomHint
            );
            intent.putExtra(KEY_SCAN_CARD_REQUEST, request);

            return intent;
        }
    }
}
