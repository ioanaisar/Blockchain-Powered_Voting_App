package ro.pub.cs.systems.eim.votingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    Button loginSignInButton = null;

    private final LoginSignInButtonClickListener loginSignINButtonClickListener = new LoginSignInButtonClickListener();

    private class LoginSignInButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, Login_SignIn_Activity.class);
            startActivity(intent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginSignInButton = findViewById(R.id.buttonLoginSignIn);
        loginSignInButton.setOnClickListener(loginSignINButtonClickListener);

    }
}