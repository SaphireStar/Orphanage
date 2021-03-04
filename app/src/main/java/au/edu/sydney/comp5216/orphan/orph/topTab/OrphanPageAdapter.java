package au.edu.sydney.comp5216.orphan.orph.topTab;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import au.edu.sydney.comp5216.orphan.orph.OrphanRank;
import au.edu.sydney.comp5216.orphan.orph.financial.OrphanFinancial;
import au.edu.sydney.comp5216.orphan.orph.wishlist.OrphanWishList;

/**
 * This class is used for handle the different fragment. Users can change different fragment by their position.
 *
 * @author Siqi Wu
 */
public class OrphanPageAdapter extends FragmentPagerAdapter {

    /**
     * String fields of attributes in the page adapter.
     * numOfTabs: the number fo tab items on the tab bar
     * currentOrphanId: the intent data received from the activity representing the current chosen orphanage ID
     * */
    private int numOfTabs;
    private String currentOrphanId;

    /**
     * Constructor of the current page adapter with fragment manager, string fields
     * */
    public OrphanPageAdapter(FragmentManager fm, int numOfTabs, String userId) {
        super(fm);
        this.numOfTabs = numOfTabs;
        this.currentOrphanId = userId;
    }

    /**
     * Change the page and return the page view by choosing different items by the item position with certain
     * parameters.
     * @param position: the item position in the top bar fragment
     * @return fragment in different position
     * */
    @Override
    public Fragment getItem(int position) {
        Bundle argu = new Bundle();
        argu.putString("userId", currentOrphanId);
        switch (position) {
            case 0:
                return OrphanFinancial.newInstance(this.currentOrphanId);
            case 1:
                return new OrphanWishList();
            case 2:
                return OrphanRank.newInstance(this.currentOrphanId);
            default:
                return null;
        }
    }

    /**
     * Override method of getting the number of tabs
     * @return numofTabs: the number of tabs
     * */
    @Override
    public int getCount() {
        return numOfTabs;
    }
}
