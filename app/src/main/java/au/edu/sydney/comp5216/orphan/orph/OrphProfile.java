package au.edu.sydney.comp5216.orphan.orph;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import au.edu.sydney.comp5216.orphan.R;
import au.edu.sydney.comp5216.orphan.StorageLocation;
import au.edu.sydney.comp5216.orphan.orph.topTab.OrphanTab;
import au.edu.sydney.comp5216.orphan.user.Forum.Forum;
import au.edu.sydney.comp5216.orphan.user.Orphanage;

/**
 * Displays the information of an orphanage and allow owners to update
 * @Author: Yiqing Yang
 */
public class OrphProfile extends AppCompatActivity {
    private ImageView imageView;

    // icons
    private ImageView changeName;
    private ImageView changeAddress;
    private ImageView changeDescription;

    // textView
    private TextView nameTv;
    private TextView addressTv;
    private TextView descriptionTv;

    // editView
    private EditText nameEv;
    private EditText addressEv;
    private EditText descriptionEv;

    // button
    private Button changeBtn;
    private Button infoBtn;

    // orphanage object
    private Orphanage orphanage;
    private String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orph_profile_orph);

        // receive messages
        String userId = getIntent().getStringExtra("userId");

        // widgets
        changeName = (ImageView) findViewById(R.id.changeName);
        changeAddress = (ImageView) findViewById(R.id.changeAddress);
        changeDescription = (ImageView) findViewById(R.id.changeDescription);

        nameTv = (TextView) findViewById(R.id.name);
        addressTv = (TextView) findViewById(R.id.address);
        descriptionTv = (TextView) findViewById(R.id.description);

        nameEv = (EditText) findViewById(R.id.nameE);
        addressEv = (EditText) findViewById(R.id.addressE);
        descriptionEv = (EditText) findViewById(R.id.descriptionE);

        changeBtn = (Button) findViewById(R.id.change);

        // initialize an orphanage object
        orphanage = new Orphanage();

        // fetch the information of the orphanage
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orph").whereEqualTo("id", userId).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // store the information
                        documentId = document.getId();
                        orphanage.setId(document.get("id").toString());
                        orphanage.setName(document.get("name").toString());
                        orphanage.setAddress(document.get("address").toString());
                        orphanage.setDescription(document.get("description").toString());
                        orphanage.setImage(document.get("image").toString());
                        orphanage.setDocumentId(document.getId());

                        // set the default text
                        nameTv.setText(orphanage.getName());
                        addressTv.setText(orphanage.getAddress());
                        descriptionTv.setText(orphanage.getDescription());

                        // fetch and setup the image
                        imageView = (findViewById(R.id.imageView));
                        File f = new File(StorageLocation.storagePath + orphanage.getId()
                                + orphanage.getImage().substring(
                                        orphanage.getImage().lastIndexOf('.')));
                        if (f.exists()) {
                            imageView.setImageURI(Uri.fromFile(f));
                        } else {
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();
                            StorageReference islandRef = storageRef.child(orphanage.getImage());

                            final long ONE_MEGABYTE = 1024 * 1024;
                            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(
                                    new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    imageView.setImageBitmap(BitmapFactory
                                            .decodeByteArray(bytes, 0, bytes.length));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    /**
     * Converts the TextView to EditView to allow users change the information
     */
    public void onChangeNameClick(View view) {
        // set visibility
        changeName.setVisibility(View.INVISIBLE);
        nameTv.setVisibility(View.INVISIBLE);
        nameEv.setVisibility(View.VISIBLE);
        changeBtn.setVisibility(View.VISIBLE);

        // set the text
        nameEv.setText(nameTv.getText());

        nameEv.setText(nameTv.getText());
    }

    /**
     * Converts the TextView to EditView to allow users change the information
     */
    public void onChangeAddressClick(View view) {
        // set visibility
        changeAddress.setVisibility(View.INVISIBLE);
        addressTv.setVisibility(View.INVISIBLE);
        addressEv.setVisibility(View.VISIBLE);
        changeBtn.setVisibility(View.VISIBLE);

        // set the text
        addressEv.setText(addressTv.getText());
    }

    /**
     * Converts the TextView to EditView to allow users change the information
     */
    public void onChangeDescriptionClick(View view) {
        // set visibility
        changeDescription.setVisibility(View.INVISIBLE);
        descriptionTv.setVisibility(View.INVISIBLE);
        descriptionEv.setVisibility(View.VISIBLE);
        changeBtn.setVisibility(View.VISIBLE);

        // set the text
        descriptionEv.setText(descriptionTv.getText());
    }

    /**
     * Saves the change on the information
     */
    public void onSaveClick(View view) {
        if (nameTv.getVisibility() == View.INVISIBLE) {
            // update text
            nameTv.setText(nameEv.getText());
            orphanage.setName(nameEv.getText().toString());
            // set visibility
            changeName.setVisibility(View.VISIBLE);
            nameTv.setVisibility(View.VISIBLE);
            nameEv.setVisibility(View.INVISIBLE);
        }
        if (addressTv.getVisibility() == View.INVISIBLE) {
            // update text
            addressTv.setText(addressEv.getText());
            orphanage.setAddress(addressEv.getText().toString());
            // set visibility
            changeAddress.setVisibility(View.VISIBLE);
            addressTv.setVisibility(View.VISIBLE);
            addressEv.setVisibility(View.INVISIBLE);
        }
        if (descriptionTv.getVisibility() == View.INVISIBLE) {
            // update text
            descriptionTv.setText(descriptionEv.getText());
            orphanage.setDescription(descriptionEv.getText().toString());
            // set visibility
            changeDescription.setVisibility(View.VISIBLE);
            descriptionTv.setVisibility(View.VISIBLE);
            descriptionEv.setVisibility(View.INVISIBLE);
        }

        // hide button
        changeBtn.setVisibility(View.INVISIBLE);

        // connect to the database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // update orph table to add the new orphanage
        db.collection("orph").document(documentId).set(
                orphanage.toFireBaseFormat()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    public void onInfoClick(View v) {
        Intent receive = getIntent();
        Bundle extras = receive.getExtras();
        String userId = "";
        if (extras != null) {
            userId = extras.getString("userId");
        }
        Intent intent = new Intent(this, OrphanTab.class);
        intent.putExtra("userId", userId);

        startActivity(intent);
    }

    public void onForumClick(View v) {
        Intent intent = new Intent(this, Forum.class);
        intent.putExtra("document", documentId);
        intent.putExtra("userType", "Manager");
        startActivity(intent);
    }
}
