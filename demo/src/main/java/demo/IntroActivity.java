package demo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        findViewById(R.id.button_next).setOnClickListener(view -> goToCardDetails());
    }

    private void goToCardDetails() {
        Intent intent = new Intent(this, CardDetailsActivity.class);
        startActivity(intent);
        finish();
    }

}
