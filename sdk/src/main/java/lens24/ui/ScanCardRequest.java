package lens24.ui;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

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

    private final int mMainColor;

    private final String mBottomHint;

    private static final ScanCardRequest sDefaultInstance = new ScanCardRequest(
            DEFAULT_ENABLE_VIBRATION, DEFAULT_SCAN_EXPIRATION_DATE, DEFAULT_SCAN_CARD_HOLDER,
            DEFAULT_GRAB_CARD_IMAGE, null, null, null, null, 0, null);

    private ScanCardRequest(Parcel in) {
        mEnableVibration = in.readByte() != 0;
        mScanExpirationDate = in.readByte() != 0;
        mScanCardHolder = in.readByte() != 0;
        mGrabCardImage = in.readByte() != 0;
        mHint = in.readString();
        mTitle = in.readString();
        mManualInputButtonLabel = in.readString();
        mLottieJsonAnimation = in.readString();
        mMainColor = in.readInt();
        mBottomHint = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (mEnableVibration ? 1 : 0));
        dest.writeByte((byte) (mScanExpirationDate ? 1 : 0));
        dest.writeByte((byte) (mScanCardHolder ? 1 : 0));
        dest.writeByte((byte) (mGrabCardImage ? 1 : 0));
        dest.writeString(mHint);
        dest.writeString(mTitle);
        dest.writeString(mManualInputButtonLabel);
        dest.writeString(mLottieJsonAnimation);
        dest.writeInt(mMainColor);
        dest.writeString(mBottomHint);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ScanCardRequest> CREATOR = new Creator<>() {
        @Override
        public ScanCardRequest createFromParcel(Parcel in) {
            return new ScanCardRequest(in);
        }

        @Override
        public ScanCardRequest[] newArray(int size) {
            return new ScanCardRequest[size];
        }
    };

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
                           String lottieJsonAnimation,
                           int mainColor,
                           String bottomHint) {
        this.mEnableVibration = enableVibration;
        this.mScanExpirationDate = scanExpirationDate;
        this.mScanCardHolder = scanCardHolder;
        this.mGrabCardImage = grabCardImage;
        this.mHint = hint;
        this.mTitle = title;
        this.mManualInputButtonLabel = manualInputButtonLabel;
        this.mLottieJsonAnimation = lottieJsonAnimation;
        this.mMainColor = mainColor;
        this.mBottomHint = bottomHint;
    }

    public boolean isVibrationEnabled() {
        return mEnableVibration;
    }

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

    public int getMainColor() {
        return mMainColor;
    }

    public String getBottomHint() {
        return mBottomHint;
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
        if (mMainColor != that.mMainColor) return false;
        if (!Objects.equals(mHint, that.mHint)) return false;
        if (!Objects.equals(mTitle, that.mTitle)) return false;
        if (!Objects.equals(mManualInputButtonLabel, that.mManualInputButtonLabel))
            return false;
        if (!Objects.equals(mLottieJsonAnimation, that.mLottieJsonAnimation))
            return false;
        return Objects.equals(mBottomHint, that.mBottomHint);
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
        result = 31 * result + mMainColor;
        result = 31 * result + (mBottomHint != null ? mBottomHint.hashCode() : 0);
        return result;
    }
}
