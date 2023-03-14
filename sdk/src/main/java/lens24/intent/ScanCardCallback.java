package lens24.intent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.jetbrains.annotations.Contract;

public final class ScanCardCallback {
    public final static class Builder {
        private SuccessListener onSuccess;
        private CanceledListener onBackPressed;
        private CanceledListener onManualInput;
        private ErrorListener onError;

        public Builder setOnSuccess(SuccessListener onSuccess) {
            this.onSuccess = onSuccess;
            return this;
        }

        public Builder setOnBackPressed(CanceledListener onBackPressed) {
            this.onBackPressed = onBackPressed;
            return this;
        }

        public Builder setOnManualInput(CanceledListener onManualInput) {
            this.onManualInput = onManualInput;
            return this;
        }

        public Builder setOnError(ErrorListener onError) {
            this.onError = onError;
            return this;
        }

        @NonNull
        @Contract(pure = true)
        public ActivityResultCallback<ActivityResult> build() {
            return result -> {
                String tag = ScanCardCallback.class.getSimpleName();
                if (result.getResultCode() == Activity.RESULT_OK && onSuccess != null) {
                    assert result.getData() != null;
                    Card card = result.getData().getParcelableExtra(ScanCardIntent.RESULT_CARD_DATA);
                    Log.i(tag, "Card info: " + card.toString());
                    byte[] cardImage = result.getData().getByteArrayExtra(ScanCardIntent.RESULT_CARD_IMAGE);
                    if (cardImage != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(cardImage, 0, cardImage.length);
                        onSuccess.onResult(card, bitmap);
                    } else {
                        onSuccess.onResult(card, null);
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    @ScanCardIntent.CancelReason final int reason;
                    if (result.getData() != null) {
                        reason = result.getData().getIntExtra(ScanCardIntent.RESULT_CANCEL_REASON, ScanCardIntent.BACK_PRESSED);
                    } else {
                        reason = ScanCardIntent.BACK_PRESSED;
                    }
                    switch (reason) {
                        case ScanCardIntent.BACK_PRESSED: {
                            Log.i(tag, "back pressed");
                            if (onBackPressed != null) {
                                onBackPressed.onCanceled();
                            }
                            break;
                        }
                        case ScanCardIntent.ADD_MANUALLY_PRESSED: {
                            Log.i(tag, "manual input button pressed");
                            if (onManualInput != null) {
                                onManualInput.onCanceled();
                            }
                            break;
                        }
                    }
                } else if (result.getResultCode() == ScanCardIntent.RESULT_CODE_ERROR) {
                    Log.i(tag, "scan failed");
                    if (onError != null) {
                        onError.onError();
                    }
                }
            };
        }
    }

    public interface SuccessListener {
        void onResult(@NonNull Card card, @Nullable Bitmap bitmap);
    }

    public interface CanceledListener {
        void onCanceled();
    }

    public interface ErrorListener {
        void onError();
    }
}




