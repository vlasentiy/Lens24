<p align="center" style="margin-bottom: 0px !important;">
  <img width="200" src="https://github.com/vlasentiy/assets/blob/main/lens24_logo.svg" alt="Lens24 logo" align="center">
</p>
<h1 align="center" style="margin-top: 0px;">Lens24</h1>

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.vlasentiy/lens24/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.vlasentiy/lens24)
[![API](https://img.shields.io/badge/API-16%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=16)
<a href="https://github.com/vlasentiy/Lens24/blob/master/LICENSE.md">
    <img src="https://img.shields.io/badge/license-MIT-blue.svg" alt="Lens24 is released under the MIT license." />
  </a>

Lens24 is SDK for Android that gives you ability to scan various of credit or payment cards in your app offline.
You can easily integrate and customize the SDK into your app by following the instructions below.

<p align="center">
  <img src="https://github.com/vlasentiy/assets/blob/main/lens24_example_1.gif" width="360" />
    &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp;
  <img src="https://github.com/vlasentiy/assets/blob/main/lens24_example_4.gif" width="360" /> 
</p>

### SDK integration

In your `build.gradle`, add maven repository to repositories list

```
    repositories {
        mavenCentral()
    }
```

Add _Lens24_ as a dependency

```
dependencies {
    implementation 'io.github.vlasentiy:lens24:1.0.0'
}
```

### Usage

Build an Intent using the `ScanCardIntent.Builder` and start a new activity to perform the scan:

#### Kotlin

```kotlin
class MyActivity : AppCompatActivity {

    private var startActivityIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            val tag = ScanCardIntent::class.java.simpleName

            if (result.resultCode == Activity.RESULT_OK) {
                val card: Card? = if (Build.VERSION.SDK_INT >= TIRAMISU) {
                    result.data?.getParcelableExtra(
                        ScanCardIntent.RESULT_CARD_DATA,
                        Card::class.java
                    )
                } else {
                    result.data?.getParcelableExtra(ScanCardIntent.RESULT_CARD_DATA)
                }

                val cardImage = result.data?.getByteArrayExtra(ScanCardIntent.RESULT_CARD_IMAGE)
                val bitmap: Bitmap =
                    BitmapFactory.decodeByteArray(cardImage, 0, cardImage?.size ?: 0)

                val cardData = """
                    Card number: ${card?.cardNumberRedacted}
                    Card holder: ${card?.cardHolderName}
                    Card expiration date: ${card?.expirationDate}
                    """.trimIndent()

                Log.i(tag, cardData)
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                @ScanCardIntent.CancelReason val reason: Int = if (result.data != null) {
                    result.data!!.getIntExtra(
                        ScanCardIntent.RESULT_CANCEL_REASON,
                        ScanCardIntent.BACK_PRESSED
                    )
                } else {
                    ScanCardIntent.BACK_PRESSED
                }
                if (reason == ScanCardIntent.ADD_MANUALLY_PRESSED) {
                    Log.i(tag, "reason: ADD_MANUALLY_PRESSED")
                }
            } else if (result.resultCode == ScanCardIntent.RESULT_CODE_ERROR) {
                Log.i(tag, "Scan failed")
            }
        }

    private fun scanCard() {
        val intent: Intent = ScanCardIntent.Builder(this)
            // customize these values to suit your needs
            .setScanCardHolder(true)
            .setScanExpirationDate(true)
            .setVibrationEnabled(false)
            .setHint(getString(R.string.hint))
            .setToolbarTitle("Scan card")
            .setSaveCard(true)
            .setManualInputButtonText("Manual input")
            .setBottomHint("bottom hint")
            .setMainColor(R.color.primary_color_dark)
            .setLottieJsonAnimation("lottie json data")
            .build()

        startActivityIntent.launch(intent)
    }
}
```

#### Java
```java
class MyActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                String tag = ScanCardIntent.class.getSimpleName();

                if (result.getResultCode() == Activity.RESULT_OK) {
                    assert result.getData() != null;
                    Card card = result.getData().getParcelableExtra(ScanCardIntent.RESULT_CARD_DATA);

                    byte[] cardImage = result.getData().getByteArrayExtra(ScanCardIntent.RESULT_CARD_IMAGE);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(cardImage, 0, cardImage.length);

                    Log.i(tag, "Card info: " + card.toString());
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    @ScanCardIntent.CancelReason final int reason;
                    if (result.getData() != null) {
                        reason = result.getData().getIntExtra(ScanCardIntent.RESULT_CANCEL_REASON, ScanCardIntent.BACK_PRESSED);
                    } else {
                        reason = ScanCardIntent.BACK_PRESSED;
                    }
                    if (reason == ScanCardIntent.ADD_MANUALLY_PRESSED) {
                        Log.i(tag, "reason: ADD_MANUALLY_PRESSED");
                    }
                } else if (result.getResultCode() == ScanCardIntent.RESULT_CODE_ERROR) {
                    Log.i(tag, "Scan failed");
                }
            });

    private void scanCard() {
        Intent intent = new ScanCardIntent.Builder(this)
                // customize these values to suit your needs
                .setScanCardHolder(true)
                .setScanExpirationDate(true)
                .setVibrationEnabled(false)
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

### Support
<p><a href="https://www.buymeacoffee.com/vlasentiy"> <img align="left" src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" height="50" width="210" alt="vlasentiy" /></a></p><br></br>


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
