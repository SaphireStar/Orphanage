package au.edu.sydney.comp5216.orphan.user.topTab;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import au.edu.sydney.comp5216.orphan.R;

/**
 * This class is used for handle Top Tab once normal users click the specific orphanage in the orphanage list
 *
 * @author Siqi Wu
 */
public class UserTab extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.tab_user);

        /** Intent data transferred between pages representing selected orphanage's ID and orphanage's name*/
        String currentOrphanaId = getIntent().getStringExtra("orphanId");
        String currentOrphanName = getIntent().getStringExtra("orphanName");

        TabLayout userTab = findViewById(R.id.tabBar_User);

        /** Initialize tab items listed on the top tab*/
        TabItem tabFinancial = findViewById(R.id.Financial_Tab_User);
        TabItem tabWishList = findViewById(R.id.WishList_Tab_User);
        TabItem tabRank = findViewById(R.id.Rank_Tab_User);

        /** Initialize the viewPager for top bar*/
        final ViewPager viewPager = findViewById(R.id.viewPager_User);

        /** Initialize the page adapter with transferred data received from the last activity*/
        UserPageAdapter userPageAdapter = new UserPageAdapter(getSupportFragmentManager(), userTab.getTabCount(),
                currentOrphanaId, currentOrphanName);

        viewPager.setAdapter(userPageAdapter);

        /** Change the tab view when the user click different tabs*/
        userTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
