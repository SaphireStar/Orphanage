package au.edu.sydney.comp5216.orphan.orph;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import au.edu.sydney.comp5216.orphan.R;

/**
 * A simple {@link Fragment} subclass. Use the {@link OrphanRank#newInstance} factory method to create an instance of this
 * fragment.
 */
public class OrphanRank extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "orphanId";

    // TODO: Rename and change types of parameters
    private String orphanId;

    public OrphanRank() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment UserRank.
     */
    // TODO: Rename and change types and number of parameters
    public static OrphanRank newInstance(String param1) {
        OrphanRank fragment = new OrphanRank();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orphanId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orphan_rank, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ListView mListView = view.findViewById(R.id.listview);
        final LinearLayout rankTitleLayout = view.findViewById(R.id.rank_title);
        final TextView emptyTextView = view.findViewById(R.id.tv_empty);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.e("OrphanRank", "onViewCreated: " + orphanId);
        final List<Map<String, Object>> mData = new ArrayList<>();
        final SimpleAdapter adapter = new SimpleAdapter(
                getContext(),
                mData,
                R.layout.orph_rank_item,
                new String[]{"donateUserName", "donateTime", "donateNumber"},
                new int[]{R.id.tv_name, R.id.tv_donate_date, R.id.tv_money});
        db.collection("userFinancialDonate")
                .whereEqualTo("orphanId", orphanId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                String newTime = parseTime(data.get("donateTime").toString());
                                data.put("donateTime", newTime);
                                mData.add(data);
                            }
                            if (mData.isEmpty()) {
                                rankTitleLayout.setVisibility(View.GONE);
                                emptyTextView.setVisibility(View.VISIBLE);
                            } else {
                                rankTitleLayout.setVisibility(View.VISIBLE);
                                emptyTextView.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }
                        }

                    }
                });

        // add data to list view
        mListView.setAdapter(adapter);
    }

    private String parseTime(String oldTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmsss", Locale.US);
        SimpleDateFormat newFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        try {
            Date date = format.parse(oldTime);
            return newFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return oldTime;
        }
    }
}