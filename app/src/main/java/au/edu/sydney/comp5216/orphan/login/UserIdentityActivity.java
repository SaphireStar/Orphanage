package au.edu.sydney.comp5216.orphan.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import au.edu.sydney.comp5216.orphan.StorageLocation;
import au.edu.sydney.comp5216.orphan.R;
import au.edu.sydney.comp5216.orphan.orph.OrphProfile;
import au.edu.sydney.comp5216.orphan.user.OrphanagesListView;

/**
 * Allows users to select their identity as donor or orphanage owner. If they want to register
 * their orphanage, a link is provided directing to the register page.
 *
 * @Author: Yiqing Yang
 */
public class UserIdentityActivity extends AppCompatActivity {
    // user information
    private String userId = "";
    private String userEmail;

    // widgets
    private Button orphBtn;
    private TextView registerOrph;
    SharedPreferences gameSettings;
    // request code
    private final int REQUEST_FOR_REGISTER = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_identity_activity);

        StorageLocation.storagePath = getFilesDir().getAbsolutePath()+"/";
        gameSettings = getSharedPreferences("MyGamePreferences", MODE_PRIVATE);
        // initialize the widgets
        orphBtn = (Button) findViewById(R.id.orphanageBtn);
        registerOrph = (TextView) findViewById(R.id.registerOrph);

        // set the default widget visibility
        orphBtn.setVisibility(View.GONE);
        registerOrph.setVisibility(View.VISIBLE);

        // receive message from the previous activity
        userEmail = getIntent().getStringExtra("userEmail");

        // Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // fetch user id based on the user email
        final boolean[] flag = {false};
        db.collection("login")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //如果有这个人的话，更新信息
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                flag[0] = true;
                                // update user id
                                String id = document.getId();  // user id
                                userId = id;
                                Log.i("Fetched the user id", userId);
                                Toast.makeText(getApplicationContext(), "Welcome",
                                        Toast.LENGTH_LONG).show();
                                SharedPreferences.Editor prefEditor = gameSettings.edit();
                                prefEditor.putString("userId", userId);
                                prefEditor.apply();
                                // change the interface based on user identity
                                boolean isOrph = document.get("isOrph").equals("true")? true:false;
                                if(isOrph){
                                    // modify the widget visibility
                                    orphBtn.setVisibility(View.VISIBLE);
                                    registerOrph.setVisibility(View.GONE);
                                }else{
                                    // modify the widget visibility
                                    orphBtn.setVisibility(View.GONE);
                                    registerOrph.setVisibility(View.VISIBLE);
                                }
                                break;
                            }
                            if(!flag[0]){
                                if(!flag[0]){
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("isOrph", "false");
                                    user.put("email", userEmail);
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    db.collection("login")
                                            .add(user)
                                            .addOnSuccessListener(
                                                    new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(
                                                        DocumentReference documentReference) {
                                                    userId = documentReference.getId();
                                                    SharedPreferences.Editor prefEditor =
                                                            gameSettings.edit();
                                                    prefEditor.putString("userId", userId);
                                                    prefEditor.apply();
                                                }
                                            });

                                }
                            }


                        }else{
                            Log.i("flag", "" + "no such email");
                        }
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Logs in as donor
     */
    public void donorOnClickEvent(View view){
        Intent intent = new Intent(UserIdentityActivity.this,
                OrphanagesListView.class);
        // pass userId
        intent.putExtra("userId", userId);
        // start the activity
        startActivity(intent);
    }

    /**
     * Logs in as orphanage
     */
    public void orphanageOnClickEvent(View view){
        Intent intent = new Intent(UserIdentityActivity.this,
                OrphProfile.class);
        // pass userId
        intent.putExtra("userId", userId);
        // start the activity
        startActivity(intent);
    }

    /**
     * Starts an activity which allows user to register their orphanage
     */
    public void registerOnClickEvent(View view){
        // initialize the intent
        Intent intent = new Intent(UserIdentityActivity.this,
                OrphanageRegisterActivity.class);
        // pass userId
        intent.putExtra("userId", userId);
        intent.putExtra("email",userEmail );
        // start the activity
        startActivityForResult(intent, REQUEST_FOR_REGISTER );

    }

    /**
     * Receives the response
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // modify the widget visibility
            orphBtn.setVisibility(View.VISIBLE);
            registerOrph.setVisibility(View.GONE);
        }
    }
}
