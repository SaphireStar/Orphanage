package au.edu.sydney.comp5216.orphan.user;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import au.edu.sydney.comp5216.orphan.user.financial.UserFinancial;
import au.edu.sydney.comp5216.orphan.user.wishlist.UserWishList;

public class UserPageAdapter extends FragmentPagerAdapter {

    private int numOfTabs;

    public UserPageAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0: return new UserFinancial();
            case 1: return new UserWishList();
            case 2: return new UserRank();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
