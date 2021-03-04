package au.edu.sydney.comp5216.orphan.orph.wishlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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
 * Use the {@link au.edu.sydney.comp5216.orphan.orph.wishlist.OrphanWishList#newInstance}
 * factory method to create an instance of this fragment.
 */

/**
 * The OrphanWishList fragment in the OrphanTab class.
 * It initialise the data from firestore
 * and fit OrphanWishListItem by OrphanagesWishListAdapter.
 *
 * @author Yuan Li
 */
public class OrphanWishList extends Fragment{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // the request code
    public final int ADD_WISHLIST_ITEM = 1;

    // the collections of data
    ArrayList<OrphanagesWishListItem> items = new ArrayList<>();

    // the adapter
    OrphanagesWishListAdapter orphanagesWishListAdapter;

    // the stored Strings
    private String mParam1;
    private String mParam2;
    private String orphId;

    public OrphanWishList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment
     * using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     *
     * @return A new instance of fragment UserFinancial.
     */
    // TODO: Rename and change types and number of parameters
    public static OrphanWishList newInstance(String param1, String param2) {
        OrphanWishList fragment = new OrphanWishList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        SharedPreferences gamesettings = this.requireActivity().getSharedPreferences(
                "MyGamePreferences", Context.MODE_PRIVATE);
        orphId = gamesettings.getString("userId", null);

        items = downloadWishListFromCloud();
    }

    /**
     * The activity life cycle function onStart()
     */
    public void onStart() {
        super.onStart();
        SharedPreferences gamesettings = this.requireActivity().getSharedPreferences(
                "MyGamePreferences", Context.MODE_PRIVATE);
        orphId = gamesettings.getString("userId", null);

        items = downloadWishListFromCloud();

        final ListView lv = (ListView) this.requireView().findViewById(R.id.orphanages_wish_list);
        orphanagesWishListAdapter = new OrphanagesWishListAdapter(this.getContext(), items);
        lv.setAdapter(orphanagesWishListAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //sendUpdateMessage(view);
            }
        });

        final Button add = this.requireView().findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMessage(v);
            }
        });
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_orphanages_wishlist, container, false);
    }

    /**
     * The life cycle function onViewCreated()
     * to add listeners to the ListView
     *
     * @param view the generated view
     * @param savedInstanceState the Bundle savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final ListView lv = view.findViewById(R.id.orphanages_wish_list);
        orphanagesWishListAdapter = new OrphanagesWishListAdapter(this.getContext(), items);
        lv.setAdapter(orphanagesWishListAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //sendUpdateMessage(view);
            }
        });

        final Button add = view.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMessage(v);
            }
        });
    }

    /**
     * To download the data from firestore
     *
     * @return the ArrayList<WishListItem> object
     */
    private ArrayList<OrphanagesWishListItem> downloadWishListFromCloud() {
        final ArrayList<OrphanagesWishListItem> res = new ArrayList<>();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference orphDoc = db.collection("wishlist");
        orphDoc
                .whereEqualTo("orphId", orphId)
                .whereEqualTo("arrived", false).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(
                                task.getResult())) {
                            //new OrphanagesWishListItem(id, orphid, name, type, desc, donors)
                            String id = document.getId();
                            Log.i("INFO", "id: " + document.getId());
                            Log.i("INFO", "data: " + document.getData());

                            String name = Objects.requireNonNull(document.get("name")).toString();
                            String type = Objects.requireNonNull(document.get("type")).toString();
                            String desc = Objects.requireNonNull(document.get("desc")).toString();

                            res.add(new OrphanagesWishListItem(id, name, type, desc));

                            Log.i("INFO", "load success");
                        }

                        orphanagesWishListAdapter.notifyDataSetChanged();
                    } else {
                    }
                }
            });
        return res;
    }

    /**
     * To send the intent to AddWishListItemActivity
     *
     * @param view the generated view in this fragment
     */
    public void sendMessage(View view) {
        Intent intent = new Intent(this.getActivity(), AddWishListItemActivity.class);
        intent.putExtra("orphId", orphId);
        startActivityForResult(intent, ADD_WISHLIST_ITEM);
        orphanagesWishListAdapter.notifyDataSetChanged();
    }

}
