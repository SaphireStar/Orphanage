package au.edu.sydney.comp5216.orphan.user.financial;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import au.edu.sydney.comp5216.orphan.R;

/**
 * A simple {@link Fragment} subclass. Use the {@link UserFinancial#newInstance} factory method to create an instance of
 * this fragment.
 */

/**
 * This class is used for UserFinancial Tab Fragment in the Top Tab. Users can make donations to the current orphanage.
 *
 * @author Siqi Wu
 */
public class UserFinancial extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * widgets in the orphan financial fragment
     */
    private LinearLayout layout1;
    private LinearLayout layout2;
    private Button donateBtn;
    private TextView descriptionTitle;
    private TextView description;
    private TextView donorsNumber;
    private TextView currentNumber;
    private TextView goalNumber;
    private TextView financialUsage;
    private ProgressBar progressBar;
    private TextView emptyNotice;

    //instance of firebaseFirestore
    private FirebaseFirestore db;

    //id representing the current document id
    private String id;

    public UserFinancial() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param param1 Parameter 1 representing the current orphanage ID
     * //@param param2 Parameter 2 representing the current orphanage name.
     *
     * @return A new instance of fragment UserFinancial.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFinancial newInstance(String param1, String param2) {
        UserFinancial fragment = new UserFinancial();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    /**
     * Initialize the User Financial Fragment's view with default parameters
     *
     * @param inflater the current fragment's layout inflater
     * @param container the viewGroup in this fragment
     * @param savedInstanceState default parameters
     *
     * @return view the current view after initializing the components in the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_financial, container, false);

        /** Initialize the widgets in this fragment*/
        donateBtn = view.findViewById(R.id.financial_btn);
        descriptionTitle = view.findViewById(R.id.financial_title);
        description = view.findViewById(R.id.financial_description);
        donorsNumber = view.findViewById(R.id.donors_number);
        currentNumber = view.findViewById(R.id.current_number);
        goalNumber = view.findViewById(R.id.goal_number);
        financialUsage = view.findViewById(R.id.detailed_usage);
        progressBar = view.findViewById(R.id.progressBar);
        emptyNotice = view.findViewById(R.id.empty_notice);
        layout1 = view.findViewById(R.id.description);
        layout2 = view.findViewById(R.id.Usage);

        //firstly initialize the layouts to be invisible before checking if there is available financial donation
        layout1.setVisibility(View.GONE);
        layout2.setVisibility(View.GONE);
        donateBtn.setVisibility(View.GONE);
        emptyNotice.setVisibility(View.GONE);

        /**
         * Check the current user's network connection.
         * If user is connected to the network, then read data from cloud
         * else prompt users with toast messages.
         * */
        if (checkNetworkConnectivity()) {
            readDataAndSetContent(view);
        } else {
            Toast.makeText(getContext(), "Please make sure you are in network connection!", Toast.LENGTH_SHORT).show();
        }

        /**
         * If the donate button is clicked, then firstly check the current network connectivity
         * If the user is connected with the network, then go to the next activity with the parameters
         * Else prompt users with toast messages
         * */
        donateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNetworkConnectivity()) {
                    Intent intent = new Intent(getContext(), PaymentDonate.class);
                    intent.putExtra("currentOrphanId", mParam1);
                    intent.putExtra("currentOrphanName", mParam2);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Please make sure you are in network connection!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
        //return inflater.inflate(R.layout.fragment_user_financial, container, false);
    }

    /**
     * Read related financial donation data for the current selected orphanage And display the data in the current
     * page.
     *
     * @param view the current view of the user financial fragment
     */
    public void readDataAndSetContent(final View view) {
        //Initialize the firebase store instance
        db = FirebaseFirestore.getInstance();

        /**
         * Read the document from the specific documentation path
         * If successfully read data, then display the information and initialize the progress bar with the number
         * read from the documentation.
         * If there is no available relevant orphanage info, then give appropriate prompts to users in this fragment.
         * */
        db.collection("orphanFinancialDonation").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int size = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String userId = document.get("id").toString();
                        if (userId.equals(mParam1)) {
                            layout1.setVisibility(View.VISIBLE);
                            layout2.setVisibility(View.VISIBLE);
                            id = document.getId();
                            descriptionTitle.setText(document.get("title").toString());
                            description.setText(document.get("description").toString());
                            donorsNumber.setText(document.get("donorsNumber").toString());
                            currentNumber.setText("$" + document.get("currentNumber").toString());
                            goalNumber.setText("$" + document.get("goal").toString());

                            /** Set progress bar to the current donation number and the goal*/
                            progressBar.setMax(Integer.parseInt(document.get("goal").toString()));
                            progressBar.setProgress(Integer.parseInt(document.get("currentNumber").toString()));
                            financialUsage.setText(document.get("usage").toString());
                            donateBtn.setVisibility(View.VISIBLE);
                        } else {
                            size++;
                        }
                    }
                    if (task.getResult().size() == size) {
                        emptyNotice.setVisibility(View.VISIBLE);
                        emptyNotice.setText("No Available Donations");
                        layout1.setVisibility(View.GONE);
                        layout2.setVisibility(View.GONE);
                        donateBtn.setVisibility(View.GONE);
                    }
                } else {
                }
            }
        });

    }

    /**
     * The function to check if the device is connected to the network because only when connecting to the network, can
     * the uploading/synchronization actions can be executed
     *
     * @return isConnected a boolean value to represent if the device is connected to the network. If true, then
     * connected, if false, then not connected.
     */
    private Boolean checkNetworkConnectivity() {
        /** Initializes a connectivity manager to check the network information*/
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}