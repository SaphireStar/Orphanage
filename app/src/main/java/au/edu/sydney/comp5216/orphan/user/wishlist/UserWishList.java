package au.edu.sydney.comp5216.orphan.user.wishlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import au.edu.sydney.comp5216.orphan.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link au.edu.sydney.comp5216.orphan.user.wishlist.UserWishList#newInstance}
 * factory method to create an instance of this fragment.
 */

/**
 * The UserWishList fragment in the UserTab class.
 * It initialise the data from firestore
 * and fit WishListItem by WishListAdapter.
 *
 * @author Yuan Li
 */
public class UserWishList extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // the collections of data
    ArrayList<WishListItem> items = new ArrayList<>();

    // the adapter
    WishListAdapter wishListAdapter;

    // the request code
    public final int DELIVER_ITEM = 888;

    // the stored Strings
    private String mParam1;
    private String userId;

    public UserWishList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment
     * using the provided parameters.
     *
     * @param param1 Parameter 1.
     *
     * @return A new instance of fragment UserFinancial.
     */
    public static UserWishList newInstance(String param1) {
        UserWishList fragment = new UserWishList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * The activity life cycle function onStart()
     */
    public void onStart() {
        super.onStart();
        SharedPreferences gamesettings = this.requireActivity()
                .getSharedPreferences("MyGamePreferences", Context.MODE_PRIVATE);
        userId = gamesettings.getString("userId", getActivity().getIntent().
                getStringExtra("userId"));
        Log.i("INFO", "userId: " + userId);
        Log.i("INFO", "currentOrphId: " + mParam1);

        items = downloadWishListFromCloud();

        final ListView lv = (ListView) this.requireView().findViewById(R.id.wish_list);
        wishListAdapter = new WishListAdapter(this.getContext(), items);
        lv.setAdapter(wishListAdapter);
    }

    /**
     * The life cycle function onCreate()
     * to get the arguments passed from the parent activity
     *
     * @param savedInstanceState the bundle savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            Log.i("INFO", "currentOrphId: " + mParam1);
        }
    }

    /**
     * The life cycle function onCreateView()
     * to create the view fitted in this fragment
     * @param inflater the LayoutInflater inflater
     * @param container the ViewGroup container
     * @param savedInstanceState the Bundle savedInstanceState
     *
     * @return the generated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences gamesettings = this.requireActivity().
                getSharedPreferences("MyGamePreferences", Context.MODE_PRIVATE);
        userId = gamesettings.getString("userId", getActivity().
                getIntent().getStringExtra("userId"));
        Log.i("INFO", "userId: " + userId);
        Log.i("INFO", "currentOrphId: " + mParam1);

        items = downloadWishListFromCloud();

        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.activity_wishlist, container, false);
        final ListView lv = (ListView) view.findViewById(R.id.wish_list);
        wishListAdapter = new WishListAdapter(this.getContext(), items);
        lv.setAdapter(wishListAdapter);

        return view;

    }

    /**
     * To download the data from firestore
     *
     * @return the ArrayList<WishListItem> object
     */
    private ArrayList<WishListItem> downloadWishListFromCloud() {
        final ArrayList<WishListItem> res = new ArrayList<>();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference orphDoc = db.collection("wishlist");
        orphDoc.whereEqualTo("orphId", mParam1).whereEqualTo("donor", null).
                whereEqualTo("arrived", false).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects
                                    .requireNonNull(task.getResult())) {
                                //new WishListItem(id, name, type, desc, donor, delivered)
                                String id = document.getId();
                                Log.i("INFO", "id: " + document.getId());
                                Log.i("INFO", "data: " + document.getData());

                                String name = Objects.requireNonNull(document.get("name"))
                                        .toString();
                                String desc = Objects.requireNonNull(document.get("desc"))
                                        .toString();
                                res.add(new WishListItem(id, name, desc));

                                Log.i("INFO", "load success");
                            }
                            wishListAdapter.notifyDataSetChanged();
                        } else {
                        }
                    }
                });
        return res;
    }
}
