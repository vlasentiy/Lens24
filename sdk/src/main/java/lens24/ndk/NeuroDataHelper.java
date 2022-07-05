package lens24.ndk;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lens24.sdk.BuildConfig;
import lens24.utils.Constants;

final class NeuroDataHelper {

    private static final boolean DBG = BuildConfig.DEBUG;
    private static final String TAG = "RecognitionCore";

    private final File mDataBasePath;

    private final AssetManager mAssetManager;

    public NeuroDataHelper(Context context) {
        Context appContext = context.getApplicationContext();
        mAssetManager = appContext.getAssets();
        mDataBasePath = new File(context.getCacheDir(), Constants.MODEL_DIR + "/" + Constants.NEURO_DATA_VERSION);
    }

    public void unpackAssets() throws IOException {
        unpackFileOrDir("");
    }

    private void unpackFileOrDir(String assetsPath) throws IOException {
        String[] assets;
        assets = mAssetManager.list(Constants.MODEL_DIR + assetsPath);
        if (assets.length == 0) {
            copyAssetToCacheDir(assetsPath);
        } else {
            File dir = getDstPath(assetsPath);
            if (!dir.exists()) {
                if (DBG) Log.v(TAG, "Create cache dir " + dir.getAbsolutePath());
                dir.mkdirs();
            }
            for (String asset : assets) {
                unpackFileOrDir(assetsPath + "/" + asset);
            }
        }
    }

    private void copyAssetToCacheDir(final String assetsPath) throws IOException {
        File f = getDstPath(assetsPath);

        OutputStream os = null;
        try (InputStream is = mAssetManager.open(Constants.MODEL_DIR + assetsPath)) {
            int fileSize = is.available();
            if (f.length() != fileSize) {
                if (DBG) Log.d(TAG, "copyAssetToCacheDir() rewrite file " + assetsPath);
                os = new FileOutputStream(f, false);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) > 0) {
                    os.write(buffer, 0, len);
                }
            }
        } finally {
            // IGNORE
            try {
                if (os != null) {
                    os.flush();
                    os.close();
                }
            } catch (IOException ioe) {
                // IGNORE
            }
        }
    }

    public File getDataBasePath() {
        return mDataBasePath;
    }

    private File getDstPath(String assetsPath) {
        return new File(mDataBasePath, assetsPath);
    }

}
