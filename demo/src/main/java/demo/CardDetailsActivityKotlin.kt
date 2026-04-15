package demo

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.textfield.TextInputLayout
import demo.validation.CardExpiryDateValidator
import demo.validation.CardHolderValidator
import demo.validation.CardNumberValidator
import demo.validation.ValidationResult
import demo.widget.CardNumberEditText
import lens24.intent.Card
import lens24.intent.ScanCardCallback
import lens24.intent.ScanCardIntent

class CardDetailsActivityKotlin : AppCompatActivity() {
    private var mToolbar: Toolbar? = null
    private var mCardNumberField: TextInputLayout? = null
    private var mCardholderField: TextInputLayout? = null
    private var mExpiryField: TextInputLayout? = null
    private var mCardNumberValidator: CardNumberValidator? = null
    private var mCardHolderValidator: CardHolderValidator? = null
    private var mExpiryDateValidator: CardExpiryDateValidator? = null

    private var activityResultCallback = ScanCardCallback.Builder()
        .setOnSuccess { card: Card, bitmap: Bitmap? -> setCard(card, bitmap) }
        .setOnBackPressed { /*Your code here*/ }
        .setOnManualInput { /*Your code here*/ }
        .setOnError { /*Your code here*/ }
        .build()

    private var startActivityIntent = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult(),
        activityResultCallback
    )

    private fun setCard(card: Card, bitmap: Bitmap?) {
        mCardNumberField!!.editText!!.setText(card.cardNumber)
        mCardholderField!!.editText!!.setText(card.cardHolderName)
        mExpiryField!!.editText!!.setText(card.expirationDate)
        setValidationResult(ValidationResult.empty())
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!BuildConfig.DEBUG) {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }
        setContentView(R.layout.activity_card_details)
        val mAdView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        mToolbar = findViewById(R.id.lens24_toolbar)
        mCardNumberField = findViewById(R.id.card_number_field)
        mCardholderField = findViewById(R.id.cardholder_field)
        mExpiryField = findViewById(R.id.expiry_date_field)
        setupToolbar()
        findViewById<View>(R.id.scan_button).setOnClickListener { view: View? -> scanCard() }
        if (savedInstanceState == null) {
            scanCard()
        }
    }

    private fun scanCard() {
        val intent = ScanCardIntent.Builder(this)
            .setScanCardHolder(true)
            .setScanExpirationDate(true)
            .setHint(getString(R.string.lens24_hint_position_card_in_frame))
            .setToolbarTitle("Scan card")
            .setSaveCard(true)
            .setVibrationEnabled(true)
            .setManualInputButtonText("Manual input")
            .setBottomHint("and wait for a moment")
            .setMainColor(R.color.lens24_primary_color)
            .build()
        startActivityIntent.launch(intent)
    }

    private fun setupToolbar() {
        setSupportActionBar(mToolbar)
        mToolbar!!.findViewById<View>(R.id.button_next).setOnClickListener { view: View? ->
            val card = readForm()
            val validationResult = validateForm(card)
            setValidationResult(validationResult)
            if (validationResult.isValid) {
                Toast.makeText(this@CardDetailsActivityKotlin, "That's All folks!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun readForm(): Card {
        val cardNumber = (mCardNumberField!!.editText as CardNumberEditText?)!!.cardNumber
        val holder = mCardholderField!!.editText!!.text.toString()
        val expiryDate = mExpiryField!!.editText!!.text.toString()
        return Card(cardNumber, holder, expiryDate)
    }

    private fun validateForm(card: Card): ValidationResult {
        if (mCardNumberValidator == null) {
            mCardNumberValidator = CardNumberValidator()
            mExpiryDateValidator = CardExpiryDateValidator()
            mCardHolderValidator = CardHolderValidator()
        }
        val results = ValidationResult(3)
        results.put(R.id.card_number_field, mCardNumberValidator!!.validate(card.cardNumber))
        results.put(R.id.cardholder_field, mCardHolderValidator!!.validate(card.cardHolderName))
        results.put(R.id.expiry_date_field, mExpiryDateValidator!!.validate(card.expirationDate))
        return results
    }

    private fun setValidationResult(result: ValidationResult) {
        mCardNumberField!!.error = result.getMessage(R.id.card_number_field, resources)
        mCardholderField!!.error = result.getMessage(R.id.cardholder_field, resources)
        mExpiryField!!.error = result.getMessage(R.id.expiry_date_field, resources)
    }
}