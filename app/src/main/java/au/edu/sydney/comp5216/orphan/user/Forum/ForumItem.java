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

public class ForumItem {
    private String userName;
    private String description;
    private String isOrph;
    private String timeStamp;

    /**
     * Save display information for each row in recycler view
     * @param userName user name
     * @param description forum content
     * @param timeStamp time created a forum
     * @param isOrph is the publisher orphanage manager or not
     */
    public ForumItem(String userName, String description, String timeStamp, String isOrph) {
        this.userName = userName;
        this.description = description;
        this.timeStamp = timeStamp;
        this.isOrph = isOrph;
    }

    public ForumItem() {
    }

    public String getUserName() {
        return userName;
    }

    public String getDescription() {
        return description;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getIsOrph() {
        return isOrph;
    }
}
