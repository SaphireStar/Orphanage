package au.edu.sydney.comp5216.orphan.login;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import au.edu.sydney.comp5216.orphan.R;
import au.edu.sydney.comp5216.orphan.user.Orphanage;

/**
 * Orphanages can register their orphanages here and upload an image
 * @Author: Yiqing Yang
 */
public class OrphanageRegisterActivity extends AppCompatActivity {
    // widgets
    private EditText nameEv;
    private EditText addressEv;
    private EditText descriptionEv;

    // user information
    private String userId;
    private String userEmail;
    private String image ="orph_images/test_image.png";

    // request code
    final int REQUEST_CODE_LOCAL_GALLERY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orph_register_activity);

        // initialize the widgets
        nameEv = (EditText) findViewById(R.id.nameE);
        addressEv = (EditText) findViewById(R.id.addressE);
        descriptionEv = (EditText) findViewById(R.id.descriptionE);

        // receive messages from the previous activity
        userId = getIntent().getStringExtra("userId");
        userEmail=getIntent().getStringExtra("email");
        Log.i("received userId" , userId);
        Log.i("received  userEmail" , userEmail);
    }

    /**
     * Uploads the orphanage information to firebase
     */
    public void onSubmitClickEvent(View v){
        if(image.equals("orph_images/test_image.png")){
            return;
        }
        // define the orphanage to add
        final Orphanage orphanage = new Orphanage();
        orphanage.setId(userId);
        orphanage.setName(nameEv.getText().toString());
        orphanage.setAddress(addressEv.getText().toString());
        orphanage.setDescription(descriptionEv.getText().toString());
        orphanage.setImage(image);

        // connect to the database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // update login table to identify the user as an orphanage owner
        Map<String, Object> user = new HashMap<>();
        user.put("isOrph", "true");
        user.put("email", userEmail);

        db.collection("login").document(userId)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

        // update orph table to add the new orphanage
        db.collection("orph").document()
                .set(orphanage.toFireBaseFormat())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // if successful, fetch the orphanage id which is the document id
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("orph")
                                .whereEqualTo("name", orphanage.getName())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document :
                                                    task.getResult()) {
                                                // return to the previous activity
                                                Intent data = new Intent();
                                                setResult(RESULT_OK, data);
                                                finish();
                                            }
                                        } else {
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    /**
     * Select an image from the local gallery
     */
    public void onUploadClickEvent(View v){
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_LOCAL_GALLERY);
    }

    /**
     * Receive messages from Mediastore
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_LOCAL_GALLERY) {
                // get the uri of the selected image
                Uri uri = data.getData();

                // get file suffix
                Cursor cursor = getContentResolver().query(uri,null,null,
                        null, null);
                cursor.moveToFirst();
                //String filename = cursor.getString(1);
                //String suffix = filename.substring(filename.lastIndexOf('.'));
                String suffix = ".jpg";

                // upload the image to firebase
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference ref = storage.getReference().child("orph_images/"+userId + suffix);
                image = "orph_images/"+userId + suffix;

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                            OrphanageRegisterActivity.this.getContentResolver(), uri);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
                    byte[] compressedData = baos.toByteArray();
                    UploadTask uploadTask = ref.putBytes(compressedData);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.i("MainActivity.upload", "Upload Failed");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.i("MainActivity.upload", "Upload Successfully");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // initialize the imageView to display the image
                ImageView ig = (ImageView) findViewById(R.id.imageView);
                // get the bitmap of the selected image
                ContentResolver cr = this.getContentResolver();
                try {
                    // get the bitmap
                    Bitmap bmp = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    // set the imageView
                    ig.setImageBitmap(bmp);

                    // hide the upload button
                    Button btn = (Button) findViewById(R.id.uploadImage);
                    btn.setVisibility(View.GONE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
