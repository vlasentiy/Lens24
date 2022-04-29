package lens24.camera;

import android.content.Context;
import androidx.annotation.RestrictTo;

import java.util.concurrent.atomic.AtomicBoolean;

import lens24.ndk.RecognitionCore;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class RecognitionCoreUtils {

    private static AtomicBoolean deployRecognitionCoreActive = new AtomicBoolean();

    private RecognitionCoreUtils() {
    }

    public static boolean isRecognitionCoreDeployRequired(Context context) {
        //noinspection RedundantIfStatement
        if (!RecognitionAvailabilityChecker.isDeviceHasCamera(context)
                || RecognitionCore.isInitialized()) {
            return false;
        }
        return true;
    }

    public static void deployRecognitionCoreSync(Context context) {
        if (!isRecognitionCoreDeployRequired(context)) return;

        try {
            RecognitionCore.deploy(context);
        } catch (Throwable e) {
            // IGNORE
        }
    }

    public static void startDeployRecognitionCore(Context context) {
        if (!RecognitionCoreUtils.isRecognitionCoreDeployRequired(context)) return;
        if (deployRecognitionCoreActive.get()) return;
        final Context appContext = context.getApplicationContext();
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (deployRecognitionCoreActive.compareAndSet(false, true)) {
                    try {
                        RecognitionCore.deploy(appContext);
                    } catch (Throwable e) {
                        // IGNORE
                    }
                    deployRecognitionCoreActive.set(true);
                }
            }
        }.start();
    }

    public static boolean isScanCardSupported(Context context) {
        RecognitionAvailabilityChecker.Result checkResult = RecognitionAvailabilityChecker.doCheck(context);

        return checkResult.isPassed()
                || checkResult.isAdditionalCheckRequired()
                || checkResult.isFailedOnCameraPermission();
    }

}
