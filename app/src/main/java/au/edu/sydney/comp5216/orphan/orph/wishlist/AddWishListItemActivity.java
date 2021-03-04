package au.edu.sydney.comp5216.orphan.orph.wishlist;

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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import au.edu.sydney.comp5216.orphan.R;

/**
 * The AddWishListItemActivity activity.
 * It displays a view for users to add/update the selected item,
 * if all the fields are filled in, it will update the data in firebase.
 */
public class AddWishListItemActivity extends AppCompatActivity {
    // the flag indicating whether to update an item
    private boolean update = false;

    /**
     * the life cycle function onCreate()
     * @param savedInstanceState the bundle savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wishlist_item);

        final String id = getIntent().getStringExtra("id");
        SharedPreferences gamesettings = this.getSharedPreferences(
                "MyGamePreferences", Context.MODE_PRIVATE);
        final String orphId = gamesettings.getString("userId", null);
        String title = getIntent().getStringExtra("name");
        final EditText uDescription = (EditText) findViewById(R.id.u_item_description);

        final EditText uTitle = (EditText) findViewById(R.id.u_item_title);

        if (title != null) {
            uTitle.setText(title);
        }
        final EditText uType = (EditText) findViewById(R.id.u_item_type);


        String description = getIntent().getStringExtra("description");
        if (description != null) {
            uDescription.setText(description);
        }

        if (title != null) {
            update = true;
        }

        int position = getIntent().getIntExtra("position", -1);

        final Button button = findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent data = new Intent();

                String title = uTitle.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(v.getContext(),
                            "Title is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                String type = uType.getText().toString();
                if (type.isEmpty()) {
                    Toast.makeText(v.getContext(),
                            "Type is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                String description = uDescription.getText().toString();
                if (description.isEmpty()) {
                    Toast.makeText(v.getContext(),
                            "Description is empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                data.putExtra("title", title);
                data.putExtra("type", type);
                data.putExtra("description", description);

                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference orph = db.collection("wishlist");

                if (update) {
                    //Delete the query and add the new query to firebase
                    Log.i("INFO", "msg: updating");

                    assert id != null;
                    DocumentReference newDoc = orph.document(id);
                    newDoc.update("name", title);
                    newDoc.update("type", type);
                    newDoc.update("desc", description);
                } else {
                    //Add new query to firebase
                    Map<String, Object> newWishList = new HashMap<>();

                    newWishList.put("name", title);
                    newWishList.put("type", type);
                    newWishList.put("desc", description);
                    newWishList.put("donor", null);
                    newWishList.put("arrived", false);
                    newWishList.put("orphId", orphId);

                    orph.document().set(newWishList);
                }

                Log.i("INFO", "data added");
                Toast.makeText(v.getContext(),
                        "data added/updated", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, data);

                finish();
                onBackPressed();
            }
        });
    }
}
