package au.edu.sydney.comp5216.orphan.orph;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import au.edu.sydney.comp5216.orphan.orph.financial.OrphanFinancial;
import au.edu.sydney.comp5216.orphan.orph.wishlist.OrphanWishList;

public class OrphanPageAdapter extends FragmentPagerAdapter {

    private int numOfTabs;

    public OrphanPageAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0: return new OrphanFinancial();
            case 1: return new OrphanWishList();
            case 2: return new OrphanRank();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
