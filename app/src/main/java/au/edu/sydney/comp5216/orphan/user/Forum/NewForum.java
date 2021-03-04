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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import au.edu.sydney.comp5216.orphan.R;

/**
 * Forum adding class
 */
public class NewForum extends AppCompatActivity {
    private EditText editDescription;
    private String orphId;
    private SharedPreferences gameSettings;
    private FirebaseFirestore db;
    private String userType;

    /**
     * Get required set up information
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_forum);
        db = FirebaseFirestore.getInstance();
        editDescription = findViewById(R.id.edit_forum_description);
        orphId = getIntent().getStringExtra("document");
        gameSettings = getSharedPreferences("MyGamePreferences", MODE_PRIVATE);
        userType = getIntent().getStringExtra("userType");
    }

    /**
     * Create save button on tool bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.new_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Action of the save not in tool bar
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_note) {
            saveNote();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Save forum content to the Firebase database and finish this activity
     */
    private void saveNote() {
        String description = editDescription.getText().toString();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        String userName = gameSettings.getString("userName", null);
        CollectionReference forumRef =
                db.collection("orph").document(orphId).collection("forum");
        forumRef.add(new ForumItem(userName, description, timeStamp, userType));

        finish();
    }
}