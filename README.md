# Lens24

Automatic recognition of bank card data using built-in camera on Android devices.

### Usage

Build an Intent using the `ScanCardIntent.Builder` and start a new activity to perform the scan:

```kotlin
companion object {
    const val REQUEST_CODE_SCAN_CARD = 128
}

private fun scanCard() {
    val intent: Intent = ScanCardIntent.Builder(requireContext())
        .setHint(getString(R.string.hint_position_card_in_frame))
        .setToolbarTitle("Scan card")
        .setSaveCard(true)
        .setManualInputButtonText("Manual input")
        .setLottieJsonAnimation("lottie json data")
        .build()
    startActivityForResult(intent, REQUEST_CODE_SCAN_CARD)
}
```

Handle the result:

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == REQUEST_CODE_SCAN_CARD) {
        val tag = ScanCardIntent::class.java.simpleName
        if (resultCode == Activity.RESULT_OK) {
            val card = data?.getParcelableExtra<Card>(ScanCardIntent.RESULT_CARD_DATA)
            val cardImage = data?.getByteArrayExtra(ScanCardIntent.RESULT_CARD_IMAGE)
            val bitmap = BitmapFactory.decodeByteArray(cardImage, 0, cardImage?.size ?: 0)
            val cardData = """
                    Card number: ${card?.cardNumberRedacted}
                    Card holder: ${card?.cardHolderName}
                    Card expiration date: ${card?.expirationDate}
                    """.trimIndent()
            Log.i(tag, cardData)
        } else if (resultCode == Activity.RESULT_CANCELED) {
            val reason: Int = data?.getIntExtra(
                ScanCardIntent.RESULT_CANCEL_REASON,
                ScanCardIntent.BACK_PRESSED
            ) ?: ScanCardIntent.BACK_PRESSED
            if (reason == ScanCardIntent.ADD_MANUALLY_PRESSED) {
                Log.i(tag, "ADD_MANUALLY_PRESSED")
            } else if (reason == ScanCardIntent.BACK_PRESSED) {
                Log.i(tag, "BACK_PRESSED")
            }
        } else if (resultCode == ScanCardIntent.RESULT_CODE_ERROR) {
            Log.i(tag, "SCAN_FAILED")
        }
    }
}
```

```java
class MyActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    assert result.getData() != null;
                    Card card = result.getData().getParcelableExtra(ScanCardIntent.RESULT_CARD_DATA);
                    byte[] cardImage = result.getData().getByteArrayExtra(ScanCardIntent.RESULT_CARD_IMAGE);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(cardImage, 0, cardImage.length);
                    if (BuildConfig.DEBUG) Log.i(TAG, "Card info: " + card);
                    setCard(card);
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    @CancelReason final int reason;
                    if (result.getData() != null) {
                        reason = result.getData().getIntExtra(ScanCardIntent.RESULT_CANCEL_REASON, ScanCardIntent.BACK_PRESSED);
                    } else {
                        reason = ScanCardIntent.BACK_PRESSED;
                    }

                    if (reason == ScanCardIntent.ADD_MANUALLY_PRESSED) {
                        if (BuildConfig.DEBUG) Log.i(TAG, "reason: ADD_MANUALLY_PRESSED");
                    }
                } else if (result.getResultCode() == ScanCardIntent.RESULT_CODE_ERROR) {
                    Log.i(TAG, "Scan failed");
                }
            });

    private void scanCard() {
        Intent intent = new ScanCardIntent.Builder(this)
                .setScanCardHolder(true)
                .setScanExpirationDate(true)
                .setHint(getString(R.string.hint))
                .setToolbarTitle("Scan card")
                .setSaveCard(true)
                .setManualInputButtonText("Manual input")
                .setBottomHint("bottom hint")
                .setMainColor(R.color.primary_color_dark)
                .setLottieJsonAnimation("lottie json data")
                .build();

        startActivityIntent.launch(intent);
    }
}
```

### License

```
MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
 
The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
 
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
