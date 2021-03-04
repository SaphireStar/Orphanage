package au.edu.sydney.comp5216.orphan.orph.financial;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import au.edu.sydney.comp5216.orphan.R;
import au.edu.sydney.comp5216.orphan.orph.topTab.OrphanTab;

/**
 * A simple {@link Fragment} subclass. Use the {@link OrphanFinancial#newInstance} factory method to create an instance
 * of this fragment.
 */

/**
 * This class is used for OrphanFinancial Tab Fragment in the Top Tab. Users can edit, add, and close their current
 * financial donations. But orphanages can only edit, and add, close the financial donations with network connection And
 * they can only edit and add with WIFI connection.
 *
 * @author Siqi Wu
 */
public class OrphanFinancial extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    //private String mParam2;

    /**
     * widgets in the orphan financial fragment
     */
    private ImageButton editUsageBtn;
    private ImageButton addBtn;
    private Button closeBtn;
    private TextView donationTitle;
    private TextView donationDescription;
    private TextView donorsNumber;
    private TextView currentNumber;
    private TextView goalNumber;
    private TextView financiaUsage;
    private LinearLayout firstLayout;
    private LinearLayout secondLayout;
    private TextView emptyNotice;
    private ProgressBar progressBar;

    //instance of firebaseFirestore
    private FirebaseFirestore db;

    //id representing the current document id
    private String id;

    //network connection type code
    private final static int STATE_WIFI = 1;
    private final static int STATE_CELLULAR = 2;


    public OrphanFinancial() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param param1 Parameter 1 representing current orphanage ID. //@param param2 Parameter 2.
     *
     * @return A new instance of fragment UserFinancial.
     */
    // TODO: Rename and change types and number of parameters
    public static OrphanFinancial newInstance(String param1) {
        OrphanFinancial fragment = new OrphanFinancial();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    /**
     * Initialize the Orphanage Financial Fragment's view with default parameters
     *
     * @param inflater the current fragment's layout inflater
     * @param container the viewGroup in this fragment
     * @param savedInstanceState default parameters
     *
     * @return view the current view after initializing the components in the fragment
     */
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orphan_financial, container, false);

        /** Initialize the widgets in this fragment*/
        addBtn = view.findViewById(R.id.addBtn);
        editUsageBtn = view.findViewById(R.id.editBtn);
        closeBtn = view.findViewById(R.id.financial_btn);
        donationTitle = view.findViewById(R.id.financial_title);
        donationDescription = view.findViewById(R.id.financial_description);
        donorsNumber = view.findViewById(R.id.donors_number);
        currentNumber = view.findViewById(R.id.current_number);
        goalNumber = view.findViewById(R.id.goal_number);
        financiaUsage = view.findViewById(R.id.financial_usage);
        progressBar = view.findViewById(R.id.progressBar);
        emptyNotice = view.findViewById(R.id.empty_notice);
        firstLayout = view.findViewById(R.id.donationFirst);
        secondLayout = view.findViewById(R.id.donationSecond);

        //firstly initialize the layouts to be invisible before checking if there is available financial donation
        firstLayout.setVisibility(View.GONE);
        secondLayout.setVisibility(View.GONE);
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
         * Add onClick method to the Add new Donations button
         * If the user is connected to the network and the type is WIFI, then do further checking,
         * Else promot users with the toast messages.
         * */
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNetworkConnectivity() && checkNetworkType() == 1) {
                    checkDonationBeforeAdd();
                } else {
                    Toast.makeText(getContext(), "Please make sure you are in WIFI connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /**
         * Add onClick method to the Edit usage Button.
         * If the user is connected to the network and the type is WIFI, then do further updating,
         * Else promot users with Toast messages
         * */
        editUsageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNetworkConnectivity() && checkNetworkType() == 1) {
                    setPromptDialog(v);
                } else {
                    Toast.makeText(getContext(), "Please make sure you are in WIFI connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /**
         * Add onClick method to the Close Current Donation Button.
         * If users are connected to the network, then they can close the current donation
         * Else prompt users with toast messages.
         * */
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNetworkConnectivity()) {
                    deleteDonation();
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
     * Read related financial donation data for the current orphanage And display the data in the current page.
     *
     * @param view the current view of the orphan financial fragment
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
                            firstLayout.setVisibility(View.VISIBLE);
                            secondLayout.setVisibility(View.VISIBLE);
                            id = document.getId();
                            donationTitle.setText(document.get("title").toString());
                            donationDescription.setText(document.get("description").toString());
                            donorsNumber.setText(document.get("donorsNumber").toString());
                            currentNumber.setText("$" + document.get("currentNumber").toString());
                            goalNumber.setText("$" + document.get("goal").toString());
                            financiaUsage.setText(document.get("usage").toString());

                            /** Set progress bar to the current donation number and the goal*/
                            progressBar.setMax(Integer.parseInt(document.get("goal").toString()));
                            progressBar.setProgress(Integer.parseInt(document.get("currentNumber").toString()));

                        } else {
                            size++;
                        }
                    }
                    if (task.getResult().size() == size) {
                        emptyNotice.setVisibility(View.VISIBLE);
                        emptyNotice.setText("There is no financial donation proposal currently. You can add a new " + "one!");
                        firstLayout.setVisibility(View.GONE);
                        secondLayout.setVisibility(View.GONE);
                    }
                } else {
                }
            }
        });

    }

    /**
     * Initialize the prompt Edit Usage EditText dialog And save the edited dialog to the cloud FireStore.
     *
     * @param view the view for the current orphan financial fragment
     */
    public void setPromptDialog(final View view) {
        /**
         * Initialize the prompt dialog with appropriate layout so that users can input edited usage information in
         * the dialog.
         * */
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        LinearLayout container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(30, 10, 30, 10);
        final EditText edittext = new EditText(getContext());
        edittext.setSingleLine(false);
        edittext.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        edittext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        edittext.setLines(5);
        edittext.setPadding(30, 30, 30, 30);
        edittext.setGravity(Gravity.TOP | Gravity.LEFT);
        edittext.setVerticalScrollBarEnabled(true);
        edittext.setMovementMethod(ScrollingMovementMethod.getInstance());
        edittext.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        Drawable bg = getResources().getDrawable(R.drawable.custom_button);
        edittext.setBackground(bg);
        edittext.setLayoutParams(lp);
        edittext.setText(financiaUsage.getText().toString());
        container.addView(edittext, lp);
        alert.setTitle("Edit Donation Usage");
        alert.setView(container);

        /**
         * Reset the new Usage and save it
         * Reload the page with new data
         * */
        alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                String newUsage = edittext.getText().toString();
                updateUsage(newUsage);
                Toast.makeText(getContext(), "Updated Successfully!", Toast.LENGTH_SHORT).show();
                readDataAndSetContent(view);
            }
        });

        /** Cancel Edit*/
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

    /**
     * Update usage for the current donation.
     *
     * @param newUsage new information to be updated in the specific firebase firestore document
     */
    public void updateUsage(String newUsage) {
        db = FirebaseFirestore.getInstance();

        db.collection("orphanFinancialDonation").document(id).update("usage", newUsage);
    }

    /**
     * Delete the current Donation once the user click the close button And refresh the page
     */
    public void deleteDonation() {
        db = FirebaseFirestore.getInstance();

        db.collection("orphanFinancialDonation").document(id).delete();
        Toast.makeText(getContext(), "Close Successfully!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getContext(), OrphanTab.class);
        intent.putExtra("userId", mParam1);
        startActivity(intent);
    }

    /**
     * Check if there is existed financial donation in the database for the current orphanage If yes, prompt users with
     * appropriate messages and limit them to add new financial information If not, then users can go to further page to
     * make donations. if there is something else wrong, prompt users to try again.
     */
    public void checkDonationBeforeAdd() {
        db = FirebaseFirestore.getInstance();

        db.collection("orphanFinancialDonation").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String userId = document.get("id").toString();
                        if (userId.equals(mParam1)) {
                            new AlertDialog.Builder(getContext()).setTitle("Kind Tips").setMessage("Please close the "
                                    + "current financial donation before adding a new one!")
                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Continue with delete operation
                                        }
                                    }).setIcon(android.R.drawable.ic_dialog_alert).show();
                            return;
                        }
                    }
                    Intent intent = new Intent(getContext(), NewDonation.class);
                    intent.putExtra("userId", mParam1);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Please try it again!", Toast.LENGTH_SHORT).show();
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

    /**
     * The function is to check the active network type is WIFI or Cellular network
     *
     * @return an int value to represent the active network type, if 1 then WIFI, if 2 then Cellular, if -1, not known
     */
    private int checkNetworkType() {
        /** Initializes a connectivity manager to check network information*/
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
            return STATE_WIFI;
        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
            return STATE_CELLULAR;
        }
        return -1;
    }
}