package lens24.camera;

import android.hardware.Camera;
import android.util.Log;

import androidx.annotation.RestrictTo;
import lens24.ndk.RecognitionCore;
import lens24.ndk.TorchStatusListener;
import lens24.utils.Constants;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class TorchManager {

    private static final boolean DBG = Constants.DEBUG;
    private static final String TAG = "TorchManager";

    private final Camera mCamera;

    private boolean mPaused;

    private boolean mTorchTurnedOn;

    private final RecognitionCore mRecognitionCore;

    private final TorchStatusListener mTorchStatusListener;

    public TorchManager(RecognitionCore recognitionCore, Camera camera, TorchStatusListener torchStatusListener) {
        mCamera = camera;
        mRecognitionCore = recognitionCore;
        mTorchStatusListener = torchStatusListener;
    }

    public TorchManager(RecognitionCore recognitionCore, Camera camera) {
        this(recognitionCore, camera, null);
    }

    public void pause() {
        if (DBG) Log.d(TAG, "pause()");
        CameraConfigurationUtils.setFlashLight(mCamera, false);
        mPaused = true;
        mRecognitionCore.setTorchListener(null);
    }

    public void resume() {
        if (DBG) Log.d(TAG, "resume()");
        mPaused = false;
        mRecognitionCore.setTorchListener(mRecognitionCoreTorchStatusListener);
        mRecognitionCore.setTorchStatus(mTorchTurnedOn);
    }

    public void destroy() {
        mRecognitionCore.setTorchListener(null);
    }

    private boolean isTorchTurnedOn() {
        String flashMode = mCamera.getParameters().getFlashMode();
        return Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)
                || Camera.Parameters.FLASH_MODE_ON.equals(flashMode);
    }

    public void toggleTorch() {
        if (mPaused) return;
        boolean newStatus = !isTorchTurnedOn();
        if (DBG) Log.d(TAG, "toggleTorch() called with newStatus: " +  newStatus);
        mRecognitionCore.setTorchStatus(newStatus);

        // onTorchStatusChanged() will not be called if the RecognitionCore internal status will not be changed.
        // Sync twice to keep safe
        CameraConfigurationUtils.setFlashLight(mCamera, newStatus);
    }

    private final TorchStatusListener mRecognitionCoreTorchStatusListener = new TorchStatusListener() {

        // called from RecognitionCore
        @Override
        public void onTorchStatusChanged(boolean turnTorchOn) {
            if (mCamera == null) return;
            if (DBG)
                Log.d(TAG, "onTorchStatusChanged() called with: " + "turnTorchOn = [" + turnTorchOn + "]");
            if (turnTorchOn) {
                mTorchTurnedOn = true;
                if (!mPaused) CameraConfigurationUtils.setFlashLight(mCamera, true);
            } else {
                mTorchTurnedOn = false;
                CameraConfigurationUtils.setFlashLight(mCamera, false);
            }
            if (mTorchStatusListener != null) {
                mTorchStatusListener.onTorchStatusChanged(turnTorchOn);
            }
        }
    };
}
