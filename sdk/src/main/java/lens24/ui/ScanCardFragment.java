package lens24.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import lens24.camera.ScanManager;
import lens24.camera.widget.CameraPreviewLayout;
import lens24.intent.Card;
import lens24.intent.ScanCardIntent;
import lens24.ndk.RecognitionResult;
import lens24.sdk.R;
import lens24.utils.Constants;

import static lens24.ndk.RecognitionConstants.RECOGNIZER_MODE_DATE;
import static lens24.ndk.RecognitionConstants.RECOGNIZER_MODE_GRAB_CARD_IMAGE;
import static lens24.ndk.RecognitionConstants.RECOGNIZER_MODE_NAME;
import static lens24.ndk.RecognitionConstants.RECOGNIZER_MODE_NUMBER;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class ScanCardFragment extends Fragment {
    @SuppressWarnings("unused")
    public static final String TAG = "ScanCardFragment";

    private CameraPreviewLayout mCameraPreviewLayout;

    private Button bManual;

    private ViewGroup mMainContent;

    @Nullable
    private Menu toolbarMenu;

    boolean isFlashSupported = false;

    @Nullable
    private ScanManager mScanManager;

    private InteractionListener mListener;

    private ScanCardRequest mRequest;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (InteractionListener) getActivity();
        } catch (ClassCastException ex) {
            throw new RuntimeException("Parent must implement " + InteractionListener.class.getSimpleName());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequest = null;
        if (getArguments() != null) {
            mRequest = getArguments().getParcelable(ScanCardIntent.KEY_SCAN_CARD_REQUEST);
        }
        if (mRequest == null) mRequest = ScanCardRequest.getDefault();
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (Constants.DEBUG)
            Log.d(TAG, "onCreateAnimation() called with: " + "transit = [" + transit + "], enter = [" + enter + "], nextAnim = [" + nextAnim + "]");
        // SurfaceView is hard to animate
        Animation a = new Animation() {
        };
        a.setDuration(0);
        return a;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.lens24_fragment_scan_card, container, false);

        mCameraPreviewLayout = root.findViewById(R.id.card_recognition_view);
        mMainContent = root.findViewById(R.id.lens24_main_content);

        initView(root);
        showMainContent();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int recognitionMode = RECOGNIZER_MODE_NUMBER;
        if (mRequest.isScanCardHolderEnabled()) recognitionMode |= RECOGNIZER_MODE_NAME;
        if (mRequest.isScanExpirationDateEnabled()) recognitionMode |= RECOGNIZER_MODE_DATE;
        if (mRequest.isGrabCardImageEnabled()) recognitionMode |= RECOGNIZER_MODE_GRAB_CARD_IMAGE;
        mScanManager = new ScanManager(recognitionMode, getActivity(), mCameraPreviewLayout, new ScanManager.Callbacks() {

            private byte[] mLastCardImage = null;

            @Override
            public void onCameraOpened(Camera.Parameters cameraParameters) {
                isFlashSupported = (cameraParameters.getSupportedFlashModes() != null
                        && !cameraParameters.getSupportedFlashModes().isEmpty());
                if (getView() == null) return;
                mCameraPreviewLayout.setBackgroundDrawable(null);
                setHasOptionsMenu(isFlashSupported);
            }

            @Override
            public void onOpenCameraError(Exception exception) {
                hideMainContent();
                finishWithError(exception);
            }

            @Override
            public void onRecognitionComplete(RecognitionResult result) {
                if (result.isFirst()) {
                    if (mScanManager != null) mScanManager.freezeCameraPreview();
                    if (mRequest.isVibrationEnabled()) vibrate();
                }
                if (result.isFinal()) {
                    String date;
                    if (TextUtils.isEmpty(result.getDate())) {
                        date = null;
                    } else {
                        assert result.getDate() != null;
                        date = result.getDate().substring(0, 2) + '/' + result.getDate().substring(2);
                    }

                    Card card = new Card(result.getNumber(), result.getName(), date);
                    byte[] cardImage = mLastCardImage;
                    mLastCardImage = null;
                    finishWithResult(card, cardImage);
                }
            }

            @Override
            public void onCardImageReceived(Bitmap cardImage) {
                mLastCardImage = compressCardImage(cardImage);
            }

            @Override
            public void onFpsReport(String report) {
            }

            @Override
            public void onAutoFocusMoving(boolean start, String cameraFocusMode) {
            }

            @Override
            public void onAutoFocusComplete(boolean success, String cameraFocusMode) {
            }

            @Override
            public void onTorchStatusChanged(boolean turnTorchOn) {
                MenuItem menuItem = toolbarMenu != null ? toolbarMenu.findItem(R.id.flash) : null;
                if (getContext() != null && menuItem != null) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            menuItem.setIcon(ContextCompat.getDrawable(getContext(), turnTorchOn ? R.drawable.lens24_ic_flash_on : R.drawable.lens24_ic_flash_off)));
                }
            }

            @Nullable
            private byte[] compressCardImage(Bitmap img) {
                byte[] result;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                if (img.compress(Bitmap.CompressFormat.JPEG, 80, stream)) {
                    result = stream.toByteArray();
                } else {
                    result = null;
                }
                return result;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mScanManager != null) mScanManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mScanManager != null) mScanManager.onPause();
    }

    private void initView(View view) {
        bManual = view.findViewById(R.id.lens24_bManual);
        bManual.setOnClickListener(v -> {
            if (v.isEnabled()) {
                v.setEnabled(false);
                if (mListener != null) {
                    hide(bManual);
                    mListener.onScanCardCanceled(ScanCardIntent.ADD_MANUALLY_PRESSED);
                }
            }
        });
        bManual.setTextColor(mRequest.getMainColor());
        bManual.setVisibility(mRequest.getManualInputButtonLabel() == null ? View.GONE : View.VISIBLE);
        bManual.setText(mRequest.getManualInputButtonLabel());
        bManual.setEnabled(true);
        TextView mHint = view.findViewById(R.id.tvHint);
        mHint.setText(mRequest.getHint());
        mHint.setVisibility(mRequest.getHint() != null ? View.VISIBLE : View.GONE);
        TextView mBottomHint = view.findViewById(R.id.tvBottomHint);
        mBottomHint.setText(mRequest.getBottomHint());
        mBottomHint.setVisibility(mRequest.getBottomHint() != null ? View.VISIBLE : View.GONE);

        initToolbar(view);
    }

    private void initToolbar(View view) {
        Toolbar mToolbar = view.findViewById(R.id.lens24_toolbar);
        mToolbar.setTitle(mRequest.getTitle() == null ? "" : mRequest.getTitle());
        AppCompatActivity appCompatActivity = ((AppCompatActivity) getActivity());
        appCompatActivity.setSupportActionBar(mToolbar);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appCompatActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationIcon(R.drawable.lens24_ic_close);
        mToolbar.setNavigationOnClickListener(v -> {
            if (mListener != null)
                mListener.onScanCardCanceled(ScanCardIntent.BACK_PRESSED);
            getActivity().onBackPressed();
        });

        mToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.flash) {
                if (mScanManager != null) mScanManager.toggleFlash();
            }

            return false;
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.lens24_menu, menu);
        toolbarMenu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void showMainContent() {
        mMainContent.setVisibility(View.VISIBLE);
        mCameraPreviewLayout.setVisibility(View.VISIBLE);
    }

    private void hideMainContent() {
        mMainContent.setVisibility(View.INVISIBLE);
        mCameraPreviewLayout.setVisibility(View.INVISIBLE);
    }

    private void finishWithError(Exception exception) {
        if (mListener != null) mListener.onScanCardFailed(exception);
    }

    private void finishWithResult(Card card, @Nullable byte[] cardImage) {
        if (mListener != null) mListener.onScanCardFinished(card, cardImage);
    }

    private void vibrate() {
        Context context = getContext();
        if (context == null) return;
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(200);
        }
    }

    private void hide(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Transition transition = new Fade();
            transition.setDuration(900);
            transition.addTarget(view);
            TransitionManager.beginDelayedTransition((ViewGroup) view.getParent(), transition);
            view.setVisibility(View.GONE);
        } else {
            view
                    .animate()
                    .alpha(0f)
                    .withEndAction(() -> view.setVisibility(View.GONE))
                    .setDuration(900)
                    .start();
        }
    }

    public interface InteractionListener {
        void onScanCardCanceled(@ScanCardIntent.CancelReason int cancelReason);

        void onScanCardFailed(Exception e);

        void onScanCardFinished(Card card, byte[] cardImage);
    }
}
