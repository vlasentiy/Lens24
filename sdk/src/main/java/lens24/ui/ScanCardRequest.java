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

    private static final ScanCardRequest sDefaultInstance = new ScanCardRequest(
            DEFAULT_ENABLE_VIBRATION, DEFAULT_SCAN_EXPIRATION_DATE, DEFAULT_SCAN_CARD_HOLDER,
            DEFAULT_GRAB_CARD_IMAGE, null);

    public static ScanCardRequest getDefault() {
        return sDefaultInstance;
    }

    public ScanCardRequest(boolean enableVibration,
                           boolean scanExpirationDate,
                           boolean scanCardHolder,
                           boolean grabCardImage,
                           String hint) {
        this.mEnableVibration = enableVibration;
        this.mScanExpirationDate = scanExpirationDate;
        this.mScanCardHolder = scanCardHolder;
        this.mGrabCardImage = grabCardImage;
        this.mHint = hint;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScanCardRequest that = (ScanCardRequest) o;

        if (mEnableVibration != that.mEnableVibration) return false;
        if (mScanExpirationDate != that.mScanExpirationDate) return false;
        if (mScanCardHolder != that.mScanCardHolder) return false;
        if (mGrabCardImage != that.mGrabCardImage) return false;
        return Objects.equals(mHint, that.mHint);
    }

    @Override
    public int hashCode() {
        int result = (mEnableVibration ? 1 : 0);
        result = 31 * result + (mScanExpirationDate ? 1 : 0);
        result = 31 * result + (mScanCardHolder ? 1 : 0);
        result = 31 * result + (mGrabCardImage ? 1 : 0);
        result = 31 * result + (mHint != null ? mHint.hashCode() : 0);
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
    }

    private ScanCardRequest(Parcel in) {
        this.mEnableVibration = in.readByte() != 0;
        this.mScanExpirationDate = in.readByte() != 0;
        this.mScanCardHolder = in.readByte() != 0;
        this.mGrabCardImage = in.readByte() != 0;
        this.mHint = in.readString();
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
