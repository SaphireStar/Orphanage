package au.edu.sydney.comp5216.orphan.user;

import android.content.Context;

import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import au.edu.sydney.comp5216.orphan.R;
import au.edu.sydney.comp5216.orphan.StorageLocation;
import au.edu.sydney.comp5216.orphan.user.Forum.Forum;
import au.edu.sydney.comp5216.orphan.user.topTab.UserTab;

/**
 * Display the information of an orphanage for donor
 * @Author: Yiqing Yang
 */
public class UserOrphProfile extends AppCompatActivity {
    private ImageView imageView;

    // user id
    private String userId;

    // orphanage information
    private String id;
    private String name;
    private String address;
    private String description;
    private String image;
    private String orphId;

    // widgets
    private TextView nameTv;
    private TextView addressTv;
    private TextView descriptionTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orph_profile_user);

        // receive messages
        userId = getIntent().getStringExtra("userId");
        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        address = getIntent().getStringExtra("address");
        description = getIntent().getStringExtra("description");
        image = getIntent().getStringExtra("image");

        orphId = getIntent().getStringExtra("document");
        // set text
        nameTv = (TextView) findViewById(R.id.name);
        addressTv = (TextView) findViewById(R.id.address);
        descriptionTv = (TextView) findViewById(R.id.description);
        nameTv.setText(name);
        addressTv.setText(address);
        descriptionTv.setText(description);

        // set image
        imageView = (findViewById(R.id.imageView));

        File f = new File(StorageLocation.storagePath + id +
                image.substring(image.lastIndexOf('.')));
        imageView.setImageURI(Uri.fromFile(f));

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference islandRef = storageRef.child(image);

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    public void onClickDonate(View v) {
        Intent intent = new Intent(this, UserTab.class);

        intent.putExtra("orphanId", id);
        intent.putExtra("orphanName", name);

        startActivity(intent);
    }

    public void onClickForum(View v) {
        if(checkNetworkConnectivity()){
            Intent intent = new Intent(this, Forum.class);
            intent.putExtra("document", orphId);
            startActivity(intent);
        }else{
            Toast.makeText(UserOrphProfile.this,
                    "Please make sure you are in network connection!",
                    Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * The function to check if the device is connected to the network because only
     * when connecting to the network,can the uploading/synchronization actions can be executed
     *
     * @return isConnected a boolean value to represent if the device is connected to the network.
     * If true, then connected, if false, then not connected.
     * */
    private Boolean checkNetworkConnectivity() {
        /** Initializes a connectivity manager to check the network information*/
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
