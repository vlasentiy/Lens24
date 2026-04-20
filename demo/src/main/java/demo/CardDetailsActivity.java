package demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.textfield.TextInputLayout;
import demo.validation.CardExpiryDateValidator;
import demo.validation.CardHolderValidator;
import demo.validation.CardNumberValidator;
import demo.validation.ValidationResult;
import demo.widget.CardNumberEditText;
import lens24.intent.Card;
import lens24.intent.ScanCardCallback;
import lens24.intent.ScanCardIntent;
import lens24.sdk.BuildConfig;

public class CardDetailsActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private TextInputLayout mCardNumberField;

    private TextInputLayout mCardholderField;

    private TextInputLayout mExpiryField;

    private CardNumberValidator mCardNumberValidator;
    private CardHolderValidator mCardHolderValidator;
    private CardExpiryDateValidator mExpiryDateValidator;

    ActivityResultCallback<ActivityResult> activityResultCallback = new ScanCardCallback.Builder()
            .setOnSuccess(this::setCard)
            .setOnBackPressed(() -> {/*Your code here*/})
            .setOnManualInput(() -> {/*Your code here*/})
            .setOnError(() -> {/*Your code here*/})
            .build();
    ActivityResultLauncher<Intent> startActivityIntent =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    activityResultCallback);

    private void setCard(@NonNull Card card, @Nullable Bitmap bitmap) {
        mCardNumberField.getEditText().setText(card.getCardNumber());
        mCardholderField.getEditText().setText(card.getCardHolderName());
        mExpiryField.getEditText().setText(card.getExpirationDate());
        setValidationResult(ValidationResult.empty());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!BuildConfig.DEBUG) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_card_details);

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mToolbar = findViewById(R.id.lens24_toolbar);
        mCardNumberField = findViewById(R.id.card_number_field);
        mCardholderField = findViewById(R.id.cardholder_field);
        mExpiryField = findViewById(R.id.expiry_date_field);
        setupToolbar();

        findViewById(R.id.scan_button).setOnClickListener(view -> scanCard());

        if (savedInstanceState == null) {
            scanCard();
        }
    }

    private void scanCard() {
        Intent intent = new ScanCardIntent.Builder(this)
                .setHint(getString(R.string.lens24_hint_position_card_in_frame))
                .setToolbarTitle("Scan card")
                .setSaveCard(true)
                .setVibrationEnabled(true)
                .setManualInputButtonText("Manual input")
                .setBottomHint("and wait for a moment")
                .setMainColor(R.color.lens24_primary_color)
                .build();

        startActivityIntent.launch(intent);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        mToolbar.findViewById(R.id.button_next).setOnClickListener(view -> {
            Card card = readForm();
            ValidationResult validationResult = validateForm(card);
            setValidationResult(validationResult);
            if (validationResult.isValid()) {
                Toast.makeText(CardDetailsActivity.this, "That's All folks!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Card readForm() {
        String cardNumber = ((CardNumberEditText) mCardNumberField.getEditText()).getCardNumber();
        String holder = mCardholderField.getEditText().getText().toString();
        String expiryDate = mExpiryField.getEditText().getText().toString();
        return new Card(cardNumber, holder, expiryDate);
    }

    private ValidationResult validateForm(Card card) {
        if (mCardNumberValidator == null) {
            mCardNumberValidator = new CardNumberValidator();
            mExpiryDateValidator = new CardExpiryDateValidator();
            mCardHolderValidator = new CardHolderValidator();
        }


        ValidationResult results = new ValidationResult(3);
        results.put(R.id.card_number_field, mCardNumberValidator.validate(card.getCardNumber()));
        results.put(R.id.cardholder_field, mCardHolderValidator.validate(card.getCardHolderName()));
        results.put(R.id.expiry_date_field, mExpiryDateValidator.validate(card.getExpirationDate()));
        return results;
    }

    private void setValidationResult(ValidationResult result) {
        mCardNumberField.setError(result.getMessage(R.id.card_number_field, getResources()));
        mCardholderField.setError(result.getMessage(R.id.cardholder_field, getResources()));
        mExpiryField.setError(result.getMessage(R.id.expiry_date_field, getResources()));
    }
}
