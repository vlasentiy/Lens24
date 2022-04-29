package lens24.utils;

import android.content.Context;

import java.util.concurrent.atomic.AtomicBoolean;

import lens24.camera.RecognitionAvailabilityChecker;
import lens24.camera.RecognitionCoreUtils;

public class Lens24Helper {

    private static final AtomicBoolean deployRecognitionCoreActive = new AtomicBoolean();

    public static boolean isScanCardSupported(Context context) {
        RecognitionAvailabilityChecker.Result checkResult = RecognitionAvailabilityChecker.doCheck(context);

        return checkResult.isPassed()
                || checkResult.isAdditionalCheckRequired()
                || checkResult.isFailedOnCameraPermission();
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
                    RecognitionCoreUtils.deployRecognitionCoreSync(appContext);
                    deployRecognitionCoreActive.set(true);
                }
            }
        }.start();
    }

}
