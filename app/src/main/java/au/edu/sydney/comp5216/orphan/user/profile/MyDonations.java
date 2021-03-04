/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.edu.sydney.comp5216.orphan.user.profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.firestore.DocumentChange;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;

import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import au.edu.sydney.comp5216.orphan.R;

/**
 * Display user's gift donation status
 */
public class MyDonations extends AppCompatActivity {

    private static final String TAG = "MyDonations";

    private ListView mListView;
    private MyDonationAdapter mAdapter;
    private ArrayList<MyDonationItem> mItems;
    private FirebaseFirestore mFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_donations);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mFirestore = FirebaseFirestore.getInstance();
        mItems = new ArrayList<>();
        mAdapter = new MyDonationAdapter(this, mItems);
        mListView = findViewById(R.id.my_donation_list_view);

        initFirestore();

    }

    /**
     * Back button selected action to finish this activity
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /**
     * Get data from Firestore database, this will load cache first.
     * if there is any update in server then load from server.
     */
    private void initFirestore() {
        SharedPreferences gameSettings = getSharedPreferences("MyGamePreferences", MODE_PRIVATE);
        String userName = gameSettings.getString("userId", null);

        mFirestore.collection("wishlist").whereEqualTo("donor", userName)
                .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen error", e);
                            return;
                        }

                        for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                            if (change.getType() == DocumentChange.Type.ADDED) {

                                mItems.add(new MyDonationItem((String) change.getDocument().getData().get("name"),
                                        (Boolean) change.getDocument().getData().get("arrived")));
                                Log.d(TAG, "New city:" + change.getDocument().getData());

                            }

                            String source = querySnapshot.getMetadata().isFromCache() ?
                                    "local cache" : "server";
                            Log.d(TAG, "Data fetched from " + source);
                        }
                        mListView.setAdapter(mAdapter);

                    }
                });
    }
}