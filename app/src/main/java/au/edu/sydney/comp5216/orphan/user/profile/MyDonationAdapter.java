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

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import au.edu.sydney.comp5216.orphan.R;

/**
 * A list view adapter to display donation content
 */
public class MyDonationAdapter extends ArrayAdapter<MyDonationItem> {
    public MyDonationAdapter(Context context, ArrayList<MyDonationItem> donationItems) {
        super(context, 0, donationItems);
    }

    /**
     * get each row in list view and set them up
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyDonationItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_donation_layout, parent,
                    false);
        }

        TextView tvDonationName = convertView.findViewById(R.id.my_donation_name);
        ImageView tvStatus = convertView.findViewById(R.id.donation_progress);

        tvDonationName.setText(item.getDonationName());

        if (item.isArrive()) {
            tvStatus.setImageResource(R.drawable.ic_baseline_check_circle_24);
            tvStatus.setColorFilter(Color.GREEN);
        } else {
            tvStatus.setImageResource(R.drawable.ic_baseline_airport_shuttle_24);
            tvStatus.setColorFilter(Color.RED);
        }

        return convertView;
    }
}
