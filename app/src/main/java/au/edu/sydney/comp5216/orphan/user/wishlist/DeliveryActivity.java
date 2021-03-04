package au.edu.sydney.comp5216.orphan.user.wishlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import au.edu.sydney.comp5216.orphan.R;

/**
 * The DeliveryActivity activity.
 * It displays a view for users to fill in tracking id and description,
 * if all the fields are filled in, it will update the data in firebase.
 */
public class DeliveryActivity extends AppCompatActivity {
    /**
     * the life cycle function onCreate()
     * @param savedInstanceState the bundle savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        final String id = getIntent().getStringExtra("id");
        SharedPreferences gamesettings =
                this.getSharedPreferences("MyGamePreferences", Context.MODE_PRIVATE);
        final String userId = gamesettings.getString("userId", null);
        Log.i("INFO", "userId: " + userId);

        final EditText tracking = (EditText) findViewById(R.id.tracking_id);

        final Button button = findViewById(R.id.deliver);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent data = new Intent();

                String trackingID = tracking.getText().toString();
                if (trackingID.isEmpty()) {
                    Toast.makeText(v.getContext(),
                            "The tracking ID is empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                // when loaded, it fetches the query
                // when submitted, it set attribute "delivered" to false
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                assert id != null;
                DocumentReference newDoc = db.collection("wishlist").document(id);
                newDoc.update("donor", userId);

                Toast.makeText(v.getContext(),
                        "Wish list item delivered", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, data);

                finish();
                onBackPressed();
            }
        });
    }
}
