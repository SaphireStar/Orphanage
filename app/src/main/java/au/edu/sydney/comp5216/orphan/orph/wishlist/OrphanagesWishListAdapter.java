package au.edu.sydney.comp5216.orphan.orph.wishlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import au.edu.sydney.comp5216.orphan.R;

/**
 * the customised adapter OrphanagesWishListAdapter for OrphanWishList
 */
public class OrphanagesWishListAdapter extends BaseAdapter {
    // the collections of data
    private ArrayList<OrphanagesWishListItem> items;

    // the layout inflater
    private LayoutInflater layoutInflater;

    // the request code
    public final int EDIT_ITEM_REQUEST_CODE = 111;

    // the firestore reference
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference wishlist = db.collection("wishlist");

    /**
     * the constructor
     * @param context the current context
     * @param items the ArrayList<OrphanagesWishListItem> object
     */
    public OrphanagesWishListAdapter(Context context, ArrayList<OrphanagesWishListItem> items) {
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
     * delete the item in specified position
     * @param pos the specified position
     */
    public void deleteItem(int pos) {
        Log.i("INFO", "delete " + pos);

        items.remove(pos);
        this.notifyDataSetChanged();
    }

    /**
     * notify the changes
     */
    public void notifyChange() {
        this.notifyDataSetChanged();
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
        final OrphanagesWishListAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.orphanages_wish_list_view, null);
            holder = new OrphanagesWishListAdapter.ViewHolder();
            holder.uName = (TextView) convertView.findViewById(R.id.name);
            holder.uDescription = (TextView) convertView.findViewById(R.id.description);
            holder.update = (Button) convertView.findViewById(R.id.update);
            holder.close = (Button) convertView.findViewById(R.id.close);
            convertView.setTag(holder);

            holder.update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OrphanagesWishListItem updateItem = (OrphanagesWishListItem) items.get(position);

                    Intent intent = new Intent(v.getContext(), AddWishListItemActivity.class);
                    intent.putExtra("id", updateItem.getId());
                    intent.putExtra("name", updateItem.getName());
                    intent.putExtra("description", updateItem.getDescription());
                    intent.putExtra("position", position);

                    ((Activity) v.getContext()).startActivityForResult(intent, EDIT_ITEM_REQUEST_CODE);
                    Toast.makeText(v.getContext(),
                            "Wish list item ready to update", Toast.LENGTH_SHORT).show();
                }
            });

            holder.close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OrphanagesWishListItem delItem = (OrphanagesWishListItem) items.get(position);
                    wishlist.document(delItem.getId()).update("arrived", true);
                    deleteItem(position);

                    Toast.makeText(v.getContext(),
                            "Wish list item closed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            holder = (OrphanagesWishListAdapter.ViewHolder) convertView.getTag();
        }

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
        Button update;
        Button close;
    }
}
