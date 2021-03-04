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

public class MyDonationItem {
    private String donationName;
    private boolean isArrive;

    public MyDonationItem(String donationName, Boolean isArrive) {
        this.donationName = donationName;
        this.isArrive = isArrive;
    }

    public String getDonationName() {
        return donationName;
    }

    public boolean isArrive() {
        return isArrive;
    }

}
