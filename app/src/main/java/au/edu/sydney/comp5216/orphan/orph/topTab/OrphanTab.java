package au.edu.sydney.comp5216.orphan.orph.topTab;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import au.edu.sydney.comp5216.orphan.R;

/**
 * This class is used for handle Top Tab once orphanage users click the detailed INFO Page
 *
 * @author Siqi Wu
 */
public class OrphanTab extends AppCompatActivity {

    /** Intent data transferred between pages representing current orphanage's ID*/
    private String currentOrphanaId;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.tab_orphan);

        currentOrphanaId = getIntent().getStringExtra("userId");

        TabLayout orphanTab = findViewById(R.id.tabBar_Orphan);

        /** Initialize tab items listed on the top tab*/
        TabItem tabFinancial = findViewById(R.id.Financial_Tab_Orphan);
        TabItem tabWishList = findViewById(R.id.WishList_Tab_Orphan);
        TabItem tabRank = findViewById(R.id.Rank_Tab_Orphan);

        /** Initialize the viewPager for top bar*/
        final ViewPager viewPager = findViewById(R.id.viewPager_Orphan);

        /** Initialize the page adapter with transferred data received from the last activity*/
        OrphanPageAdapter orphanPageAdapter = new OrphanPageAdapter(getSupportFragmentManager(),
                orphanTab.getTabCount(), currentOrphanaId);

        viewPager.setAdapter(orphanPageAdapter);

        /** Change the tab view when the user click different tabs*/
        orphanTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

    /**
     * Return to the parent activity with transferred data if user click the Back button.
     * @return intent of returning Parent Activity with currentOrphanId as the parameter/extra string field
     * */
    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        if (intent != null) {
            intent.putExtra("userId", currentOrphanaId);
            return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        return intent;
    }
}
