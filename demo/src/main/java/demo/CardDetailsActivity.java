package demo;

import com.google.android.material.textfield.TextInputLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Method;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import demo.validation.CardExpiryDateValidator;
import demo.validation.CardHolderValidator;
import demo.validation.CardNumberValidator;
import demo.validation.ValidationResult;
import demo.widget.CardNumberEditText;
import lens24.intent.Card;
import lens24.intent.ScanCardIntent;
import lens24.intent.ScanCardIntent.CancelReason;

public class CardDetailsActivity extends AppCompatActivity {

    private static final String TAG = "CardDetailsActivity";

    private static final int REQUEST_CODE_SCAN_CARD = 1;

    private Toolbar mToolbar;

    private TextInputLayout mCardNumberField;

    private TextInputLayout mCardholderField;

    private TextInputLayout mExpiryField;

    private CardNumberValidator mCardNumberValidator;
    private CardHolderValidator mCardHolderValidator;
    private CardExpiryDateValidator mExpiryDateValidator;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_card_details);

        mToolbar = findViewById(R.id.toolbar);
        mCardNumberField = findViewById(R.id.card_number_field);
        mCardholderField = findViewById(R.id.cardholder_field);
        mExpiryField = findViewById(R.id.expiry_date_field);
        setupToolbar();

        findViewById(R.id.scan_button).setOnClickListener(view -> scanCard());

        if (savedInstanceState == null) {
            scanCard();
        }
    }


    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.findViewById(R.id.button_next).setOnClickListener(view -> {
            Card card = readForm();
            ValidationResult validationResult = validateForm(card);
            setValidationResult(validationResult);
            if (validationResult.isValid()) {
                Toast.makeText(CardDetailsActivity.this, "That's All folks!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN_CARD) {
            if (resultCode == Activity.RESULT_OK) {
                Card card = data.getParcelableExtra(ScanCardIntent.RESULT_CARD_DATA);
                byte[] cardImage = data.getByteArrayExtra(ScanCardIntent.RESULT_CARD_IMAGE);
                Bitmap bitmap = BitmapFactory.decodeByteArray(cardImage, 0, cardImage.length);
                if (BuildConfig.DEBUG) Log.i(TAG, "Card info: " + card);
                setCard(card);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                @CancelReason final int reason;
                if (data != null) {
                    reason = data.getIntExtra(ScanCardIntent.RESULT_CANCEL_REASON, ScanCardIntent.BACK_PRESSED);
                } else {
                    reason = ScanCardIntent.BACK_PRESSED;
                }

                if (reason == ScanCardIntent.ADD_MANUALLY_PRESSED) {
                    showIme(mCardNumberField.getEditText());
                }
            } else if (resultCode == ScanCardIntent.RESULT_CODE_ERROR) {
                Log.i(TAG, "Scan failed");
            }
        }
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

    private void setCard(@NonNull Card card) {
        mCardNumberField.getEditText().setText(card.getCardNumber());
        mCardholderField.getEditText().setText(card.getCardHolderName());
        mExpiryField.getEditText().setText(card.getExpirationDate());
        setValidationResult(ValidationResult.empty());
    }

    private void scanCard() {
        Intent intent = new ScanCardIntent.Builder(this)
                .setHint(getString(R.string.hint_position_card_in_frame))
                .setToolbarTitle("Scan card")
                .setSaveCard(true)
                .setManualInputButtonText("Manual input")
                .setLottieJsonAnimation("{\"v\":\"4.8.0\",\"meta\":{\"g\":\"LottieFiles AE 1.0.0\",\"a\":\"\",\"k\":\"\",\"d\":\"\",\"tc\":\"\"},\"fr\":30,\"ip\":0,\"op\":60,\"w\":128,\"h\":128,\"nm\":\"Camera loader\",\"ddd\":0,\"assets\":[],\"layers\":[{\"ddd\":0,\"ind\":1,\"ty\":4,\"nm\":\"1st anima\",\"sr\":1,\"ks\":{\"o\":{\"a\":0,\"k\":100,\"ix\":11},\"r\":{\"a\":0,\"k\":0,\"ix\":10},\"p\":{\"a\":0,\"k\":[60.25,58.137,0],\"ix\":2},\"a\":{\"a\":0,\"k\":[39,45,0],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100,100],\"ix\":6}},\"ao\":0,\"shapes\":[{\"ty\":\"gr\",\"it\":[{\"ty\":\"gr\",\"it\":[{\"ind\":0,\"ty\":\"sh\",\"ix\":1,\"ks\":{\"a\":0,\"k\":{\"i\":[[4.418,0],[0,0],[0,4.419],[0,0],[-4.938,0.101],[0,0],[0,0],[0,0],[0,0],[0,0],[0,-4.418],[0,0]],\"o\":[[0,0],[-4.418,0],[0,0],[0,-4.418],[0,0],[0,0],[0,0],[0,0],[0,0],[4.418,0],[0,0],[0,4.419]],\"v\":[[35.75,24.017],[-28.312,23.993],[-36.312,16.012],[-36.312,-32.425],[-27.438,-40.238],[-15.969,-40.25],[-8.562,-48.031],[16.013,-48.1],[23.486,-40.107],[35.75,-40.151],[43.75,-32.339],[43.75,16.036]],\"c\":true},\"ix\":2},\"nm\":\"Path 1\",\"mn\":\"ADBE Vector Shape - Group\",\"hd\":false},{\"ty\":\"tm\",\"s\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.16],\"y\":[0.819]},\"o\":{\"x\":[0.132],\"y\":[0]},\"t\":0,\"s\":[30]},{\"i\":{\"x\":[0.16],\"y\":[-0.405]},\"o\":{\"x\":[0.333],\"y\":[0.263]},\"t\":25.246,\"s\":[76.9]},{\"i\":{\"x\":[0.16],\"y\":[-0.407]},\"o\":{\"x\":[0.333],\"y\":[0.263]},\"t\":26.497,\"s\":[77.2]},{\"i\":{\"x\":[0.16],\"y\":[-1.108]},\"o\":{\"x\":[0.333],\"y\":[0.395]},\"t\":27.749,\"s\":[77.5]},{\"i\":{\"x\":[0.667],\"y\":[0.236]},\"o\":{\"x\":[0.333],\"y\":[-0.071]},\"t\":29,\"s\":[77.7]},{\"i\":{\"x\":[0.668],\"y\":[1]},\"o\":{\"x\":[0.334],\"y\":[0.454]},\"t\":45,\"s\":[63.497]},{\"t\":60,\"s\":[41]}],\"ix\":1},\"e\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.668],\"y\":[1.49]},\"o\":{\"x\":[0.446],\"y\":[0]},\"t\":0,\"s\":[27]},{\"i\":{\"x\":[0.726],\"y\":[4.405]},\"o\":{\"x\":[0.729],\"y\":[-3.919]},\"t\":25.246,\"s\":[21]},{\"i\":{\"x\":[0.749],\"y\":[1]},\"o\":{\"x\":[0.407],\"y\":[0.271]},\"t\":45,\"s\":[19.711]},{\"t\":60,\"s\":[38]}],\"ix\":2},\"o\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.26],\"y\":[0.741]},\"o\":{\"x\":[0.161],\"y\":[0.079]},\"t\":0,\"s\":[-92.951]},{\"i\":{\"x\":[0.693],\"y\":[0.223]},\"o\":{\"x\":[0.131],\"y\":[0.036]},\"t\":23.201,\"s\":[-7]},{\"i\":{\"x\":[0.559],\"y\":[0.697]},\"o\":{\"x\":[0.222],\"y\":[0.31]},\"t\":45,\"s\":[96.665]},{\"t\":60,\"s\":[225.666]}],\"ix\":3},\"m\":1,\"ix\":2,\"nm\":\"Trim Paths 1\",\"mn\":\"ADBE Vector Filter - Trim\",\"hd\":false},{\"ty\":\"st\",\"c\":{\"a\":0,\"k\":[0.635294139385,0.827450990677,0.376470595598,1],\"ix\":3},\"o\":{\"a\":0,\"k\":100,\"ix\":4},\"w\":{\"a\":0,\"k\":6.2,\"ix\":5},\"lc\":2,\"lj\":2,\"bm\":0,\"nm\":\"Stroke 1\",\"mn\":\"ADBE Vector Graphic - Stroke\",\"hd\":false},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[39,63],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":0,\"k\":100,\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"Transform\"}],\"nm\":\"Group 4\",\"np\":3,\"cix\":2,\"bm\":0,\"ix\":2,\"mn\":\"ADBE Vector Group\",\"hd\":false},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[39,45],\"ix\":2},\"a\":{\"a\":0,\"k\":[39,45],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":0,\"k\":100,\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"Transform\"}],\"nm\":\"Group 1\",\"np\":2,\"cix\":2,\"bm\":0,\"ix\":1,\"mn\":\"ADBE Vector Group\",\"hd\":false}],\"ip\":0,\"op\":91,\"st\":0,\"bm\":0},{\"ddd\":0,\"ind\":2,\"ty\":4,\"nm\":\"Loaders/Loader_Camera\",\"sr\":1,\"ks\":{\"o\":{\"a\":0,\"k\":100,\"ix\":11},\"r\":{\"a\":0,\"k\":0,\"ix\":10},\"p\":{\"a\":0,\"k\":[64,64,0],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0,0],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100,100],\"ix\":6}},\"ao\":0,\"shapes\":[{\"ty\":\"gr\",\"it\":[{\"ind\":0,\"ty\":\"sh\",\"ix\":1,\"ks\":{\"a\":0,\"k\":{\"i\":[[0,0],[0,-11.046],[-11.046,0],[0,11.046],[11.046,0]],\"o\":[[-11.046,0],[0,11.046],[11.046,0],[0,-11.046],[0,0]],\"v\":[[0,-16],[-20,4],[0,24],[20,4],[0,-16]],\"c\":true},\"ix\":2},\"nm\":\"Path 1\",\"mn\":\"ADBE Vector Shape - Group\",\"hd\":false},{\"ty\":\"st\",\"c\":{\"a\":0,\"k\":[0.635294117647,0.827450980392,0.376470588235,1],\"ix\":3},\"o\":{\"a\":0,\"k\":100,\"ix\":4},\"w\":{\"a\":0,\"k\":6,\"ix\":5},\"lc\":1,\"lj\":1,\"ml\":4,\"bm\":0,\"nm\":\"Stroke 1\",\"mn\":\"ADBE Vector Graphic - Stroke\",\"hd\":false},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[0,0],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":0,\"k\":100,\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"Transform\"}],\"nm\":\"Path\",\"np\":2,\"cix\":2,\"bm\":0,\"ix\":1,\"mn\":\"ADBE Vector Group\",\"hd\":false},{\"ty\":\"gr\",\"it\":[{\"ind\":0,\"ty\":\"sh\",\"ix\":1,\"ks\":{\"a\":0,\"k\":{\"i\":[[0,0],[0,0],[0,0],[0,0],[0,0],[0,0],[0,-4.418],[0,0],[4.418,0],[0,0],[0,4.418],[0,0],[-4.418,0]],\"o\":[[0,0],[0,0],[0,0],[0,0],[0,0],[4.418,0],[0,0],[0,4.418],[0,0],[-4.418,0],[0,0],[0,-4.418],[0,0]],\"v\":[[-32,-28],[-20,-28],[-12,-36],[12,-36],[20,-28],[32,-28],[40,-20],[40,28],[32,36],[-32,36],[-40,28],[-40,-20],[-32,-28]],\"c\":true},\"ix\":2},\"nm\":\"Path 1\",\"mn\":\"ADBE Vector Shape - Group\",\"hd\":false},{\"ty\":\"st\",\"c\":{\"a\":0,\"k\":[1,1,1,1],\"ix\":3},\"o\":{\"a\":0,\"k\":100,\"ix\":4},\"w\":{\"a\":0,\"k\":6,\"ix\":5},\"lc\":1,\"lj\":1,\"ml\":1,\"bm\":0,\"nm\":\"Stroke 1\",\"mn\":\"ADBE Vector Graphic - Stroke\",\"hd\":false},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[0,0],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":0,\"k\":100,\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"Transform\"}],\"nm\":\"Shape\",\"np\":2,\"cix\":2,\"bm\":0,\"ix\":2,\"mn\":\"ADBE Vector Group\",\"hd\":false}],\"ip\":0,\"op\":150,\"st\":0,\"bm\":0}],\"markers\":[]}")
                .build();
        startActivityForResult(intent, REQUEST_CODE_SCAN_CARD);
    }

    private static void showIme(@Nullable View view) {
        if (view == null) return;
        if (view instanceof EditText) view.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService
                (Context.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        try {
            Method showSoftInputUnchecked = InputMethodManager.class.getMethod(
                    "showSoftInputUnchecked", int.class, ResultReceiver.class);
            showSoftInputUnchecked.setAccessible(true);
            showSoftInputUnchecked.invoke(imm, 0, null);
        } catch (Exception e) {
            // ho hum
            imm.showSoftInput(view, 0);
        }
    }

}
