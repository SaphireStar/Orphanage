package au.edu.sydney.comp5216.orphan.user.wishlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import au.edu.sydney.comp5216.orphan.R;

/**
 * the customised adapter WishListAdapter for UserWishList
 */
public class WishListAdapter extends BaseAdapter {
    // the collections of data
    private ArrayList<WishListItem> items;

    // the layout inflater
    private LayoutInflater layoutInflater;

    // the request code
    public final int DELIVER_CODE = 333;

    /**
     * the constructor
     * @param context the current context
     * @param items the ArrayList<OrphanagesWishListItem> object
     */
    public WishListAdapter(Context context, ArrayList<WishListItem> items) {
        this.items = items;
        layoutInflater = LayoutInflater.from(context);
    }

    /**
     * count how many items in the adapter
     * @return the size of the ArrayList
     */
    @Override
    public int getCount() {
        return items.size();
    }

    /**
     * get the item in specified position
     * @param position the specified position
     * @return the object fetched
     */
    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    /**
     * get the position of item in specified position
     * @param position the specified position
     * @return the item position in the adapter
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * get the view, bind the ViewHolder and onClickListener
     *
     * @param position the specified position
     * @param convertView the convertView
     * @param parent the parent ViewGroup
     * @return the modified/created convertView
     */
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.wish_list_view, null);
            holder = new ViewHolder();
            holder.uName = (TextView) convertView.findViewById(R.id.name);
            holder.uDescription = (TextView) convertView.findViewById(R.id.description);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Button deliverView = (Button) convertView.findViewById(R.id.deliver_gift);
        deliverView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                WishListItem currItem = items.get(position);
                Intent i = new Intent(parent.getContext(), DeliveryActivity.class);
                i.putExtra("id", currItem.getId());

                ((Activity) v.getContext()).startActivityForResult(i, DELIVER_CODE);
            }
        });

        holder.uName.setText(items.get(position).getName());
        holder.uDescription.setText(items.get(position).getDescription());
        return convertView;
    }

    /**
     * the class ViewHolder to store all the View needed to bind
     */
    static class ViewHolder {
        TextView uName;
        TextView uDescription;
    }
}
