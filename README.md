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

### Demo

[<img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' width="210"/>](https://play.google.com/store/apps/details?id=lens24.demo&hl=en&gl=US)


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
    implementation 'io.github.vlasentiy:lens24:1.1.0'
}
```

### Usage

Build an Intent using the `ScanCardIntent.Builder` and start a new activity to perform the scan:

#### Kotlin

```kotlin
class MyActivity : AppCompatActivity {

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
        /*Your code here*/
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
        /*Your code here*/
    }

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
