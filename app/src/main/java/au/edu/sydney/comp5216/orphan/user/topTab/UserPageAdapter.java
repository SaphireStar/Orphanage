package au.edu.sydney.comp5216.orphan.user.topTab;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import au.edu.sydney.comp5216.orphan.user.UserRank;
import au.edu.sydney.comp5216.orphan.user.financial.UserFinancial;
import au.edu.sydney.comp5216.orphan.user.wishlist.UserWishList;

/**
 * This class is used for handle the different fragment. Users can change different fragment by their position.
 *
 * @author Siqi Wu
 */
public class UserPageAdapter extends FragmentPagerAdapter {

    /**
     * String fields of attributes in the page adapter.
     * numOfTabs: the number fo tab items on the tab bar
     * currentOrphanId: the intent data received from the activity representing the current chosen orphanage ID
     * currentOrphanName: the intent data received from the activity representing the current chosen orphanage's name
     * */
    private int numOfTabs;
    private String currentOrphanId;
    private String currentOrphanName;

    /**
     * Constructor of the current page adapter with fragment manager, string fields
     * */
    public UserPageAdapter(FragmentManager fm, int numOfTabs, String orphanId, String currentOrphanName) {
        super(fm);
        this.numOfTabs = numOfTabs;
        this.currentOrphanId = orphanId;
        this.currentOrphanName = currentOrphanName;
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
                return UserFinancial.newInstance(this.currentOrphanId, currentOrphanName);
            case 1:
                return UserWishList.newInstance(this.currentOrphanId);
            case 2:
                return UserRank.newInstance(this.currentOrphanId);
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
