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

package au.edu.sydney.comp5216.orphan.user.Forum;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import au.edu.sydney.comp5216.orphan.R;

/**
 * Load forum message directly from Firebase database to recycle view
 */
public class Forum extends AppCompatActivity {
    private ForumAdapter mAdapter;
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private CollectionReference mForumRef;
    private String mOrphanageId;
    private String mUserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_forum);

        Bundle extras = getIntent().getExtras();
        mUserType = extras.getString("userType");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mOrphanageId = getIntent().getStringExtra("document");
        mForumRef = mDb.collection("orph").document(mOrphanageId).collection("forum");
        FloatingActionButton fab = findViewById(R.id.forum_add_button);
        setUpRecyclerView();
        RecyclerView recyclerView = findViewById(R.id.forum_recycler_view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Forum.this, NewForum.class);
                intent.putExtra("document", mOrphanageId);
                intent.putExtra("userType", mUserType);
                startActivity(intent);
            }
        });
    }

    /**
     * initialize data from database into a recycle view adapter
     */
    private void setUpRecyclerView() {
        Query query = mForumRef.orderBy("timeStamp", Query.Direction.DESCENDING).limit(20);

        FirestoreRecyclerOptions<ForumItem> options = new FirestoreRecyclerOptions.Builder<ForumItem>()
                .setQuery(query, ForumItem.class)
                .build();
        mAdapter = new ForumAdapter(options);
    }

    /**
     * Back button to previous activity in tool bar
     * @param item back button
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
     * listen data change in recycle view
     */
    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    /**
     * stop listen data change in recycle view
     */
    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}
