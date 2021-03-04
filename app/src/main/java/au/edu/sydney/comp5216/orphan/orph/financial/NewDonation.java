package au.edu.sydney.comp5216.orphan.orph.financial;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import au.edu.sydney.comp5216.orphan.R;
import au.edu.sydney.comp5216.orphan.orph.topTab.OrphanTab;

/**
 * This class is used for handle the NewDonation page for orphanage users. Users can add new financial donations and the
 * information will be inserted into firebasestore.
 *
 * @author Siqi Wu
 */
public class NewDonation extends AppCompatActivity {
    //widgets in the page
    private ImageButton cancelBtn;
    private EditText donationTitle;
    private EditText donationType;
    private EditText donationDescription;
    private EditText donationUsage;
    private EditText donationGoal;

    //String data of received intent extra field representing the current selected orphanage ID
    private String currentOrphanageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_donation);

        //Initialize the widgets
        cancelBtn = findViewById(R.id.cancelBtn);
        donationTitle = findViewById(R.id.donationTitle);
        donationType = findViewById(R.id.donationType);
        donationDescription = findViewById(R.id.addDescription);
        donationUsage = findViewById(R.id.addUsage);
        donationGoal = findViewById(R.id.editGoal);

        //receive the data from the pass intent string extra field
        currentOrphanageId = getIntent().getStringExtra("userId");
        //donationTitle.setText(getIntent().getStringExtra("userId"));
    }

    /**
     * If the cancel button is clicked, it will call this method to back to the previous activity
     *
     * @param v the view handler of the current page
     */
    public void onCancelNewDonation(View v) {
        Intent intent = new Intent(this, OrphanTab.class);
        intent.putExtra("userId", currentOrphanageId);
        startActivity(intent);
    }

    /**
     * If the submit button is clicked, it will call this method to upload the donation information to the firebase
     * firestore. If there are some blacks in the input boxes, then prompt information to the users
     *
     * @param v the view handler of the current page
     */
    public void onSumitNewDonation(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewDonation.this);
        builder.setTitle("Some inputs are missing!");
        builder.setCancelable(true);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();

        FirebaseFirestore db;
        Map<String, Object> financialDonation;

        // create connection to Firestore
        db = FirebaseFirestore.getInstance();
        //Create a new Donation with specific info
        financialDonation = new HashMap<>();
        financialDonation.put("id", currentOrphanageId);

        //check if there is any blank in the input box
        if (donationTitle.getText() != null && donationTitle.getText().length() != 0) {
            financialDonation.put("title", donationTitle.getText().toString());
        } else {
            alert.show();
        }
        if (donationType.getText() != null && donationType.getText().length() != 0) {
            financialDonation.put("type", donationType.getText().toString());
        } else {
            alert.show();
        }
        if (donationDescription.getText() != null && donationDescription.getText().length() != 0) {
            financialDonation.put("description", donationDescription.getText().toString());
        } else {
            alert.show();
        }
        if (donationUsage.getText() != null && donationUsage.getText().length() != 0) {
            financialDonation.put("usage", donationUsage.getText().toString());
        } else {
            alert.show();
        }
        if (donationGoal.getText() != null && donationGoal.getText().length() != 0) {
            financialDonation.put("goal", donationGoal.getText().toString());
        } else {
            alert.show();
        }

        financialDonation.put("donorsNumber", "0");
        financialDonation.put("currentNumber", "0");

        if (financialDonation.size() == 8) {
            db.collection("orphanFinancialDonation").add(financialDonation).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(NewDonation.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                    Intent returnIntent = new Intent(NewDonation.this, OrphanTab.class);
                    returnIntent.putExtra("userId", currentOrphanageId);
                    startActivity(returnIntent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(NewDonation.this, "Failed to upload new donation.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}

