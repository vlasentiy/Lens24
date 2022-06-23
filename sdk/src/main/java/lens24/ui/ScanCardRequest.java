package lens24.ui;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class ScanCardRequest implements Parcelable {

    public static final boolean DEFAULT_ENABLE_VIBRATION = true;

    public static final boolean DEFAULT_SCAN_EXPIRATION_DATE = true;

    public static final boolean DEFAULT_SCAN_CARD_HOLDER = true;

    public static final boolean DEFAULT_GRAB_CARD_IMAGE = false;

    private final boolean mEnableVibration;

    private final boolean mScanExpirationDate;

    private final boolean mScanCardHolder;

    private final boolean mGrabCardImage;

    private final String mHint;

    private final String mTitle;

    private final String mManualInputButtonLabel;

    private final String mLottieJsonAnimation;

    private static final ScanCardRequest sDefaultInstance = new ScanCardRequest(
            DEFAULT_ENABLE_VIBRATION, DEFAULT_SCAN_EXPIRATION_DATE, DEFAULT_SCAN_CARD_HOLDER,
            DEFAULT_GRAB_CARD_IMAGE, null, null, null, null);

    public static ScanCardRequest getDefault() {
        return sDefaultInstance;
    }

    public ScanCardRequest(boolean enableVibration,
                           boolean scanExpirationDate,
                           boolean scanCardHolder,
                           boolean grabCardImage,
                           String hint,
                           String title,
                           String manualInputButtonLabel,
                           String lottieJsonAnimation) {
        this.mEnableVibration = enableVibration;
        this.mScanExpirationDate = scanExpirationDate;
        this.mScanCardHolder = scanCardHolder;
        this.mGrabCardImage = grabCardImage;
        this.mHint = hint;
        this.mTitle = title;
        this.mManualInputButtonLabel = manualInputButtonLabel;
        this.mLottieJsonAnimation = lottieJsonAnimation;
    }

    public boolean isVibrationEnabled() { return mEnableVibration; }

    public boolean isScanExpirationDateEnabled() {
        return mScanExpirationDate;
    }

    public boolean isScanCardHolderEnabled() {
        return mScanCardHolder;
    }

    public boolean isGrabCardImageEnabled() {
        return mGrabCardImage;
    }

    public String getHint() {
        return mHint;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getManualInputButtonLabel() {
        return mManualInputButtonLabel;
    }

    public String getLottieJsonAnimation() {
        return mLottieJsonAnimation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScanCardRequest that = (ScanCardRequest) o;

        if (mEnableVibration != that.mEnableVibration) return false;
        if (mScanExpirationDate != that.mScanExpirationDate) return false;
        if (mScanCardHolder != that.mScanCardHolder) return false;
        if (mGrabCardImage != that.mGrabCardImage) return false;
        if (mHint != null ? !mHint.equals(that.mHint) : that.mHint != null) return false;
        if (mTitle != null ? !mTitle.equals(that.mTitle) : that.mTitle != null) return false;
        if (mManualInputButtonLabel != null ? !mManualInputButtonLabel.equals(that.mManualInputButtonLabel) : that.mManualInputButtonLabel != null)
            return false;
        return mLottieJsonAnimation != null ? mLottieJsonAnimation.equals(that.mLottieJsonAnimation) : that.mLottieJsonAnimation == null;
    }

    @Override
    public int hashCode() {
        int result = (mEnableVibration ? 1 : 0);
        result = 31 * result + (mScanExpirationDate ? 1 : 0);
        result = 31 * result + (mScanCardHolder ? 1 : 0);
        result = 31 * result + (mGrabCardImage ? 1 : 0);
        result = 31 * result + (mHint != null ? mHint.hashCode() : 0);
        result = 31 * result + (mTitle != null ? mTitle.hashCode() : 0);
        result = 31 * result + (mManualInputButtonLabel != null ? mManualInputButtonLabel.hashCode() : 0);
        result = 31 * result + (mLottieJsonAnimation != null ? mLottieJsonAnimation.hashCode() : 0);
        return result;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.mEnableVibration ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mScanExpirationDate ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mScanCardHolder ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mGrabCardImage ? (byte) 1 : (byte) 0);
        dest.writeString(this.mHint);
        dest.writeString(this.mTitle);
        dest.writeString(this.mManualInputButtonLabel);
        dest.writeString(this.mLottieJsonAnimation);
    }

    private ScanCardRequest(Parcel in) {
        this.mEnableVibration = in.readByte() != 0;
        this.mScanExpirationDate = in.readByte() != 0;
        this.mScanCardHolder = in.readByte() != 0;
        this.mGrabCardImage = in.readByte() != 0;
        this.mHint = in.readString();
        this.mTitle = in.readString();
        this.mManualInputButtonLabel = in.readString();
        this.mLottieJsonAnimation = in.readString();
    }

    public static final Creator<ScanCardRequest> CREATOR = new Creator<ScanCardRequest>() {
        @Override
        public ScanCardRequest createFromParcel(Parcel source) {
            return new ScanCardRequest(source);
        }

        @Override
        public ScanCardRequest[] newArray(int size) {
            return new ScanCardRequest[size];
        }
    };
}
