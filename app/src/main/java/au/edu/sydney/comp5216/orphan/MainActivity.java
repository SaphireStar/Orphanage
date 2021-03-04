package au.edu.sydney.comp5216.orphan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import au.edu.sydney.comp5216.orphan.login.UserIdentityActivity;

/**
 * Welcome page, which invokes the Firebase AuthUI for logging in
 */
public class MainActivity extends AppCompatActivity {
    // request code
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // call Firebase AuthUI
        enableLoggingPage();
    }

    /**
     * Calls the Firebase AuthUI
     */
    public void enableLoggingPage(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build());
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    /**
     * Receives data from Firebase AuthUI
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // start the UserIdentityActivity activity
                Intent intent = new Intent(MainActivity.this, UserIdentityActivity.class);
                // pass user email
                intent.putExtra("userEmail", user.getEmail());
                // share information
                SharedPreferences gameSettings = getSharedPreferences("MyGamePreferences", MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = gameSettings.edit();
                prefEditor.putString("userName", user.getDisplayName());
                prefEditor.apply();
                // start activity
                startActivity(intent);
            } else {
            }
        }
    }
}