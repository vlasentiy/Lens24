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
                .setLottieJsonAnimation("{\"v\":\"5.1.8\",\"fr\":25,\"ip\":0,\"op\":50,\"w\":240,\"h\":240,\"nm\":\"合成 1\",\"ddd\":0,\"assets\":[],\"layers\":[{\"ddd\":0,\"ind\":1,\"ty\":4,\"nm\":\"3\",\"parent\":4,\"sr\":1,\"ks\":{\"o\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833],\"y\":[0.833]},\"o\":{\"x\":[0.167],\"y\":[0.167]},\"n\":[\"0p833_0p833_0p167_0p167\"],\"t\":32,\"s\":[100],\"e\":[0]},{\"i\":{\"x\":[0.833],\"y\":[0.833]},\"o\":{\"x\":[0.167],\"y\":[0.167]},\"n\":[\"0p833_0p833_0p167_0p167\"],\"t\":33,\"s\":[0],\"e\":[100]},{\"t\":34}],\"ix\":11},\"r\":{\"a\":0,\"k\":0,\"ix\":10},\"p\":{\"a\":1,\"k\":[{\"i\":{\"x\":0.833,\"y\":0.833},\"o\":{\"x\":0.167,\"y\":0.167},\"n\":\"0p833_0p833_0p167_0p167\",\"t\":29,\"s\":[40,40,0],\"e\":[40,43.75,0],\"to\":[0,0.625,0],\"ti\":[0,0,0]},{\"i\":{\"x\":0.833,\"y\":0.833},\"o\":{\"x\":0.167,\"y\":0.167},\"n\":\"0p833_0p833_0p167_0p167\",\"t\":32,\"s\":[40,43.75,0],\"e\":[40,40,0],\"to\":[0,0,0],\"ti\":[0,0.625,0]},{\"t\":35}],\"ix\":2},\"a\":{\"a\":0,\"k\":[40,40,0],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100,100],\"ix\":6}},\"ao\":0,\"shapes\":[{\"ty\":\"gr\",\"it\":[{\"ind\":0,\"ty\":\"sh\",\"ix\":1,\"ks\":{\"a\":0,\"k\":{\"i\":[[0,0],[0,0]],\"o\":[[0,0],[0,0]],\"v\":[[14,12.5],[21,12.5]],\"c\":false},\"ix\":2},\"nm\":\"路径 1\",\"mn\":\"ADBE Vector Shape - Group\",\"hd\":false},{\"ty\":\"st\",\"c\":{\"a\":0,\"k\":[0.5529411764705883,0.7764705882352941,0.2549019607843137,1],\"ix\":3},\"o\":{\"a\":0,\"k\":100,\"ix\":4},\"w\":{\"a\":0,\"k\":5,\"ix\":5},\"lc\":2,\"lj\":1,\"ml\":10,\"nm\":\"描边 1\",\"mn\":\"ADBE Vector Graphic - Stroke\",\"hd\":false},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[0,0],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":0,\"k\":100,\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"变换\"}],\"nm\":\"组 8\",\"np\":2,\"cix\":2,\"ix\":1,\"mn\":\"ADBE Vector Group\",\"hd\":false}],\"ip\":0,\"op\":750,\"st\":0,\"bm\":0},{\"ddd\":0,\"ind\":2,\"ty\":4,\"nm\":\"形状图层 1\",\"parent\":4,\"td\":1,\"sr\":1,\"ks\":{\"o\":{\"a\":0,\"k\":100,\"ix\":11},\"r\":{\"a\":0,\"k\":0,\"ix\":10},\"p\":{\"a\":0,\"k\":[41.873,46.334,0],\"ix\":2},\"a\":{\"a\":0,\"k\":[0.125,-173.25,0],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100,100],\"ix\":6}},\"ao\":0,\"shapes\":[{\"ty\":\"gr\",\"it\":[{\"ty\":\"rc\",\"d\":1,\"s\":{\"a\":0,\"k\":[47.169,30.875],\"ix\":2},\"p\":{\"a\":0,\"k\":[0,0],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":4},\"nm\":\"矩形路径 1\",\"mn\":\"ADBE Vector Shape - Rect\",\"hd\":false},{\"ty\":\"st\",\"c\":{\"a\":0,\"k\":[1,1,1,1],\"ix\":3},\"o\":{\"a\":0,\"k\":100,\"ix\":4},\"w\":{\"a\":0,\"k\":0,\"ix\":5},\"lc\":1,\"lj\":1,\"ml\":4,\"nm\":\"描边 1\",\"mn\":\"ADBE Vector Graphic - Stroke\",\"hd\":false},{\"ty\":\"fl\",\"c\":{\"a\":0,\"k\":[1,1,1,1],\"ix\":4},\"o\":{\"a\":0,\"k\":100,\"ix\":5},\"r\":1,\"nm\":\"填充 1\",\"mn\":\"ADBE Vector Graphic - Fill\",\"hd\":false},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[-0.04,-173.688],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":0,\"k\":100,\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"变换\"}],\"nm\":\"矩形 1\",\"np\":3,\"cix\":2,\"ix\":1,\"mn\":\"ADBE Vector Group\",\"hd\":false}],\"ip\":0,\"op\":750,\"st\":0,\"bm\":0},{\"ddd\":0,\"ind\":3,\"ty\":4,\"nm\":\"2\",\"parent\":4,\"tt\":1,\"sr\":1,\"ks\":{\"o\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833],\"y\":[0.833]},\"o\":{\"x\":[0.167],\"y\":[0.167]},\"n\":[\"0p833_0p833_0p167_0p167\"],\"t\":32,\"s\":[100],\"e\":[0]},{\"i\":{\"x\":[0.833],\"y\":[0.833]},\"o\":{\"x\":[0.167],\"y\":[0.167]},\"n\":[\"0p833_0p833_0p167_0p167\"],\"t\":33,\"s\":[0],\"e\":[100]},{\"t\":34}],\"ix\":11},\"r\":{\"a\":0,\"k\":0,\"ix\":10},\"p\":{\"a\":1,\"k\":[{\"i\":{\"x\":0.833,\"y\":0.833},\"o\":{\"x\":0.167,\"y\":0.167},\"n\":\"0p833_0p833_0p167_0p167\",\"t\":10,\"s\":[40,68,0],\"e\":[38.893,35.94,0],\"to\":[-0.18453942239285,-5.34331512451172,0],\"ti\":[0.12675994634628,4.11141729354858,0]},{\"i\":{\"x\":0.833,\"y\":0.833},\"o\":{\"x\":0.167,\"y\":0.167},\"n\":\"0p833_0p833_0p167_0p167\",\"t\":14,\"s\":[38.893,35.94,0],\"e\":[39.239,43.331,0],\"to\":[-0.12675994634628,-4.11141729354858,0],\"ti\":[-0.03640662878752,-0.56304621696472,0]},{\"i\":{\"x\":0.833,\"y\":0.833},\"o\":{\"x\":0.167,\"y\":0.167},\"n\":\"0p833_0p833_0p167_0p167\",\"t\":18,\"s\":[39.239,43.331,0],\"e\":[39.111,39.318,0],\"to\":[0.03640662878752,0.56304621696472,0],\"ti\":[0.02137284353375,0.46051827073097,0]},{\"i\":{\"x\":0.833,\"y\":0.833},\"o\":{\"x\":0.167,\"y\":0.167},\"n\":\"0p833_0p833_0p167_0p167\",\"t\":23,\"s\":[39.111,39.318,0],\"e\":[39.111,40.568,0],\"to\":[-0.02137284353375,-0.46051827073097,0],\"ti\":[0,-0.20833332836628,0]},{\"t\":26}],\"ix\":2},\"a\":{\"a\":0,\"k\":[40,40,0],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100,100],\"ix\":6}},\"ao\":0,\"shapes\":[{\"ty\":\"gr\",\"it\":[{\"ind\":0,\"ty\":\"sh\",\"ix\":1,\"ks\":{\"a\":0,\"k\":{\"i\":[[6.075,0],[0,-6.075],[-6.075,0],[0,6.075]],\"o\":[[-6.075,0],[0,6.075],[6.075,0],[0,-6.075]],\"v\":[[0,-11],[-11,0],[0,11],[11,0]],\"c\":true},\"ix\":2},\"nm\":\"路径 1\",\"mn\":\"ADBE Vector Shape - Group\",\"hd\":false},{\"ind\":1,\"ty\":\"sh\",\"ix\":2,\"ks\":{\"a\":0,\"k\":{\"i\":[[-3.309,0],[0,-3.309],[3.309,0],[0,3.309]],\"o\":[[3.309,0],[0,3.309],[-3.309,0],[0,-3.309]],\"v\":[[0,-6],[6,0],[0,6],[-6,0]],\"c\":true},\"ix\":2},\"nm\":\"路径 2\",\"mn\":\"ADBE Vector Shape - Group\",\"hd\":false},{\"ty\":\"mm\",\"mm\":1,\"nm\":\"合并路径 1\",\"mn\":\"ADBE Vector Filter - Merge\",\"hd\":false},{\"ty\":\"fl\",\"c\":{\"a\":0,\"k\":[0.5529411764705883,0.7764705882352941,0.2549019607843137,1],\"ix\":4},\"o\":{\"a\":0,\"k\":100,\"ix\":5},\"r\":1,\"nm\":\"填充 1\",\"mn\":\"ADBE Vector Graphic - Fill\",\"hd\":false},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[39,47],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":0,\"k\":100,\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"变换\"}],\"nm\":\"组 1\",\"np\":4,\"cix\":2,\"ix\":1,\"mn\":\"ADBE Vector Group\",\"hd\":false},{\"ty\":\"gr\",\"it\":[{\"ind\":0,\"ty\":\"sh\",\"ix\":1,\"ks\":{\"a\":0,\"k\":{\"i\":[[4.687,0],[0,4.687],[-4.687,0],[0,-4.687]],\"o\":[[-4.687,0],[0,-4.687],[4.687,0],[0,4.687]],\"v\":[[0,8.5],[-8.5,0],[0,-8.5],[8.5,0]],\"c\":true},\"ix\":2},\"nm\":\"路径 1\",\"mn\":\"ADBE Vector Shape - Group\",\"hd\":false},{\"ty\":\"fl\",\"c\":{\"a\":0,\"k\":[0.4392156862745098,0.6196078431372549,0.20392156862745098,1],\"ix\":4},\"o\":{\"a\":0,\"k\":100,\"ix\":5},\"r\":1,\"nm\":\"填充 1\",\"mn\":\"ADBE Vector Graphic - Fill\",\"hd\":false},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[39,47],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":0,\"k\":100,\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"变换\"}],\"nm\":\"组 2\",\"np\":2,\"cix\":2,\"ix\":2,\"mn\":\"ADBE Vector Group\",\"hd\":false}],\"ip\":0,\"op\":750,\"st\":0,\"bm\":0},{\"ddd\":0,\"ind\":4,\"ty\":4,\"nm\":\"1\",\"sr\":1,\"ks\":{\"o\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833],\"y\":[0.833]},\"o\":{\"x\":[0.167],\"y\":[0.167]},\"n\":[\"0p833_0p833_0p167_0p167\"],\"t\":32,\"s\":[100],\"e\":[0]},{\"i\":{\"x\":[0.833],\"y\":[0.833]},\"o\":{\"x\":[0.167],\"y\":[0.167]},\"n\":[\"0p833_0p833_0p167_0p167\"],\"t\":33,\"s\":[0],\"e\":[100]},{\"t\":34}],\"ix\":11},\"r\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.667],\"y\":[1]},\"o\":{\"x\":[0.333],\"y\":[0]},\"n\":[\"0p667_1_0p333_0\"],\"t\":0,\"s\":[-13],\"e\":[11]},{\"i\":{\"x\":[0.667],\"y\":[1]},\"o\":{\"x\":[0.333],\"y\":[0]},\"n\":[\"0p667_1_0p333_0\"],\"t\":10.02,\"s\":[11],\"e\":[6]},{\"i\":{\"x\":[0.667],\"y\":[1]},\"o\":{\"x\":[0.333],\"y\":[0]},\"n\":[\"0p667_1_0p333_0\"],\"t\":14,\"s\":[6],\"e\":[-4]},{\"i\":{\"x\":[0.667],\"y\":[1]},\"o\":{\"x\":[0.333],\"y\":[0]},\"n\":[\"0p667_1_0p333_0\"],\"t\":18.132,\"s\":[-4],\"e\":[0]},{\"t\":23}],\"ix\":10},\"p\":{\"a\":1,\"k\":[{\"i\":{\"x\":0.667,\"y\":1},\"o\":{\"x\":0.333,\"y\":0},\"n\":\"0p667_1_0p333_0\",\"t\":0,\"s\":[120,-61,0],\"e\":[120,134,0],\"to\":[0,32.5,0],\"ti\":[0,-28.75,0]},{\"i\":{\"x\":0.667,\"y\":1},\"o\":{\"x\":0.333,\"y\":0},\"n\":\"0p667_1_0p333_0\",\"t\":9.969,\"s\":[120,134,0],\"e\":[120,111.5,0],\"to\":[0,28.75,0],\"ti\":[0,-0.58333331346512,0]},{\"i\":{\"x\":0.667,\"y\":1},\"o\":{\"x\":0.333,\"y\":0},\"n\":\"0p667_1_0p333_0\",\"t\":14,\"s\":[120,111.5,0],\"e\":[120,137.5,0],\"to\":[0,0.58333331346512,0],\"ti\":[0,-4.08333349227905,0]},{\"i\":{\"x\":0.667,\"y\":1},\"o\":{\"x\":0.333,\"y\":0},\"n\":\"0p667_1_0p333_0\",\"t\":18.132,\"s\":[120,137.5,0],\"e\":[120,136,0],\"to\":[0,4.08333349227905,0],\"ti\":[0,-0.41666665673256,0]},{\"i\":{\"x\":0.667,\"y\":1},\"o\":{\"x\":0.333,\"y\":0},\"n\":\"0p667_1_0p333_0\",\"t\":23,\"s\":[120,136,0],\"e\":[120,140,0],\"to\":[0,0.41666665673256,0],\"ti\":[0,-0.66666668653488,0]},{\"t\":26}],\"ix\":2},\"a\":{\"a\":0,\"k\":[40,40,0],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100,100],\"ix\":6}},\"ao\":0,\"shapes\":[{\"ty\":\"gr\",\"it\":[{\"ind\":0,\"ty\":\"sh\",\"ix\":1,\"ks\":{\"a\":0,\"k\":{\"i\":[[2.761,0],[0,0],[0,-2.761],[0,0],[0,0],[0,0]],\"o\":[[0,0],[-2.761,0],[0,0],[0,0],[0,0],[0,-2.761]],\"v\":[[5,-6],[-5,-6],[-10,-1],[-11,6],[11,6],[10,-1]],\"c\":true},\"ix\":2},\"nm\":\"路径 1\",\"mn\":\"ADBE Vector Shape - Group\",\"hd\":false},{\"ind\":1,\"ty\":\"sh\",\"ix\":2,\"ks\":{\"a\":0,\"k\":{\"i\":[[0,0],[0,0],[0,0],[0,0],[0,0],[0,0],[0,0],[0,0]],\"o\":[[0,0],[0,0],[0,0],[0,0],[0,0],[0,0],[0,0],[0,0]],\"v\":[[5,-1],[5,-0.645],[5.05,-0.293],[5.235,1],[-5.235,1],[-5.05,-0.293],[-5,-0.645],[-5,-1]],\"c\":true},\"ix\":2},\"nm\":\"路径 2\",\"mn\":\"ADBE Vector Shape - Group\",\"hd\":false},{\"ty\":\"mm\",\"mm\":1,\"nm\":\"合并路径 1\",\"mn\":\"ADBE Vector Filter - Merge\",\"hd\":false},{\"ty\":\"fl\",\"c\":{\"a\":0,\"k\":[0.5529411764705883,0.7764705882352941,0.2549019607843137,1],\"ix\":4},\"o\":{\"a\":0,\"k\":100,\"ix\":5},\"r\":1,\"nm\":\"填充 1\",\"mn\":\"ADBE Vector Graphic - Fill\",\"hd\":false},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[39,17],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":0,\"k\":100,\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"变换\"}],\"nm\":\"组 3\",\"np\":4,\"cix\":2,\"ix\":1,\"mn\":\"ADBE Vector Group\",\"hd\":false},{\"ty\":\"gr\",\"it\":[{\"ind\":0,\"ty\":\"sh\",\"ix\":1,\"ks\":{\"a\":0,\"k\":{\"i\":[[2.761,0],[0,0],[0,-2.761],[0,0],[-2.761,0],[0,0],[0,2.761],[0,0]],\"o\":[[0,0],[-2.761,0],[0,0],[0,2.761],[0,0],[2.761,0],[0,0],[0,-2.761]],\"v\":[[24,-23],[-24,-23],[-29,-18],[-29,18],[-24,23],[24,23],[29,18],[29,-18]],\"c\":true},\"ix\":2},\"nm\":\"路径 1\",\"mn\":\"ADBE Vector Shape - Group\",\"hd\":false},{\"ind\":1,\"ty\":\"sh\",\"ix\":2,\"ks\":{\"a\":0,\"k\":{\"i\":[[0,0],[0,0],[0,0],[0,0]],\"o\":[[0,0],[0,0],[0,0],[0,0]],\"v\":[[24,18],[-24,18],[-24,-18],[24,-18]],\"c\":true},\"ix\":2},\"nm\":\"路径 2\",\"mn\":\"ADBE Vector Shape - Group\",\"hd\":false},{\"ty\":\"mm\",\"mm\":1,\"nm\":\"合并路径 1\",\"mn\":\"ADBE Vector Filter - Merge\",\"hd\":false},{\"ty\":\"fl\",\"c\":{\"a\":0,\"k\":[0.5529411764705883,0.7764705882352941,0.2549019607843137,1],\"ix\":4},\"o\":{\"a\":0,\"k\":100,\"ix\":5},\"r\":1,\"nm\":\"填充 1\",\"mn\":\"ADBE Vector Graphic - Fill\",\"hd\":false},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[39,42],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":0,\"k\":100,\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"变换\"}],\"nm\":\"组 4\",\"np\":4,\"cix\":2,\"ix\":2,\"mn\":\"ADBE Vector Group\",\"hd\":false},{\"ty\":\"gr\",\"it\":[{\"ind\":0,\"ty\":\"sh\",\"ix\":1,\"ks\":{\"a\":0,\"k\":{\"i\":[[0,0],[0,0]],\"o\":[[0,0],[0,0]],\"v\":[[12.5,31.5],[65.5,31.5]],\"c\":false},\"ix\":2},\"nm\":\"路径 1\",\"mn\":\"ADBE Vector Shape - Group\",\"hd\":false},{\"ty\":\"st\",\"c\":{\"a\":0,\"k\":[0.5529411764705883,0.7764705882352941,0.2549019607843137,1],\"ix\":3},\"o\":{\"a\":0,\"k\":100,\"ix\":4},\"w\":{\"a\":0,\"k\":5,\"ix\":5},\"lc\":2,\"lj\":1,\"ml\":10,\"nm\":\"描边 1\",\"mn\":\"ADBE Vector Graphic - Stroke\",\"hd\":false},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[0,0],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":0,\"k\":100,\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"变换\"}],\"nm\":\"组 5\",\"np\":2,\"cix\":2,\"ix\":3,\"mn\":\"ADBE Vector Group\",\"hd\":false},{\"ty\":\"gr\",\"it\":[{\"ind\":0,\"ty\":\"sh\",\"ix\":1,\"ks\":{\"a\":0,\"k\":{\"i\":[[0,0],[0,0]],\"o\":[[0,0],[0,0]],\"v\":[[55,37.5],[57,37.5]],\"c\":false},\"ix\":2},\"nm\":\"路径 1\",\"mn\":\"ADBE Vector Shape - Group\",\"hd\":false},{\"ty\":\"st\",\"c\":{\"a\":0,\"k\":[0.4392156862745098,0.6196078431372549,0.20392156862745098,1],\"ix\":3},\"o\":{\"a\":0,\"k\":100,\"ix\":4},\"w\":{\"a\":0,\"k\":5,\"ix\":5},\"lc\":2,\"lj\":1,\"ml\":10,\"nm\":\"描边 1\",\"mn\":\"ADBE Vector Graphic - Stroke\",\"hd\":false},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[0,0],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":0,\"k\":100,\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"变换\"}],\"nm\":\"组 6\",\"np\":2,\"cix\":2,\"ix\":4,\"mn\":\"ADBE Vector Group\",\"hd\":false},{\"ty\":\"gr\",\"it\":[{\"ind\":0,\"ty\":\"sh\",\"ix\":1,\"ks\":{\"a\":0,\"k\":{\"i\":[[2.761,0],[0,0],[0,2.761],[0,0],[-2.761,0],[0,0],[0,-2.761],[0,0]],\"o\":[[0,0],[-2.761,0],[0,0],[0,-2.761],[0,0],[2.761,0],[0,0],[0,2.761]],\"v\":[[22,21.5],[-22,21.5],[-27,16.5],[-27,-16.5],[-22,-21.5],[22,-21.5],[27,-16.5],[27,16.5]],\"c\":true},\"ix\":2},\"nm\":\"路径 1\",\"mn\":\"ADBE Vector Shape - Group\",\"hd\":false},{\"ty\":\"fl\",\"c\":{\"a\":0,\"k\":[0.7215686274509804,0.9137254901960784,0.5254901960784314,1],\"ix\":4},\"o\":{\"a\":0,\"k\":100,\"ix\":5},\"r\":1,\"nm\":\"填充 1\",\"mn\":\"ADBE Vector Graphic - Fill\",\"hd\":false},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[46,48.5],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":0,\"k\":100,\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"变换\"}],\"nm\":\"组 7\",\"np\":2,\"cix\":2,\"ix\":5,\"mn\":\"ADBE Vector Group\",\"hd\":false}],\"ip\":0,\"op\":750,\"st\":0,\"bm\":0}],\"markers\":[]}")
                ////privatbank animation
                //.setLottieJsonAnimation("{\"v\":\"4.6.8\",\"fr\":29.9700012207031,\"ip\":0,\"op\":119.000004846969,\"w\":256,\"h\":256,\"nm\":\"Comp 1\",\"ddd\":0,\"assets\":[],\"layers\":[{\"ddd\":0,\"ind\":1,\"ty\":4,\"nm\":\"Shape Layer 4\",\"ks\":{\"o\":{\"a\":0,\"k\":100},\"r\":{\"a\":0,\"k\":0},\"p\":{\"a\":0,\"k\":[128,128.016,0]},\"a\":{\"a\":0,\"k\":[13,13.016,0]},\"s\":{\"a\":0,\"k\":[100,100,100]}},\"ao\":0,\"shapes\":[{\"ty\":\"gr\",\"it\":[{\"d\":1,\"ty\":\"el\",\"s\":{\"a\":0,\"k\":[200,200]},\"p\":{\"a\":0,\"k\":[0,0]},\"nm\":\"Ellipse Path 1\",\"mn\":\"ADBE Vector Shape - Ellipse\"},{\"ty\":\"tm\",\"s\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.667],\"y\":[1]},\"o\":{\"x\":[0.333],\"y\":[0]},\"n\":[\"0p667_1_0p333_0\"],\"t\":92,\"s\":[0],\"e\":[25]},{\"i\":{\"x\":[0.667],\"y\":[1]},\"o\":{\"x\":[0.333],\"y\":[0]},\"n\":[\"0p667_1_0p333_0\"],\"t\":105,\"s\":[25],\"e\":[49]},{\"i\":{\"x\":[0.667],\"y\":[1]},\"o\":{\"x\":[0.333],\"y\":[0]},\"n\":[\"0p667_1_0p333_0\"],\"t\":112,\"s\":[49],\"e\":[100]},{\"t\":120.0000048877}],\"ix\":1},\"e\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.667],\"y\":[1]},\"o\":{\"x\":[0.333],\"y\":[0]},\"n\":[\"0p667_1_0p333_0\"],\"t\":90,\"s\":[1],\"e\":[100]},{\"t\":120.0000048877}],\"ix\":2},\"o\":{\"a\":0,\"k\":0,\"ix\":3},\"m\":1,\"ix\":2,\"nm\":\"Trim Paths 1\",\"mn\":\"ADBE Vector Filter - Trim\"},{\"ty\":\"st\",\"c\":{\"a\":0,\"k\":[0.023529,0.854902,0.238708,1]},\"o\":{\"a\":0,\"k\":100},\"w\":{\"a\":0,\"k\":20},\"lc\":2,\"lj\":1,\"ml\":4,\"nm\":\"Stroke 1\",\"mn\":\"ADBE Vector Graphic - Stroke\"},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[12,13],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":0,\"k\":100,\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"Transform\"}],\"nm\":\"Ellipse 1\",\"np\":4,\"cix\":2,\"ix\":1,\"mn\":\"ADBE Vector Group\"}],\"ip\":90.0000036657751,\"op\":390.000015885026,\"st\":90.0000036657751,\"bm\":0,\"sr\":1},{\"ddd\":0,\"ind\":2,\"ty\":4,\"nm\":\"Shape Layer 3\",\"ks\":{\"o\":{\"a\":0,\"k\":100},\"r\":{\"a\":0,\"k\":0},\"p\":{\"a\":0,\"k\":[128,128.016,0]},\"a\":{\"a\":0,\"k\":[13,13.016,0]},\"s\":{\"a\":0,\"k\":[100,100,100]}},\"ao\":0,\"shapes\":[{\"ty\":\"gr\",\"it\":[{\"d\":1,\"ty\":\"el\",\"s\":{\"a\":0,\"k\":[200,200]},\"p\":{\"a\":0,\"k\":[0,0]},\"nm\":\"Ellipse Path 1\",\"mn\":\"ADBE Vector Shape - Ellipse\"},{\"ty\":\"tm\",\"s\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.667],\"y\":[1]},\"o\":{\"x\":[0.333],\"y\":[0]},\"n\":[\"0p667_1_0p333_0\"],\"t\":62,\"s\":[0],\"e\":[25]},{\"i\":{\"x\":[0.667],\"y\":[1]},\"o\":{\"x\":[0.333],\"y\":[0]},\"n\":[\"0p667_1_0p333_0\"],\"t\":75,\"s\":[25],\"e\":[49]},{\"i\":{\"x\":[0.667],\"y\":[1]},\"o\":{\"x\":[0.333],\"y\":[0]},\"n\":[\"0p667_1_0p333_0\"],\"t\":82,\"s\":[49],\"e\":[100]},{\"t\":90.0000036657751}],\"ix\":1},\"e\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.667],\"y\":[1]},\"o\":{\"x\":[0.333],\"y\":[0]},\"n\":[\"0p667_1_0p333_0\"],\"t\":60,\"s\":[1],\"e\":[100]},{\"t\":90.0000036657751}],\"ix\":2},\"o\":{\"a\":0,\"k\":0,\"ix\":3},\"m\":1,\"ix\":2,\"nm\":\"Trim Paths 1\",\"mn\":\"ADBE Vector Filter - Trim\"},{\"ty\":\"st\",\"c\":{\"a\":0,\"k\":[0.854902,0.023529,0.2582694,1]},\"o\":{\"a\":0,\"k\":100},\"w\":{\"a\":0,\"k\":20},\"lc\":2,\"lj\":1,\"ml\":4,\"nm\":\"Stroke 1\",\"mn\":\"ADBE Vector Graphic - Stroke\"},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[12,13],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":0,\"k\":100,\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"Transform\"}],\"nm\":\"Ellipse 1\",\"np\":4,\"cix\":2,\"ix\":1,\"mn\":\"ADBE Vector Group\"}],\"ip\":60.0000024438501,\"op\":360.000014663101,\"st\":60.0000024438501,\"bm\":0,\"sr\":1},{\"ddd\":0,\"ind\":3,\"ty\":4,\"nm\":\"Shape Layer 2\",\"ks\":{\"o\":{\"a\":0,\"k\":100},\"r\":{\"a\":0,\"k\":0},\"p\":{\"a\":0,\"k\":[128,128.016,0]},\"a\":{\"a\":0,\"k\":[13,13.016,0]},\"s\":{\"a\":0,\"k\":[100,100,100]}},\"ao\":0,\"shapes\":[{\"ty\":\"gr\",\"it\":[{\"d\":1,\"ty\":\"el\",\"s\":{\"a\":0,\"k\":[200,200]},\"p\":{\"a\":0,\"k\":[0,0]},\"nm\":\"Ellipse Path 1\",\"mn\":\"ADBE Vector Shape - Ellipse\"},{\"ty\":\"tm\",\"s\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.667],\"y\":[1]},\"o\":{\"x\":[0.333],\"y\":[0]},\"n\":[\"0p667_1_0p333_0\"],\"t\":32,\"s\":[0],\"e\":[25]},{\"i\":{\"x\":[0.667],\"y\":[1]},\"o\":{\"x\":[0.333],\"y\":[0]},\"n\":[\"0p667_1_0p333_0\"],\"t\":45,\"s\":[25],\"e\":[49]},{\"i\":{\"x\":[0.667],\"y\":[1]},\"o\":{\"x\":[0.333],\"y\":[0]},\"n\":[\"0p667_1_0p333_0\"],\"t\":52,\"s\":[49],\"e\":[100]},{\"t\":60.0000024438501}],\"ix\":1},\"e\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.667],\"y\":[1]},\"o\":{\"x\":[0.333],\"y\":[0]},\"n\":[\"0p667_1_0p333_0\"],\"t\":30,\"s\":[1],\"e\":[100]},{\"t\":60.0000024438501}],\"ix\":2},\"o\":{\"a\":0,\"k\":0,\"ix\":3},\"m\":1,\"ix\":2,\"nm\":\"Trim Paths 1\",\"mn\":\"ADBE Vector Filter - Trim\"},{\"ty\":\"st\",\"c\":{\"a\":0,\"k\":[0.023529,0.6886272,0.854902,1]},\"o\":{\"a\":0,\"k\":100},\"w\":{\"a\":0,\"k\":20},\"lc\":2,\"lj\":1,\"ml\":4,\"nm\":\"Stroke 1\",\"mn\":\"ADBE Vector Graphic - Stroke\"},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[12,13],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":0,\"k\":100,\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"Transform\"}],\"nm\":\"Ellipse 1\",\"np\":4,\"cix\":2,\"ix\":1,\"mn\":\"ADBE Vector Group\"}],\"ip\":30.0000012219251,\"op\":330.000013441176,\"st\":30.0000012219251,\"bm\":0,\"sr\":1},{\"ddd\":0,\"ind\":4,\"ty\":4,\"nm\":\"Shape Layer 1\",\"ks\":{\"o\":{\"a\":0,\"k\":100},\"r\":{\"a\":0,\"k\":0},\"p\":{\"a\":0,\"k\":[128,128.016,0]},\"a\":{\"a\":0,\"k\":[13,13.016,0]},\"s\":{\"a\":0,\"k\":[100,100,100]}},\"ao\":0,\"shapes\":[{\"ty\":\"gr\",\"it\":[{\"d\":1,\"ty\":\"el\",\"s\":{\"a\":0,\"k\":[200,200]},\"p\":{\"a\":0,\"k\":[0,0]},\"nm\":\"Ellipse Path 1\",\"mn\":\"ADBE Vector Shape - Ellipse\"},{\"ty\":\"tm\",\"s\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.667],\"y\":[1]},\"o\":{\"x\":[0.333],\"y\":[0]},\"n\":[\"0p667_1_0p333_0\"],\"t\":2,\"s\":[0],\"e\":[25]},{\"i\":{\"x\":[0.667],\"y\":[1]},\"o\":{\"x\":[0.333],\"y\":[0]},\"n\":[\"0p667_1_0p333_0\"],\"t\":15,\"s\":[25],\"e\":[49]},{\"i\":{\"x\":[0.667],\"y\":[1]},\"o\":{\"x\":[0.333],\"y\":[0]},\"n\":[\"0p667_1_0p333_0\"],\"t\":22,\"s\":[49],\"e\":[100]},{\"t\":30.0000012219251}],\"ix\":1},\"e\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.667],\"y\":[1]},\"o\":{\"x\":[0.333],\"y\":[0]},\"n\":[\"0p667_1_0p333_0\"],\"t\":0,\"s\":[1],\"e\":[100]},{\"t\":30.0000012219251}],\"ix\":2},\"o\":{\"a\":0,\"k\":0,\"ix\":3},\"m\":1,\"ix\":2,\"nm\":\"Trim Paths 1\",\"mn\":\"ADBE Vector Filter - Trim\"},{\"ty\":\"st\",\"c\":{\"a\":0,\"k\":[0.854902,0.54902,0.023529,1]},\"o\":{\"a\":0,\"k\":100},\"w\":{\"a\":0,\"k\":20},\"lc\":2,\"lj\":1,\"ml\":4,\"nm\":\"Stroke 1\",\"mn\":\"ADBE Vector Graphic - Stroke\"},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[12,13],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":0,\"k\":100,\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"Transform\"}],\"nm\":\"Ellipse 1\",\"np\":4,\"cix\":2,\"ix\":1,\"mn\":\"ADBE Vector Group\"}],\"ip\":0,\"op\":300.00001221925,\"st\":0,\"bm\":0,\"sr\":1}]}")
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
