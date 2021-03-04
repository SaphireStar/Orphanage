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

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;

import java.io.InputStreamReader;

import au.edu.sydney.comp5216.orphan.R;

/**
 * Display user's current financial amount
 */
public class UserGoal extends AppCompatActivity {
    TextView money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_goal);
        money = findViewById(R.id.current_goal_value);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        money.setText("$" + currentDonation());
    }

    /**
     * load cache which save user's donation information and sum them up
     * @return current donation amount
     */
    public int currentDonation() {
        SharedPreferences gamesettings = this.getSharedPreferences("MyGamePreferences", Context.MODE_PRIVATE);
        String userName = gamesettings.getString("userName", "Anonymous");
        File file = new File(this.getCacheDir(), userName + ".usr");
        FileInputStream is;
        BufferedReader reader;
        int currentSum = 0;
        if (file.exists()) {
            try {
                is = new FileInputStream(file);
                reader = new BufferedReader(new InputStreamReader(is));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    String[] separated = line.split(":");
                    currentSum += Integer.parseInt(separated[3]);
                    ;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return currentSum;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}