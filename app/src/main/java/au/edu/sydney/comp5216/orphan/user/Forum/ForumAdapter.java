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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import au.edu.sydney.comp5216.orphan.R;

/**
 * RecyclerView adapter to attach downloaded information to every widget in each row.
 */
public class ForumAdapter extends FirestoreRecyclerAdapter<ForumItem, ForumAdapter.ForumHolder> {
    public ForumAdapter(@NonNull FirestoreRecyclerOptions<ForumItem> options) {
        super(options);
    }
    @Override
    protected void onBindViewHolder(@NonNull ForumHolder holder, int position, @NonNull ForumItem model) {
        holder.userName.setText(model.getUserName());
        holder.forumDescription.setText(model.getDescription());
        holder.timeStamp.setText(model.getTimeStamp());
        holder.userType.setText(model.getIsOrph());
    }

    /**
     * Return a bundled widget according to xml layout
     */
    @NonNull
    @Override
    public ForumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_layout, parent, false);
        return new ForumHolder(v);
    }

    /**
     * Bundle each widget id together
     */
    class ForumHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView forumDescription;
        TextView timeStamp;
        TextView userType;

        public ForumHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            forumDescription = itemView.findViewById(R.id.forum_description);
            timeStamp = itemView.findViewById(R.id.time_stamp);
            userType = itemView.findViewById(R.id.user_type);
        }
    }
}
