package ro.pub.cs.systems.eim.votingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;

public class Login_SignIn_Activity extends AppCompatActivity {

    Button loginButton = null;

    String username = null;
    String password = null;

    private EditText userEditText = null;
    private EditText passwordEditText = null;

    private final LoginButtonClickListener loginButtonClickListener = new LoginButtonClickListener();

    private class LoginButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            username = userEditText.getText().toString();
            password = passwordEditText.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Username and password are empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            setupBouncyCastle();


            User user = null;
            try {
                user = createNewEthereumAccount(username, password);
            } catch (InvalidAlgorithmParameterException | IOException | NoSuchProviderException |
                     NoSuchAlgorithmException | CipherException e) {
                throw new RuntimeException(e);
            }

            Intent intent = new Intent(Login_SignIn_Activity.this, OptionsActivity.class);
            intent.putExtra("USER", user);
            startActivity(intent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_in);

        loginButton = findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(loginButtonClickListener);

        userEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
    }


    private void setupBouncyCastle() {
        final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider == null) {
            return;
        }
        if (provider.getClass().equals(BouncyCastleProvider.class)) {
            return;
        }
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    private User createNewEthereumAccount(String username, String password) throws InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, NoSuchProviderException, CipherException, IOException {
        File directory = getFilesDir();
        ECKeyPair ecKeyPair = Keys.createEcKeyPair();

        String fileName = WalletUtils.generateWalletFile(password, ecKeyPair, directory, false);

        String privateKey = ecKeyPair.getPrivateKey().toString(16);
        String address = "0x".concat(Keys.getAddress(ecKeyPair));
        return new User(username, address, password, privateKey, fileName);
    }


}