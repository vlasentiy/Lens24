package lens24.ndk;


import android.graphics.Bitmap;
import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface RecognitionStatusListener {

    void onRecognitionComplete(RecognitionResult result);

    void onCardImageReceived(Bitmap bitmap);

}
