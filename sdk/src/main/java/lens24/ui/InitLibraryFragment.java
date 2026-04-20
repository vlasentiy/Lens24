package lens24.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.fragment.app.Fragment;
import lens24.camera.RecognitionAvailabilityChecker;
import lens24.camera.RecognitionCoreUtils;
import lens24.camera.RecognitionUnavailableException;
import lens24.camera.widget.CameraPreviewLayout;
import lens24.intent.ScanCardIntent;
import lens24.ndk.RecognitionCore;
import lens24.sdk.R;
import lens24.ui.views.ProgressBarIndeterminate;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class InitLibraryFragment extends Fragment {

    public static final String TAG = "InitLibraryFragment";

    private InteractionListener mListener;

    private static final int REQUEST_CAMERA_PERMISSION_CODE = 1;

    private ProgressBarIndeterminate mProgressBar;
    private CameraPreviewLayout mCameraPreviewLayout;
    private ViewGroup mMainContent;
    private Button bEnterManually;

    private DeployCoreTask mDeployCoreTask;

    private ScanCardRequest mScanCardRequest;

    public InitLibraryFragment() {
        this.mScanCardRequest = ScanCardRequest.getDefault();
    }

    public InitLibraryFragment(ScanCardRequest scanCardRequest) {
        this.mScanCardRequest = scanCardRequest;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (InteractionListener) getActivity();
        } catch (ClassCastException ex) {
            throw new RuntimeException("Parent must implement " + ScanCardFragment.InteractionListener.class.getSimpleName());
        }
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.lens24_fragment_scan_card, container, false);

        mMainContent = root.findViewById(R.id.lens24_main_content);
        mProgressBar = root.findViewById(R.id.lens24_progress_bar);
        mCameraPreviewLayout = root.findViewById(R.id.card_recognition_view);
        bEnterManually = root.findViewById(R.id.lens24_bManual);

        mProgressBar.setColor(mScanCardRequest.getMainColor());

        bEnterManually.setOnClickListener(v -> {
            if (mListener != null)
                mListener.onScanCardCanceled(ScanCardIntent.ADD_MANUALLY_PRESSED);
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showLoader(false);
        mMainContent.setVisibility(View.VISIBLE);
        mCameraPreviewLayout.setVisibility(View.VISIBLE);
        mCameraPreviewLayout.getSurfaceView().setVisibility(View.GONE);
        mCameraPreviewLayout.setBackgroundColor(Color.BLACK);
        mCameraPreviewLayout.setMainColor(mScanCardRequest.getMainColor());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecognitionAvailabilityChecker.Result checkResult = RecognitionAvailabilityChecker.doCheck(getContext());
        if (checkResult.isFailedOnCameraPermission()) {
            if (savedInstanceState == null) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_CODE);
            }
        } else {
            subscribeToInitCore(getActivity());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                subscribeToInitCore(getActivity());
            } else {
                if (mListener != null) mListener.onInitLibraryFailed(
                        new RecognitionUnavailableException(RecognitionUnavailableException.ERROR_NO_CAMERA_PERMISSION));
            }
        }
    }

    private void showLoader(boolean enable) {
        if (enable) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.hideSlow();
        }
    }

    private void subscribeToInitCore(Context context) {
        showLoader(true);
        if (mDeployCoreTask != null) mDeployCoreTask.cancel(false);
        mDeployCoreTask = new DeployCoreTask(this);
        mDeployCoreTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDeployCoreTask != null) {
            mDeployCoreTask.cancel(false);
            mDeployCoreTask = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mProgressBar = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface InteractionListener {
        void onScanCardCanceled(@ScanCardIntent.CancelReason int actionId);

        void onInitLibraryFailed(Throwable e);

        void onInitLibraryComplete();
    }

    private static class DeployCoreTask extends AsyncTask<Void, Void, Throwable> {

        private final WeakReference<InitLibraryFragment> fragmentRef;

        @SuppressLint("StaticFieldLeak")
        private final Context appContext;

        DeployCoreTask(InitLibraryFragment parent) {
            this.fragmentRef = new WeakReference<>(parent);
            this.appContext = parent.getContext().getApplicationContext();
        }

        @Override
        protected Throwable doInBackground(Void... voids) {
            try {
                RecognitionAvailabilityChecker.Result checkResult = RecognitionAvailabilityChecker.doCheck(appContext);
                if (checkResult.isFailed()) {
                    throw new RecognitionUnavailableException();
                }
                RecognitionCoreUtils.deployRecognitionCoreSync(appContext);
                if (!RecognitionCore.getInstance(appContext).isDeviceSupported()) {
                    throw new RecognitionUnavailableException();
                }
                return null;
            } catch (RecognitionUnavailableException e) {
                return e;
            }
        }

        @Override
        protected void onPostExecute(@Nullable Throwable lastError) {
            super.onPostExecute(lastError);
            InitLibraryFragment fragment = fragmentRef.get();
            if (fragment == null
                    || fragment.mProgressBar == null
                    || fragment.mListener == null) return;

            fragment.showLoader(false);
            if (lastError == null) {
                fragment.mListener.onInitLibraryComplete();
            } else {
                fragment.mListener.onInitLibraryFailed(lastError);
            }
        }
    }
}
